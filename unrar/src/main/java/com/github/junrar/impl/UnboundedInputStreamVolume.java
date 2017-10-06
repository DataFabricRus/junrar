package com.github.junrar.impl;

import com.github.junrar.IArchive;
import com.github.junrar.Volume;
import com.github.junrar.io.IReadOnlyAccess;
import com.github.junrar.io.InputStreamReadOnlyAccess;

import java.io.IOException;
import java.io.InputStream;

public class UnboundedInputStreamVolume implements Volume {
    private final IArchive archive;
    private final InputStream inputStream;

    public UnboundedInputStreamVolume(IArchive archive, InputStream inputstream) throws IOException {
        this.archive = archive;
        this.inputStream = inputstream;
    }

    @Override
    public IReadOnlyAccess getReadOnlyAccess() throws IOException {
        return new InputStreamReadOnlyAccess(inputStream);
    }

    //read up to the end of archive (stream)
    @Override
    public long getLength() {
        return Long.MAX_VALUE;
    }

    @Override
    public IArchive getArchive() {
        return archive;
    }
}