package replete.xstream;

import java.util.List;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.Mapper;

import replete.plugins.PluginManager;

// This class and VersioningMetadataXStream could theoretically be
// replaced with a builder pattern of some sort where the developer
// asks that an XStream instance be created with certain properties.

public class ExtensibleMetadataXStream extends VersioningMetadataXStream {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ExtensibleMetadataXStream() {
        init();
    }
    public ExtensibleMetadataXStream(ReflectionProvider reflectionProvider) {
        super(reflectionProvider);
        init();
    }
    public ExtensibleMetadataXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(hierarchicalStreamDriver);
        init();
    }
    public ExtensibleMetadataXStream(ReflectionProvider reflectionProvider,
                           HierarchicalStreamDriver hierarchicalStreamDriver) {
        super(reflectionProvider, hierarchicalStreamDriver);
        init();
    }
    public ExtensibleMetadataXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                           ClassLoader classLoader) {
        super(reflectionProvider, driver, classLoader);
        init();
    }
    public ExtensibleMetadataXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                           ClassLoader classLoader, Mapper mapper) {
        super(reflectionProvider, driver, classLoader, mapper);
        init();
    }
    public ExtensibleMetadataXStream(ReflectionProvider reflectionProvider, HierarchicalStreamDriver driver,
                           ClassLoader classLoader, Mapper mapper, ConverterLookup converterLookup,
                           ConverterRegistry converterRegistry) {
        super(reflectionProvider, driver, classLoader, mapper, converterLookup, converterRegistry);
        init();
    }

    private void init() {
        List providers = PluginManager.getExtensionsForPoint(XStreamConfigurator.class);
        for(Object p : providers) {
            XStreamConfigurator configurator = (XStreamConfigurator) p;
            configurator.configure(this);
        }
    }
}
