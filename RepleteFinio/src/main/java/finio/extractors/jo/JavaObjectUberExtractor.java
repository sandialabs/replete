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
import finio.core.it.KeyValueIterator;
import finio.core.pop.NonTerminalPopulator;
import finio.extractors.NonTerminalExtractor;
import finio.extractors.jo.rules.Rule;
import finio.extractors.jo.rules.RuleNode;
import finio.platform.exts.sc.FieldResultResolver;
import finio.renderers.map.FMapRenderer;
import finio.renderers.map.StandardAMapRenderer;

// RECURSIVE COMPONENTIZED NT-IZATION:
//  - Extraction (creation of a new map)
//  - Population (adding keys to an existing map)
//  - Iteration (extraction and iteration over key-value pairs from some object)
//  - Resolution/Replacement (replacement of key or value objects before placed into map)
//  - Registration (placing of each k-v pair into the map)
//  - Population... (recurse on those objects that also need maps that need to be populated)

public class JavaObjectUberExtractor extends NonTerminalExtractor implements NonTerminalPopulator {


    ////////////
    // FIELDS //
    ////////////

    private Object O;
    private PopulateParamsProvider paramsProvider;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public JavaObjectUberExtractor() {
        this(null, null);
    }
    public JavaObjectUberExtractor(Object O) {
        this(O, null);
    }
    public JavaObjectUberExtractor(Object O, PopulateParamsProvider paramsProvider) {
        this.O = O;
        if(paramsProvider == null) {
            paramsProvider = new PopulateParamsProvider();
        }
        this.paramsProvider = paramsProvider;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Object getO() {
        return O;
    }
    public PopulateParamsProvider getParamsProvider() {
        return paramsProvider;
    }

    // Mutators

    public JavaObjectUberExtractor setO(Object O) {
        this.O = O;
        return this;
    }
    public JavaObjectUberExtractor setParamsProvider(PopulateParamsProvider paramsProvider) {
        this.paramsProvider = paramsProvider;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Extractor

    @Override
    protected String getName() {
        return "Java Object Uber Extractor";
    }

    @Override
    public NonTerminal extractInner() {
        if(!canHandle(O)) {
            throw new UnsupportedObjectTypeException(
                "Object must be NonTerminal-like (and non-null).", O);
        }
        paramsProvider.checkDefaults();
        Set<Object> visited = new HashSet<>();      // Recursion detection
        KeyPath P = KeyPath.KP();
        NonTerminal Mhost = paramsProvider.getHostNonTerminal(O, P);
        populate(Mhost, O, visited, P, paramsProvider);
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

        RevisitPolicy policy = paramsProvider.getRevisitPolicy(O, P);

        // Configurable Base Case/Recursion Detection
        if(policy != RevisitPolicy.NO_RESTRICTION) {
            if(visited.contains(O)) {
                return
                    policy == RevisitPolicy.NO_DUP_PATH ?
                        PopulateResult.ALREADY_SEEN_PATH :
                        PopulateResult.ALREADY_SEEN_GLOBAL;
            }
            visited.add(O);
        }

        try {

            if(canHandle(O)) {

                // NOTE: Notice here now, that since the iterator
                // has been abstracted, we can see using additional
                // reflection/abstraction to choose the proper
                // KV Iterator given the current key path (P), level (N),
                // object type (T) or query specification (Q).
                KeyValueIterator it = paramsProvider.getKeyValueIterator(O, P);
                KeyValueRegistrar rg = paramsProvider.getKeyValueRegistrar(O, P);

                if(it != null && rg != null) {
                    for(KeyValue KV : new KVIt(it)) {
                        Object K = KV.getK();
                        Object V = KV.getV();

                        Object Knew = paramsProvider.resolveKey(O, P, K, V);
                        Object Vnew = paramsProvider.resolveValue(O, P, K, V);
                        // ^ Technically you could also go off of Knew and not K...

                        // Registration
                        rg.addValue(M, O, Knew, Vnew, visited, P, paramsProvider, it, this);
                    }
                }

                // Apply the post fields modifier for this object and path
                // if there is one.
                PostFieldsModifier mod = paramsProvider.getPostFieldsModifier(O, P);
                if(mod != null && mod.canHandle(O)) {
                    mod.modify(M, O, this, visited, P, paramsProvider);
                }

                // Append native object source fields for this object and path
                // if such is desired.
                if(paramsProvider.isRecordJavaSource(O, P)) {
                    FUtil.recordJavaSource(M, O);
                }

            } else {
                throw new UnsupportedObjectTypeException(
                    "Object must be NonTerminal-like (and non-null).", O);
            }
        } finally {
            if(policy == RevisitPolicy.NO_DUP_PATH) {
                visited.remove(O);
            }
        }

        return PopulateResult.ADDED;
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

//        JavaObjectMapAwareExtractor X = new JavaObjectMapAwareExtractor(m);
//        JavaObjectReflectionExtractor X = new JavaObjectReflectionExtractor(m);

        PopulateParamsProvider paramsProvider = new PopulateParamsProvider();
        JavaObjectUberExtractor X = new JavaObjectUberExtractor(m)
            .setParamsProvider(paramsProvider)
        ;

        PopulateParams childParams = new PopulateParams().setValueResolver(new FieldResultResolver());
//            new PopulatorKeyValueIteratorCreator() {
//                public KeyValueIterator create(Object O, KeyPath P) {
//                    return null;
//                }
//            }
//        );

        Rule rule = new Rule() {
            @Override
            public boolean appliesTo(Object O, KeyPath P) {
                return true;
            }
        };
        RuleNode child = new RuleNode(childParams, rule);

        PopulateParams childParams2 = new PopulateParams().setRecordJavaSource(true);
        Rule rule2 = new Rule() {
            @Override
            public boolean appliesTo(Object O, KeyPath P) {
                return !P.isEmpty() && P.last().equals("South Africa");
            }
        };
        RuleNode child2 = new RuleNode(childParams2, rule2);

        RuleNode node = paramsProvider.ruleTree.getRoot();
        node.addChild(child);
        node.addChild(child2);

        FMapRenderer R = new StandardAMapRenderer().setRenderSysMeta(true);
        NonTerminal M = X.extract();
        System.out.println(R.render(null, M));

//        LocalScrutinizationExtractor X2 = new LocalScrutinizationExtractor();
//        X2.params = paramsProvider;
//        NonTerminal M2 = X2.extract();
//        System.out.println(R.render(null, M2));

    }
}
