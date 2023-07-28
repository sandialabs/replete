package finio.extractors.jo;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import finio.core.FUtil;
import finio.core.KeyPath;
import finio.core.KeyValue;
import finio.core.NonTerminal;
import finio.core.errors.UnsupportedObjectTypeException;
import finio.core.it.KVIt;
import finio.core.it.NonTerminalLikeKeyValueIterator;
import finio.core.pop.NonTerminalPopulator;
import finio.core.warnings.RecursionWarning;
import finio.core.warnings.UnsupportedDataTypeWarning;
import finio.extractors.NonTerminalExtractor;
import finio.renderers.map.FMapRenderer;
import finio.renderers.map.StandardAMapRenderer;

// RECURSIVE COMPONENTIZED NT-IZATION:
//  - Extraction (creation of a new map)
//  - Population (adding keys to an existing map)
//  - Iteration (extraction and iteration over key-value pairs from some object)
//  - Resolution/Replacement (replacement of key or value objects before placed into map)
//  - Registration (placing of each k-v pair into the map)
//  - Population... (recurse on those objects that also need maps that need to be populated)

public class JavaObjectMapAwareExtractor extends NonTerminalExtractor implements NonTerminalPopulator {


    ///////////
    // FIELD //
    ///////////

    private Object O;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public JavaObjectMapAwareExtractor(Object O) {
        this.O = O;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Extractor

    @Override
    public NonTerminal extractInner() {
        if(!canHandle(O)) {
            throw new UnsupportedObjectTypeException(
                "Object must be NonTerminal-like (and non-null).", O);
        }
        NonTerminal Mhost = createBlankNonTerminal();
        Set<Object> visited = new HashSet<>();      // Recursion detection
        KeyPath P = KeyPath.KP();
        populate(Mhost, O, visited, P, null);
        return Mhost;
    }

    // Populator

    @Override
    public PopulateResult populate(NonTerminal M, Object O,
                                   Set<Object> visited, KeyPath P,
                                   PopulateParamsProvider paramsProvider)
                                       throws UnsupportedObjectTypeException {

        // Base case/Recursion Detection
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
                NonTerminalLikeKeyValueIterator it =
                    new NonTerminalLikeKeyValueIterator(O);
                for(KeyValue KV : new KVIt(it)) {
                    Object K = KV.getK();
                    Object V = KV.getV();

                    // Resolution/Replacement (needs extension)
                    //   K = transformKey(K);
                    //   V = transformValue(V);

                    // Registration
                    addValue(M, O, K, V, visited, P, paramsProvider);
                }

                // Modification

                //Recording
                FUtil.recordJavaSource(M, O);

            } else {
                throw new UnsupportedObjectTypeException(
                    "Object must be NonTerminal-like (and non-null).", O);
            }
        } finally {
            visited.remove(O);
        }

        return PopulateResult.ADDED;
    }

    private void addValue(NonTerminal M, Object O, Object K, Object V,
                          Set<Object> visited, KeyPath P,
                          PopulateParamsProvider paramsProvider) {

        if(FUtil.isNonTerminalLike(V)) {
            NonTerminal Mchild = createBlankNonTerminal();  // Inconsistency in location atm...
            try {
                P.append(K);
                try {
                    PopulateResult result = populate(Mchild, V, visited, P, paramsProvider);
                    if(result == PopulateResult.ADDED) {
                        M.put(K, Mchild);
                    } else {
                        M.put(K, new RecursionWarning(V));
                    }
                } finally {
                    P.removeLast();
                }
            } catch(UnsupportedObjectTypeException ex) {        // Can't happen here
                M.put(K, new UnsupportedDataTypeWarning(V));
            }
        } else {
            M.put(K, V);
        }
    }

    @Override
    protected String getName() {
        return "Java Object Map-Aware Extractor";
    }

    @Override
    public boolean canHandle(Object O) {
        return FUtil.isNonTerminalLike(O);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        Map sa = new LinkedHashMap<>();
        sa.put("Executive", "Pretoria");
        sa.put("Legislative", "Cape Town");
        sa.put("Judicial", "Bloemfontein");
        Map m = new LinkedHashMap<>();
        m.put("Colorado", "Denver");
        m.put("New Mexico", "Santa Fe");
        m.put("South Africa", sa);
        JavaObjectMapAwareExtractor X = new JavaObjectMapAwareExtractor(m);
        NonTerminal M = X.extract();
        FMapRenderer R = new StandardAMapRenderer().setRenderSysMeta(false);
        System.out.println(R.render(null, M));
    }
}
