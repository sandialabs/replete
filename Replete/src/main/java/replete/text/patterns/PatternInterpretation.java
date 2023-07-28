package replete.text.patterns;

import java.io.Serializable;

import replete.text.StringUtil;

public class PatternInterpretation implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    public static final String DEFAULT_HIER_DELIM = ".";
    private PatternInterpretationType type = PatternInterpretationType.LITERAL;
    private Boolean caseSensitive = false;
    private Boolean wholeMatch = false;       // Considered "loose interpretation" when false
    private String hierarchicalDelim = null;
    // ^Only applicable for HIER_LEFT_TO_RIGHT and HIER_RIGHT_TO_LEFT types.  Can be
    //  non-null if type is not one of these, but will just have no effect.

    // The fields caseSensitive and wholeMatch are triple state
    // (Boolean instead of boolean) because this object can also
    // be used as a "default interpretation" when an interpretation
    // is not provided by the user.  If these fields are null
    // (including hierarchicalDelim) then at that point the
    // values are pulled from the type's defaults).  The type
    // field, however, can never be null, because we haven't
    // invented (nor would we want to) a global type default
    // for all our apps.


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PatternInterpretation() {
        // Use mutators
    }
    public PatternInterpretation(PatternInterpretationType type, Boolean caseSensitive,
                                 Boolean wholeMatch, String hierarchicalDelim) {
        this.type = type;
        this.caseSensitive = caseSensitive;
        this.wholeMatch = wholeMatch;
        this.hierarchicalDelim = hierarchicalDelim;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public PatternInterpretationType getType() {
        return type;
    }
    public Boolean isCaseSensitive() {
        return caseSensitive;
    }
    public Boolean isWholeMatch() {
        return wholeMatch;
    }
    public String getHierarchicalDelim() {
        return hierarchicalDelim;
    }

    // Accessors (Computed)

    public String toNiceString() {
        return
            type +
            (caseSensitive ? " +CS" : "") +
            (wholeMatch ? " +WM" : "") +
            (!StringUtil.isBlank(hierarchicalDelim) &&
                (type == PatternInterpretationType.HIER_LEFT_TO_RIGHT ||
                    type == PatternInterpretationType.HIER_RIGHT_TO_LEFT) ? " HD=" + hierarchicalDelim : ""
            )
        ;
    }

    // Mutators

    public PatternInterpretation setType(PatternInterpretationType type) {
        this.type = type;
        return this;
    }
    public PatternInterpretation setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        return this;
    }
    public PatternInterpretation setWholeMatch(Boolean wholeMatch) {
        this.wholeMatch = wholeMatch;
        return this;
    }
    public PatternInterpretation setHierarchicalDelim(String hierarchicalDelim) {
        this.hierarchicalDelim = hierarchicalDelim;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((caseSensitive == null) ? 0 : caseSensitive.hashCode());
        result = prime * result + ((hierarchicalDelim == null) ? 0 : hierarchicalDelim.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((wholeMatch == null) ? 0 : wholeMatch.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        PatternInterpretation other = (PatternInterpretation) obj;
        if(caseSensitive == null) {
            if(other.caseSensitive != null) {
                return false;
            }
        } else if(!caseSensitive.equals(other.caseSensitive)) {
            return false;
        }
        if(hierarchicalDelim == null) {
            if(other.hierarchicalDelim != null) {
                return false;
            }
        } else if(!hierarchicalDelim.equals(other.hierarchicalDelim)) {
            return false;
        }
        if(type != other.type) {
            return false;
        }
        if(wholeMatch == null) {
            if(other.wholeMatch != null) {
                return false;
            }
        } else if(!wholeMatch.equals(other.wholeMatch)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[" + type + " CS=" + caseSensitive + " WM=" + wholeMatch + " HD=" + hierarchicalDelim + "]";
    }
}
