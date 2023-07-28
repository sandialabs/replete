package replete.numbers;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.TreeMap;

import replete.text.StringUtil;


/**
 * Convenience methods for manipulating variables at
 * the bit level.  Some of these methods are more of
 * an exercise than they are of a practical purpose,
 * as they are replicated in the Java API or would
 * not actually be used in real applications.
 * Here is some example code using some standard API
 * calls relating to byte manipulation:
 *    RandomAccessFile file = new RandomAccessFile(filename, "r");
 *    byte[] recordBuffer = new byte[RECORD_LENGTH];
 *    ByteBuffer record = ByteBuffer.wrap(recordBuffer);
 *    record.order(ByteOrder.BIG_ENDIAN);
 *    FloatBuffer floatRecordBuffer = record.asFloatBuffer();
 *    IntBuffer intRecordBuffer = record.asIntBuffer();
 *
 *  @author Derek Trumbo
 */

public class BitUtil {


    //////////////////
    // DISTRIBUTION //
    //////////////////

    public static Map<Integer, Integer> countByteValues(byte[] bytes) {
        Map<Integer, Integer> dist = new TreeMap<>();
        for(byte b : bytes) {
            int i = b & 0xFF;
            Integer count = dist.get(i);
            if(count == null) {
                count = 0;
            }
            dist.put(i, count + 1);
        }
        return dist;
    }


    //////////////////
    // REVERSE BITS //
    //////////////////

    // No equivalent in Byte class.
    public static byte reverseBits(byte val) {
        byte newVal = 0;
        int numBits = Byte.SIZE;
        for(int x = 0; x < numBits; x++) {
            if((val & 1) != 0) {
                newVal |= 1;
            }
            if(x != numBits - 1) {
                newVal <<= 1;
                val >>>= 1;
            }
        }
        return newVal;
    }
    // No equivalent in Short class.
    public static short reverseBits(short val) {
        short newVal = 0;
        int numBits = Short.SIZE;
        for(int x = 0; x < numBits; x++) {
            if((val & 1) != 0) {
                newVal |= 1;
            }
            if(x != numBits - 1) {
                newVal <<= 1;
                val >>>= 1;
            }
        }
        return newVal;
    }
    // Same as Integer.reverse.
    public static int reverseBits(int val) {
        int newVal = 0;
        int numBits = Integer.SIZE;
        for(int x = 0; x < numBits; x++) {
            if((val & 1) != 0) {
                newVal |= 1;
            }
            if(x != numBits - 1) {
                newVal <<= 1;
                val >>>= 1;
            }
        }
        return newVal;
    }
    // Same as Long.reverse.
    public static long reverseBits(long val) {
        long newVal = 0L;
        int numBits = Long.SIZE;
        for(int x = 0; x < numBits; x++) {
            if((val & 1L) != 0L) {
                newVal |= 1L;
            }
            if(x != numBits - 1) {
                newVal <<= 1L;
                val >>>= 1;
            }
        }
        return newVal;
    }
    public static float reverseBits(float val) {
        return Float.intBitsToFloat(Integer.reverse(Float.floatToRawIntBits(val)));
    }
    public static double reverseBits(double val) {
        return Double.longBitsToDouble(Long.reverse(Double.doubleToRawLongBits(val)));
    }

    ///////////////////
    // REVERSE BYTES //
    ///////////////////

    // Change between little-endian and big-endian.

    // Same as Short.reverseBytes.
    public static short reverseBytes(short s) {
        int byte0 = (s >> 8) & 0xFF;
        int byte1 = s << 8;
        int byte10 = byte1 | byte0;
        short ret = (short) byte10;
        return ret;
    }
    // Same as Integer.reverseBytes.
    public static int reverseBytes(int i) {
        int byte0 = (i >> 24) & 0xFF;
        int byte1 = (i >> 8) & 0xFF00;
        int byte2 = (i << 8) & 0xFF0000;
        int byte3 = i << 24;
        return byte3 | byte2 | byte1 | byte0;
    }
    // Same as Long.reverseBytes.
    public static long reverseBytes(long l) {
        long byte0 = (l >> 56) & 0xFFL;
        long byte1 = (l >> 40) & 0xFF00L;
        long byte2 = (l >> 24) & 0xFF0000L;
        long byte3 = (l >> 8)  & 0xFF000000L;
        long byte4 = (l << 8)  & 0xFF00000000L;
        long byte5 = (l << 24) & 0xFF0000000000L;
        long byte6 = (l << 40) & 0xFF000000000000L;
        long byte7 = l << 56;
        return byte7 | byte6 | byte5 | byte4 | byte3 | byte2 | byte1 | byte0;
    }
    public static float reverseBytes(float f) {
        return Float.intBitsToFloat(Integer.reverseBytes(Float.floatToRawIntBits(f)));
    }
    public static double reverseBytes(double d) {
        return Double.longBitsToDouble(Long.reverseBytes(Double.doubleToRawLongBits(d)));
    }
    public static byte[] reverseBytes(byte[] bytes) {
        byte[] bytesReversed = new byte[bytes.length];
        for(int i = 0; i < bytes.length; i++) {
            bytesReversed[bytes.length - 1 - i] = bytes[i];
        }
        return bytesReversed;
    }
    // This one is a little faster than reverseBytes(byte[])
    // if you don't need to keep your original array unmodified.
    public static void reverseBytesInPlace(byte[] bytes) {
        int halfLen = bytes.length / 2;
        int lastInd = bytes.length - 1;
        for(int i = 0; i < halfLen; i++) {
            int other = lastInd - i;
            byte temp = bytes[i];
            bytes[i] = bytes[other];
            bytes[other] = temp;
        }
    }

    ///////////////////////////////////
    // COPYING / CONVERT TO UNSIGNED //
    ///////////////////////////////////

    // Copies the bits in the given byte to an
    // integer, but without any sign extension.
    // The statements
    //     int i = b;
    //     int i = 0 | b;
    // both extend the sign.  So if b is -1,
    // i will also be -1 afterwards.  The return
    // value of
    //     copyToInt((byte) -1);
    // is 255.
    public static short copyToShort(byte b) {
        return (short) (b & 0xFF);
    }
    public static int copyToInt(byte b) {
        return b & 0xFF;
    }
    public static int copyToInt(short s) {
        return s & 0xFFFF;
    }
    public static long copyToLong(byte b) {
        return b & 0xFFL;
    }
    public static long copyToLong(short s) {
        return s & 0xFFFFL;
    }
    public static long copyToLong(int i) {
        return i & 0xFFFFFFFFL;
    }

    // Use Float.intBitsToFloat and Double.longBitsToDouble
    // to interpret the bits in an int or a long as a floating
    // point number.

    ////////////////////////////
    // CREATE FROM BYTE ARRAY //
    ////////////////////////////

    // Assuming big-endian.

    public static short toShort(byte[] bytes) {
        int byte0 = bytes[1] & 0xFF;
        int byte1 = bytes[0] << 8;
        int byte10 = byte1 | byte0;
        short ret = (short) byte10;
        return ret;
    }
    public static int toInt(byte[] bytes) {
        return toInt(bytes, 0);
    }
    public static int toInt(byte[] bytes, int position) {
        int byte0 = bytes[position + 3] & 0xFF;
        int byte1 = (bytes[position + 2] << 8) & 0xFF00;
        int byte2 = (bytes[position + 1] << 16) & 0xFF0000;
        int byte3 = bytes[position] << 24;
        return byte3 | byte2 | byte1 | byte0;
    }
    public static long toLong(byte[] bytes) {
        return toLong(bytes, 0);
    }
    public static long toLong(byte[] bytes, int position) {
        long byte0 = bytes[position + 7] & 0xFFL;
        long byte1 = ((long) bytes[position + 6] << 8) & 0xFF00L;
        long byte2 = ((long) bytes[position + 5] << 16) & 0xFF0000L;
        long byte3 = ((long) bytes[position + 4] << 24) & 0xFF000000L;
        long byte4 = ((long) bytes[position + 3] << 32) & 0xFF00000000L;
        long byte5 = ((long) bytes[position + 2] << 40) & 0xFF0000000000L;
        long byte6 = ((long) bytes[position + 1] << 48) & 0xFF000000000000L;
        long byte7 = ((long) bytes[position + 0] << 56);
        return byte7 | byte6 | byte5 | byte4 | byte3 | byte2 | byte1 | byte0;
    }
    public static float toFloat(byte[] bytes) {
        return Float.intBitsToFloat(toInt(bytes));
    }
    public static double toDouble(byte[] bytes) {
        return Double.longBitsToDouble(toLong(bytes));
    }

    ///////////////////////////
    // STRING REPRESENTATION //
    ///////////////////////////

    // Makes the string returned by toBinaryString methods
    // a little easier to read.  Assumes big-endian format.
    public static String markupBinaryString(String s) {
        String ret = "";
        for(int i = s.length() - 1; i >= 0; i--) {
            int which = (s.length() - 1) - i;
            if(which % 8 == 0) {
                ret = " " + ret;
            } else if(which % 4 == 0) {
                ret = ":" + ret;
            }
            ret = s.charAt(i) + ret;
        }
        return ret.trim();
    }

    // Making up for the lack of toBinaryString in the
    // Byte/Short classes.
    public static String toBinaryString(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        for(int b = 0; b < bytes.length; b++) {
            buffer.append(toBinaryString(bytes[b]));
        }
        return buffer.toString();
    }
    public static String toBinaryString(byte value) {
        String ret = "";
        for(int b = 0; b < Byte.SIZE; b++) {
            ret = (value & 1) + ret;
            value >>= 1;
        }
        return ret;
    }
    public static String toBinaryString(short value) {
        String ret = "";
        for(int b = 0; b < Short.SIZE; b++) {
            ret = (value & 1) + ret;
            value >>= 1;
        }
        return ret;
    }

    // Building on Integer.toBinaryString and Long.toBinaryString
    // by padding the result with zeros to fill the full number
    // of bits in the data type.
    public static String toBinaryString(int i) {
        String str = Integer.toBinaryString(i);
        return StringUtil.padLeft(str, '0', Integer.SIZE);
    }
    public static String toBinaryString(long l) {
        String str = Long.toBinaryString(l);
        return StringUtil.padLeft(str, '0', Long.SIZE);
    }
    public static String toBinaryString(float f) {
        return toBinaryString(Float.floatToRawIntBits(f));
    }
    public static String toBinaryString(double d) {
        return toBinaryString(Double.doubleToRawLongBits(d));
    }

    // Show a byte's binary value, its value in hex, the
    // value if it were unsigned (if different), and its
    // character on the ASCII table (if valid).
    public static String byteInfo(byte b) {

        // Hex value.
        String hex1 = Integer.toHexString(copyToInt(b));
        String hex2 = "0x" + StringUtil.padLeft(hex1.toUpperCase(), '0', 2);

        // Unsigned value.
        String unsignedVal = "";
        if(b < 0) {
            unsignedVal = "+" + copyToInt(b);
        }

        // Character value.
        String ch = "?";
        if(b >= 32 && b <= 126) {
            ch = "" + ((char) b);
        } else if(b == 10) {
            ch = "\\n";
        } else if(b == 13) {
            ch = "\\r";
        }

        return hex2 + "/" + b + unsignedVal + "/" + ch;
    }

    public static String toPrettyByte(byte b) {
        return markupBinaryString(toBinaryString(b)) + "[" + byteInfo(b) + "]";
    }

    ///////////////////////////////
    // DIAGNOSTIC RECORD CLASSES //
    ///////////////////////////////

    // Useful for investigating the contents of a binary file.
    // These classes can be used to more easily represent
    // the contents of a binary file:
    //   RandomAccessFile rf = new RandomAccessFile(file, "r");
    //   StringRecord rec = new StringRecord(rf);    // Some string data
    //   IntegerRecord rec2 = new IntegerRecord();   // Followed by an integer
    //   System.out.println(rec);
    //   System.out.println(rec2);
    // Each record knows the exact bytes that were read out and
    // the file pointer before and after it was read.  However,
    // after you know the layout of the file, for performance's
    // sake you probably wouldn't need to use these.  It would be
    // better to just use the RandomAccessFile's read* methods.

    public static abstract class Record {
        protected byte[] bytes;
        public long startPointer = -1;    // File byte pointer before reading record.
        public long endPointer = -1;      // File byte pointer after reading record.
        public Record(RandomAccessFile rf, Object ...args) throws IOException {
            startPointer = rf.getFilePointer();
            read(rf, args);
            endPointer = rf.getFilePointer();
        }
        protected abstract void read(RandomAccessFile rf, Object ... args) throws IOException;
        protected String getByteRange() {
            return "[" + startPointer + "-" + (endPointer - 1) + "]";
        }
        protected long getLength() {
            return endPointer - startPointer;
        }
    }

    public static class StringRecord extends Record {
        public String val;
        public StringRecord(RandomAccessFile rf) throws IOException {
            super(rf);
        }
        public StringRecord(RandomAccessFile rf, int len) throws IOException {
            super(rf, new Object[] {len});
        }
        @Override
        protected void read(RandomAccessFile rf, Object ... args) throws IOException {
            int len;
            if(args == null || args.length == 0) {
                IntegerRecord irec = new IntegerRecord(rf);
                len = irec.valLE;
            } else {
                len = (Integer) args[0];
            }
            buildVal(rf, len);
        }
        protected void buildVal(RandomAccessFile rf, int len) throws IOException {
            bytes = new byte[len];
            rf.read(bytes);
            StringBuilder builder = new StringBuilder(len);
            for(int b = 0; b < len; b++) {
                builder.append((char) bytes[b]);
            }
            val = builder.toString();
        }
        @Override
        public String toString() {
            return "[" + val.length() + "] " + val;
        }
    }
    public static abstract class NumberRecord extends Record {
        protected abstract String getBE();  // Get big-endian representation.
        protected abstract String getLE();  // Get little-endian representation.
        public NumberRecord(RandomAccessFile rf, Object ...args) throws IOException {
            super(rf, args);
        }
        @Override
        public String toString() {
            String ret = getByteRange() + " {BE=" + getBE() + ",LE=" + getLE() + "} ";
            for(int b = 0; b < bytes.length; b++) {
                ret += BitUtil.toPrettyByte(bytes[b]) + " ";
            }
            return ret;
        }
    }
    public static class ByteRecord extends NumberRecord {
        public ByteRecord(RandomAccessFile rf) throws IOException {
            super(rf);
        }
        @Override
        protected void read(RandomAccessFile rf, Object ... args) throws IOException {
            bytes = new byte[1];
            rf.read(bytes);
        }
        @Override
        public String toString() {
            return "[" + startPointer + "] " + BitUtil.toPrettyByte(bytes[0]);
        }
        @Override
        protected String getBE() {
            return "" + bytes[0];
        }
        @Override
        protected String getLE() {
            return "" + bytes[0];
        }
    }
    public static class IntegerRecord extends NumberRecord {
        public int valBE;  // Big-endian
        public int valLE;  // Little-endian
        public IntegerRecord(RandomAccessFile rf) throws IOException {
            super(rf);
        }
        @Override
        protected void read(RandomAccessFile rf, Object ... args) throws IOException {
            bytes = new byte[4];
            rf.read(bytes);
            valBE = BitUtil.toInt(bytes);
            byte[] bytesReversed = BitUtil.reverseBytes(bytes);
            valLE = BitUtil.toInt(bytesReversed);
        }
        @Override
        protected String getBE() {
            return "" + valBE;
        }
        @Override
        protected String getLE() {
            return "" + valLE;
        }
    }
    public static class LongRecord extends NumberRecord {
        public long valBE;  // Big-endian
        public long valLE;  // Little-endian
        public LongRecord(RandomAccessFile rf) throws IOException {
            super(rf);
        }
        @Override
        protected void read(RandomAccessFile rf, Object ... args) throws IOException {
            bytes = new byte[8];
            rf.read(bytes);
            valBE = BitUtil.toLong(bytes);
            byte[] bytesReversed = BitUtil.reverseBytes(bytes);
            valLE = BitUtil.toLong(bytesReversed);
        }
        @Override
        protected String getBE() {
            return "" + valBE;
        }
        @Override
        protected String getLE() {
            return "" + valLE;
        }
    }
    public static class FloatRecord extends NumberRecord {
        public float valBE;  // Big-endian
        public float valLE;  // Little-endian
        public FloatRecord(RandomAccessFile rf) throws IOException {
            super(rf);
        }
        @Override
        protected void read(RandomAccessFile rf, Object ... args) throws IOException {
            bytes = new byte[4];
            rf.read(bytes);
            valBE = BitUtil.toFloat(bytes);
            byte[] bytesReversed = BitUtil.reverseBytes(bytes);
            valLE = BitUtil.toFloat(bytesReversed);
        }
        @Override
        protected String getBE() {
            return "" + valBE;
        }
        @Override
        protected String getLE() {
            return "" + valLE;
        }
    }
    public static class DoubleRecord extends NumberRecord {
        public double valBE;  // Big-endian
        public double valLE;  // Little-endian
        public DoubleRecord(RandomAccessFile rf) throws IOException {
            super(rf);
        }
        @Override
        protected void read(RandomAccessFile rf, Object ... args) throws IOException {
            bytes = new byte[8];
            rf.read(bytes);
            valBE = BitUtil.toDouble(bytes);
            byte[] bytesReversed = BitUtil.reverseBytes(bytes);
            valLE = BitUtil.toDouble(bytesReversed);
        }
        @Override
        protected String getBE() {
            return "" + valBE;
        }
        @Override
        protected String getLE() {
            return "" + valLE;
        }
    }
}
