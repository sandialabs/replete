package finio.extractors.jo;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.core.it.KeyValueIterator;
import finio.extractors.jo.rules.RuleTree;

public class PopulateParamsProvider {


    ////////////
    // FIELDS //
    ////////////

    public RuleTree ruleTree;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PopulateParamsProvider() {
        this(null);
    }
    public PopulateParamsProvider(PopulateParams params) {
        if(params == null) {
            params = new ReflectionDefaultPopulateParamsCreator().create();
        }
        ruleTree = new RuleTree(params);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public RuleTree getRuleTree() {
        return ruleTree;
    }

    // Computed

    public PopulateParams getDefaultParameters() {
        return ruleTree.getRoot().getParams();
    }
    // NOTE: Populate Parameters Change Area - have to update this if a parameter is added.
    public NonTerminal getHostNonTerminal(Object O, KeyPath P) {
        PopulateParams p = ruleTree.getParameters(O, P);
        return p.getHostNonTerminalCreator().create(O, P);
    }
    public KeyValueIterator getKeyValueIterator(Object O, KeyPath P) {
        PopulateParams p = ruleTree.getParameters(O, P);
        return p.getKeyValueIteratorCreator().create(O, P);
    }
    public KeyValueRegistrar getKeyValueRegistrar(Object O, KeyPath P) {
        PopulateParams p = ruleTree.getParameters(O, P);
        return p.getKeyValueRegistrar();
    }
    public PostFieldsModifier getPostFieldsModifier(Object O, KeyPath P) {
        PopulateParams p = ruleTree.getParameters(O, P);
        return p.getPostFieldsModifier();
    }
    public boolean isRecordJavaSource(Object O, KeyPath P) {
        PopulateParams p = ruleTree.getParameters(O, P);
        return p.isRecordJavaSource();
    }
    public Object resolveKey(Object O, KeyPath P, Object K, Object V) {
        PopulateParams p = ruleTree.getParameters(O, P);
        ObjectResolver resolver = p.getKeyResolver();
        if(resolver.canHandle(K)) {
            return resolver.resolve(K);
        }
        return K;
    }
    public Object resolveValue(Object O, KeyPath P, Object K, Object V) {
        PopulateParams p = ruleTree.getParameters(O, P);
        ObjectResolver resolver = p.getValueResolver();
        if(resolver.canHandle(V)) {
            return resolver.resolve(V);
        }
        return V;
    }
    public boolean shouldExpandAsNonTerminal(Object O, KeyPath P, Object V) {
        PopulateParams p = ruleTree.getParameters(O, P);
        return p.getNonTerminalExpansionDecider().shouldExpandNonTerminal(V);
    }
    public RevisitPolicy getRevisitPolicy(Object O, KeyPath P) {
        PopulateParams p = ruleTree.getParameters(O, P);
        return p.getRevisitPolicy();
    }


    //////////
    // MISC //
    //////////

    public void checkDefaults() {
        getDefaultParameters().check();
    }
}
