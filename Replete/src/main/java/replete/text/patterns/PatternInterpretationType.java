package replete.text.patterns;

public enum PatternInterpretationType {
    LITERAL            ("Literal",             "LT",  "#", true,  true),
    WILDCARDS          ("Wildcards",           "WC",  "*", false, true),
    REGEX              ("Regular Expressions", "RE",  "~", false, false),
    HIER_LEFT_TO_RIGHT ("L->R Hierarchical",   "HLR", "<", false, false, PatternInterpretation.DEFAULT_HIER_DELIM),
    HIER_RIGHT_TO_LEFT ("R->L Hierarchical",   "HRL", ">", false, false, PatternInterpretation.DEFAULT_HIER_DELIM);

    // HIER_LEFT_TO_RIGHT - Special usage of wildcards
    //   Example:  parent.child.grandchild (with . as level delimiter)
    //   Good For: IP Addresses, File Paths
    // HIER_RIGHT_TO_LEFT - Special usage of wildcards
    //   Example:  grandchild.child.parent (with . as level delimiter)
    //   Good For: Domain Names

    // A special "type" called "DEFAULT", indicated by "D", can be used instead
    // of these within the pattern interpretation tag to request that the software
    // use whatever the software was using by default, but still have a chance to
    // change some of the other properties. See PatternUtil.parsePatternInterpretation().


    ////////////
    // FIELDS //
    ////////////

    public static final String DEFAULT_SHORT_LABEL = "D";
    private String label;
    private String shortLabel;
    private String flag;
    private boolean defaultCaseSensitive;
    private boolean defaultWholeMatch;
    private String defaultHierDelimiter;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    private PatternInterpretationType(String label, String shortLabel, String flag,
                                      boolean defaultCaseSensitive, boolean defaultWholeMatch) {
        this(label, shortLabel, flag, defaultCaseSensitive, defaultWholeMatch, null);
    }
    private PatternInterpretationType(String label, String shortLabel, String flag,
                                  boolean defaultCaseSensitive, boolean defaultWholeMatch,
                                  String defaultHierDelimiter) {
        this.flag = flag;
        this.shortLabel = shortLabel;
        this.label = label;
        this.defaultCaseSensitive = defaultCaseSensitive;
        this.defaultWholeMatch = defaultWholeMatch;
        this.defaultHierDelimiter = defaultHierDelimiter;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getLabel() {
        return label;
    }
    public String getShortLabel() {
        return shortLabel;
    }
    public String getFlag() {
        return flag;
    }
    public boolean isDefaultCaseSensitive() {
        return defaultCaseSensitive;
    }
    public boolean isDefaultWholeMatch() {
        return defaultWholeMatch;
    }
    public String getDefaultHierDelimiter() {
        return defaultHierDelimiter;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return label;
    }


    //////////
    // MISC //
    //////////

    public static String createPipedList() {
        StringBuilder buffer = new StringBuilder();
        for(PatternInterpretationType type : PatternInterpretationType.values()) {
            buffer.append(type.getShortLabel());
            buffer.append("|");
        }
        buffer.append(DEFAULT_SHORT_LABEL);
        return buffer.toString();
    }
}
