package finio.plugins.extpoints;

import replete.plugins.ExtensionPoint;

public interface StringPatternMatcher extends ExtensionPoint {
    String getName();
    StringMatchResult match(String str);
}
