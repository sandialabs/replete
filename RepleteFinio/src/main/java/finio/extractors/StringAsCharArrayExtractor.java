package finio.extractors;

import finio.core.FUtil;
import finio.core.NonTerminal;

public class StringAsCharArrayExtractor extends NonTerminalExtractor {


    ///////////
    // FIELD //
    ///////////

    private String V;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public StringAsCharArrayExtractor(String V) {
        this.V = V;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public NonTerminal extractInner() {
        NonTerminal M = createBlankNonTerminal();
        for(int i = 0; i < V.length(); i++) {
            M.put(i, V.charAt(i));
        }
        FUtil.recordJavaSource(M, V);
        return M;
    }

    @Override
    protected String getName() {
        return "String As Character Array Extractor";
    }
}
