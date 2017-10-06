package com.github.junrar.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class SinglePassAccessStream extends InputStream {
    private static final int BLOCK_SIZE = 512;
    private static final int BLOCK_MASK = 511;
    private static final int BLOCK_SHIFT = 9;

    private InputStream src;
    private long pointer;
    private int length;
    private boolean foundEOS;
    private byte abyte0[];


    public SinglePassAccessStream(InputStream inputstream) {
        pointer = 0L;
        length = 0;
        foundEOS = false;
        src = inputstream;
    }


    public int getFilePointer() throws IOException {
        return (int) pointer;
    }

    public long getLongFilePointer() throws IOException {

        return pointer;
    }

    public int read() throws IOException {
        long l = pointer + 1L;
        long l1 = readUntil(l, (int) (pointer >> BLOCK_SHIFT));
        if (l1 >= l) {
            //was not checked, may be incorrect
            return abyte0[(int) (pointer++ & BLOCK_MASK)] & 0xff;
        } else
            return -1;
    }

    public int read(byte[] bytes, int off, int len) throws IOException {
        if (bytes == null)
            throw new NullPointerException();
        if (off < 0 || len < 0 || off + len > bytes.length)
            throw new IndexOutOfBoundsException();
        if (len == 0)
            return 0;
        long l = readUntil(pointer + len, (int) (pointer >> BLOCK_SHIFT));
        if (l <= pointer)
            return -1;
        else {
            int k = Math.min(len, BLOCK_SIZE - (int) (pointer & BLOCK_MASK));
            System.arraycopy(abyte0, (int) (pointer & BLOCK_MASK), bytes, off,
                    k);
            pointer += k;
            return k;
        }
    }

    public final void readFully(byte[] bytes) throws IOException {
        readFully(bytes, bytes.length);
    }

    public final void readFully(byte[] bytes, int len) throws IOException {
        int read = 0;
        do {
            int l = read(bytes, read, len - read);
            if (l < 0)
                break;
            read += l;
        } while (read < len);
    }

    @SuppressWarnings("unchecked")
    private long readUntil(long l, int needFor) throws IOException {
        if (foundEOS)
            return length;
        int i = needFor;
        long j = length >>> BLOCK_SHIFT;
        for (long k = j; k <= i; k++) {
            abyte0 = new byte[BLOCK_SIZE];
            int i1 = BLOCK_SIZE;
            int j1 = 0;
            while (i1 > 0) {
                int k1 = src.read(abyte0, j1, i1);
                if (k1 == -1) {
                    foundEOS = true;
                    return length;
                }
                j1 += k1;
                i1 -= k1;
                length += k1;
            }

        }

        return length;
    }

    public void seek(long loc) throws IOException {
        if (loc < 0L)
            pointer = 0L;
        else
            pointer = loc;
    }

    public void seek(int loc) throws IOException {
        long lloc = ((long) loc) & 0xffffffffL;
        if (lloc < 0L)
            pointer = 0L;
        else
            pointer = lloc;
    }

    public final int readInt() throws IOException {
        int i = read();
        int j = read();
        int k = read();
        int l = read();
        if ((i | j | k | l) < 0)
            throw new EOFException();
        else
            return (i << 24) + (j << 16) + (k << 8) + l;
    }

    public final long readLong() throws IOException {
        return ((long) readInt() << 32) + ((long) readInt() & 0xffffffffL);
    }

    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public final short readShort() throws IOException {
        int i = read();
        int j = read();
        if ((i | j) < 0)
            throw new EOFException();
        else
            return (short) ((i << 8) + j);
    }

    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public void close() throws IOException {
        src.close();
    }
}