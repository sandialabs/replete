package replete.xstream;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;

import replete.io.FileUtil;
import replete.util.ClassUtil;
import replete.util.ZipUtil;


/**
 * Wrapper class for using the XStream library.  The main
 * purpose of this class is to automatically serialize
 * valuable metadata along with the object you want
 * serialized.  Thus, if you use this wrapper class to
 * serialize your objects to XML, then you will always
 * know what application and version created that object
 * along with some other information.
 *
 * @author Derek Trumbo
 */

// This class is now deprecated in favor of subclasses of XStream
// like MetadataXStream (which contains most of the features that
// this class contains).

@Deprecated
public class XStreamWrapper {
    protected static final XStream xstream;
    protected static SerializationMetadata szMetadata;

    // After any load method, this method may be called to
    // retrieve metadata about the object just deserialized
    // on the same thread.
    public static SerializationMetadata getMetadata() {
        return szMetadata;
    }

    ////////////////////////////
    // XStream Initialization //
    ////////////////////////////

    static {
        xstream = new XStream();

        // To make the XML smaller and more readable, replace
        // every class reference with it's simple name.
        // NOTE: Currently disabled because ClassUtil does
        // not work when the classes are within a JAR file.
        //initializeAliases();
    }

    public static void addOmit(Class<?> clazz, String field) {
        xstream.omitField(clazz, field);
    }
    public static void addAlias(Class<?> clazz) {
        xstream.alias(clazz.getSimpleName(), clazz);
    }
    public static void addAlias(String alias, Class<?> clazz) {
        xstream.alias(alias, clazz);
    }
    public static void registerConverter(Converter converter) {
        xstream.registerConverter(converter);
    }
    public static void processAnnotations(Class clazz) {
        xstream.processAnnotations(clazz);
    }
    public static ReflectionConverter getBaseConverter() {
        return (ReflectionConverter) xstream.getConverterLookup().lookupConverterForType(Object.class);
    }

    protected static void initializeAliases() {
        try {
            List<Class<?>> allClasses = ClassUtil.findAll();

            Set<String> dupClasses = new HashSet<>();
            Set<String> uniqueClassNames = new HashSet<>();

            for(Class<?> cls : allClasses) {
                String clsName = cls.getSimpleName();
                if(uniqueClassNames.contains(clsName)) {
                    dupClasses.add(clsName);
                } else {
                    uniqueClassNames.add(clsName);
                }
            }

            for(Class<?> cls : allClasses) {
                String clsName = cls.getSimpleName();
                if(!dupClasses.contains(clsName)) {
                    xstream.alias(clsName, cls);
                }
            }
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    /////////////
    // WRITING //
    /////////////

    public static void writeToFile(Object obj, File file) throws IOException {
        writeToFile(obj, file, null, false);
    }
    public static void writeToFile(Object obj, File file, boolean doZip) throws IOException {
        writeToFile(obj, file, null, doZip);
    }
    public static void writeToFile(Object obj, File file, SerializationMetadata metadata) throws IOException {
        writeToFile(obj, file, metadata, false);
    }
    public static void writeToFile(Object obj, File file, SerializationMetadata metadata, boolean doZip) throws IOException {

        if(metadata == null) {
            metadata = new SerializationMetadata(obj);
        }

        // Set up result.
        SerializationResult result = new SerializationResult(metadata, obj);

        OutputStreamWriter osw = null;
        try {        // Have to used old-fashioned IO try-catch due to doZip option

            // Set up the stream.
            OutputStream os = new FileOutputStream(file);
            if(doZip) {
                os = new GZIPOutputStream(os);
            }
            BufferedOutputStream bos = new BufferedOutputStream(os);
            osw = new OutputStreamWriter(bos, "UTF-8");

            // Write result to file.
            xstream.toXML(result, osw);

        } finally {
            if(osw != null) {
                try {
                    osw.close();          // Close all the streams
                } catch(Exception e) {
                    // Do nothing
                }
            }
        }
    }

    public static String writeToString(Object obj) {
        return writeToString(obj, null);
    }
    public static String writeToString(Object obj, SerializationMetadata metadata) {

        if(metadata == null) {
            metadata = new SerializationMetadata(obj);
        }

        SerializationResult result = new SerializationResult(metadata, obj);

        return xstream.toXML(result);
    }


    /////////////
    // LOADING //
    /////////////

    // Loads both the serialization metadata and the target
    // object from the string.
    public static <T extends Object> T loadTarget(String xml) {
        SerializationResult result = (SerializationResult) xstream.fromXML(xml);
        szMetadata = result.getMetadata();
        return (T) result.getTargetObject();
    }

    // Loads both the serialization metadata and the target
    // object from the file.
    public static <T extends Object> T loadTarget(File file) throws IOException {

        // Set up stream.
        InputStream is;
        if(ZipUtil.isGZIPFormat(file)) {
            is = new GZIPInputStream(new FileInputStream(file));
        } else {
            is = new FileInputStream(file);
        }

        return loadTarget(is);
    }

    public static <T extends Object> T loadTarget(InputStream is) throws UnsupportedEncodingException, IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        InputStreamReader isr = new InputStreamReader(bis, "UTF-8");

        // Read result from file.
        SerializationResult result = (SerializationResult) xstream.fromXML(isr);

        isr.close();

        szMetadata = result.getMetadata();
        return (T) result.getTargetObject();
    }

    // Parsing out the XML you want is only necessary because
    // XStream.omitField does not work when the field exists
    // in the class.  It's behavior was designed to ignore
    // those XML elements that do not have corresponding
    // elements in the class any longer.
    protected static final String TARGET_START_ELEM = "<targetObject ";
    protected static final String TARGET_END_ELEM = "</targetObject>";

    // Loads only the serialization metadata from the string
    // without parsing the target object element.
    public static SerializationMetadata loadMetadataFromString(String xml) {
        int start = xml.indexOf(TARGET_START_ELEM);
        int end = xml.indexOf(TARGET_END_ELEM);

        if(start == -1 || end == -1) {
            szMetadata = null;
            return null;
        }

        xml = xml.substring(0, start) + xml.substring(end + TARGET_END_ELEM.length());

        loadTarget(xml);

        return szMetadata;
    }

    // Loads only the serialization metadata from the file
    // without parsing the target object element.
    public static SerializationMetadata loadMetadataFromFile(File file) {
        String fileContents = FileUtil.getTextContent(file);
        return loadMetadataFromString(fileContents);
    }


    /////////////
    // TESTING //
    /////////////

    public static void mainx(String[] args) {
        System.setProperty("program.name", "testp");
        System.setProperty("program.version", "testv");

        TestObject o = new TestObject();
        String xml = XStreamWrapper.writeToString(o);
        System.out.println(xml);
        TestObject o2 = (TestObject) XStreamWrapper.loadTarget(xml);
        System.out.println(o.equals(o2));
    }

    private static class TestObject {
        String a = "ABC";
        int b = 10;
        @Override
        public boolean equals(Object o) {
            TestObject to = (TestObject) o;
            return a.equals(to.a) && b == to.b;
        }
    }
}
