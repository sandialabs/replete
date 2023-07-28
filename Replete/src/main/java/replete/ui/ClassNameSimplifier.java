package replete.ui;

import replete.plugins.ExtensionPoint;

public interface ClassNameSimplifier extends ExtensionPoint {
    boolean appliesTo(String className);   // Actually used in this case to explicitly know if a simplifier is going to do anything
    String simplifyAndMarkupClassName(String className);
}
