package replete.xstream;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import replete.bc.SoftwareVersionLookupManager;

// This might not end up in Replete, as its Mongo counterpart will
// require that Replete be dependent on Mongo but at least this is
// a good staging ground for now.

// This converter accepts all object types (->Object), like its base class.

public class VersioningReflectionConverter extends ReflectionConverter {


    ////////////
    // FIELDS //
    ////////////

    private static final String VERSION_ATTR_NAME = "version";
    private static final String CLASS_ATTR_NAME = "class";


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public VersioningReflectionConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        super(mapper, reflectionProvider);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        String version = SoftwareVersionLookupManager.getVersion(value);
        if(version != null) {
            writer.addAttribute(VERSION_ATTR_NAME, version);
        }
        super.marshal(value, writer, context);          // TODO: Get converter skipping working so don't
    }                                                   //       bypass other registered converters

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

        // Currently, sometimes elemName this is a fully-qualified class name, and sometimes
        // it is a field name within an enclosing object's XML.  Relatedly, sometimes "class" is
        // populated by XStream and sometimes it's not, depending on what it knows about a
        // field's type.  Need to research this more to determine if this will work for
        // deserialization logic.
//        String elemName = reader.getNodeName();
//        String version = reader.getAttribute(VERSION_ATTR_NAME);
//        String clazz = reader.getAttribute(CLASS_ATTR_NAME);
//        System.out.println("ELEM: " + elemName + " = " + version + " | " + ((clazz == null) ? "(NONE)" : clazz));

        // TODO: Backwards compatibility logic eventually (p-code)
        // reader + context represent the serialized Dx
//        String currentDomainsVersion = SomeVersionManager.lookup(clazz);
//        return XStreamBackwardsCompatibilityManager.chainedUnmarshal(
//              reader, context, version, clazz, currentDomainsVersion);

        return super.unmarshal(reader, context);
    }
}
