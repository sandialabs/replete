package finio.extractors.jo;

import java.util.Set;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.errors.UnsupportedObjectTypeException;
import finio.core.it.KeyValueIterator;
import finio.core.pop.NonTerminalPopulator;
import finio.core.warnings.AlreadySeenGlobalWarning;
import finio.core.warnings.RecursionWarning;
import finio.core.warnings.UnsupportedDataTypeWarning;

public class DefaultKeyValueRegistrar implements KeyValueRegistrar {

    @Override
    public void addValue(NonTerminal M, Object O, Object K, Object V,
                          Set<Object> visited, KeyPath P,
                          PopulateParamsProvider paramsProvider,
                          KeyValueIterator kvIterator, NonTerminalPopulator populator) {

        boolean expand = paramsProvider.shouldExpandAsNonTerminal(O, P, V);

        if(expand) {
            P.append(K);
            try {
                NonTerminal MhostChild = paramsProvider.getHostNonTerminal(V, P);
                try {
                    PopulateResult result = populator.populate(MhostChild, V, visited, P, paramsProvider);
                    Object Vadd;
                    if(result == PopulateResult.ADDED) {
                        Vadd = MhostChild;
                    } else if(result == PopulateResult.ALREADY_SEEN_PATH) {
                        Vadd = new RecursionWarning(V);
                    } else { /*if(result == PopulateResult.ALREADY_SEEN_GLOBAL) {*/
                        Vadd = new AlreadySeenGlobalWarning(V);
                    }
                    addValue(M, K, Vadd);
                } catch(UnsupportedObjectTypeException ex) {        // Can't happen here
                    addValue(M, K, new UnsupportedDataTypeWarning(V));
                }
            } finally {
                P.removeLast();
            }
        } else {
            addValue(M, K, V);
        }
    }

    private void addValue(NonTerminal M, Object K, Object V) {
        M.put(K, V);
    }

    @Override
    public String toString() {
        return "Default Key Value Registrar";
    }
}
