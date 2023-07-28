package finio.core.pop;

import java.util.Set;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.errors.UnsupportedObjectTypeException;
import finio.extractors.jo.PopulateParamsProvider;
import finio.extractors.jo.PopulateResult;

public interface NonTerminalPopulator {

    public boolean canHandle(Object O);

    public PopulateResult populate(NonTerminal M, Object O,
                                   Set<Object> visited, KeyPath P,
                                   PopulateParamsProvider params)
                                       throws UnsupportedObjectTypeException;

}
