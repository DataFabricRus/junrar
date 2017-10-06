package com.github.junrar.impl;

import com.github.junrar.IArchive;
import com.github.junrar.Volume;
import com.github.junrar.VolumeManager;

import java.io.IOException;
import java.io.InputStream;

public class UnboundedInputStreamVolumeManager implements VolumeManager {
    private final InputStream firstVolume;

    public UnboundedInputStreamVolumeManager(InputStream firstVolume) {
        this.firstVolume = firstVolume;
    }

    @Override
    public Volume nextArchive(IArchive archive, Volume last)
            throws IOException {
        return new UnboundedInputStreamVolume(archive, firstVolume);
    }
}
