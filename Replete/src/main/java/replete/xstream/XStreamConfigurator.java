package replete.xstream;

import com.thoughtworks.xstream.XStream;

import replete.plugins.ExtensionPoint;

// This extension point allows projects to express in a detailed manner
// how their serialized objects should be treated when the provided
// instance of XStream attempts to serialize/deserialize them.

// Common operations in such extensions would be to add special
// converters or invoke other common XStream methods (e.g.
// processAnnotations, alias, omit, etc.).

public interface XStreamConfigurator extends ExtensionPoint {
    void configure(XStream xStream);
}
