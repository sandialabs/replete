package replete.scripting.rscript.parser;

import replete.scripting.rscript.parser.gen.ParseException;
import replete.scripting.rscript.parser.gen.RScriptParserGenerated;

// This class exists as a wrapper around the
// JavaCC-generated parser for consistency and
// future configurability.
public class RScriptParser {
    public RScript parse(String source) throws ParseException {
        return RScriptParserGenerated.parse(source);
    }
}
