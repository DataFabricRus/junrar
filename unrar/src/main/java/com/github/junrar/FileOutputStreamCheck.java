/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.junrar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/** OutputStream for comparing generated stream with predefined stream to find differences
 *
 * @author Roy Damman
 * 
 */
public class FileOutputStreamCheck extends FileOutputStream
{
//    protected FileInputStream _fileis;
    protected BufferedInputStream _fileis;
    
    public FileOutputStreamCheck(File fileIn, File fileCheck) throws FileNotFoundException 
    {
        super(fileIn);
//        _fileis = new FileInputStream(fileCheck);
        _fileis = new BufferedInputStream(new FileInputStream(fileCheck));
    }
    
    @Override
    public void close() throws IOException
    {
        super.close();
        _fileis.close();
    }

    @Override
    public void write(int b) throws IOException
    {
        compareWithIS((byte)b);
        super.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        for (int iT1 = 0; iT1 < b.length; iT1++)
            compareWithIS(b[iT1]);
        super.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        for (int iT1 = off; iT1 < off+len; iT1++)
            compareWithIS(b[iT1]);
        super.write(b, off, len);
    }
    
    int _iOffsetIn = 0;

    protected void compareWithIS(byte i8In) throws IOException
    {
        int iHelp = _fileis.read();
        
        if (iHelp < 0)
            System.out.println("Beyound end of file");
        byte i8Help = (byte)iHelp;
        if (i8Help != i8In)
//            System.out.println("Compare error at: " + Long.toString(_fileis.getChannel().position())
            System.out.println("Compare error at: " + Long.toString(_iOffsetIn)
               + " Value: " + Integer.toString(i8In) + " Expected: " + Integer.toString(i8Help));
        _iOffsetIn++;
    }
}
