package finio.extractors.jo.rules;

import finio.core.KeyPath;


// Catch-all rule to be used for root rule nodes, for example.

public class AllRule extends Rule {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean appliesTo(Object O, KeyPath P) {
        return true;
    }
}
