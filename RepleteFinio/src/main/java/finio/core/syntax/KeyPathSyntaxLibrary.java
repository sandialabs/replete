package finio.core.syntax;

import java.util.LinkedHashMap;
import java.util.Map;

public class KeyPathSyntaxLibrary {
    private static Map<String, KeyPathSyntax> kpSyntaxes = new LinkedHashMap<String, KeyPathSyntax>();

    static {
       kpSyntaxes.put("Java List ToString",
            new KeyPathSyntax()
                .setPathOpen("[")
                .setPathClose("]")
                .setKeyOpen("\"")
                .setKeyClose("\"")
                .setSegSep(",")
                .setSegSepOpen("")
                .setSegSepClose("")
                .setEscape("$"));
       kpSyntaxes.put("INI File",
           new KeyPathSyntax()
               .setPathOpen("")
               .setPathClose("")
               .setKeyOpen("")
               .setKeyClose("")
               .setSegSep(".")
               .setSegSepOpen("")
               .setSegSepClose("")
               .setEscape("$"));
       kpSyntaxes.put("Windows File System Path",
           new KeyPathSyntax()
               .setPathOpen("")
               .setPathClose("")
               .setKeyOpen("")
               .setKeyClose("")
               .setSegSep("\\")
               .setSegSepOpen("")
               .setSegSepClose("")
               .setEscape("$"));
       kpSyntaxes.put("Unix File System Path",
           new KeyPathSyntax()
               .setPathOpen("")
               .setPathClose("")
               .setKeyOpen("")
               .setKeyClose("")
               .setSegSep("/")
               .setSegSepOpen("")
               .setSegSepClose("")
               .setEscape("$"));
    }

    public static Map<String, KeyPathSyntax> getSyntaxes() {
        return kpSyntaxes;
    }
    public static KeyPathSyntax getSyntax(String name) {
        return kpSyntaxes.get(name);
    }
}
