
package replete.logging;

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;

public class ShortLevelPatternConverter extends PatternConverter {

    public ShortLevelPatternConverter(FormattingInfo formattingInfo) {
        super(formattingInfo);
    }

    @Override
    public String convert(LoggingEvent event) {
        return event.getLevel().toString().substring(0, 1);
    }
}
