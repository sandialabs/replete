package finio.platform.exts.stringmatch;

import finio.plugins.extpoints.StringMatchResult;
import finio.plugins.extpoints.StringPatternMatcher;
import replete.numbers.NumUtil;

public class IpV4StringMatcher implements StringPatternMatcher {

    @Override
    public String getName() {
        return "IPv4";
    }

    @Override
    public StringMatchResult match(String str) {
        if(str.matches("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")) {
            String[] octets = str.split("\\.");
            if(octets.length == 4) {
                boolean all = true;
                for(String octet : octets) {
                    try {
                        if(NumUtil.isInt(octet)) {
                            int i = NumUtil.i(octet);
                            if(i < 0 || i >= 256) {
                                all = false;
                            }
                        }
                    } catch(Exception e) {
                        all = false;
                    }
                }
                if(all) {
                    return new StringMatchResult(1.0, null);
                }
            }
        }
        return null;
    }

}
