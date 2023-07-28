package finio.core.impl;

import finio.core.NonTerminal;

public class WrapperNonTerminal extends AbstractWrapperNonTerminal {


    ////////////
    // FIELDS //
    ////////////

    private NonTerminal M;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WrapperNonTerminal(NonTerminal M) {
        this.M = M;
        subscribe();
    }


    //////////////
    // ACCESSOR //
    //////////////

    @Override
    protected NonTerminal getM() {
        return M;
    }
}
