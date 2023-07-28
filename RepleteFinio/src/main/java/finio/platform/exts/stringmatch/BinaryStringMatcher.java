package finio.platform.exts.stringmatch;

import finio.core.NonTerminal;
import finio.core.impl.FMap;
import finio.plugins.extpoints.StringMatchResult;
import finio.plugins.extpoints.StringPatternMatcher;

public class BinaryStringMatcher implements StringPatternMatcher {

    @Override
    public String getName() {
        return "Binary Number";
    }

    @Override
    public StringMatchResult match(String str) {
        if(str.matches("^[01]+$")) {
            NonTerminal Mdetails = FMap.A();
            Mdetails.put("numericValueTwosComplement", 777);    // TODO
            return new StringMatchResult(1.0, Mdetails);
        }
        return null;
    }

}
