package replete.logging;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;

public class ShortLevelPatternParser extends PatternParser {

    public ShortLevelPatternParser(String pattern) {
        super(pattern);
    }

    @Override
    protected void finalizeConverter(char c) {
        if(c == 'p') {
            PatternConverter pc =
                new ShortLevelPatternConverter(formattingInfo);
            currentLiteral.setLength(0);
            addConverter(pc);
        } else {
            super.finalizeConverter(c);
        }
    }

}
