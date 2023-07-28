package replete.ui.lay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LayUtil {
    public static String[] parseFunctionCall(String value) {
        String clrPat = "([a-zA-Z0-9]+)\\(([a-zA-Z0-9#]+)(?:,([a-zA-Z0-9#]+|\\[[^\\]]*\\]))?\\)";
        Pattern p = Pattern.compile(clrPat);
        Matcher m = p.matcher(value);
        if(m.matches()) {
            return new String[] {m.group(1), m.group(2), m.group(3)};
        }
        return new String[0];
    }
    public static int[] parseNumberList(String value) {
        String numberListPattern =
            "^\\[?\\s*([0-9]+)(?:\\s*,\\s*([0-9]+))?(?:\\s*,\\s*([0-9]+))?(?:\\s*,\\s*([0-9]+))?(?:\\s*,\\s*([0-9]+))?\\s*\\]?$";
        Pattern p = Pattern.compile(numberListPattern);
        Matcher m = p.matcher(value);
        List<Integer> numList = new ArrayList<>();
        if(m.find()) {
            for(int i = 1; i <= m.groupCount(); i++) {
                String grp = m.group(i);
                if(grp != null) {
                    numList.add(Integer.parseInt(grp));
                }
            }
        }
        int[] nums = new int[numList.size()];
        int i = 0;
        for(Integer num : numList) {
            nums[i++] = num;
        }
        return nums;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(parseNumberList("[20,30,40]")));
    }
}
