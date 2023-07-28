package replete.ui.params.hier.test;

import java.io.Serializable;

import replete.text.patterns.PatternInterpretation;
import replete.text.patterns.PatternInterpretationType;


public abstract class Rule implements Serializable {

    public static final PatternInterpretation DEFAULT_PATTERN_INTERPRETATION = new PatternInterpretation()
        .setType(PatternInterpretationType.WILDCARDS)
        .setCaseSensitive(false)
        .setWholeMatch(false)
    ;

    public abstract boolean appliesTo(String url);
}
