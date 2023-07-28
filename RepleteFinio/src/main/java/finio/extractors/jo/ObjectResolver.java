package finio.extractors.jo;

import replete.plugins.ExtensionPoint;

public interface ObjectResolver extends ExtensionPoint {
    boolean canHandle(Object O);
    Object resolve(Object O);
}
