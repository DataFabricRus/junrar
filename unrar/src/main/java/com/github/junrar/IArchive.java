package com.github.junrar;

import com.github.junrar.io.IReadOnlyAccess;
import com.github.junrar.rarfile.FileHeader;
import com.github.junrar.rarfile.MainHeader;

import java.io.IOException;

public interface IArchive {
    IReadOnlyAccess getRof();

    void bytesReadRead(int count);

    FileHeader nextFileHeader();

    VolumeManager getVolumeManager();

    Volume getVolume();

    UnrarCallback getUnrarCallback();

    void setVolume(Volume volume) throws IOException;

    boolean isOldFormat();

    MainHeader getMainHeader();
}
