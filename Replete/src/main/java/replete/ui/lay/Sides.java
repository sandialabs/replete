package replete.ui.lay;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sides {
    public int top;
    public int left;
    public int bottom;
    public int right;

    public Sides(String code) {
        String patStr = "\\s*([0-9]+)([tlbr]*)\\s*";
        if(!code.matches("(?:" + patStr + ")+")) {
            throw new IllegalArgumentException("Invalid empty border code: " + code);
        }
        Pattern p = Pattern.compile(patStr);
        Matcher m = p.matcher(code.toLowerCase());
        while(m.find()) {
            String thick = m.group(1);
            int width = Integer.parseInt(thick);
            String sides = m.group(2);
            if(sides.length() == 0) {
                top = left = bottom = right = width;
            } else {
                for(int c = 0; c < sides.length(); c++) {
                    switch(sides.charAt(c)) {
                        case 't': top = width;    break;
                        case 'l': left = width;   break;
                        case 'b': bottom = width; break;
                        case 'r': right = width;  break;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "top=" + top + ",left=" + left + ",bottom=" + bottom + ",right=" + right;
    }
}
