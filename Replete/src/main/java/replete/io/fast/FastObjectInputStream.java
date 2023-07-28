package replete.io.fast;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sun.misc.Unsafe;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class FastObjectInputStream implements Closeable, FastObjectStreamConstants {

//    private static class Dummy {
//        static int st = 12;
//        int x = 5;
//        @Override
//        public String toString() {
//            return "!" + x + "/" + st;
//        }
//        private void writeObject(FastObjectOutputStream out) {
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
////      Object o = theUnsafe.allocateInstance(Dummy.class);
////      System.out.println(o);
//
//        Method writeObjectMethod = getPrivateMethod(Dummy.class, "writeObject",
//            new Class<?>[] { ObjectOutputStream.class },
//            Void.TYPE);
//        System.out.println(writeObjectMethod);
//    }
//
//    private static Method getPrivateMethod(Class<?> cl, String name,
//                                           Class<?>[] argTypes,
//                                           Class<?> returnType)
//    {
//        try {
//            Method meth = cl.getDeclaredMethod(name, argTypes);
//            meth.setAccessible(true);
//            int mods = meth.getModifiers();
//            return ((meth.getReturnType() == returnType) &&
//                    ((mods & Modifier.STATIC) == 0) &&
//                    ((mods & Modifier.PRIVATE) != 0)) ? meth : null;
//        } catch (NoSuchMethodException ex) {
//            return null;
//        }
//    }


    ////////////
    // FIELDS //
    ////////////

    private static Map<String, Class> primitiveClasses = new HashMap<String, Class>();
    private BufferedInputStream bin;
    private PassThruObjectInputStream pin;
    private Map<Integer, InputClassDescriptor> classDescriptorCache =
        new LinkedHashMap<Integer, InputClassDescriptor>();
    public FastObjectInputHandleTable handles;
    public int[] debugCounts = new int[TYPE_ARRAY_PRIM_CX + 1];

    static {
        primitiveClasses.put("boolean", boolean.class);
        primitiveClasses.put("byte", byte.class);
        primitiveClasses.put("short", short.class);
        primitiveClasses.put("int", int.class);
        primitiveClasses.put("long", long.class);
        primitiveClasses.put("float", float.class);
        primitiveClasses.put("double", double.class);
        primitiveClasses.put("char", char.class);
    }

    private static Unsafe theUnsafe = getUnsafe();
    public static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(null);
            return unsafe;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FastObjectInputStream(InputStream in) throws IOException {
        bin = new BufferedInputStream(in);
        pin = new PassThruObjectInputStream();
        handles = new FastObjectInputHandleTable(100);
    }


    //////////
    // READ //
    //////////

    public Object readObject() throws IOException, ClassNotFoundException {
        byte type = bin.readByte();
debugCounts[type]++;

        switch(type) {
            case TYPE_NULL:          return null;
            case TYPE_BOOL_TRUE:     return true;
            case TYPE_BOOL_FALSE:    return false;
            case TYPE_BYTE:          return bin.readByte();
            case TYPE_SHORT:         return bin.readShort();
            case TYPE_INTEGER:       return bin.readInteger();
            case TYPE_LONG:          return bin.readLong();
            case TYPE_FLOAT:         return bin.readFloat();
            case TYPE_DOUBLE:        return bin.readDouble();
            case TYPE_CHARACTER:     return bin.readCharacter();
            case TYPE_STRING1:       return bin.readString(0x000000FF & bin.readByte());
            case TYPE_STRING2:       return bin.readString(0x0000FFFF & bin.readShort());
            case TYPE_STRING4:       return bin.readString(bin.readInteger());
            case TYPE_OBJECT:        return readObject0(false);
            case TYPE_OBJECT_CX:     return readObject0(true);
            case TYPE_OBJECT_REF:    return objectReference();
            case TYPE_ARRAY:         return readArray(false);
            case TYPE_ARRAY_CX:      return readArray(true);
            case TYPE_ARRAY_PRIM:    return readArrayPrimitive(false);
            case TYPE_ARRAY_PRIM_CX: return readArrayPrimitive(true);
//            case TYPE_LIST:          throw new IOException("type not supported");
//            case TYPE_MAP:           return readMap(false);
//            case TYPE_MAP_CX:        return readMap(true);
        }
        throw new IOException("Invalid type: " + type);
    }

    private Object objectReference() throws IOException {
        int h = bin.readInteger();
        return handles.lookupObject(h);
    }

    private Object readArray(boolean useCache) throws IOException,
            ArrayIndexOutOfBoundsException, IllegalArgumentException, ClassNotFoundException {
        InputClassDescriptor desc = readClassDescriptor(useCache, true);
        int len = bin.readInteger();
        Object array;
        try {
            array = Array.newInstance(desc.clazz, len);
        } catch(Exception e) {
            throw new RuntimeException("Could not instaniate array.");
        }
        handles.assign(array);
        for(int e = 0; e < len; e++) {
            Array.set(array, e, readObject());
        }
        return array;
    }

    private Object readArrayPrimitive(boolean useCache) throws IOException,
            ArrayIndexOutOfBoundsException, IllegalArgumentException, ClassNotFoundException {
        InputClassDescriptor desc = readClassDescriptorPrimitive(useCache);
        int len = bin.readInteger();
        Object array;
        try {
            array = Array.newInstance(desc.clazz, len);
        } catch(NegativeArraySizeException e) {
            throw new RuntimeException("Adfdsd", e);
        }
        handles.assign(array);
        // HOPEFULLY THIS CAN BE DONE FASTER!  With a low-level byte copy maybe
        // Maybe use Unsafe?  For 12 iterations, 603 of 1315 ms was SELF TIME!
        // Clearly in the loop < and ++.
        if(desc.clazz == boolean.class) {
            for(int e = 0; e < len; e++) {
                Array.set(array, e, bin.readBoolean());
            }
        } else if(desc.clazz == byte.class) {
            for(int e = 0; e < len; e++) {
                Array.set(array, e, bin.readByte());
            }
        } else if(desc.clazz == short.class) {
            for(int e = 0; e < len; e++) {
                Array.set(array, e, bin.readShort());
            }
        } else if(desc.clazz == int.class) {
            for(int e = 0; e < len; e++) {
                Array.set(array, e, bin.readInteger());
            }
        } else if(desc.clazz == long.class) {
            for(int e = 0; e < len; e++) {
                Array.set(array, e, bin.readLong());
            }
        } else if(desc.clazz == float.class) {
            for(int e = 0; e < len; e++) {
                Array.set(array, e, bin.readFloat());
            }
        } else if(desc.clazz == double.class) {
            for(int e = 0; e < len; e++) {
                Array.set(array, e, bin.readDouble());
            }
        } else if(desc.clazz == char.class) {
            for(int e = 0; e < len; e++) {
                Array.set(array, e, bin.readCharacter());
            }
        }
        return array;
    }

    private InputClassDescriptor readClassDescriptor(boolean useCache, boolean fields) throws IOException,
                                                      FastObjectStreamException {
        if(useCache) {
            int cacheId = 0x000000FF & bin.readByte();
            return classDescriptorCache.get(cacheId);
        }

        InputClassDescriptor desc = new InputClassDescriptor();
        desc.cacheId = classDescriptorCache.size();
        String className = readStringShort();
        try {
            desc.clazz = Class.forName(className);
        } catch(Exception e) {
            throw new FastObjectStreamException("could not instantiate object for class " + className);
        }
        classDescriptorCache.put(desc.cacheId, desc);

        if(fields) {
            desc.fieldNames = new ArrayList<String>();
            int fieldCount = 0x000000FF & bin.readByte();   // [OPTIONAL] save & op if assume 127 or fewer fields
            for(int f = 0; f < fieldCount; f++) {
                desc.fieldNames.add(readStringShort());
            }
        }
        desc.fieldMap = FastObjectStreamUtil.getFields(desc.clazz);
        return desc;
    }

    private InputClassDescriptor readClassDescriptorPrimitive(boolean useCache) throws IOException {
        if(useCache) {
            int cacheId = 0x000000FF & bin.readByte();
            return classDescriptorCache.get(cacheId);
        }
        InputClassDescriptor desc = new InputClassDescriptor();
        desc.cacheId = classDescriptorCache.size();
        String className = readStringShort();
        desc.clazz = primitiveClasses.get(className);
        classDescriptorCache.put(desc.cacheId, desc);
        return desc;
    }

    private Map readMap(boolean useCache) throws IOException, ClassNotFoundException {
        InputClassDescriptor desc = readClassDescriptor(useCache, false);
        Object object = createObject(desc);
        Map map = (Map) object;
        int fieldCount = bin.readInteger();
        for(int f = 0; f < fieldCount; f++) {
            Object key = readObject();
            Object value = readObject();
            map.put(key, value);
        }
        return map;
    }

    private Object createObject(InputClassDescriptor desc) throws FastObjectStreamException {
        try {
            return theUnsafe.allocateInstance(desc.clazz);
        } catch(InstantiationException e) {
            throw new FastObjectStreamException("Could not instantiate object for class '" + desc.clazz.getName() + "'", e);
        }
//        try {
//            Constructor ctor = desc.clazz.getConstructor(new Class[0]);
//            return ctor.newInstance(new Object[0]);
////          ReflectionFactory rf = ReflectionFactory.getReflectionFactory();
////          Constructor ctor = Object.class.getDeclaredConstructor();
////          Constructor intConstr = rf.newConstructorForSerialization(clazz, ctor);
////          object = clazz.cast(intConstr.newInstance());
////www.javaspecialists.eu/archive/Issue175.html
//        } catch(Exception e) {
//            throw new FastObjectStreamException("could not instantiate object for class " + desc.clazz.getName());
//        }
    }

    private Object readObject0(boolean useCache) throws IOException, ClassNotFoundException {
        InputClassDescriptor desc = readClassDescriptor(useCache, true);
        Object object = createObject(desc);

        handles.assign(object);

        Class clazz = desc.clazz;

        try {
            Method readObject = FastObjectStreamUtil.getReadObjectMethod(clazz);
            if(readObject != null) {
                pin.setContext(object, desc);
                readObject.invoke(object, new Object[]{ pin });
                return object;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        readFieldValues(desc, object);

        Method readResolve = FastObjectStreamUtil.getReadResolveMethod(clazz);
        if(readResolve != null) {
            try {
                Object newObject = readResolve.invoke(object, null);
                object = newObject;
            } catch(Exception e) {
                throw new FastObjectStreamException("Error invoking readResolve method on class '" + clazz.getName() + "'.", e);
            }
        }
        return object;
    }

    private Map<String, Object> readFieldValues(InputClassDescriptor desc, Object object) throws IOException,
                                                                          ClassNotFoundException {
        Map<String, Object> vals = new HashMap<String, Object>();
        for(int f = 0; f < desc.fieldNames.size(); f++) {
            String fieldName = desc.fieldNames.get(f);
            Object value = readObject();
            Field field = desc.fieldMap.get(fieldName);
            if(field != null) {
                try {
                    field.set(object, value);
                    vals.put(fieldName, value);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return vals;
    }

    private String readStringShort() throws IOException {
        return bin.readString(0x000000FF & bin.readByte());
    }


    ///////////
    // CLOSE //
    ///////////

    @Override
    public void close() throws IOException {
        bin.close();
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getNumBytesRead() {
        return bin.getNumBytesRead();
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class BufferedInputStream implements Closeable {


        ////////////
        // FIELDS //
        ////////////

        private byte[] buf = new byte[MAX_BUFFER_BLOCK_SIZE];
        private InputStream in;
        private int numBytesRead;
        private int pos;
        private int size;


        //////////////////
    // CONSTRUCTORS //
    //////////////////

        public BufferedInputStream(InputStream in) {
            this.in = in;
        }


        //////////
        // READ //
        //////////

        public boolean readBoolean() throws IOException {
            return readByte() != 0;
        }
        public byte readByte() throws IOException {
            if(pos == size) {
                fill();
            }
            return buf[pos++];
        }
        public short readShort() throws IOException {
            if(pos + SHORT_CHAR_BYTES > size) {
                fill();
            }
            short value =
                (short)
                    ((buf[pos + 1] & 0xFF) +
                    (buf[pos] << 8));
            pos += SHORT_CHAR_BYTES;
            return value;
        }
        public int readInteger() throws IOException {
            if(pos + INTEGER_FLOAT_BYTES > size) {
                fill();
            }
            int value =
                ((buf[pos + 3] & 0xFF)      ) +
                ((buf[pos + 2] & 0xFF) <<  8) +
                ((buf[pos + 1] & 0xFF) << 16) +
                ((buf[pos    ]       ) << 24);
            pos += INTEGER_FLOAT_BYTES;
            return value;
        }
        public long readLong() throws IOException {
            if(pos + LONG_DOUBLE_BYTES > size) {
                fill();
            }
            long value =
                ((buf[pos + 7] & 0xFFL)      ) +
                ((buf[pos + 6] & 0xFFL) <<  8) +
                ((buf[pos + 5] & 0xFFL) << 16) +
                ((buf[pos + 4] & 0xFFL) << 24) +
                ((buf[pos + 3] & 0xFFL) << 32) +
                ((buf[pos + 2] & 0xFFL) << 40) +
                ((buf[pos + 1] & 0xFFL) << 48) +
                (((long) buf[pos])      << 56);
            pos += LONG_DOUBLE_BYTES;
            return value;
        }
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(readInteger());
        }
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(readLong());
        }
        public char readCharacter() throws IOException {
            if(pos + SHORT_CHAR_BYTES > size) {
                fill();
            }
            char value =
                (char)
                    ((buf[pos + 1] & 0xFF) +
                    (buf[pos] << 8));
            pos += SHORT_CHAR_BYTES;
            return value;
        }
        public String readString(int len) throws IOException {
            byte[] buf2 = new byte[len];
            readFully(buf2, 0, len);
            return new String(buf2, STRING_ENCODING);
        }

        public void readFully(byte[] buf2, int off, int len) throws IOException {
            int read = off;
            len = len + off;
            while(read < len) {
                if(pos == size) {
                    fill();
                }
                int needRead = Math.min(len - read, size - pos);
                System.arraycopy(buf, pos, buf2, read /*+ off*/, needRead);
                read += needRead;
                pos += needRead;
            }
        }


        //////////
        // FILL //
        //////////

        private void fill() throws IOException {

            // There are bytes in the buffer but no attempt to read
            // any has happened, so don't do anything.
            if(pos == 0 && size != 0) {
                return;
            }

            int read;
            if(size == 0) {
                read = in.read(buf, 0, MAX_BUFFER_BLOCK_SIZE);
                size = read;
            } else if(size == buf.length) {
                int save = size - pos;
                System.arraycopy(buf, pos, buf, 0, save);
                read = in.read(buf, save, MAX_BUFFER_BLOCK_SIZE - save);
                size = read + save;
            } else {
                throw new IOException("No more bytes");
            }
            numBytesRead += read;

            // Reset the position of the buffer
            pos = 0;
        }


        ///////////
        // CLOSE //
        ///////////

        public void close() throws IOException {
            in.close();   // Close underlying stream
        }


        ///////////////
    // ACCESSORS //
    ///////////////

        public int getNumBytesRead() {
            return numBytesRead;
        }
    }

    public class InputClassDescriptor {


        ////////////
        // FIELDS //
        ////////////

        int cacheId;
        Class clazz;
        List<String> fieldNames;
        Map<String, Field> fieldMap;
    }

    private class FastObjectInputHandleTable {
        Object[] entries;
        int size = 0;
        FastObjectInputHandleTable(int initialCapacity) {
            entries = new Object[initialCapacity];
        }
        int assign(Object obj) {
            if (size >= entries.length) {
                grow();
            }
            entries[size] = obj;
            return size++;
        }
        Object lookupObject(int handle) {
            return entries[handle];
        }
        private void grow() {
            int newCapacity = (entries.length << 1) + 1;
            Object[] newEntries = new Object[newCapacity];
            System.arraycopy(entries, 0, newEntries, 0, size);
            entries = newEntries;
        }
    }
    public class PassThruObjectInputStream extends ObjectInputStream {
        private Object object;
        private InputClassDescriptor desc;
        private Map<String, Object> lastFields;
        private GetField lastGetField = new PTOISGetField();
        protected PassThruObjectInputStream() throws IOException, SecurityException {
            super();
        }
        public void setContext(Object object, InputClassDescriptor desc) {
            this.object = object;
            this.desc = desc;
        }
        @Override
        protected Object readObjectOverride() throws IOException, ClassNotFoundException {
            return FastObjectInputStream.this.readObject();
        }

        @Override
        public int read(byte[] buf, int off, int len) throws IOException {
            readFully(buf, off, len);  //?!?!?
            return len;
        }
        @Override
        public GetField readFields() throws IOException, ClassNotFoundException {
            defaultReadObject();
            return lastGetField;
        }
        @Override
        public void readFully(byte[] buf) throws IOException {
            readFully(buf, 0, buf.length);
        }
        @Override
        public void readFully(byte[] buf, int off, int len) throws IOException {
            bin.readFully(buf, off, len);
        }

        @Override
        public boolean readBoolean() throws IOException {
            return bin.readBoolean();
        }
        @Override
        public int read() throws IOException {
            return bin.readByte() & 0x000000FF;
        }
        @Override
        public byte readByte() throws IOException {
            return bin.readByte();
        }
        @Override
        public short readShort() throws IOException {
            return bin.readShort();
        }
        @Override
        public int readInt() throws IOException {
            return bin.readInteger();
        }
        @Override
        public long readLong() throws IOException {
            return bin.readLong();
        }
        @Override
        public float readFloat() throws IOException {
            return bin.readFloat();
        }
        @Override
        public double readDouble() throws IOException {
            return bin.readDouble();
        }
        @Override
        public char readChar() throws IOException {
            return bin.readCharacter();
        }
        @Override
        public String readUTF() throws IOException {
            throw new NotImplementedException();
        }
        @Override
        public Object readUnshared() throws IOException, ClassNotFoundException {
            throw new NotImplementedException();
        }
        @Override
        public int readUnsignedByte() throws IOException {
            throw new NotImplementedException();
        }
        @Override
        public int readUnsignedShort() throws IOException {
            throw new NotImplementedException();
        }

        @Override
        public void defaultReadObject() throws IOException, ClassNotFoundException {
            lastFields = readFieldValues(desc, object);
        }

        private class PTOISGetField extends GetField {
            @Override
            public ObjectStreamClass getObjectStreamClass() {
                throw new NotImplementedException();
            }
            @Override
            public boolean defaulted(String name) throws IOException {
                return !lastFields.containsKey(name);
            }
            @Override
            public boolean get(String name, boolean val) throws IOException {
                try {
                    return (Boolean) lastFields.get(name);
                } catch(Exception e) {
                    throw new IOException();
                }
            }
            @Override
            public byte get(String name, byte val) throws IOException {
                try {
                    return (Byte) lastFields.get(name);
                } catch(Exception e) {
                    throw new IOException();
                }
            }
            @Override
            public char get(String name, char val) throws IOException {
                try {
                    return (Character) lastFields.get(name);
                } catch(Exception e) {
                    throw new IOException();
                }
            }
            @Override
            public short get(String name, short val) throws IOException {
                try {
                    return (Short) lastFields.get(name);
                } catch(Exception e) {
                    throw new IOException();
                }
            }
            @Override
            public int get(String name, int val) throws IOException {
                try {
                    return (Integer) lastFields.get(name);
                } catch(Exception e) {
                    throw new IOException();
                }
            }
            @Override
            public long get(String name, long val) throws IOException {
                try {
                    return (Long) lastFields.get(name);
                } catch(Exception e) {
                    throw new IOException();
                }
            }
            @Override
            public float get(String name, float val) throws IOException {
                try {
                    return (Float) lastFields.get(name);
                } catch(Exception e) {
                    throw new IOException();
                }
            }
            @Override
            public double get(String name, double val) throws IOException {
                try {
                    return (Double) lastFields.get(name);
                } catch(Exception e) {
                    throw new IOException();
                }
            }
            @Override
            public Object get(String name, Object val) throws IOException {
                try {
                    return lastFields.get(name);
                } catch(Exception e) {
                    throw new IOException();
                }
            }
        }
    }
}
