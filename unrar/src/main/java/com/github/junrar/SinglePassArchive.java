package com.github.junrar;

import com.github.junrar.exception.RarException;
import com.github.junrar.io.IReadOnlyAccess;
import com.github.junrar.rarfile.*;
import com.github.junrar.unpack.ComprDataIO;
import com.github.junrar.unpack.Unpack;

import java.io.*;
import java.util.logging.Logger;

public class SinglePassArchive implements IArchive {
    private static Logger logger = Logger.getLogger(SinglePassArchive.class.getName());


    private VolumeManager volumeManager;
    private final UnrarCallback unrarCallback;
    private final ComprDataIO dataIO;
    private IReadOnlyAccess rof;
    private Unpack unpack;
    private MarkHeader markHead = null;
    private MainHeader newMhd = null;


    private long totalPackedRead = 0L;
    private long totalPackedSize = 0L;

    private int toRead;
    private int size = 0;
    private long newpos = 0;
    private byte[] baseBlockBuffer;
    private long position;


    public SinglePassArchive(VolumeManager volumeManager, boolean some) throws RarException, IOException {
        this.volumeManager = volumeManager;
        this.unrarCallback = null;
        dataIO = new ComprDataIO(this);
        rof = this.volumeManager.nextArchive(this, null).getReadOnlyAccess();
    }

    public void extractOnlyFile(FileHeader hd, OutputStream os) throws RarException {
        try {
            doExtractFile(hd, os);
        } catch (Exception e) {
            if (e instanceof RarException) {
                throw (RarException) e;
            } else {
                throw new RarException(e);
            }
        }
    }

    private void doExtractFile(FileHeader hd, OutputStream os)
            throws RarException, IOException {
        dataIO.init(os);
        dataIO.init(hd);
        dataIO.setUnpFileCRC(this.isOldFormat() ? 0 : 0xffFFffFF);
        if (unpack == null) {
            unpack = new Unpack(dataIO);
        }
        if (!hd.isSolid()) {
            unpack.init(null);
        }
        unpack.setDestSize(hd.getFullUnpackSize());
        try {
            unpack.doUnpack(hd.getUnpVersion(), hd.isSolid());
            hd = dataIO.getSubHeader();
            long actualCRC = hd.isSplitAfter() ? ~dataIO.getPackedCRC()
                    : ~dataIO.getUnpFileCRC();
            int expectedCRC = hd.getFileCRC();
            if (actualCRC != expectedCRC) {
                throw new RarException(RarException.RarExceptionType.crcError);
            }
        } catch (Exception e) {
            unpack.cleanUp();
            if (e instanceof RarException) {
                throw (RarException) e;
            } else {
                throw new RarException(e);
            }
        }
    }


    /**
     * Returns an {@link InputStream} that will allow to read the file and
     * stream it. Please note that this method will create a new Thread and an a
     * pair of Pipe streams.
     *
     * @param hd the header to be extracted
     * @throws RarException
     * @throws IOException  if any IO error occur
     */
    public InputStream getInputStream(final FileHeader hd) throws RarException,
            IOException {
        final PipedInputStream in = new PipedInputStream(32 * 1024);
        final PipedOutputStream out = new PipedOutputStream(in);

        // creates a new thread that will write data to the pipe. Data will be
        // available in another InputStream, connected to the OutputStream.
        new Thread(new Runnable() {
            public void run() {
                try {
                    extractOnlyFile(hd, out);
                } catch (RarException e) {
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }).start();

        return in;
    }


    public FileHeader readHeader() throws IOException, RarException {
        toRead = 0;
        while (true) {
            size = 0;
            newpos = 0;
            baseBlockBuffer = new byte[BaseBlock.BaseBlockSize];

            position = rof.getPosition();

            // Weird, but is trying to read beyond the end of the file
            if (position >= Long.MAX_VALUE) {
                break;
            }
            size = rof.readFully(baseBlockBuffer, BaseBlock.BaseBlockSize);
            if (size == 0) {
                break;
            }

            BaseBlock block = new BaseBlock(baseBlockBuffer);

            block.setPositionInFile(position);

            switch (block.getHeaderType()) {

                case MarkHeader:
                    markHead = new MarkHeader(block);
                    if (!markHead.isSignature()) {
                        throw new RarException(
                                RarException.RarExceptionType.badRarArchive);
                    }
                    break;

                case MainHeader:
                    toRead = block.hasEncryptVersion() ? MainHeader.mainHeaderSizeWithEnc
                            : MainHeader.mainHeaderSize;
                    byte[] mainbuff = new byte[toRead];
                    rof.readFully(mainbuff, toRead);
                    MainHeader mainhead = new MainHeader(block, mainbuff);
                    this.newMhd = mainhead;
                    if (newMhd.isEncrypted()) {
                        throw new RarException(
                                RarException.RarExceptionType.rarEncryptedException);
                    }
                    break;

                case SignHeader:
                    toRead = SignHeader.signHeaderSize;
                    byte[] signBuff = new byte[toRead];
                    rof.readFully(signBuff, toRead);
                    SignHeader signHead = new SignHeader(block, signBuff);
                    break;

                case AvHeader:
                    toRead = AVHeader.avHeaderSize;
                    byte[] avBuff = new byte[toRead];
                    rof.readFully(avBuff, toRead);
                    AVHeader avHead = new AVHeader(block, avBuff);
                    break;

                case CommHeader:
                    toRead = CommentHeader.commentHeaderSize;
                    byte[] commBuff = new byte[toRead];
                    rof.readFully(commBuff, toRead);
                    CommentHeader commHead = new CommentHeader(block, commBuff);
                    newpos = commHead.getPositionInFile()
                            + commHead.getHeaderSize();
                    rof.setPosition(newpos);

                    break;
                case EndArcHeader:

                    toRead = 0;
                    if (block.hasArchiveDataCRC()) {
                        toRead += EndArcHeader.endArcArchiveDataCrcSize;
                    }
                    if (block.hasVolumeNumber()) {
                        toRead += EndArcHeader.endArcVolumeNumberSize;
                    }
                    EndArcHeader endArcHead;
                    if (toRead > 0) {
                        byte[] endArchBuff = new byte[toRead];
                        rof.readFully(endArchBuff, toRead);
                        endArcHead = new EndArcHeader(block, endArchBuff);
                    } else {
                        endArcHead = new EndArcHeader(block, null);
                    }
                    return null;

                default:
                    byte[] blockHeaderBuffer = new byte[BlockHeader.blockHeaderSize];
                    rof.readFully(blockHeaderBuffer, BlockHeader.blockHeaderSize);
                    BlockHeader blockHead = new BlockHeader(block,
                            blockHeaderBuffer);

                    switch (blockHead.getHeaderType()) {
                        case NewSubHeader:
                        case FileHeader:
                            toRead = blockHead.getHeaderSize()
                                    - BlockHeader.BaseBlockSize
                                    - BlockHeader.blockHeaderSize;
                            byte[] fileHeaderBuffer = new byte[toRead];
                            rof.readFully(fileHeaderBuffer, toRead);


                            FileHeader fh = new FileHeader(blockHead, fileHeaderBuffer);

                            newpos = fh.getPositionInFile() + fh.getHeaderSize()
                                    + fh.getFullPackSize();
                            rof.setPosition(newpos);
                            if (!fh.getHeaderType().equals(UnrarHeadertype.FileHeader) || fh.isDirectory()) {
                                break;
                            }
                            return fh;

                        case ProtectHeader:
                            toRead = blockHead.getHeaderSize()
                                    - BlockHeader.BaseBlockSize
                                    - BlockHeader.blockHeaderSize;
                            byte[] protectHeaderBuffer = new byte[toRead];
                            rof.readFully(protectHeaderBuffer, toRead);
                            ProtectHeader ph = new ProtectHeader(blockHead,
                                    protectHeaderBuffer);

                            newpos = ph.getPositionInFile() + ph.getHeaderSize()
                                    + ph.getDataSize();
                            rof.setPosition(newpos);
                            break;

                        case SubHeader: {
                            byte[] subHeadbuffer = new byte[SubBlockHeader.SubBlockHeaderSize];
                            rof.readFully(subHeadbuffer,
                                    SubBlockHeader.SubBlockHeaderSize);
                            SubBlockHeader subHead = new SubBlockHeader(blockHead,
                                    subHeadbuffer);
                            subHead.print();
                            switch (subHead.getSubType()) {
                                case MAC_HEAD: {
                                    byte[] macHeaderbuffer = new byte[MacInfoHeader.MacInfoHeaderSize];
                                    rof.readFully(macHeaderbuffer,
                                            MacInfoHeader.MacInfoHeaderSize);
                                    MacInfoHeader macHeader = new MacInfoHeader(subHead,
                                            macHeaderbuffer);
                                    macHeader.print();
                                    break;
                                }
                                // TODO implement other subheaders
                                case BEEA_HEAD:
                                    break;
                                case EA_HEAD: {
                                    byte[] eaHeaderBuffer = new byte[EAHeader.EAHeaderSize];
                                    rof.readFully(eaHeaderBuffer, EAHeader.EAHeaderSize);
                                    EAHeader eaHeader = new EAHeader(subHead,
                                            eaHeaderBuffer);
                                    eaHeader.print();
                                    break;
                                }
                                case NTACL_HEAD:
                                    break;
                                case STREAM_HEAD:
                                    break;
                                case UO_HEAD:
                                    toRead = subHead.getHeaderSize();
                                    toRead -= BaseBlock.BaseBlockSize;
                                    toRead -= BlockHeader.blockHeaderSize;
                                    toRead -= SubBlockHeader.SubBlockHeaderSize;
                                    byte[] uoHeaderBuffer = new byte[toRead];
                                    rof.readFully(uoHeaderBuffer, toRead);
                                    UnixOwnersHeader uoHeader = new UnixOwnersHeader(
                                            subHead, uoHeaderBuffer);
                                    uoHeader.print();
                                    break;
                                default:
                                    break;
                            }

                            break;
                        }
                        default:
                            logger.warning("Unknown Header");
                            throw new RarException(RarException.RarExceptionType.notRarArchive);

                    }
            }
        }
        return null;
    }

    @Override
    public IReadOnlyAccess getRof() {
        return rof;
    }

    @Override
    public void bytesReadRead(int count) {
        if (count > 0) {
            totalPackedRead += count;
            if (unrarCallback != null) {
                unrarCallback.volumeProgressChanged(totalPackedRead,
                        totalPackedSize);
            }
        }
    }

    @Override
    public FileHeader nextFileHeader() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VolumeManager getVolumeManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Volume getVolume() {
        throw new UnsupportedOperationException();
    }

    @Override
    public UnrarCallback getUnrarCallback() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setVolume(Volume volume) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOldFormat() {
        return markHead.isOldFormat();
    }

    @Override
    public MainHeader getMainHeader() {
        throw new UnsupportedOperationException();
    }
}
