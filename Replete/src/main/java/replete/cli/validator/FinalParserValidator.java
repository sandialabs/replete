package replete.cli.validator;

import replete.cli.CommandLineParser;

/**
 * @author Derek Trumbo
 */

public interface FinalParserValidator {
    String validate(CommandLineParser parser);
}
