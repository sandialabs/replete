package replete.io.flexible;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;

import replete.util.ReflectionUtil;

// Default behavior in Java for when a class loaded in the
// JVM has more fields than a serialized object.  If the
// serialVersionUID's do not match, the serialization fails.
// If you set a specific UID for both the written object
// and the class in memory, then Java will just leave the
// new fields uninitialized without error.  Additionally,
// fields that no longer exist are ignored in the
// serialized object.  Java actually has 90% of the serialization
// behavior that we want when you ignore the serialVersionUID
// field! (i.e. keep it set to the same value always or
// use a custom object input stream that ignores them auto-
// matically)  The last remaining issues are:
//   1) what happens when you rename a field, and
//   2) changing the type of a field but keeping name the same

// XStream is a very feature-rich library.  However it still
// has some drawbacks.  Although it will happily not initialize
// fields in the class in memory that do not exist in the
// serialized XML, it very much complains about fields that
// exist in the XML that no longer exist in the loaded class.
// There are two solutions for this:
//   1) using
//          XStream x = new XStream();
//          x.omitField(A.class, "shouldCopyWithProject");
//      but then you have to MANUALLY specify which ones
//      must be ignored
//   2) using XStream x = new XStream() {
//          ...implement custom mapper...
//          if (definedIn == Object.class) {
//              return false;
//          }
//          ...
//      but then it ignores support for implicit collections.
// basically XStream does not support this well yet.  What's
// nice is that if you are using Externalizable combined with,
// this CustomSerializationUtil, XStream will respect the
// custom serialization, use it, and no longer care about
// extra fields in the serialized object.  XStream
// does have good support for aliases and custom object
// translators, which is nice.

// TODO: Need good design pattern / best practices socialized.

public class FlexibleSerializationUtil {


    ///////////
    // WRITE //
    ///////////

    // What if get throws exception?
    // What about writing the same object out to the file
    // multiple times??  Need to check this on both Java & XML.
    // What about when values' class names change?  Is this
    // even possible to correct?

    public static void write(ObjectOutput out, Object target) {
        write(out, target, null);
    }
    public static void write(ObjectOutput out, Object target,
                             FlexibleSerializationProperties props) {
        try {
            writeInner(out, target, props);
        } catch(Exception e) {
            //??
        }
    }
//    public static ManualTimeProfiler Pg = null;
    public static int X = 0;
    public static int Y = 0;
    private static void writeInner(ObjectOutput out, Object target,
                                  FlexibleSerializationProperties localProps) throws Exception {

//        if(!(target instanceof FlexibleExternalizable)) {
//            throw new IllegalArgumentException("target not FlexibleExternalizable");
//        }

        // Get the properties to be used for the writing.
        // The global properties will never be null, but rather
        // just an empty object if no properties have been
        // defined.  This means there will be a lot of empty
        // properties lying around, one per each class, but
        // this order is better than always having to append
        // the global properties to the blank local properties.
        // (CPU vs. RAM again!)
//        Class clazz = target.getClass();
//        FlexibleSerializationProperties allProps =
//            FlexibleSerializationPropertyManager.getProperties(clazz);
//        if(localProps != null) {             // Usually will be null
//            allProps.append(localProps);
//        }
//X++;
        // Get ALL fields for this object - those defined in
        // its own class but also every field defined in
        // ancestor classes.
//long T = System.currentTimeMillis();
//        Field[] allFields = ReflectionUtil.getFields(target);
//Y += System.currentTimeMillis() - T;
        // Figure out which fields will actually be written.
        // This has to be done ahead of time because we have
        // to write the number of fields to be read first thing!
//        List<Field> writeFields = new ArrayList<Field>();
//        for(Field field : allFields) {
//
//            // Do not write out any static or transient fields.
//            // This mimics Java's & XSStream's default object
//            // serialization.
//            if(Modifier.isStatic(field.getModifiers()) ||
//                            Modifier.isTransient(field.getModifiers())) {
//                continue;
//            }
//
//            String fName = field.getName();
//
//            if(allProps.getSkipClassFields() != null &&
//                            allProps.getSkipClassFields().contains(fName)) {
//                continue;
//            }
//            if(allProps.getAliasesClassToSerialized() != null) {
//                String newFName = allProps.getAliasesClassToSerialized().get(fName);
//                if(newFName != null) {
//                    fName = newFName;
//                }
//            }
//            if(allProps.getSkipSerializedFields() != null &&
//                            allProps.getSkipSerializedFields().contains(fName)) {
//                continue;
//            }
//
//            // It's good!
//            writeFields.add(field);
//        }

        // Write out the total number of fields to be written.
//        out.writeInt(writeFields.size());
        out.writeInt(42);//allFields.length);

//        for(Field field : allFields) {

            // Allow the field to be read even if it has inappropriate
            // access modifiers.
//            field.setAccessible(true);

//            String key = field.getName();

//            Object value = field.get(target);
//            boolean localTranslate = false;
//            if(allProps.getClassValueTranslators() != null) {
//                OneWayValueTranslator T = allProps.getClassValueTranslators().get(key);
//                if(T != null) {
//                    value = T.translate(value);
//                    localTranslate = true;
//                }
//            }
//            if(!localTranslate) {
//                // global value converter based on type if applicable
//            }

//            if(allProps.getAliasesClassToSerialized() != null) {
//                String newKey = allProps.getAliasesClassToSerialized().get(key);
//                if(newKey != null) {
//                    key = newKey;
//                }
//            }

            // Write out the name of the field (String).
//            out.writeObject(key);

            // Write out the value of the field.
//            try {
//                out.writeObject(value);
//            } catch(Exception e) {
                // ??
//                e.printStackTrace();
//            }
//        }
    }


    //////////
    // READ //
    //////////

    // What if a field's type has changed?  What about when values' class
    // names change?  Is this even possible to correct?  What happens
    // when an exception occurs in the set?  How to determine
    // that an object being read here was NOT serialized in an
    // externalized fashion???

    public static void read(ObjectInput in, Object target) {
        read(in, target, null);
    }
    public static void read(ObjectInput in, Object target,
                            FlexibleSerializationProperties props) {
        try {
            readInner(in, target, props);
        } catch(Exception e) {
            //??
            e.printStackTrace();
        }
    }
    private static void readInner(ObjectInput in, Object target,
                                  FlexibleSerializationProperties localProps) throws Exception {

        // Good error handling check is to comment this if out,
        // but leave in the one for writing and make the Person
        // class not FlexibleExternalizable, so it doesn't get actually
        // written at all causing errors on readInt below.

        if(!(target instanceof FlexibleExternalizable)) {
            throw new IllegalArgumentException("target not FlexibleExternalizable");
        }

        // Get the properties to be used for the reading.
        // The global properties will never be null, but rather
        // just an empty object if no properties have been
        // defined.  This means there will be a lot of empty
        // properties lying around, one per each class, but
        // this order is better than always having to append
        // the global properties to the blank local properties.
        // (CPU vs. RAM again!)
        Class clazz = target.getClass();
        FlexibleSerializationProperties allProps =
            FlexibleSerializationPropertyManager.getProperties(clazz);
        if(localProps != null) {             // Usually will be null
            allProps.append(localProps);
        }

        // Read how many key-value pairs there will be.
        int pairs = in.readInt();                    // Could fail right here!

        for(int p = 0; p < pairs; p++) {
            String key = (String) in.readObject();
            String origKey = key;

            if(allProps.getSkipSerializedFields() != null &&
                            allProps.getSkipSerializedFields().contains(key)) {
                continue;
            }
            if(allProps.getAliasesSerializedToClass() != null) {
                String newKey = allProps.getAliasesSerializedToClass().get(key);
                if(newKey != null) {
                    key = newKey;
                }
            }
            if(allProps.getSkipClassFields() != null &&
                            allProps.getSkipClassFields().contains(key)) {
                continue;
            }

            Object value = in.readObject();            // Could fail!
            boolean localTranslate = false;
            if(allProps.getSerializedValueTranslators() != null) {
                OneWayValueTranslator T = allProps.getSerializedValueTranslators().get(origKey);
                if(T != null) {
                    value = T.translate(value);
                    localTranslate = true;
                }
            }
            if(!localTranslate) {
                // global value converter based on type if applicable
            }

            try {
                Field field = ReflectionUtil.getField(target, key);
                field.setAccessible(true);
                // need better error handling on this line... for example when
                // you change the data type of the value!
                field.set(target, value);
            } catch(Exception e) {
                e.printStackTrace();
                System.out.println("not found: " + key);
            }
        }
    }
}