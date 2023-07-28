package finio.extractors.jo;

import replete.plugins.ExtensionPoint;

public interface NonTerminalExpansionDecider extends ExtensionPoint {
    boolean shouldExpandNonTerminal(Object V);
}
