package finio.core.syntax;


public class FMapSyntax {


    ////////////
    // FIELDS //
    ////////////

    // Constant

    public static final String DEFAULT_PATTERN =
        "${MO}$(${PO}${KO}${K}${KC}${AO}${A}${AC}${VO}${V}${VC}${PC}$)${MC}";

    // Core

    private String pattern = DEFAULT_PATTERN;
    private String mapOpen;
    private String mapClose;
    private String listOpen;
    private String listClose;
    private String pairOpen;
    private String pairClose1;
    private String pairClose2;
    private String keyOpen;
    private String keyClose;
    private String valueOpen;
    private String valueClose;
    private String assign;
    private String assignOpen;
    private String assignClose;
    private String escape;
    private boolean userDefined;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FMapSyntax() {
        userDefined = true;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getPattern() {
        return pattern;
    }
    public String getMapOpen() {
        return mapOpen;
    }
    public String getMapClose() {
        return mapClose;
    }
    public String getListOpen() {
        return listOpen;
    }
    public String getListClose() {
        return listClose;
    }
    public String getPairOpen() {
        return pairOpen;
    }
    public String getPairClose1() {
        return pairClose1;
    }
    public String getPairClose2() {
        return pairClose2;
    }
    public String getKeyOpen() {
        return keyOpen;
    }
    public String getKeyClose() {
        return keyClose;
    }
    public String getValueOpen() {
        return valueOpen;
    }
    public String getValueClose() {
        return valueClose;
    }
    public String getAssign() {
        return assign;
    }
    public String getAssignOpen() {
        return assignOpen;
    }
    public String getAssignClose() {
        return assignClose;
    }
    public String getEscape() {
        return escape;
    }
    public boolean isUserDefined() {
        return userDefined;
    }

    // Mutators

    public FMapSyntax setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }
    public FMapSyntax setMapOpen(String mapOpen) {
        this.mapOpen = mapOpen;
        return this;
    }
    public FMapSyntax setMapClose(String mapClose) {
        this.mapClose = mapClose;
        return this;
    }
    public FMapSyntax setListOpen(String listOpen) {
        this.listOpen = listOpen;
        return this;
    }
    public FMapSyntax setListClose(String listClose) {
        this.listClose = listClose;
        return this;
    }
    public FMapSyntax setPairOpen(String pairOpen) {
        this.pairOpen = pairOpen;
        return this;
    }
    public FMapSyntax setPairClose1(String pairClose1) {
        this.pairClose1 = pairClose1;
        return this;
    }
    public FMapSyntax setPairClose2(String pairClose2) {
        this.pairClose2 = pairClose2;
        return this;
    }
    public FMapSyntax setKeyOpen(String keyOpen) {
        this.keyOpen = keyOpen;
        return this;
    }
    public FMapSyntax setKeyClose(String keyClose) {
        this.keyClose = keyClose;
        return this;
    }
    public FMapSyntax setValueOpen(String valueOpen) {
        this.valueOpen = valueOpen;
        return this;
    }
    public FMapSyntax setValueClose(String valueClose) {
        this.valueClose = valueClose;
        return this;
    }
    public FMapSyntax setAssign(String assign) {
        this.assign = assign;
        return this;
    }
    public FMapSyntax setAssignOpen(String assignOpen) {
        this.assignOpen = assignOpen;
        return this;
    }
    public FMapSyntax setAssignClose(String assignClose) {
        this.assignClose = assignClose;
        return this;
    }
    public FMapSyntax setEscape(String escape) {
        this.escape = escape;
        return this;
    }
}

