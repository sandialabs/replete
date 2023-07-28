package finio.renderers.path;

import finio.core.KeyPath;
import finio.core.syntax.KeyPathSyntax;
import finio.core.syntax.KeyPathSyntaxLibrary;

public class StandardKeyPathRenderer implements KeyPathRenderer {


    ////////////
    // FIELDS //
    ////////////

    private static KeyPathSyntax defaultSyntax = KeyPathSyntaxLibrary.getSyntax("Unix File System Path");
    private KeyPathSyntax syntax;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StandardKeyPathRenderer() {
        this(null);
    }
    public StandardKeyPathRenderer(KeyPathSyntax syntax) {
        if(syntax == null) {
            syntax = defaultSyntax;
        }
        this.syntax = syntax;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    public String render(KeyPath P) {
        StringBuilder buffer = new StringBuilder();
        int s = 0;
        buffer.append(syntax.getPathOpen());
        for(Object segment : P) {
            buffer.append(syntax.getKeyOpen());
            String segTS = segment.toString();
            buffer.append(segTS);                      // This could technically be an ADT/map, etc and screw up string
            //if(segTS.contains(/*any of these special chars*/)) {
                // set a warning flag!
            //}
            buffer.append(syntax.getKeyClose());
            if(s != P.size() - 1) {
                buffer.append(syntax.getSegSepOpen());
                buffer.append(syntax.getSegSep());
                buffer.append(syntax.getSegSepClose());
            }
            s++;
        }
        buffer.append(syntax.getPathClose());
        return buffer.toString();
    }


    //////////
    // MISC //
    //////////

    public static void setDefaultSyntax(KeyPathSyntax syntax) {
        defaultSyntax = syntax;
    }
}
