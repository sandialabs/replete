package replete.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.Mapper;

// This class and ExtensibleMetadataXStream could theoretically be
// replaced with a builder pattern of some sort where the developer
// asks that an XStream instance be created with certain properties.

public class VersioningMetadataXStream extends MetadataXStream {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public VersioningMetadataXStream() {
        init();
    }
    public VersioningMetadataXStream(ReflectionProvider reflectionProvider) {
        super(reflectionProvider);
        init();
    }
    public VersioningMetadataXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(hierarchicalStreamDriver);
        init();
    }
    public VersioningMetadataXStream(ReflectionProvider reflectionProvider,
                           HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(reflectionProvider, hierarchicalStreamDriver);
        init();
    }
    public VersioningMetadataXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                           ClassLoader classLoader) {
        super(reflectionProvider, driver, classLoader);
        init();
    }
    public VersioningMetadataXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                           ClassLoader classLoader, Mapper mapper) {
        super(reflectionProvider, driver, classLoader, mapper);
        init();
    }
    public VersioningMetadataXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                           ClassLoader classLoader, Mapper mapper, ConverterLookup converterLookup,
                           ConverterRegistry converterRegistry) {
        super(reflectionProvider, driver, classLoader, mapper, converterLookup, converterRegistry);
        init();
    }

    private void init() {
        Converter converter = new VersioningReflectionConverter(getMapper(), getReflectionProvider());
        int justAboveStandardReflectionConverter = XStream.PRIORITY_VERY_LOW + 1;
        registerConverter(converter, justAboveStandardReflectionConverter);
    }
}
