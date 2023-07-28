package finio.extractors.jo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import finio.core.FUtil;
import finio.core.KeyPath;
import finio.core.KeyValue;
import finio.core.NonTerminal;
import finio.core.errors.UnsupportedObjectTypeException;
import finio.core.it.FieldReflectionKeyValueIterator;
import finio.core.it.KVIt;
import finio.core.pop.NonTerminalPopulator;
import finio.core.warnings.FieldAccessWarning;
import finio.core.warnings.RecursionWarning;
import finio.extractors.NonTerminalExtractor;

// Could eventually just parameterize a
// JavaObjectUberExtractor to do the same
// task as below.  However, would get
// an unknown amount of better performance
// by having this class implemented to do
// just the reflection task.  Good to
// compare both methods when both implemented.

public class JavaObjectReflectionExtractor extends NonTerminalExtractor implements NonTerminalPopulator {


    ////////////
    // FIELDS //
    ////////////

    // Constant

    private static final String STATIC_KEY = "^static";   // Hopefully abstracted away somewhere eventually

    // Core

    private Object O;
    private boolean staticEnabled = true;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public JavaObjectReflectionExtractor(Object O) {
        this.O = O;
    }
    public JavaObjectReflectionExtractor(Object O, boolean staticEnabled) {
        this.O = O;
        this.staticEnabled = staticEnabled;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Extractor

    @Override
    public NonTerminal extractInner() {
        if(!canHandle(O)) {
            throw new UnsupportedObjectTypeException("Object non-null.", O);   // NullObjectException ??
        }
        NonTerminal Mhost = createBlankNonTerminal();
        Set<Object> visited = new HashSet<>();      // Recursion detection
        KeyPath P = KeyPath.KP();
        populate(Mhost, O, visited, P, null);
        return Mhost;
    }

    // Populator

    @Override
    public boolean canHandle(Object O) {
        return !FUtil.isNull(O);
    }

    @Override
    public PopulateResult populate(NonTerminal M, Object O,
                                   Set<Object> visited, KeyPath P,
                                   PopulateParamsProvider paramsProvider)
                                       throws UnsupportedObjectTypeException {

        // Base Case/Recursion Detection
        if(visited.contains(O)) {
            return PopulateResult.ALREADY_SEEN_PATH;
        }
        visited.add(O);

        try {

            if(canHandle(O)) {

                // NOTE: Notice here now, that since the iterator
                // has been abstracted, we can see using additional
                // reflection/abstraction to choose the proper
                // KV Iterator given the current key path (P), level (N),
                // object type (T) or query specification (Q).
                // EXTRA: (except for unresolved specific need)
                FieldReflectionKeyValueIterator it =
                    new FieldReflectionKeyValueIterator(O, true);
                for(KeyValue KV : new KVIt(it)) {
                    Object K = KV.getK();
                    Object V = KV.getV();

                    Field F = it.getCurrentField();         // Unresolved specific need as of yet
                    boolean isStatic = Modifier.isStatic(F.getModifiers());

                    // Resolution/Replacement:
                    //   K = transformKey(K);
                    //   V = transformValue(V);

                    // Registration
                    if(!isStatic || staticEnabled) {
                        addValue(M, O, K, V, visited, P, paramsProvider, isStatic);
                    }
                }

                // PostFieldsModifiers

                FUtil.recordJavaSource(M, O);

            } else {
                throw new UnsupportedObjectTypeException("Object non-null.", O);   // NullObjectException ??
            }
        } finally {
            visited.remove(O);
        }

        return PopulateResult.ADDED;
    }

    private void addValue(NonTerminal M, Object O, Object K, Object V,
                          Set<Object> visited, KeyPath P,
                          PopulateParamsProvider paramsProvider, boolean isStatic) {

        boolean expand =
            !FUtil.isNull(V) &&
            !FUtil.isPrimitive(V) &&
            !FUtil.isJavaArray(V) &&
            !(V instanceof FieldAccessWarning);

        // Add any other object after it is recursively populated
        // into a new non-terminal.
        if(expand) {
            P.append(K);
            try {
                NonTerminal MhostChild = createBlankNonTerminal();
                PopulateResult result = populate(MhostChild, V, visited, P, paramsProvider);
                Object Vadd;
                if(result == PopulateResult.ADDED) {
                    Vadd = MhostChild;
                } else {
                    Vadd = new RecursionWarning(V);
                }
                addValue(M, K, Vadd, isStatic);
            } finally {
                P.removeLast();
            }

        // Any primitive types are not expanded and are considered
        // base cases of this recursion.
        // Any error caught during reflection should also not be
        // expanded further.
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
                Mstatic = createBlankNonTerminal();
                M.put(STATIC_KEY, Mstatic);
            }
            MaddTo = Mstatic;
        } else {
            MaddTo = M;
        }
        MaddTo.put(K, V);
    }

    @Override
    protected String getName() {
        return "Java Object Reflection Extractor";
    }
}
