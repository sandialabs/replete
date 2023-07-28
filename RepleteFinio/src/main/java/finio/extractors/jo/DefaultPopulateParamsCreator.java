package finio.extractors.jo;

import replete.plugins.ExtensionPoint;

public interface DefaultPopulateParamsCreator extends ExtensionPoint {
    PopulateParams create();
}
