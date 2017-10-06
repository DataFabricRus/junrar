package com.github.junrar;

import com.github.junrar.exception.RarException;
import com.github.junrar.impl.UnboundedInputStreamVolumeManager;
import com.github.junrar.rarfile.FileHeader;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

public class SinglePassArchiveTest {

    private static final String FILENAME = "data.rar";
    private static final String ARCHIVED_ONE = "date.txt";
    private static final String ARCHIVED_TWO = "text.txt";

    @Test
    public void unpackTest() {
        String filepath = SinglePassArchiveTest.class.getClassLoader().getResource(FILENAME).getPath();
        String directory = filepath.replace(FILENAME, "");
        File f = new File(filepath);
        SinglePassArchive a = null;
        try {
            InputStream targetStream = new FileInputStream(f);
            a = new SinglePassArchive(new UnboundedInputStreamVolumeManager(targetStream), false);
        } catch (RarException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (a != null) {
            FileHeader fh;
            try {
                while ((fh = a.readHeader()) != null) {
                    try {
                        File out = new File(
                                directory + fh.getFileNameString().trim()
                        );
                        InputStream is = a.getInputStream(fh);
                        FileUtils.copyToFile(is, out);
                        is.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (RarException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RarException e) {
                e.printStackTrace();
            }
        }

        File archived_one = new File(directory + ARCHIVED_ONE);
        assertTrue(archived_one.exists());
        File archived_two = new File(directory + ARCHIVED_TWO);
        assertTrue(archived_two.exists());
        try {
            Files.delete(archived_one.toPath());
            Files.delete(archived_two.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
