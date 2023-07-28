package finio.core;

import static finio.core.impl.FMap.A;

import finio.core.impl.FMap;
import replete.text.StringUtil;

public class Util {

    // Could be a StringUtil method except for depending on AMaps.
    public static String sideBySide(String... strs) {
        FMap maxLengths = A();
        FMap linesMap = A();
        int maxNumLines = -1;
        for(String str : strs) {
            String[] lines = str.split("\n");
            int maxLength = -1;
            for(String line : lines) {
                if(line.length() > maxLength) {
                    maxLength = line.length();
                }
            }
            maxLengths.put(str, maxLength);
            linesMap.put(str, lines);
            if(lines.length > maxNumLines) {
                maxNumLines = lines.length;
            }
        }
        StringBuilder buffer = new StringBuilder();
        for(int l = 0; l < maxNumLines; l++) {
            for(String str : strs) {
                String[] lines = (String[]) linesMap.get(str);
                String line;
                if(l < lines.length) {
                    line = lines[l];
                } else {
                    line = "";
                }
                String padding = StringUtil.spaces((Integer) maxLengths.get(str) - line.length() + 5);
                buffer.append(line + padding);
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }
}
