package finio.extractors.jo;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import replete.plugins.ExtensionPoint;

public interface HostNonTerminalCreator extends ExtensionPoint {
    NonTerminal create(Object O, KeyPath P);
}
