package finio.platform.exts.stringmatch;

import java.net.URL;

import finio.core.NonTerminal;
import finio.core.impl.FMap;
import finio.plugins.extpoints.StringMatchResult;
import finio.plugins.extpoints.StringPatternMatcher;
import gov.sandia.webcomms.http.util.UriCleaner;

public class UrlStringMatcher implements StringPatternMatcher {

    @Override
    public String getName() {
        return "URL";
    }

    @Override
    public StringMatchResult match(String str) {
        try {
            String cleanedUrl = UriCleaner.clean(str);
            NonTerminal Mdetails = FMap.A();
            Mdetails.put("url", new URL(cleanedUrl));
            return new StringMatchResult(1.0, Mdetails);
        } catch(Exception e) {

        }
        return null;
    }

}
