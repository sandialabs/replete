package finio.core.syntax;

public class KeyPathSyntax {


    ////////////
    // FIELDS //
    ////////////

    private String pathOpen;
    private String pathClose;
    private String keyOpen;
    private String keyClose;
    private String segSep;
    private String segSepOpen;
    private String segSepClose;
    private String escape;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors
    public String getPathOpen() {
        return pathOpen;
    }
    public String getPathClose() {
        return pathClose;
    }
    public String getKeyOpen() {
        return keyOpen;
    }
    public String getKeyClose() {
        return keyClose;
    }
    public String getSegSep() {
        return segSep;
    }
    public String getSegSepOpen() {
        return segSepOpen;
    }
    public String getSegSepClose() {
        return segSepClose;
    }
    public String getEscape() {
        return escape;
    }

    // Mutators

    public KeyPathSyntax setPathOpen(String pathOpen) {
        this.pathOpen = pathOpen;
        return this;
    }
    public KeyPathSyntax setPathClose(String pathClose) {
        this.pathClose = pathClose;
        return this;
    }
    public KeyPathSyntax setKeyOpen(String keyOpen) {
        this.keyOpen = keyOpen;
        return this;
    }
    public KeyPathSyntax setKeyClose(String keyClose) {
        this.keyClose = keyClose;
        return this;
    }
    public KeyPathSyntax setSegSep(String segSep) {
        this.segSep = segSep;
        return this;
    }
    public KeyPathSyntax setSegSepOpen(String segSepOpen) {
        this.segSepOpen = segSepOpen;
        return this;
    }
    public KeyPathSyntax setSegSepClose(String segSepClose) {
        this.segSepClose = segSepClose;
        return this;
    }
    public KeyPathSyntax setEscape(String escape) {
        this.escape = escape;
        return this;
    }
}
