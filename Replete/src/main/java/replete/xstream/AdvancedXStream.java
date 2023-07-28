package replete.xstream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.security.AnyTypePermission;

import replete.errors.RuntimeConvertedException;

public class AdvancedXStream extends XStream {


    ////////////
    // FIELDS //
    ////////////

    private static final Charset FILE_CHARSET = StandardCharsets.UTF_8;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AdvancedXStream() {
        init();
    }
    public AdvancedXStream(ReflectionProvider reflectionProvider) {
        super(reflectionProvider);
        init();
    }
    public AdvancedXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(hierarchicalStreamDriver);
        init();
    }
    public AdvancedXStream(ReflectionProvider reflectionProvider,
                           HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(reflectionProvider, hierarchicalStreamDriver);
        init();
    }
    public AdvancedXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                           ClassLoader classLoader) {
        super(reflectionProvider, driver, classLoader);
        init();
    }
    public AdvancedXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                           ClassLoader classLoader, Mapper mapper) {
        super(reflectionProvider, driver, classLoader, mapper);
        init();
    }
    public AdvancedXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                           ClassLoader classLoader, Mapper mapper, ConverterLookup converterLookup,
                           ConverterRegistry converterRegistry) {
        super(reflectionProvider, driver, classLoader, mapper, converterLookup, converterRegistry);
        init();
    }

    private void init() {
        addPermission(AnyTypePermission.ANY);
    }


    //////////
    // MISC //
    //////////

    public void toXML(Object obj, File file) {
        toXML(obj, file, false);
    }
    public void toXML(Object obj, File file, boolean doZip) {
        OutputStream os = null;
        OutputStreamWriter osw = null;
        try {        // Have to used old-fashioned IO try-catch due to doZip option

            // Set up the stream.
            os = new FileOutputStream(file);
            if(doZip) {
                os = new GZIPOutputStream(os);
            }
            BufferedOutputStream bos = new BufferedOutputStream(os);
            osw = new OutputStreamWriter(bos, FILE_CHARSET);

            // Write result to file.
            super.toXML(obj, osw);

        } catch(Exception e) {
            throw new RuntimeConvertedException(e);

        } finally {
            if(osw != null) {
                try {
                    osw.close();          // Close all the streams
                } catch(Exception e) {
                    // Do nothing
                }
            } else if(os != null) {
                try {
                    os.close();
                } catch(Exception e) {
                    // Do nothing
                }
            }
        }
    }
}
