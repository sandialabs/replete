package replete.io.fast;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import replete.extensions.io.OOSHandleTableOriginal1_7_0_51;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

// Assumption: Field names' lengths are <= 255
// Assumption: Class names' lengths are <= 255
// Assumption: Classes have <= 255 fields
// Assumption: The number of cached class descriptors <= 255

// NOTE: Does not require serializable
// NOTE: Can also add magic starting flag / ending flag and a hash code for QA.
// NOTE: Do we need reset methods
// NOTE: need to write magic & header?

public class FastObjectOutputStream implements Closeable, Flushable, FastObjectStreamConstants {


    ////////////
    // FIELDS //
    ////////////

    private BufferedWrapperOutputStream bout;
    private PassThruObjectOutputStream pout;
    private Map<String, OutputClassDescriptor> classDescriptorCache =
        new LinkedHashMap<String, OutputClassDescriptor>();
    public OOSHandleTableOriginal1_7_0_51 handles;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Create the underlying wrapper stream
    public FastObjectOutputStream(OutputStream out) {
        bout = new BufferedWrapperOutputStream(out);
        try {
            pout = new PassThruObjectOutputStream();
        } catch(SecurityException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        handles = new OOSHandleTableOriginal1_7_0_51(10, 3F);
//        writeStreamHeader();
    }
//    protected void writeStreamHeader() throws IOException {
//        bout.writeShort(STREAM_MAGIC);
//        bout.writeShort(STREAM_VERSION);
//    }

    // Write out any object regardless of its type to the
    // output stream.  The object's type will be inspected
    // and appropriate format will be written.
    public void writeObject(Object value) throws IOException {

        // Write out the null type code for null.
        if(value == null) {
            bout.writeByte(TYPE_NULL);

        // Write out one of the two boolean type codes
        // for a boolean.
        } else if(value instanceof Boolean) {
            bout.writeByte((Boolean) value ? TYPE_BOOL_TRUE : TYPE_BOOL_FALSE);

        // Write out a byte type code and the value.
        } else if(value instanceof Byte) {
            bout.writeByte(TYPE_BYTE);
            bout.writeByte((Byte) value);

        // Write out a short type code and the value.
        } else if(value instanceof Short) {
            bout.writeByte(TYPE_SHORT);
            bout.writeShort((Short) value);

        // Write out an integer type code and the value.
        } else if(value instanceof Integer) {
            bout.writeByte(TYPE_INTEGER);
            bout.writeInteger((Integer) value);

        // Write out a long type code and the value.
        } else if(value instanceof Long) {
            bout.writeByte(TYPE_LONG);
            bout.writeLong((Long) value);

        // Write out a float type code and the value.
        } else if(value instanceof Float) {
            bout.writeByte(TYPE_FLOAT);
            bout.writeFloat((Float) value);

        // Write out a double type code and the value.
        } else if(value instanceof Double) {
            bout.writeByte(TYPE_DOUBLE);
            bout.writeDouble((Double) value);

        // Write out a character type code and the value.
        } else if(value instanceof Character) {
            bout.writeByte(TYPE_CHARACTER);
            bout.writeCharacter((Character) value);

        // Write out a string by writing the correct type code
        // given the length of the string and then the string's bytes.
        } else if(value instanceof String) {
            writeStringVariable((String) value);

        // Write out an array (either a primitive or object array).
        } else if(value.getClass().isArray()) {
            writeArray(value);

        // ??
//        } else if(value instanceof Map) {
//            writeMap((Map) value);

        // lists?

        // Write out any other arbitrarily-typed objects.
        } else {
            writeObject0(value);
        }
    }

    // Write out an array (either a primitive or object array).
    private void writeArray(Object array) throws IOException {

        // Find out if this object has already been written to
        // the string and if so, write out just the necessary
        // handle, otherwise record that this object is being
        // written to the stream for future handle lookups.
        int h = handles.lookup(array);
        if(h != -1) {
            bout.writeByte(TYPE_OBJECT_REF);
            bout.writeInteger(h);
            return;
        }
        handles.assign(array);

        // Get the array's component type and length.
        Class clazz = array.getClass();
        Class eClazz = clazz.getComponentType();
        int len = Array.getLength(array);

        // Write out primitive array.
        if(eClazz.isPrimitive()) {
            writeClassDescriptor(eClazz, TYPE_ARRAY_PRIM, TYPE_ARRAY_PRIM_CX, false);
            bout.writeInteger(len);
            if(eClazz == boolean.class) {
                for(int e = 0; e < len; e++) {
                    bout.writeBoolean(Array.getBoolean(array, e));
                }
            } else if(eClazz == byte.class) {
                for(int e = 0; e < len; e++) {
                    bout.writeByte(Array.getByte(array, e));
                }
            } else if(eClazz == short.class) {
                for(int e = 0; e < len; e++) {
                    bout.writeShort(Array.getShort(array, e));
                }
            } else if(eClazz == int.class) {
                for(int e = 0; e < len; e++) {
                    bout.writeInteger(Array.getInt(array, e));
                }
            } else if(eClazz == long.class) {
                for(int e = 0; e < len; e++) {
                    bout.writeLong(Array.getLong(array, e));
                }
            } else if(eClazz == float.class) {
                for(int e = 0; e < len; e++) {
                    bout.writeFloat(Array.getFloat(array, e));
                }
            } else if(eClazz == double.class) {
                for(int e = 0; e < len; e++) {
                    bout.writeDouble(Array.getDouble(array, e));
                }
            } else if(eClazz == char.class) {
                for(int e = 0; e < len; e++) {
                    bout.writeCharacter(Array.getChar(array, e));
                }
            }

        // Write out an object array.
        } else {
            writeClassDescriptor(eClazz, TYPE_ARRAY, TYPE_ARRAY_CX, true);
            bout.writeInteger(len);
            for(int e = 0; e < len; e++) {
                writeObject(Array.get(array, e));
            }
        }
    }
//    private void writeMap(Map map) throws IOException {
//        Class clazz = map.getClass();
//        writeClassDescriptor(clazz, TYPE_MAP, TYPE_MAP_CX, false);
//        bout.writeInteger(map.size());
//        for(Object key : map.keySet()) {
//            Object value = map.get(key);
//            writeObject(key);
//            writeObject(value);
//        }
//    }

    // Write out an arbitrary object by writing out its
    // field's values in the order that the field names
    // are listed in the class descriptor.
    protected void writeObject0(Object object) throws IOException {

        // Write the class descriptor.
        Class clazz = object.getClass();

        // Check if this object has a writeReplace method
        // and call if so.
        Object origObject = object;
        for(;;) {
            Method writeReplace = FastObjectStreamUtil.getWriteReplaceMethod(clazz);
            if(writeReplace == null) {
                break;
            }
            try {
                object = writeReplace.invoke(object, null);
            } catch(Exception e) {
                throw new FastObjectStreamException("Error invoking writeReplace method on class '" + clazz.getName() + "'.", e);
            }
            if(object == null) {
                break;
            }
            Class repCl = object.getClass();
            if(repCl == clazz) {
                break;
            }
            clazz = repCl;
        }
        if(object != origObject) {
            writeObject(object);
            return;
        }

        // Find out if this object has already been written to
        // the string and if so, write out just the necessary
        // handle, otherwise record that this object is being
        // written to the stream for future handle lookups.
        int h = handles.lookup(object);
        if(h != -1) {
            bout.writeByte(TYPE_OBJECT_REF);
            bout.writeInteger(h);
            return;
        }
        handles.assign(object);

        // Write the class information for this object.
        Map<String, Field> fields = writeClassDescriptor(clazz, TYPE_OBJECT, TYPE_OBJECT_CX, true);

        // This code is necessary to serialize those objects which override
        // writeObject/readObject.  These methods take an ObjectOutputStream
        // which provides certain methods for writing data of any type
        // to the underlying stream.  We don't need this code to be active
        // if we happen to know none of the objects in our object graph
        // have these methods.
        try {
            Method writeObject = FastObjectStreamUtil.getWriteObjectMethod(clazz);
            if(writeObject != null) {
                pout.setContext(object, fields);
                writeObject.invoke(object, new Object[]{ pout });
                return;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        // If there is no writeObject, then write out the object's field values.
        // This is the "default write object" bevhavior.
        writeFieldValues(object, fields);
    }

    private void writeFieldValues(Object object, Map<String, Field> fields) throws IOException {
        // Write the object's field values.
        for(Field field : fields.values()) {
            try {
                writeObject(field.get(object));
            } catch(IllegalAccessException e) {
                throw new FastObjectStreamException("Could not access field '" +
                    field + "' on object of type '" + object.getClass().getName() + "'.",
                    e);
            }
        }
    }

    // Write a string with a length of 255 or less
    // required bytes.
    private void writeStringShort(String value) throws IOException {
        byte[] stringBytes = value.getBytes(STRING_ENCODING);
        if(stringBytes.length > MAX_UNSIGNED_BYTE_VALUE) {
            throw new FastObjectStreamException("String too long");
        }
        bout.writeString(stringBytes, (byte) stringBytes.length);
    }

    // Write a string using the smallest possible data type
    // to represent its length.
    private void writeStringVariable(String value) throws IOException {
        byte[] stringBytes = value.getBytes(STRING_ENCODING);
        int len = stringBytes.length;
        if(len <= MAX_UNSIGNED_BYTE_VALUE) {
            bout.writeByte(TYPE_STRING1);
            bout.writeString(stringBytes, (byte) len);
        } else if(len <= MAX_UNSIGNED_SHORT_VALUE) {
            bout.writeByte(TYPE_STRING2);
            bout.writeString(stringBytes, (short) len);
        } else {
            bout.writeByte(TYPE_STRING4);
            bout.writeString(stringBytes, len);
        }
    }

    // Write a class descriptor to the output stream.
    private Map<String, Field> writeClassDescriptor(Class clazz, byte nonCachedType, byte cachedType, boolean populateFields) throws IOException {

        // Look up the class descriptor.
        String name = clazz.getName();
        OutputClassDescriptor desc = classDescriptorCache.get(name);

        // If the class has been encountered before, write
        // the cached type code and the cache ID.
        if(desc != null) {
            bout.writeByte(cachedType);
            bout.writeByte((byte) desc.cacheId);
            return desc.fields;
        }

        // Make sure we're not attempting to write out the
        // 257th class descriptor.
        if(classDescriptorCache.size() == MAX_UNSIGNED_BYTE_VALUE + 1) {
            throw new FastObjectStreamException("Too many class descriptors");
        }

        // If this class hasn't been encountered before,
        // create and save new descriptor.
        desc = new OutputClassDescriptor();
        desc.cacheId = classDescriptorCache.size();
        classDescriptorCache.put(name, desc);

        // Write type for a non-cached class type and
        // the name of the class to the output stream.
        bout.writeByte(nonCachedType);
        writeStringShort(name);

        // If desired, populate the descriptor with the
        // fields that will be written for each object
        // of this class type written to the stream.
        if(populateFields) {

            // Find all the fields that will be written
            // for each object of this type.
            Map<String, Field> fields = FastObjectStreamUtil.getFieldsNoStaticTransient(clazz);

            // Write out the number of fields (limited to 255) for this class.
            int f = fields.size();
            bout.writeByte((byte) f);

            // Write out the names of the fields
            for(String fieldName : fields.keySet()) {
                writeStringShort(fieldName);
            }

            desc.fields = fields;
            return fields;
        }

        return null;
    }


    //////////////////////
    // LOW-LEVEL WRITES //
    //////////////////////

    // Implementing these allows us to move towards a FastObjectOutputStream
    // being able to stand in for an ObjectOutputStream in more situations.

    /*public void write(byte[] buf) throws IOException {
        bout.writeBytes(buf);
    }
    public void write(byte[] buf, int off, int len) throws IOException {
        bout.writeBytes(buf, off, len);
    }
    public void writeBytes(String str) {
        //?!?!
    }
    public void writeBoolean(boolean val) throws IOException {
        bout.writeBoolean(val);
    }
    public void write(int val) throws IOException {
        bout.writeByte((byte) val);
    }
    public void writeByte(int val) throws IOException {
        bout.writeByte((byte) val);
    }
    public void writeShort(int val) throws IOException {
        bout.writeShort((short) val);
    }
    public void writeInt(int val) throws IOException {
        bout.writeInteger(val);
    }
    public void writeLong(long val) throws IOException {
        bout.writeLong(val);
    }
    public void writeFloat(float val) throws IOException {
        bout.writeFloat(val);
    }
    public void writeDouble(double val) throws IOException {
        bout.writeDouble(val);
    }
    public void writeChar(int val) throws IOException {
        bout.writeCharacter((char) val);
    }
    public void writeChars(String str) {

    }
    public void writeUTF(String str) {

    }*/


    ///////////////////
    // FLUSH & CLOSE //
    ///////////////////

    public void flush() throws IOException {
        bout.flush();
    }
    public void close() throws IOException {
        // could write checksum and totalbytes written here?
        flush();
        bout.close();
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getNumBytesWritten() {
        return bout.getNumBytesWritten();
    }

    public byte[] getDebugBuffer() {
        byte[] ret = new byte[bout.posDebug];
        System.arraycopy(bout.bufDebug, 0, ret, 0, bout.posDebug);
        return ret;
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class BufferedWrapperOutputStream implements Flushable, Closeable {


        ////////////
        // FIELDS //
        ////////////

        private byte[] buf = new byte[MAX_BUFFER_BLOCK_SIZE];
        private OutputStream out;
        private int numBytesWritten;
        private int pos = 0;

        // DEBUG
        private byte[] bufDebug = new byte[MAX_BUFFER_BLOCK_SIZE * 10];
        private int posDebug = 0;


        //////////////////
    // CONSTRUCTORS //
    //////////////////

        public BufferedWrapperOutputStream(OutputStream out) {
            this.out = out;
        }


        ///////////
        // WRITE //
        ///////////

        public void writeBoolean(boolean value) throws IOException {
            if(pos == MAX_BUFFER_BLOCK_SIZE) {    // Save a "+ 1"
                drain();
            }
            buf[pos++] = (byte) (value ? 1 : 0);
        }
        public void writeByte(byte value) throws IOException {
            if(pos == MAX_BUFFER_BLOCK_SIZE) {    // Save a "+ 1"
                drain();
            }
            buf[pos++] = value;
        }
        public void writeShort(short value) throws IOException {
            if(pos + SHORT_CHAR_BYTES > MAX_BUFFER_BLOCK_SIZE) {
                drain();
            }
            buf[pos + 1] = (byte) (value      );
            buf[pos    ] = (byte) (value >>> 8);
            pos += SHORT_CHAR_BYTES;
        }
        public void writeInteger(int value) throws IOException {
            if(pos + INTEGER_FLOAT_BYTES > MAX_BUFFER_BLOCK_SIZE) {
                drain();
            }
            buf[pos + 3] = (byte) (value       );
            buf[pos + 2] = (byte) (value >>>  8);
            buf[pos + 1] = (byte) (value >>> 16);
            buf[pos    ] = (byte) (value >>> 24);
            pos += INTEGER_FLOAT_BYTES;
        }
        public void writeLong(long value) throws IOException {
            if(pos + LONG_DOUBLE_BYTES > MAX_BUFFER_BLOCK_SIZE) {
                drain();
            }
            buf[pos + 7] = (byte) (value       );
            buf[pos + 6] = (byte) (value >>>  8);
            buf[pos + 5] = (byte) (value >>> 16);
            buf[pos + 4] = (byte) (value >>> 24);
            buf[pos + 3] = (byte) (value >>> 32);
            buf[pos + 2] = (byte) (value >>> 40);
            buf[pos + 1] = (byte) (value >>> 48);
            buf[pos    ] = (byte) (value >>> 56);
            pos += LONG_DOUBLE_BYTES;
        }
        public void writeFloat(float value) throws IOException {
            writeInteger(Float.floatToIntBits(value));
        }
        public void writeDouble(double value) throws IOException {
            writeLong(Double.doubleToLongBits(value));
        }
        public void writeCharacter(char value) throws IOException {
            if(pos + SHORT_CHAR_BYTES > MAX_BUFFER_BLOCK_SIZE) {     // Worth just writing 1 char?
                drain();
            }
            buf[pos + 1] = (byte) (value      );
            buf[pos    ] = (byte) (value >>> 8);
            pos += SHORT_CHAR_BYTES;
        }
        public void writeString(byte[] stringBytes, byte length) throws IOException {
            writeByte(length);
            writeBytes(stringBytes);
        }
        public void writeString(byte[] stringBytes, short length) throws IOException {
            writeShort(length);
            writeBytes(stringBytes);
        }
        public void writeString(byte[] stringBytes, int length) throws IOException {
            writeInteger(length);
            writeBytes(stringBytes);
        }
        private void writeBytes(byte[] buf2) throws IOException {
            writeBytes(buf2, 0, buf2.length);
        }
        public void writeBytes(byte[] buf2, int off, int len) throws IOException {
            int written = off;
            len = len + off;
            while(written < len) {
                if(pos == MAX_BUFFER_BLOCK_SIZE) {
                    drain();
                }
                int needWrite = len - written;
                int bufSpace = MAX_BUFFER_BLOCK_SIZE - pos;
                int canWrite = Math.min(needWrite, bufSpace);
                System.arraycopy(buf2, written/* + off*/, buf, pos, canWrite);
                written += canWrite;
                pos += canWrite;
            }
        }


        ///////////
        // DRAIN //
        ///////////

        // Place the bytes in the buffer into the underlying stream
        private void drain() throws IOException {

            // Nothing to drain
            if(pos == 0) {
                return;
            }

            // Write the buffer bytes to the underlying stream
            out.write(buf, 0, pos);
            numBytesWritten += pos;

//            try {
//                System.arraycopy(buf, 0, bufDebug, posDebug, pos);
//                posDebug += pos;
//            } catch(Exception e) {
//                System.err.println("Remember to turn off debug!");
//                e.printStackTrace();
//            }

            // Reset the position of the buffer
            pos = 0;
        }


        ///////////////////
        // FLUSH & CLOSE //
        ///////////////////

        public void flush() throws IOException {
            drain();       // Dump buffer
            out.flush();   // Flush underlying stream
        }
        public void close() throws IOException {
            flush();
            out.close();   // Close underlying stream
        }


        ///////////////
    // ACCESSORS //
    ///////////////

        public int getNumBytesWritten() {
            return numBytesWritten;
        }
    }

    private class OutputClassDescriptor {


        ////////////
        // FIELDS //
        ////////////

        int cacheId;
        Map<String, Field> fields;


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        public String toString() {
            return cacheId + "/" + fields;
        }
    }

    public class PassThruObjectOutputStream extends ObjectOutputStream {
        private Object object;
        private Map<String, Field> fields;
        protected PassThruObjectOutputStream() throws IOException, SecurityException {
            super();
        }
        public void setContext(Object object, Map<String, Field> fields) {
            this.object = object;
            this.fields = fields;
        }
        @Override
        protected void writeObjectOverride(Object obj) throws IOException {
            FastObjectOutputStream.this.writeObject(obj);
        }
        @Override
        public void write(byte[] buf) throws IOException {
            bout.writeBytes(buf);
        }
        @Override
        public void write(byte[] buf, int off, int len) throws IOException {
            bout.writeBytes(buf, off, len);
        }
        @Override
        public void writeBytes(String str) {
            //?!?!
            throw new NotImplementedException();
        }
        @Override
        public void writeBoolean(boolean val) throws IOException {
            bout.writeBoolean(val);
        }
        @Override
        public void write(int val) throws IOException {
            bout.writeByte((byte) val);
        }
        @Override
        public void writeByte(int val) throws IOException {
            bout.writeByte((byte) val);
        }
        @Override
        public void writeShort(int val) throws IOException {
            bout.writeShort((short) val);
        }
        @Override
        public void writeInt(int val) throws IOException {
            bout.writeInteger(val);
        }
        @Override
        public void writeLong(long val) throws IOException {
            bout.writeLong(val);
        }
        @Override
        public void writeFloat(float val) throws IOException {
            bout.writeFloat(val);
        }
        @Override
        public void writeDouble(double val) throws IOException {
            bout.writeDouble(val);
        }
        @Override
        public void writeChar(int val) throws IOException {
            bout.writeCharacter((char) val);
        }
        @Override
        public void writeChars(String str) {
            throw new NotImplementedException();
        }
        @Override
        public void writeUTF(String str) {
            throw new NotImplementedException();
        }
        @Override
        public void writeUnshared(Object obj) throws IOException {
            throw new NotImplementedException();
        }

        @Override
        public void defaultWriteObject() throws IOException {
            writeFieldValues(object, fields);
        }
    }
}