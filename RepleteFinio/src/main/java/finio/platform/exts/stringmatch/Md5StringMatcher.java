package finio.platform.exts.stringmatch;

import finio.plugins.extpoints.StringMatchResult;
import finio.plugins.extpoints.StringPatternMatcher;

public class Md5StringMatcher implements StringPatternMatcher {

    @Override
    public String getName() {
        return "MD5";
    }

    @Override
    public StringMatchResult match(String str) {
        if(str.matches("^[a-f0-9]{32}$")) {
            return new StringMatchResult(1.0, null);
        }
        return null;
    }

}
