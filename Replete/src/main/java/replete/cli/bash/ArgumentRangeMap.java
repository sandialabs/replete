package replete.cli.bash;

import java.util.LinkedHashMap;

import replete.text.StringUtil;

// This class exists merely to provide a nice toString for JUnit testing.
public class ArgumentRangeMap extends LinkedHashMap<Integer, ArgumentRange> {
    @Override
    public String toString() {
        String result = "";

        for(Integer key : keySet()) {
            ArgumentRange range = get(key);
            result += key + "=" + range.toSimpleString() + ";";
        }
        result = StringUtil.cut(result, 1);
        return result;
    }
}
