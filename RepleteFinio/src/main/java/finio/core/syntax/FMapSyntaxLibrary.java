package finio.core.syntax;

import java.util.LinkedHashMap;
import java.util.Map;

public class FMapSyntaxLibrary {
    private static Map<String, FMapSyntax> mapSyntaxes = new LinkedHashMap<String, FMapSyntax>();

    static {
        mapSyntaxes.put("JSON",
            new FMapSyntax()
                .setMapOpen("{\n")
                .setMapClose("$I}")
                .setListOpen("[")
                .setListClose("]")
                .setPairOpen("$I")
                .setPairClose1(",")
                .setPairClose2("\n")
                .setKeyOpen("\"")
                .setKeyClose("\"")
                .setValueOpen("\"")
                .setValueClose("\"")
                .setAssign(":")
                .setAssignOpen("")
                .setAssignClose(" ")
                .setEscape("$"));
        mapSyntaxes.put("Indent Only",
            new FMapSyntax()
                .setMapOpen("\n")
                .setMapClose("")
                .setListOpen("[")
                .setListClose("]")
                .setPairOpen("$I")
                .setPairClose1("")
                .setPairClose2("\n")
                .setKeyOpen("")
                .setKeyClose("")
                .setValueOpen("\"")
                .setValueClose("\"")
                .setAssign("=")
                .setAssignOpen(" ")
                .setAssignClose(" ")
                .setEscape("$"));
        mapSyntaxes.put("XML",
            new FMapSyntax()
                .setMapOpen("<ELEM ")
                .setMapClose("</ELEM>")
                .setListOpen("[")
                .setListClose("]")
                .setPairOpen("")
                .setPairClose1(" ")
                .setPairClose2("")
                .setKeyOpen("")
                .setKeyClose("")
                .setValueOpen("\"")
                .setValueClose("\"")
                .setAssign("=")
                .setAssignOpen("")
                .setAssignClose("")
                .setEscape("$"));
        mapSyntaxes.put("Java Map ToString",
            new FMapSyntax()
                .setMapOpen("{")
                .setMapClose("}")
                .setListOpen("[")
                .setListClose("]")
                .setPairOpen("")
                .setPairClose1(", ")
                .setPairClose2("")
                .setKeyOpen("")
                .setKeyClose("")
                .setValueOpen("")
                .setValueClose("")
                .setAssign("=")
                .setAssignOpen("")
                .setAssignClose("")
                .setEscape("$"));
        mapSyntaxes.put("Finio",
            new FMapSyntax()
                .setMapOpen("{\n")
                .setMapClose("$I}")
                .setListOpen("[")
                .setListClose("]")
                .setPairOpen("$I")
                .setPairClose1("")
                .setPairClose2("\n")
                .setKeyOpen("")
                .setKeyClose("")
                .setValueOpen("\"")
                .setValueClose("\"")
                .setAssign("=")
                .setAssignOpen(" ")
                .setAssignClose(" ")
                .setEscape("$"));
    }

    public static Map<String, FMapSyntax> getSyntaxes() {
        return mapSyntaxes;
    }
    public static FMapSyntax getSyntax(String name) {
        return mapSyntaxes.get(name);
    }
}
