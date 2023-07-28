package replete.diff;

// Analogous to Validation framework's MessageLevel enum
// though there are slightly different semantics.  We
// can't necessarily think of differences as "errors"
// or validation problems but we can still attempt to
// encode whether the diffing algorithm perceives a
// change to be of a large importance to the fact that
// two objects are in fact "different" - as that term
// is HIGHLY subjective and relative to the analyst
// and context.  The Importance enum will give just
// a little more control over that subjectivity.

public enum Importance {
    HIGH,        // Analogous to "ERROR"
    MEDIUM,      // Analogous to "WARNING"
    LOW          // Analogous to "INFO"
}
