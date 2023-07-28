package finio.extractors.jo.rules;

import java.io.Serializable;

import finio.core.KeyPath;
import finio.extractors.jo.PopulateParams;

public class RuleTree implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private RuleNode root;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    // No default parameter objects' fields should be null.
    public RuleTree(PopulateParams defaultParams) {
        root = new RuleNode(defaultParams, new AllRule());
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public RuleNode getRoot() {
        return root;
    }

    // Computed

    public PopulateParams getParameters(Object O, KeyPath P) {
        PopulateParams params = new PopulateParams();
        root.appendParams(O, P, params);
        return params;
    }
}
