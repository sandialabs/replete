package replete.xstream;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.Mapper;

import replete.ui.params.hier.test.TestPropertyParams;

// TODO: Thread safety for lastLoadedMetadata.
// TODO: Methods to return both metadata & target in one call
// TODO: Methods to just parse metadata?  Can't do this for
//       any arbitrary source I don't believe... And it doesn't
//       look to be a popular feature.
// TODO: Methods to unzip files in fromXML.

public class MetadataXStream extends AdvancedXStream {


    ////////////
    // FIELDS //
    ////////////

    private SerializationMetadata lastLoadedMetadata;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public MetadataXStream() {}
    public MetadataXStream(ReflectionProvider reflectionProvider) {
        super(reflectionProvider);
    }
    public MetadataXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(hierarchicalStreamDriver);
    }
    public MetadataXStream(ReflectionProvider reflectionProvider,
                           HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(reflectionProvider, hierarchicalStreamDriver);
    }
    public MetadataXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                           ClassLoader classLoader) {
        super(reflectionProvider, driver, classLoader);
    }
    public MetadataXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                           ClassLoader classLoader, Mapper mapper) {
        super(reflectionProvider, driver, classLoader, mapper);
    }
    public MetadataXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                           ClassLoader classLoader, Mapper mapper, ConverterLookup converterLookup,
                           ConverterRegistry converterRegistry) {
        super(reflectionProvider, driver, classLoader, mapper, converterLookup, converterRegistry);
    }


    ///////////
    // toXML //
    ///////////

    // New and modified toXML methods

    @Override
    public String toXML(Object obj) {
        obj = wrap(obj, null);
        Writer writer = new StringWriter();
        super.toXML(obj, writer);
        return writer.toString();
    }
    public String toXML(Object obj, SerializationMetadata metadata) {
        obj = wrap(obj, metadata);
        Writer writer = new StringWriter();
        super.toXML(obj, writer);
        return writer.toString();
    }

    @Override
    public void toXML(Object obj, File file) {
        obj = wrap(obj, null);
        super.toXML(obj, file, false);
    }
    public void toXML(Object obj, File file, SerializationMetadata metadata) {
        obj = wrap(obj, metadata);
        super.toXML(obj, file, false);
    }

    @Override
    public void toXML(Object obj, File file, boolean doZip) {
        obj = wrap(obj, null);
        super.toXML(obj, file, doZip);
    }
    public void toXML(Object obj, File file, boolean doZip, SerializationMetadata metadata) {
        obj = wrap(obj, metadata);
        super.toXML(obj, file, doZip);
    }

    @Override
    public void toXML(Object obj, OutputStream out) {
        obj = wrap(obj, null);
        super.toXML(obj, out);
    }
    public void toXML(Object obj, OutputStream out, SerializationMetadata metadata) {
        obj = wrap(obj, metadata);
        super.toXML(obj, out);
    }

    @Override
    public void toXML(Object obj, Writer out) {
        obj = wrap(obj, null);
        super.toXML(obj, out);
    }
    public void toXML(Object obj, Writer out, SerializationMetadata metadata) {
        obj = wrap(obj, metadata);
        super.toXML(obj, out);
    }

    // Helper

    private SerializationResult wrap(Object obj, SerializationMetadata metadata) {
        if(obj instanceof SerializationResult) {
            throw new IllegalArgumentException("Possible call inconsistency, you are attempting to wrap an already wrapped object.");
        }
        if(metadata == null) {
            metadata = new SerializationMetadata(obj);
        }
        return new SerializationResult(metadata, obj);
    }


    /////////////
    // fromXML //
    /////////////

    // New and modified fromXML methods
    // TODO: Methods to unzip

    @Override
    public Object fromXML(File file) {
        Object obj = super.fromXML(file, null);
        return unwrap(obj);
    }
    @Override
    public Object fromXML(File file, Object root) {
        Object obj = super.fromXML(file, root);
        return unwrap(obj);
    }
    @Override
    public Object fromXML(InputStream input) {
        Object obj = super.fromXML(input);
        return unwrap(obj);
    }
    @Override
    public Object fromXML(InputStream input, Object root) {
        Object obj = super.fromXML(input, root);
        return unwrap(obj);
    }
    @Override
    public Object fromXML(Reader reader) {
        Object obj = super.fromXML(reader);
        return unwrap(obj);
    }
    @Override
    public Object fromXML(Reader xml, Object root) {
        Object obj = super.fromXML(xml, root);
        return unwrap(obj);
    }
    @Override
    public Object fromXML(String xml) {
        Object obj = super.fromXML(new StringReader(xml));
        return unwrap(obj);
    }
    @Override
    public Object fromXML(String xml, Object root) {
        Object obj = super.fromXML(new StringReader(xml), root);
        return unwrap(obj);
    }
    @Override
    public Object fromXML(URL url) {
        Object obj = super.fromXML(url, null);
        return unwrap(obj);
    }
    @Override
    public Object fromXML(URL url, Object root) {
        Object obj = super.fromXML(url, root);
        return unwrap(obj);
    }

    // Helper

    private <T extends Object> T unwrap(Object obj) {
        if(obj instanceof SerializationResult) {
            SerializationResult result = (SerializationResult) obj;
            lastLoadedMetadata = result.getMetadata();     // Not threadsafe, but generally deserialization is a single-threaded process
            obj = result.getTargetObject();
        } else {
            lastLoadedMetadata = null;                     // Not threadsafe, but generally deserialization is a single-threaded process
        }
        return (T) obj;
    }


    //////////////////////
    // Extended fromXML //
    //////////////////////

    // These methods just have a more convenient return type ("XML" left upper case).
    // They have to be separate methods because the compiler doesn't think the return
    // type "<T extends Object> T" is equivalent to "Object".

    public <T extends Object> T fromXMLExt(File file) {
        Object obj = super.fromXML(file, null);
        return unwrap(obj);
    }
    public <T extends Object> T fromXMLExt(String xml) {
        Object obj = super.fromXML(new StringReader(xml));
        return unwrap(obj);
    }
    public <T extends Object> T fromXMLExt(InputStream input) {
        Object obj = super.fromXML(input);
        return unwrap(obj);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public SerializationMetadata getLastLoadedMetadata() {
        return lastLoadedMetadata;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        MetadataXStream x = new MetadataXStream();
        String xml = x.toXML(new TestPropertyParams());// x.toXML(new TestPropertyParams().setValue(true));
        Object o = x.fromXML(xml);
        System.out.println(
            xml
        );
        System.out.println(o);
        System.out.println(x.getLastLoadedMetadata());
    }
}
