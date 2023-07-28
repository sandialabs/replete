package replete.params.hier;

import java.util.Set;

// This exception occurs when the property tree resolution did not produce
// all of the required parameters.  To fix, the user must ensure all possible
// resolution paths provide all parameter slots.  The easy fix is to ensure
// that root node provides all 'default' parameter slots.

public class PropertySetUnsatisfiedSpecException extends PropertySetResolutionException {


    ////////////
    // FIELDS //
    ////////////

    private Set<String> missingKeys;
    private Set<String> extraKeys;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PropertySetUnsatisfiedSpecException(Set<String> missingKeys, Set<String> extraKeys) {
        this.missingKeys = missingKeys;
        this.extraKeys = extraKeys;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Set<String> getMissingKeys() {
        return missingKeys;
    }
    public Set<String> getExtraKeys() {
        return extraKeys;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getMessage() {
        return "missing=" + missingKeys + ", extra=" + extraKeys;
    }
}
