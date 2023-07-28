package finio.core;

// Contains some basic constants used by NonTerminal framework.

public class FConst {
    public static final String SYS_KV_SPECIAL   = "$$";
    public static final String SYS_KV_SPECIAL_2 = "$#";                 // For the @@display field only right now...

    public static final String SYS_PREFIX     = "@@";                   // This might need to be even more complicated.
    public static final String SYS_META_KEY   = SYS_PREFIX + "meta";    // To describe the non-terminal
    public static final String SYS_VALUE_KEY  = SYS_PREFIX + "value";   // Core of a semi-terminal
    public static final String SYS_ALT_KEY    = SYS_PREFIX + "alt";     // An alternates map as a value to some key
    public static final String SYS_IMAGES     = SYS_PREFIX + "images";  // Image set for the map
    public static final String SYS_CHILD_META = SYS_PREFIX + "cmeta";   // Thought in progress
    public static final String SYS_DISPLAY    = SYS_PREFIX + "display"; // Choosing text to display for a non-terminal
    
    public static final String SYS_INHERENT_ORDERING = "$$InitialInherentOrdering";

    public static final String JAVA_CLASS_KEY = "java-class";
    public static final String JAVA_HASH_KEY  = "java-hash";
    public static final String JAVA_IHASH_KEY = "java-ident-hash";
    public static final String JAVA_REF_KEY   = "java-orig-obj-ref";
}
