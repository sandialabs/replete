package finio.extractors.jo;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.impl.FMap;

public class FMapHostNonTerminalCreator implements HostNonTerminalCreator {
    public NonTerminal create(Object O, KeyPath P) {
        return new FMap();
    }

    @Override
    public String toString() {
        return "FMap Creator";
    }
}
