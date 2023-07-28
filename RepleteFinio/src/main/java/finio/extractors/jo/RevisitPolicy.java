package finio.extractors.jo;

public enum RevisitPolicy {
    NO_RESTRICTION("No Restriction"),                          // Could result in infinite recursion without depth limit
    NO_DUP_PATH("No Duplicates Along Path"),                   // Basic recursion detection
    NO_DUP_GLOBAL("No Duplicates Across Entire Expansion");    // More restrictive than basic recursion detection,
                                                               // won't populate same object even on different paths

    private String label;
    RevisitPolicy(String label) {
        this.label = label;
    }
    @Override
    public String toString() {
        return label;
    }
}
