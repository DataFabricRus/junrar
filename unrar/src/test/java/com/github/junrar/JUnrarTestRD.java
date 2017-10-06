package com.github.junrar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;
import java.io.BufferedOutputStream;

/** Testprogram for comparing decompressed stream with predefined stream to find differences
 *
 * @author Roy Damman
 * 
 */
public class JUnrarTestRD
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.out.println("Error: Missing parameters.");
            System.out.println("usage: java -jar extractArchive.jar <thearchive> "
                    + "<decompressed reference file> <the destination directory>.");
            return;
        }
        String sRarArchive = args[0];
        String sUncompressedFile = args[1];
        String sOutputDir = args[2];
        File fileArchive = new File(sRarArchive);
        Archive archive = null;
        try
        {
            archive = new Archive(new FileVolumeManager(fileArchive));
        } catch (RarException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (sOutputDir.charAt(sOutputDir.length()-1) != File.separatorChar)
            sOutputDir = sOutputDir + File.separatorChar;
        
        if (archive != null)
        {
            archive.getMainHeader().print();
            FileHeader fileheader = archive.nextFileHeader();
            while (fileheader != null)
            {
                try
                {
                    File fileOut = new File(sOutputDir + fileheader.getFileNameString().trim());
                    System.out.println(fileOut.getAbsolutePath());
                    File fileCheck = new File(sUncompressedFile);
                    FileOutputStreamCheck fileoscheck = new FileOutputStreamCheck(fileOut, fileCheck);
//                    archive.extractFile(fileheader, fileoscheck);
//                    fileoscheck.close();
                    //
                    BufferedOutputStream bufoutpstr = new BufferedOutputStream(fileoscheck);
                    archive.extractFile(fileheader, bufoutpstr);
                    bufoutpstr.close();

                } catch (FileNotFoundException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (RarException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                fileheader = archive.nextFileHeader();
            }
        }
    }
}
