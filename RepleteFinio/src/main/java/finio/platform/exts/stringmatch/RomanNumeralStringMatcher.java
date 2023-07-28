package finio.platform.exts.stringmatch;

import finio.plugins.extpoints.StringMatchResult;
import finio.plugins.extpoints.StringPatternMatcher;

public class RomanNumeralStringMatcher implements StringPatternMatcher {

    @Override
    public String getName() {
        return "Roman Numeral";
    }

    @Override
    public StringMatchResult match(String str) {
        if(str.matches("^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$")) {
            return new StringMatchResult(1.0, null);  // Value someday
        } else if(str.matches("^[IVXLCDM]+$")) {
            return new StringMatchResult(0.7, null);
        }
        return null;
    }

}
