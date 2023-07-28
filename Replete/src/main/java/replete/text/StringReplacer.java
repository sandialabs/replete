package replete.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringReplacer {


    ////////////
    // FIELDS //
    ////////////

    Pattern p;
    Replacer replacer;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StringReplacer(String pattern, Replacer replacer) {
        p = Pattern.compile(pattern);
        this.replacer = replacer;
    }


    /////////////
    // REPLACE //
    /////////////

    public String replace(String str) {
        StringBuilder buffer = new StringBuilder(str.length() * 2);
        Matcher m = p.matcher(str);
        int prevStart = 0;
        while(m.find()) {
            buffer.append(str.substring(prevStart, m.start()));
            String[] captureGroups = new String[m.groupCount()];
            for(int i = 0; i < m.groupCount(); i++) {
                captureGroups[i] = m.group(i + 1);
            }
            String replaced = replacer.replace(m.group(), captureGroups);
            buffer.append(replaced);
            prevStart = m.end();
        }
        buffer.append(str.substring(prevStart, str.length()));
        return buffer.toString();
    }
}
