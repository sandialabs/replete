package replete.logging;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternParser;

public class ShortLevelPatternLayout extends PatternLayout {

    public ShortLevelPatternLayout() {}

    public ShortLevelPatternLayout(String pattern) {
        super(pattern);
    }

    @Override
    protected PatternParser createPatternParser(String pattern) {
        return new ShortLevelPatternParser(pattern);
    }

}
