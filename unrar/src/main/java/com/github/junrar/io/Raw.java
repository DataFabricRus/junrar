/*
 * Copyright (c) 2007 innoSysTec (R) GmbH, Germany. All rights reserved.
 * Original author: Edmund Wagner
 * Creation date: 18.06.2007
 * Modifications by: Roy Damman
 * Creation date: 21.04.2016
 *
 * Source: $HeadURL$
 * Last changed: $LastChangedDate$
 * 
 * the unrar licence applies to all junrar source and binary distributions 
 * you are not allowed to use this source to re-create the RAR compression algorithm
 * 
 * Here some html entities which can be used for escaping javadoc tags:
 * "&":  "&#038;" or "&amp;"
 * "<":  "&#060;" or "&lt;"
 * ">":  "&#062;" or "&gt;"
 * "@":  "&#064;" 
 */
package com.github.junrar.io;

/**
 * Read / write numbers to a byte[] regarding the endianness of the array
 * 
 * @author $LastChangedBy$
 * @version $LastChangedRevision$
 */
public class Raw {
    /**
     * Read a short value from the byte array at the given position (Big Endian)
     * 
     * @param array
     *            the array to read from
     * @param pos
     *            the position
     * @return the value
     */
    public static final short readShortBigEndian(byte[] array, int pos) {
	short temp = (short) array[pos];
	temp <<= 8;
	temp |= (short) array[pos + 1] & 0xFF;
	return temp;
    }

    /**
     * Read a int value from the byte array at the given position (Big Endian)
     * 
     * @param array
     *            the array to read from
     * @param pos
     *            the offset
     * @return the value
     */
    public static final int readIntBigEndian(byte[] array, int pos) {
	int temp = (int) array[pos];
	temp <<= 8;
	temp |= array[++pos] & 0xFF;
	temp <<= 8;
	temp |= array[++pos] & 0xFF;
	temp <<= 8;
	temp |= array[++pos] & 0xFF;
	return temp;
    }

    /**
     * Read a long value from the byte array at the given position (Big Endian)
     * 
     * @param array
     *            the array to read from
     * @param pos
     *            the offset
     * @return the value
     */
    public static final long readLongBigEndian(byte[] array, int pos) {
	long temp = array[pos];
	temp <<= 8;
	temp |= array[++pos] & 0xFF;
	temp <<= 8;
	temp |= array[++pos] & 0xFF;
	temp <<= 8;
	temp |= array[++pos] & 0xFF;
	temp <<= 8;
	temp |= array[++pos] & 0xFF;
	temp <<= 8;
	temp |= array[++pos] & 0xFF;
	temp <<= 8;
	temp |= array[++pos] & 0xFF;
	temp <<= 8;
	temp |= array[++pos] & 0xFF;
	return temp;
    }

    /**
     * Read a short value from the byte array at the given position (little
     * Endian)
     * 
     * @param array
     *            the array to read from
     * @param pos
     *            the offset
     * @return the value
     */
    public static final short readShortLittleEndian(byte[] array, int pos) {
	short result = (short) array[pos + 1];
	result <<= 8;
	result |= (short) array[pos] & 0xFF;
	return result;
    }

    /**
     * Read an int value from the byte array at the given position (little
     * Endian)
     * 
     * @param array
     *            the array to read from
     * @param pos
     *            the offset
     * @return the value
     */
    public static final int readIntLittleEndian(byte[] array, int pos)
    {
        pos += 3;
        int temp = array[pos];
	temp <<= 8;
	temp |= array[--pos] & 0xFF;
	temp <<= 8;
	temp |= array[--pos] & 0xFF;
	temp <<= 8;
	temp |= array[--pos] & 0xFF;
        return temp;
    }

    /**
     * Read an long value(unsigned int) from the byte array at the given
     * position (little Endian)
     * 
     * @param array
     * @param pos
     * @return
     */
    public static final long readIntLittleEndianAsLong(byte[] array, int pos) {
//	return (((long) array[pos + 3] & 0xff) << 24)
//		| (((long) array[pos + 2] & 0xff) << 16)
//		| (((long) array[pos + 1] & 0xff) << 8)
//		| (((long) array[pos] & 0xff));
        long lHelp = (long) readIntLittleEndian(array, pos); 
        return lHelp & 0xFFFFFFFFL; 
    }

    /**
     * Read a long value from the byte array at the given position (little
     * Endian)
     * 
     * @param array
     *            the array to read from
     * @param pos
     *            the offset
     * @return the value
     */
    public static final long readLongLittleEndian(byte[] array, int pos) 
    {
        pos += 7;
        long temp = array[pos];
	temp <<= 8;
	temp |= array[--pos] & 0xFF;
	temp <<= 8;
	temp |= array[--pos] & 0xFF;
	temp <<= 8;
	temp |= array[--pos] & 0xFF;
	temp <<= 8;
	temp |= array[--pos] & 0xFF;
	temp <<= 8;
	temp |= array[--pos] & 0xFF;
	temp <<= 8;
	temp |= array[--pos] & 0xFF;
	temp <<= 8;
	temp |= array[--pos] & 0xFF;
	return temp;
    }

    /**
     * Write a short value into the byte array at the given position (Big
     * endian)
     * 
     * @param array
     *            the array
     * @param pos
     *            the offset
     * @param value
     *            the value to write
     */
    public static final void writeShortBigEndian(byte[] array, int pos, short value) 
    {
	array[pos] = (byte) (value >>> 8);
	array[pos + 1] = (byte) (value & 0xFF);
    }

    /**
     * Write an int value into the byte array at the given position (Big endian)
     * 
     * @param array
     *            the array
     * @param pos
     *            the offset
     * @param value
     *            the value to write
     */
    public static final void writeIntBigEndian(byte[] array, int pos, int value)
    {
        pos += 3;
        array[pos] =   (byte) (value);
        value >>>= 8;
	array[--pos] = (byte) (value);
        value >>>= 8;
	array[--pos] = (byte) (value);
        value >>>= 8;
	array[--pos] = (byte) (value);
    }

    /**
     * Write a long value into the byte array at the given position (Big endian)
     * 
     * @param array
     *            the array
     * @param pos
     *            the offset
     * @param value
     *            the value to write
     */
    public static final void writeLongBigEndian(byte[] array, int pos, long value)
    {
        pos += 7;
        array[pos] =   (byte) (value);
        value >>>= 8;
	array[--pos] = (byte) (value);
        value >>>= 8;
	array[--pos] = (byte) (value);
        value >>>= 8;
	array[--pos] = (byte) (value);
        value >>>= 8;
	array[--pos] = (byte) (value);
        value >>>= 8;
	array[--pos] = (byte) (value);
        value >>>= 8;
	array[--pos] = (byte) (value);
        value >>>= 8;
	array[--pos] = (byte) (value);
    }

    /**
     * Write a short value into the byte array at the given position (little
     * endian)
     * 
     * @param array
     *            the array
     * @param pos
     *            the offset
     * @param value
     *            the value to write
     */
    public static final void writeShortLittleEndian(byte[] array, int pos,
	    short value) {
	array[pos + 1] = (byte) (value >>> 8);
	array[pos] = (byte) (value);

    }

    /**
     * Increment a short value at the specified position by the specified amount
     * (little endian).
     */
    public static final void incShortLittleEndian(byte[] array, int pos, int dv) {
	int c = ((array[pos] & 0xff) + (dv & 0xff)) >>> 8;
	array[pos] += dv & 0xff;
	if ((c > 0) || ((dv & 0xff00) != 0)) {
	    array[pos + 1] += ((dv >>> 8) & 0xff) + c;
	}
    }

    /**
     * Write an int value into the byte array at the given position (little
     * endian)
     * 
     * @param array
     *            the array
     * @param pos
     *            the offset
     * @param value
     *            the value to write
     */
    public static final void writeIntLittleEndian(byte[] array, int pos, int value)
    {
	array[pos] =   (byte) (value);
        value >>>= 8;
	array[++pos] = (byte) (value);
        value >>>= 8;
	array[++pos] = (byte) (value);
        value >>>= 8;
	array[++pos] = (byte) (value);
    }

    /**
     * Write a long value into the byte array at the given position (little
     * endian)
     * 
     * @param array
     *            the array
     * @param pos
     *            the offset
     * @param value
     *            the value to write
     */
    public static final void writeLongLittleEndian(byte[] array, int pos, long value)
    {
	array[pos] =   (byte) (value);
        value >>>= 8;
	array[++pos] = (byte) (value);
        value >>>= 8;
	array[++pos] = (byte) (value);
        value >>>= 8;
	array[++pos] = (byte) (value);
        value >>>= 8;
	array[++pos] = (byte) (value);
        value >>>= 8;
	array[++pos] = (byte) (value);
        value >>>= 8;
	array[++pos] = (byte) (value);
        value >>>= 8;
	array[++pos] = (byte) (value);
     }
    
    public static void main(String[] args)
    {
        test(true);
    }
    
    public static void test(boolean bPrestaties)
    {
       // maken buffer
       byte[] abyteTest = new byte[32];
       //
       long lTestVal1 = 0xFFDCBA980102048EL;
       long lTestVal2 = 0xDCBA988F0102048EL;
       long lTestVal3 = 0x7F1881790102048EL;
       long lTestVal4 = 0x7901107F0102048FL;
       writeLongLittleEndian(abyteTest, 0, lTestVal1);
       writeLongLittleEndian(abyteTest, 8, lTestVal2);
       writeLongLittleEndian(abyteTest, 16, lTestVal3);
       writeLongLittleEndian(abyteTest, 24, lTestVal4);
       if (readLongLittleEndian(abyteTest, 0) != lTestVal1)
          System.out.println("Fout LongLittleEndian 1");
       if (readLongLittleEndian(abyteTest, 8) != lTestVal2)
          System.out.println("Fout LongLittleEndian 2");
       if (readLongLittleEndian(abyteTest, 16) !=  lTestVal3)
          System.out.println("Fout LongLittleEndian 3");
       if (readLongLittleEndian(abyteTest, 24) !=  lTestVal4)
          System.out.println("Fout LongLittleEndian 4");
       //
       writeLongBigEndian(abyteTest, 0, lTestVal1);
       writeLongBigEndian(abyteTest, 8, lTestVal2);
       writeLongBigEndian(abyteTest, 16, lTestVal3);
       writeLongBigEndian(abyteTest, 24, lTestVal4);
       if (readLongBigEndian(abyteTest, 0) != lTestVal1)
          System.out.println("Fout LongBigEndian 1");
       if (readLongBigEndian(abyteTest, 8) != lTestVal2)
          System.out.println("Fout LongBigEndian 2");
       if (readLongBigEndian(abyteTest, 16) !=  lTestVal3)
          System.out.println("Fout LongBigEndian 3");
       if (readLongBigEndian(abyteTest, 24) !=  lTestVal4)
          System.out.println("Fout LongBigEndian 4");
       //
       int iTestVal1 = 0xFFDCBA98;
       int iTestVal2 = 0xDCBA988F;
       int iTestVal3 = 0x7F188179;
       int iTestVal4 = 0x0102048E;
       writeIntLittleEndian(abyteTest, 0, iTestVal1);
       writeIntLittleEndian(abyteTest, 8, iTestVal2);
       writeIntLittleEndian(abyteTest, 16, iTestVal3);
       writeIntLittleEndian(abyteTest, 24, iTestVal4);
       if (readIntLittleEndian(abyteTest, 0) != iTestVal1)
          System.out.println("Fout IntLittleEndian 1");
       if (readIntLittleEndian(abyteTest, 8) != iTestVal2)
          System.out.println("Fout IntLittleEndian 2");
       if (readIntLittleEndian(abyteTest, 16) !=  iTestVal3)
          System.out.println("Fout IntLittleEndian 3");
       if (readIntLittleEndian(abyteTest, 24) !=  iTestVal4)
          System.out.println("Fout IntLittleEndian 4");
       //
       if (readIntLittleEndianAsLong(abyteTest, 0) != (((long)iTestVal1) & 0xFFFFFFFFL))
          System.out.println("Fout IntLongLittleEndian 1");
       if (readIntLittleEndianAsLong(abyteTest, 8) != (((long)iTestVal2) & 0xFFFFFFFFL))
          System.out.println("Fout IntLongLittleEndian 2");
       if (readIntLittleEndianAsLong(abyteTest, 16) !=  (((long)iTestVal3) & 0xFFFFFFFFL))
          System.out.println("Fout IntLongLittleEndian 3");
       if (readIntLittleEndianAsLong(abyteTest, 24) !=  (((long)iTestVal4) & 0xFFFFFFFFL))
          System.out.println("Fout IntLongLittleEndian 4");
       //
       writeIntBigEndian(abyteTest, 0, iTestVal1);
       writeIntBigEndian(abyteTest, 8, iTestVal2);
       writeIntBigEndian(abyteTest, 16, iTestVal3);
       writeIntBigEndian(abyteTest, 24, iTestVal4);
       if (readIntBigEndian(abyteTest, 0) != iTestVal1)
          System.out.println("Fout IntBigEndian 1");
       if (readIntBigEndian(abyteTest, 8) != iTestVal2)
          System.out.println("Fout IntBigEndian 2");
       if (readIntBigEndian(abyteTest, 16) !=  iTestVal3)
          System.out.println("Fout IntBigEndian 3");
       if (readIntBigEndian(abyteTest, 24) !=  iTestVal4)
          System.out.println("Fout IntBigEndian 4");
       //
       short i2TestVal1 = (short)0xFFDC;
       short i2TestVal2 = (short)0xDCBA;
       short i2TestVal3 = (short)0x7F18;
       short i2TestVal4 = (short)0x0102;
       writeShortLittleEndian(abyteTest, 0, i2TestVal1);
       writeShortLittleEndian(abyteTest, 8, i2TestVal2);
       writeShortLittleEndian(abyteTest, 16, i2TestVal3);
       writeShortLittleEndian(abyteTest, 24, i2TestVal4);
       if (readShortLittleEndian(abyteTest, 0) != i2TestVal1)
          System.out.println("Fout ShortLittleEndian 1");
       if (readShortLittleEndian(abyteTest, 8) != i2TestVal2)
          System.out.println("Fout ShortLittleEndian 2");
       if (readShortLittleEndian(abyteTest, 16) !=  i2TestVal3)
          System.out.println("Fout ShortLittleEndian 3");
       if (readShortLittleEndian(abyteTest, 24) !=  i2TestVal4)
          System.out.println("Fout ShortLittleEndian 4");
       //
       writeShortBigEndian(abyteTest, 0, i2TestVal1);
       writeShortBigEndian(abyteTest, 8, i2TestVal2);
       writeShortBigEndian(abyteTest, 16, i2TestVal3);
       writeShortBigEndian(abyteTest, 24, i2TestVal4);
       if (readShortBigEndian(abyteTest, 0) != i2TestVal1)
          System.out.println("Fout ShortBigEndian 1");
       if (readShortBigEndian(abyteTest, 8) != i2TestVal2)
          System.out.println("Fout ShortBigEndian 2");
       if (readShortBigEndian(abyteTest, 16) !=  i2TestVal3)
          System.out.println("Fout ShortBigEndian 3");
       if (readShortBigEndian(abyteTest, 24) !=  i2TestVal4)
          System.out.println("Fout ShortBigEndian 4");
       
       
       System.out.println("Gereed");
    }
    
}
