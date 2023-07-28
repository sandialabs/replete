package finio.extractors.jo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.errors.UnsupportedObjectTypeException;
import finio.core.impl.FMap;
import finio.core.it.FieldReflectionKeyValueIterator;
import finio.core.it.KeyValueIterator;
import finio.core.pop.NonTerminalPopulator;
import finio.core.warnings.AlreadySeenGlobalWarning;
import finio.core.warnings.RecursionWarning;
import finio.core.warnings.UnsupportedDataTypeWarning;

public class FieldReflectionKeyValueRegistrar implements KeyValueRegistrar {

    private static final String STATIC_KEY = "^static";   // Hopefully abstracted away somewhere eventually

    @Override
    public void addValue(NonTerminal M, Object O, Object K, Object V,
                          Set<Object> visited, KeyPath P,
                          PopulateParamsProvider paramsProvider,
                          KeyValueIterator kvIterator, NonTerminalPopulator populator) {

        Field F = ((FieldReflectionKeyValueIterator) kvIterator).getCurrentField();
        boolean isStatic = Modifier.isStatic(F.getModifiers());

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
                    addValue(M, K, Vadd, isStatic);
                } catch(UnsupportedObjectTypeException ex) {        // Can't happen here
                    addValue(M, K, new UnsupportedDataTypeWarning(V), isStatic);
                }
            } finally {
                P.removeLast();
            }
        } else {
            addValue(M, K, V, isStatic);
        }
    }

    private void addValue(NonTerminal M, Object K, Object V, boolean isStatic) {
        NonTerminal MaddTo;

        // Make sure static fields are placed into a separate sub map
        // for organization & visualization purposes.
        if(isStatic) {
            NonTerminal Mstatic = (NonTerminal) M.get(STATIC_KEY);
            if(Mstatic == null) {
                Mstatic = createBlankNonTerminal();     // Could get from PopulateParamsProvider
                M.put(STATIC_KEY, Mstatic);
            }
            MaddTo = Mstatic;
        } else {
            MaddTo = M;
        }
        MaddTo.put(K, V);
    }

    private NonTerminal createBlankNonTerminal() {
        return new FMap();
    }

    @Override
    public String toString() {
        return "Field Reflection Key Value Registrar";
    }
}
