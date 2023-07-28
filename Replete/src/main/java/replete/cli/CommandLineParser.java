package replete.cli;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import replete.cli.argfile.ArgFileConfig;
import replete.cli.argfile.ArgFileParser;
import replete.cli.errors.CommandLineParseException;
import replete.cli.errors.FinalParserValidationException;
import replete.cli.errors.NonOptionCountException;
import replete.cli.errors.OptionAlreadyExistsException;
import replete.cli.errors.OptionNotRelatedException;
import replete.cli.errors.RequiredOptionException;
import replete.cli.errors.ResultsNotParsedException;
import replete.cli.errors.TooManyValuesForOptionException;
import replete.cli.errors.UnknownOptionException;
import replete.cli.errors.UnknownSuboptionException;
import replete.cli.errors.UserRequestedHelpException;
import replete.cli.errors.ValueRequiredSuboptionException;
import replete.cli.errors.XArgsException;
import replete.cli.options.BooleanOption;
import replete.cli.options.DoubleOption;
import replete.cli.options.FloatOption;
import replete.cli.options.IntegerOption;
import replete.cli.options.LongOption;
import replete.cli.options.Option;
import replete.cli.options.ParseContext;
import replete.cli.options.PathOption;
import replete.cli.options.StringOption;
import replete.cli.validator.FinalParserValidator;
import replete.collections.Pair;
import replete.io.FileUtil;
import replete.text.StringUtil;


/**
 * A highly-GNU compatible command-line options parser.  This parser
 * was based off of the JArgs parser but contains more features
 * and is easier to use.
 *   - More consistent & complete error classes and messages.
 *   - Allows '--bool-opt=false' to imply false instead of true.
 *     (supports 'true' and 'false' for boolean options when the
 *     equals sign (long form) is used)
 *   - Simpler getOptionValue(s) methods so state isn't changed.
 *   - No locale support.
 *   - Allows short form options without long forms.
 *   - Has getOptionValues return null when an option was not specified.
 *     (instead of an empty vector or array).
 *   - Contains getOptionValuesMap.
 *   - Allows a short form option that requires a value to be the last
 *     option in a short form option group (similar to 'tar -xvf x.tar').
 *   - Adding a new option checks to see if option already added.
 *   - Option names are validated when options added.
 *   - Throws custom exceptions if result methods called before parse.
 *   - Uses LinkedHashMap instead of Hashtable.
 *   - Uses ArrayList instead of Vector.
 *   - Has float data type for completeness.
 *   - Can specify whether or not an option is required.  If an option is
 *     specified as required when added, an exception will be thrown
 *     during parse if the option was not provided in the arguments.
 *   - Can specify aliases for options (long and short forms).
 *   - Can enable the -- option which allows the interpretation of all
 *     options following it as non-flags.
 *   - Can use the --// option to abandon (and thus disallow error detection)
 *     all additional processing of arguments.  Allows command lines to
 *     get a built-in end-of-line comment.
 *   - Automatic usage message creation.
 *
 * Acceptable option forms:
 *   * Boolean options (value not required):
 *         -b, --bool, --bool=true, --bool=false, -bcd
 *         NOT: --bool true, --bool false.
 *      where 'b', 'c', 'd', and 'bool' are boolean options.  The long form
 *      can actually be used with a value, but the = sign must be used and
 *      the only acceptable values are 'true' and 'false' (case insensitive).
 *   * Integer, Long, Float, Double, String options (value required):
 *         -v val, --valopt val, --valopt=val, -bcdv val
 *         NOT: -bcdv=val
 *      where 'v' and 'valopt' are options that require a value.  Short
 *      form options can appear at the end of a group of boolean short form
 *      options.  The next argument will be used as the value.  The equals
 *      form cannot be used in this manner however.
 *
 * Example Usage:
 *    String[] args = ...;
 *    CommandLineParser parser = new CommandLineParser();
 *    parser.setDashDashEnabled(true);
 *    Option optDebug = parser.addBooleanOption('d', "debug");
 *    Option optCount = parser.addIntegerOption('c', "count", true);
 *    try {
 *        parser.parse(args);
 *    } catch(OptionParseException e) {
 *        System.out.println(parser.getUsageMessage(e, "MyApp", 80, 20));
 *        return;
 *    }
 *    boolean debug = (Boolean) parser.getOptionValue(optDebug, false);
 *    int count = (Integer) parser.getOptionValue(optCount);
 *    String[] remaining = parser.getRemainingArgs();
 *
 * TODO: Implement --colour[=WHEN], --color[=WHEN] (from grep man page)
 * if not already implemented.
 *
 * Also "XArgs" in this class means "extensible arguments" and has
 * nothing to do with the Unix command.
 *
 * TODO: Implement argfile merge, implement argstr, better getOption
 * symmetry, add default version option, make sure default options
 * (help, version, argfile) all sort near the front.  Can't accidentally
 * show options in the help after XArgs options.
 *
 * @author Derek Trumbo
 */

public class CommandLineParser implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    // Constants
    public static final String DASH               = "-";     // Prefix of a short form option argument.
    public static final String DASH_DASH          = "--";    // Both the prefix of a long form option argument
                                                             // and denotes to treat all following arguments
                                                             // as non-option arguments.
    private static final String DASH_DASH_COMMENT = "--//";  // Used as a special argument to cause all
                                                             // arguments that follow it to be ignored.
    private static final String DASH_DASH_BANG    = "--!";   // Used to set the XArgs escape token
                                                             // (e.g. "--!TOKEN").

    // --// Takes precedence over all other operators.  Cannot nest these.  Once one is encountered
    // ALL remaining args no matter what are ignored.

    // Options
    private Set<Option> allOptions = new LinkedHashSet<>();
    private Map<String, Option> optionMap = new LinkedHashMap<>();

    // Configuration
    private int minNonOptions = 0;                         // And Usage Message
    private int maxNonOptions = Integer.MAX_VALUE;         // And Usage Message
    private boolean enableDashDash;                        // Enables -- handling.
    private boolean enableDashDashComment;                 // Enables --// handling.
    private boolean autoPrintParseException = true;
    private boolean useDefaultHelpOptionBehavior = true;
    private transient List<FinalParserValidator> validators = new ArrayList<>();

    // Usage Message Configuration
    private String commandName;
    private String[] nonOptNames;
    private String addlUsageMessage;
    private int addlUsageMessageIndent;
    private String customUsageLine;
    private boolean printParamDelimiters;
    private String defaultXArgsEscapeToken = null;

    // Arg File Configuration
    private ArgFileConfig argFileConfig;   // Default arg file configuration
    private Option argFileOption;
    private Option pathsRelToFileOption;

    // Last set of arguments parsed
    private String[] commandLineArguments;

    // Results
    private Map<Option, List<Object>> parsedOptionValues;        // Holds options and their values.
    private String[] parsedRemainingArgs;                        // Holds all non-option arguments.
    private Pair<Option<?>, Object>[] orderedParsedArguments;    // Holds all option- and non-option arguments in an ordered list
    private Map<Option, List<List<String>>> xargsParsedResults;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public CommandLineParser() {}
    public CommandLineParser(int maximumNonOptionArgs) {
        setMaximumNonOptionArguments(maximumNonOptionArgs);
    }
    public CommandLineParser(int minimumNonOptionArgs, int maximumNonOptionArgs) {
        setMiniumNonOptionArguments(minimumNonOptionArgs);
        setMaximumNonOptionArguments(maximumNonOptionArgs);
    }


    ///////////
    // PARSE //
    ///////////

    public final void parse(String[] argv) throws CommandLineParseException, UserRequestedHelpException {

        try {

            parseInner(argv);

        } catch(CommandLineParseException e) {

            // Clear any results generated during attempted parse.
            parsedOptionValues     = null;
            parsedRemainingArgs    = null;
            orderedParsedArguments = null;
            xargsParsedResults     = null;

            if(autoPrintParseException) {
                System.err.println(renderUsageMessage(commandName, 80, 5, e));
                System.err.println();
            }

            throw e;
        }

    }

    private void parseInner(String[] argv) throws CommandLineParseException, UserRequestedHelpException {

        // Save the most recent, original command line arguments that were attempted to be parsed.
        commandLineArguments = argv;

        // Convert the command line arguments into intermediate results.
        ParseResults mainResults = createResultsOuter(argv);

        // Save intermediate parse results into the command line parser, even if they are found invalid
        // after post validation, in case the client wants to inspect.  This is only because
        // performPostValidation currently uses the parser's internal fields (e.g. getOptionValue).
        // This should probably be cleaned up at some time.
        saveInitialResults(mainResults);

        // Perform any more additional validation steps that are not performed during the intermediate
        // results creation.
        performPostValidation();
    }

    private ParseResults createResultsOuter(String[] argv) throws CommandLineParseException {
        ParseResults mainResults = createResultsInner(argv, false, null);

        // If the arg file is enabled and the corresponding option has been provided
        // on the command line the then parse the file for additional arguments
        // and add them to the overall results.
        if(argFileConfig != null) {
            Option optArgFile = optionMap.get("--" + argFileConfig.getOptionName());
            if(optArgFile != null) {
                List<Object> values = mainResults.getParsedOptionValues().get(optArgFile);  // List stuff here just because
                if(values != null) {                                                        // we're dealing with the maps directly
                    File argFilePath = (File) values.get(0);                                // and don't have CLP API support.

                    String[] argFileArgs = ArgFileParser.parse(argFilePath, argFileConfig);
                    boolean doChangeRelPaths = isChangePathsInArgsFile(mainResults, argFilePath, null);
                    ParseResults argFileResults = createResultsInner(argFileArgs, doChangeRelPaths, argFilePath);

                    doChangeRelPaths = isChangePathsInArgsFile(mainResults, argFilePath, argFileResults);
                    if(doChangeRelPaths) {
                        checkChangePathsInArgsFile(mainResults, argFilePath, argFileResults);
                    }

                    mainResults.overlay(argFileResults);
                    // ^Right now the arg file's arguments override the main arguments
                    // due to current lack of design with respect to merging arg file
                    // arguments and main arguments.  This is actually the opposite of
                    // what you'd expect.  Generally arguments explicitly provided on
                    // the command line would presumably override arguments provided in
                    // a static, shared, curated file.  Once this is officially designed
                    // there might be various different merge strategies that can be
                    // selected.
                }
            }
        }

        return mainResults;
    }
    private boolean isChangePathsInArgsFile(ParseResults mainResults, File argFilePath,
                                            ParseResults argFileResults) {
        boolean changePaths = false;

        if(pathsRelToFileOption != null) {

            // Check main args for the configured "rel paths" option (e.g. "--configpathsrel").
            List<Object> pathsRel = mainResults.getParsedOptionValues().get(pathsRelToFileOption);  // List stuff here just because
            if(pathsRel != null) {                                                                  // we're dealing with the maps directly
                changePaths = (Boolean) pathsRel.get(0);                                            // and don't have CLP API support.

            // Check the args parsed from the args file for the configured "rel paths" option (e.g. "--configpathsrel").
            } else if(argFileResults != null) {
                pathsRel = argFileResults.getParsedOptionValues().get(pathsRelToFileOption);
                if(pathsRel != null) {
                    changePaths = (Boolean) pathsRel.get(0);
                }
            }
        }

        return changePaths;
    }
    private void checkChangePathsInArgsFile(ParseResults mainResults, File argFilePath,
                                            ParseResults argFileResults) {

        // Convert the File objects in the parsed option values map
        for(List<Object> optionValues : argFileResults.getParsedOptionValues().values()) {
            int i = 0;
            for(Object optionValue : optionValues) {
                if(optionValue instanceof File) {
                    File optionValuePath = (File) optionValue;
                    optionValuePath = convertPathFromArgFile(optionValuePath, argFilePath);
                    optionValues.set(i, optionValuePath);
                }
                i++;
            }
        }

        // Convert the File objects in the ordered parsed arguments list
        // (the two data structures are storing more or less the same info
        // but in a different structure).
        for(Pair<Option<?>, Object> optionValuePair : argFileResults.getOrderedParsedArguments()) {
            Object optionValue = optionValuePair.getValue2();
            if(optionValue instanceof File) {
                File optionValuePath = (File) optionValue;
                optionValuePath = convertPathFromArgFile(optionValuePath, argFilePath);
                optionValuePair.setValue2(optionValuePath);
            }
        }
    }

    private File convertPathFromArgFile(File optionValuePath, File argFilePath) {
        File argFileParent = argFilePath.getParentFile();
        return FileUtil.relativeTo(optionValuePath, argFileParent);
    }
    private void saveInitialResults(ParseResults mainResults) {
        parsedOptionValues     = mainResults.getParsedOptionValues();
        parsedRemainingArgs    = mainResults.getParsedRemainingArgs().toArray(new String[0]);
        orderedParsedArguments = mainResults.getOrderedParsedArguments().toArray(new Pair[0]);
        xargsParsedResults     = mainResults.getXargsParsedResults();
    }

    private void performPostValidation() throws CommandLineParseException, UserRequestedHelpException {
        if(useDefaultHelpOptionBehavior) {
            Option optHelp = getOption('?');
            if(optHelp == null) {
                optHelp = getOption("--help");
            }
            if(optHelp instanceof BooleanOption) {
                boolean help = getOptionValue((BooleanOption) optHelp, false);
                if(help) {
                    System.out.println(renderUsageMessage(commandName, 80, 5));
                    System.out.println();
                    throw new UserRequestedHelpException();
                }
            }
        }

        for(Option opt : optionMap.values()) {
            if(opt.isRequired() && getOptionValue(opt) == null) {
                throw new RequiredOptionException(opt);
            }
        }

        int sz = parsedRemainingArgs.length;
        if(sz < minNonOptions) {
            String str = Arrays.toString(parsedRemainingArgs);
            throw new NonOptionCountException("Too few non-option arguments provided: " + str + " (Min " + minNonOptions + ")");
        }
        if(sz > maxNonOptions) {
            String str = Arrays.toString(parsedRemainingArgs);
            throw new NonOptionCountException("Too many non-option arguments provided: " + str + " (Max " + maxNonOptions + ")");
        }

        for(FinalParserValidator v : validators) {
            String errMsg = v.validate(this);
            if(errMsg != null) {
                throw new FinalParserValidationException(errMsg);
            }
        }
    }

    public final ParseResults createResultsInner(String[] argv, boolean doChangeRelPaths, File argFilePath) throws CommandLineParseException {
        Map<Option, List<Object>> parsedOptionValues         = new LinkedHashMap<>();
        List<String> parsedRemainingArgs                     = new ArrayList<>();
        List<Pair<Option<?>, Object>> orderedParsedArguments = new ArrayList<>();
        Map<Option, List<List<String>>> xargsParsedResults   = new LinkedHashMap<>();

        int position = 0;
        Option xargsEnabledOption = null;
        String currentXArgsEscapeToken = defaultXArgsEscapeToken;

        while(position < argv.length) {
            String curArg = argv[position];

            if(enableDashDashComment && curArg.equals(DASH_DASH_COMMENT)) {
                // Do not increment position.  Will fall to subsequent
                // loop and also break out of that one.
                break;
            }

            // XArgs processing
            if(currentXArgsEscapeToken != null && currentXArgsEscapeToken.equals(curArg)) {
                xargsEnabledOption = null;
                position++;
                continue;
            }
            if(xargsEnabledOption != null) {
                List<List<String>> optionsXArgsLists = xargsParsedResults.get(xargsEnabledOption);
                List<String> xxxx = optionsXArgsLists.get(optionsXArgsLists.size() - 1);
                xxxx.add(curArg);
                position++;
                continue;
            }
            if(curArg.startsWith(DASH_DASH_BANG)) {
                currentXArgsEscapeToken = curArg.substring(DASH_DASH_BANG.length());
                if(StringUtil.isBlank(currentXArgsEscapeToken)) {
                    throw new XArgsException(currentXArgsEscapeToken);
                }
                position++;
                continue;
            }

            // If the current argument starts with a dash...
            if(curArg.startsWith(DASH)) {

                // If the current argument is -- then this
                // indicates the end of options.  Break
                // the loop and the arguments will be handled
                // afterwards.
                if(enableDashDash && curArg.equals(DASH_DASH)) {
                    position++;
                    break;        // This could also be a "continue" if we work to restructure the code
                }                 // and make things more consistent.

                String valueArg = null;

                // If this is the long form...
                if(curArg.startsWith(DASH_DASH)) {

                    // If this long form contains an equals sign,
                    // take its value from the right-hand side.
                    int equalsPos = curArg.indexOf("=");
                    if(equalsPos != -1) {
                        valueArg = curArg.substring(equalsPos + 1);
                        curArg = curArg.substring(0,equalsPos);
                    }

                // Else if this isn't the long form, but the argument
                // contains more than a dash and a single character,
                // treat the argument as a group of short form arguments.
                } else if(curArg.length() > 2) {

                    // Start at second character and check each short
                    // form argument.
                    for(int i = 1; i < curArg.length(); i++) {

                        Option opt = findOption(DASH + curArg.charAt(i), curArg);

                        // If this is an option that wants a value, only look
                        // for one if it is the last option of the group.
                        if(opt.wantsValue()) {
                            if(i != curArg.length() - 1) {
                                throw new ValueRequiredSuboptionException(opt, curArg);
                            }

                            position++;
                            if(position < argv.length) {
                                valueArg = argv[position];
                            }
                        }

                        // The ParseContext concept is a little rushed and could be cleaned up.
                        Object value = opt.getValue(valueArg, curArg, new ParseContext() {
                            @Override
                            public boolean isConvertRelPaths() {
                                boolean changePaths = false;
                                if(pathsRelToFileOption != null) {
                                    List<Object> pathsRel = parsedOptionValues.get(pathsRelToFileOption);  // List stuff here just because
                                    if(pathsRel != null) {                                                 // we're dealing with the maps directly
                                        changePaths = (Boolean) pathsRel.get(0);                           // and don't have CLP API support.
                                    }
                                }
                                return doChangeRelPaths || changePaths;
                            }
                            @Override
                            public File getArgsFile() {
                                return argFilePath;
                            }
                            @Override
                            public File convertPath(File path, File argsFile) {
                                return convertPathFromArgFile(path, argsFile);
                            }
                        });
                        addValue(parsedOptionValues, opt, value);
                        orderedParsedArguments.add(new Pair<>(opt, value));

                        // Duplicate Code Block (1)
                        if(opt.hasXArgs()) {
                            xargsEnabledOption = opt;
                            List<List<String>> optionsXArgsLists = xargsParsedResults.get(opt);
                            if(optionsXArgsLists == null) {
                                optionsXArgsLists = new ArrayList<>();
                                xargsParsedResults.put(opt, optionsXArgsLists);
                            }
                            optionsXArgsLists.add(new ArrayList<>());
                        }
                    }

                    // All options within the group are handled above,
                    // move to the next argument.
                    position++;
                    continue;
                }

                Option opt = findOption(curArg, null);

                // If the option is one that wants a value
                // and the value argument has not already
                // been decided (i.e. --long-opt=val), then
                // take the next argument as the value argument.
                if(opt.wantsValue() && valueArg == null) {
                    position++;
                    if(position < argv.length) {
                        valueArg = argv[position];
                    }
                }

                // The ParseContext concept is a little rushed and could be cleaned up.
                Object value = opt.getValue(valueArg, null, new ParseContext() {
                    @Override
                    public boolean isConvertRelPaths() {
                        boolean changePaths = false;
                        if(pathsRelToFileOption != null) {
                            List<Object> pathsRel = parsedOptionValues.get(pathsRelToFileOption);  // List stuff here just because
                            if(pathsRel != null) {                                                 // we're dealing with the maps directly
                                changePaths = (Boolean) pathsRel.get(0);                           // and don't have CLP API support.
                            }
                        }
                        return doChangeRelPaths || changePaths;
                    }
                    @Override
                    public File getArgsFile() {
                        return argFilePath;
                    }
                    @Override
                    public File convertPath(File path, File argsFile) {
                        return convertPathFromArgFile(path, argsFile);
                    }
                });
                addValue(parsedOptionValues, opt, value);
                orderedParsedArguments.add(new Pair<>(opt, value));

                // Duplicate Code Block (2)
                if(opt.hasXArgs()) {
                    xargsEnabledOption = opt;
                    List<List<String>> optionsXArgsLists = xargsParsedResults.get(opt);
                    if(optionsXArgsLists == null) {
                        optionsXArgsLists = new ArrayList<>();
                        xargsParsedResults.put(opt, optionsXArgsLists);
                    }
                    optionsXArgsLists.add(new ArrayList<>());
                }

                position++;

            // Else if the current argument does not start with a
            // dash, add it to the remaining arguments.
            } else {
                parsedRemainingArgs.add(curArg);
                orderedParsedArguments.add(new Pair<>(null, curArg));
                position++;
            }
        }

        // Add all remaining arguments if the -- option was used.
        for( ; position < argv.length; position++) {
            String curArg = argv[position];
            if(enableDashDashComment && curArg.equals(DASH_DASH_COMMENT)) {
                break;
            }
            parsedRemainingArgs.add(curArg);
            orderedParsedArguments.add(new Pair<>(null, curArg));
        }

        return new ParseResults(
            parsedOptionValues,
            parsedRemainingArgs,
            orderedParsedArguments,
            xargsParsedResults
        );
    }

    private Option findOption(String optionName, String optionGroup) throws UnknownOptionException {
        Option opt = optionMap.get(optionName);
        if(opt == null) {
            if(optionGroup != null) {
                throw new UnknownSuboptionException(optionName.charAt(1) + "", optionGroup);
            }
            throw new UnknownOptionException(optionName);
        }
        return opt;
    }

    private void addValue(Map<Option, List<Object>> parsedOptionValuesTemp,
                          Option opt, Object value) throws CommandLineParseException {
        List<Object> optVals = parsedOptionValuesTemp.get(opt);
        if (optVals == null) {
            optVals = new ArrayList<>();
            parsedOptionValuesTemp.put(opt, optVals);
        }
        if(!opt.isAllowMulti() && !optVals.isEmpty()) {
            throw new TooManyValuesForOptionException(opt);
        }
        optVals.add(value);
    }


    ///////////////////
    // USAGE MESSAGE //
    ///////////////////

    // For if the user wants to see the usage message via some help flag for example.
    public String renderUsageMessage(String commandName, int totalLength, int helpMsgIndent) {
        ParserRenderer lu = new ParserRenderer(this);

        String result =
            lu.renderUsageLine(commandName, customUsageLine) + "\n" +
            lu.renderOptionDescriptions(totalLength, helpMsgIndent) + "\n";

        if(addlUsageMessage != null) {
            String addl = "Additional Information";
            result += "\n" + addl + "\n" + StringUtil.replicateChar('-', addl.length()) + "\n";
            result += StringUtil.padNewLines(addlUsageMessage, addlUsageMessageIndent, true);
        }

        return result.trim();
    }

    // For if the usage message is desired due to some error with the arguments.
    public String renderUsageMessage(String commandName, int totalLength, int helpMsgIndent, CommandLineParseException e) {
        String result =
            "Invalid Arguments: " + e.getMessage() + "\n" +
            renderUsageMessage(commandName, totalLength, helpMsgIndent);
        return result.trim();
    }


    ////////////////////
    // ADDING OPTIONS //
    ////////////////////

    public final <T extends Option> T addOption(Option option) {
        if(option == null) {
            return null;
        }
        if(option.getShortForm() != null) {
            String key = DASH + option.getShortForm();
            if(optionMap.get(key) != null) {
                throw new OptionAlreadyExistsException(key);
            }
            optionMap.put(key, option);
        }
        if(option.getLongForm() != null) {
            String key = DASH_DASH + option.getLongForm();
            if(optionMap.get(key) != null) {
                throw new OptionAlreadyExistsException(key);
            }
            optionMap.put(key, option);
        }
        allOptions.add(option);
        return (T) option;
    }

    // Special
    public final Option addDefaultHelpOption() {
        return
            addBooleanOption('?', "help")
                .setHelpDescription("Print the usage and options of this application and exit.");
    }

    // Boolean
    public final BooleanOption addBooleanOption(char shortForm) {
        return addBooleanOption("" + shortForm, null, Option.DEFAULT_REQUIRED, Option.DEFAULT_ALLOW_MULTI);
    }
    public final BooleanOption addBooleanOption(String longForm) {
        return addBooleanOption(null, longForm, Option.DEFAULT_REQUIRED, Option.DEFAULT_ALLOW_MULTI);
    }
    public final BooleanOption addBooleanOption(char shortForm, String longForm) {
        return addBooleanOption("" + shortForm, longForm, Option.DEFAULT_REQUIRED, Option.DEFAULT_ALLOW_MULTI);
    }
    public final BooleanOption addBooleanOption(char shortForm, boolean required) {
        return addBooleanOption("" + shortForm, null, required, Option.DEFAULT_ALLOW_MULTI);
    }
    public final BooleanOption addBooleanOption(String longForm, boolean required) {
        return addBooleanOption(null, longForm, required, Option.DEFAULT_ALLOW_MULTI);
    }
    public final BooleanOption addBooleanOption(char shortForm, String longForm, boolean required) {
        return addBooleanOption("" + shortForm, longForm, required, Option.DEFAULT_ALLOW_MULTI);
    }
    public final BooleanOption addBooleanOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        return addBooleanOption("" + shortForm, longForm, required, allowMulti);
    }
    public final BooleanOption addBooleanOption(String shortForm, String longForm, boolean required, boolean allowMulti) {
        return addOption(new BooleanOption(shortForm, longForm, required, allowMulti));
    }

    // Integer
    public final IntegerOption addIntegerOption(char shortForm) {
        return addOption(new IntegerOption(shortForm));
    }
    public final IntegerOption addIntegerOption(String longForm) {
        return addOption(new IntegerOption(longForm));
    }
    public final IntegerOption addIntegerOption(char shortForm, String longForm) {
        return addOption(new IntegerOption(shortForm, longForm));
    }
    public final IntegerOption addIntegerOption(char shortForm, boolean required) {
        return addOption(new IntegerOption(shortForm, required));
    }
    public final IntegerOption addIntegerOption(String longForm, boolean required) {
        return addOption(new IntegerOption(longForm, required));
    }
    public final IntegerOption addIntegerOption(char shortForm, String longForm, boolean required) {
        return addOption(new IntegerOption(shortForm, longForm, required, Option.DEFAULT_ALLOW_MULTI));
    }
    public final IntegerOption addIntegerOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        return addOption(new IntegerOption(shortForm, longForm, required, allowMulti));
    }

    // Long
    public final LongOption addLongOption(char shortForm) {
        return addOption(new LongOption(shortForm));
    }
    public final LongOption addLongOption(String longForm) {
        return addOption(new LongOption(longForm));
    }
    public final LongOption addLongOption(char shortForm, String longForm) {
        return addOption(new LongOption(shortForm, longForm));
    }
    public final LongOption addLongOption(char shortForm, boolean required) {
        return addOption(new LongOption(shortForm, required));
    }
    public final LongOption addLongOption(String longForm, boolean required) {
        return addOption(new LongOption(longForm, required));
    }
    public final LongOption addLongOption(char shortForm, String longForm, boolean required) {
        return addOption(new LongOption(shortForm, longForm, required, Option.DEFAULT_ALLOW_MULTI));
    }
    public final LongOption addLongOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        return addOption(new LongOption(shortForm, longForm, required, allowMulti));
    }

    // Float
    public final FloatOption addFloatOption(char shortForm) {
        return addOption(new FloatOption(shortForm));
    }
    public final FloatOption addFloatOption(String longForm) {
        return addOption(new FloatOption(longForm));
    }
    public final FloatOption addFloatOption(char shortForm, String longForm) {
        return addOption(new FloatOption(shortForm, longForm));
    }
    public final FloatOption addFloatOption(char shortForm, boolean required) {
        return addOption(new FloatOption(shortForm, required));
    }
    public final FloatOption addFloatOption(String longForm, boolean required) {
        return addOption(new FloatOption(longForm, required));
    }
    public final FloatOption addFloatOption(char shortForm, String longForm, boolean required) {
        return addOption(new FloatOption(shortForm, longForm, required, Option.DEFAULT_ALLOW_MULTI));
    }
    public final FloatOption addFloatOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        return addOption(new FloatOption(shortForm, longForm, required, allowMulti));
    }

    // Double
    public final DoubleOption addDoubleOption(char shortForm) {
        return addOption(new DoubleOption(shortForm));
    }
    public final DoubleOption addDoubleOption(String longForm) {
        return addOption(new DoubleOption(longForm));
    }
    public final DoubleOption addDoubleOption(char shortForm, String longForm) {
        return addOption(new DoubleOption(shortForm, longForm));
    }
    public final DoubleOption addDoubleOption(char shortForm, boolean required) {
        return addOption(new DoubleOption(shortForm, required));
    }
    public final DoubleOption addDoubleOption(String longForm, boolean required) {
        return addOption(new DoubleOption(longForm, required));
    }
    public final DoubleOption addDoubleOption(char shortForm, String longForm, boolean required) {
        return addOption(new DoubleOption(shortForm, longForm, required, Option.DEFAULT_ALLOW_MULTI));
    }
    public final DoubleOption addDoubleOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        return addOption(new DoubleOption(shortForm, longForm, required, allowMulti));
    }

    // String
    public final StringOption addStringOption(char shortForm) {
        return addOption(new StringOption(shortForm));
    }
    public final StringOption addStringOption(String longForm) {
        return addOption(new StringOption(longForm));
    }
    public final StringOption addStringOption(char shortForm, String longForm) {
        return addOption(new StringOption(shortForm, longForm));
    }
    public final StringOption addStringOption(char shortForm, boolean required) {
        return addOption(new StringOption(shortForm, required));
    }
    public final StringOption addStringOption(String longForm, boolean required) {
        return addOption(new StringOption(longForm, required));
    }
    public final StringOption addStringOption(char shortForm, String longForm, boolean required) {
        return addOption(new StringOption(shortForm, longForm, required, Option.DEFAULT_ALLOW_MULTI));
    }
    public final StringOption addStringOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        return addOption(new StringOption(shortForm, longForm, required, allowMulti));
    }

    // Path
    public final PathOption addPathOption(char shortForm) {
        return addOption(new PathOption(shortForm));
    }
    public final PathOption addPathOption(String longForm) {
        return addOption(new PathOption(longForm));
    }
    public final PathOption addPathOption(char shortForm, String longForm) {
        return addOption(new PathOption(shortForm, longForm));
    }
    public final PathOption addPathOption(char shortForm, boolean required) {
        return addOption(new PathOption(shortForm, required));
    }
    public final PathOption addPathOption(String longForm, boolean required) {
        return addOption(new PathOption(longForm, required));
    }
    public final PathOption addPathOption(char shortForm, String longForm, boolean required) {
        return addOption(new PathOption(shortForm, longForm, required, Option.DEFAULT_ALLOW_MULTI));
    }
    public final PathOption addPathOption(char shortForm, String longForm, boolean required, boolean allowMulti) {
        return addOption(new PathOption(shortForm, longForm, required, allowMulti));
    }


    /////////////
    // ALIASES //
    /////////////

    public final void addAlias(Option option, char shortForm) throws OptionNotRelatedException {
        if(option == null) {
            throw new NullPointerException("Option cannot be null.");
        }
        checkOptionRelated(option);
        Option.validateShortForm("" + shortForm);
        String key = DASH + shortForm;
        if(optionMap.get(key) != null) {
            throw new OptionAlreadyExistsException(key);
        }
        optionMap.put(key, option);
        option.addAlias(shortForm);  // Option objects should know their aliases for usage message.
    }

    public final void addAlias(Option option, String longForm) throws OptionNotRelatedException{
        if(option == null) {
            throw new NullPointerException("Option cannot be null.");
        }
        checkOptionRelated(option);
        Option.validateLongForm(longForm);
        String key = DASH_DASH + longForm;
        if(optionMap.get(key) != null) {
            throw new OptionAlreadyExistsException(key);
        }
        optionMap.put(key, option);
        option.addAlias(longForm);  // Option objects should know their aliases for usage message.
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors (Computed)

    // Options
    public Set<Option> getOptions() {
        return Collections.unmodifiableSet(allOptions);
    }
    public Option getOption(String key) {
        return optionMap.get(key);           // Needs to be thought out more
    }
    public Option getOption(char key) {
        return optionMap.get(DASH + key);     // Needs to be thought out more
    }

    // Results
    public final <T> T getOptionValue(Option option) {
        return (T) getOptionValue(option, null);
    }
    public final <T> T getOptionValue(Option option, Object def) {
        if(option == null) {
            throw new NullPointerException("Option cannot be null.");
        }
        checkOptionRelated(option);
        if(parsedOptionValues == null) {
            throw new ResultsNotParsedException("option values");
        }
        List<Object> v = parsedOptionValues.get(option);
        if(v == null) {
            return (T) def;
        }
        return (T) v.get(0);
    }
    public final <T> T[] getOptionValues(Option option) {
        return getOptionValues(option, null);
    }
    public final <T> T[] getOptionValues(Option option, Object[] def) {
        if(option == null) {
            throw new NullPointerException("Option cannot be null.");
        }
        checkOptionRelated(option);
        if(parsedOptionValues == null) {
            throw new ResultsNotParsedException("option values");
        }
        List<Object> v = parsedOptionValues.get(option);
        if(v == null) {
            return (T[]) def;
        }
        Object[] newArray = (Object[]) Array.newInstance(option.getValueType(), v.size());
        for(int i = 0; i < v.size(); i++) {
            newArray[i] = v.get(i);
        }
        return (T[]) newArray;
    }
    public final Map<Option, Object[]> getOptionValuesMap() {
        if(parsedOptionValues == null) {
            throw new ResultsNotParsedException("option values");
        }
        Map<Option, Object[]> map = new LinkedHashMap<>();
        for(Option opt : parsedOptionValues.keySet()) {
            map.put(opt, getOptionValues(opt));
        }
        return map;
    }

    public final String[] getNonOptionArguments() {
        if(parsedRemainingArgs == null) {
            throw new ResultsNotParsedException("remaining arguments");
        }
        return parsedRemainingArgs;
    }
    public Pair<Option<?>, Object>[] getOrderedParsedArguments() {
        if(orderedParsedArguments == null) {
            throw new ResultsNotParsedException("ordered, parsed arguments");
        }
        return orderedParsedArguments;
    }

    public final String[] getXArgsArray(Option option) {
        if(xargsParsedResults == null) {
            throw new ResultsNotParsedException("XArgs");
        }
        List<List<String>> optionXArgs = xargsParsedResults.get(option);
        if(optionXArgs == null) {
            return null;
        }
        List<String> args = optionXArgs.get(0);     // Should be at least 1 element if non-null
        return args.toArray(new String[0]);
    }
    public final String[][] getXArgsArrays(Option option) {
        if(xargsParsedResults == null) {
            throw new ResultsNotParsedException("XArgs");
        }
        List<List<String>> optionXArgs = xargsParsedResults.get(option);
        if(optionXArgs == null) {
            return null;
        }
        String[][] args = new String[optionXArgs.size()][];
        int l = 0;
        for(List<String> list : optionXArgs) {
            args[l++] = list.toArray(new String[0]);
        }
        return args;
    }

    // Accessors

    // Configuration
    public int getMiniumNonOptionArguments() {
        return minNonOptions;
    }
    public int getMaximumNonOptionArguments() {
        return maxNonOptions;
    }
    public boolean isDashDashEnabled() {
        return enableDashDash;
    }
    public boolean isDashDashCommentEnabled() {
        return enableDashDashComment;
    }
    public boolean isAutoPrintParseException() {
        return autoPrintParseException;
    }
    public boolean isUseDefaultHelpOptionBehavior() {
        return useDefaultHelpOptionBehavior;
    }
    public ArgFileConfig getArgFileConfig() {
        return argFileConfig;
    }
    public Option getArgFileOption() {
        return argFileOption;
    }
    public Option getPathsRelToFileOption() {
        return pathsRelToFileOption;
    }

    // Usage Message Configuration
    public String getCommandName() {
        return commandName;
    }
    public String[] getNonOptNames() {
        return nonOptNames;
    }
    public String getAddlUsageMessage() {
        return addlUsageMessage;
    }
    public int getAddlUsageMessageIndent() {
        return addlUsageMessageIndent;
    }
    public String getCustomUsageLine() {
        return customUsageLine;
    }
    public boolean isPrintParamDelimiters() {
        return printParamDelimiters;
    }
    public String getDefaultXArgsEscapeToken() {
        return defaultXArgsEscapeToken;
    }

    // Last set of arguments parsed
    public String[] getCommandLineArguments() {
        return commandLineArguments;
    }

    // Mutators (Builder)

    // Configuration
    public CommandLineParser setMiniumNonOptionArguments(int minNonOptions) {
        this.minNonOptions = minNonOptions;
        return this;
    }
    public CommandLineParser setMaximumNonOptionArguments(int maxNonOptions) {
        this.maxNonOptions = maxNonOptions;
        return this;
    }
    public CommandLineParser setDashDashEnabled(boolean enableDashDash) {
        this.enableDashDash = enableDashDash;
        return this;
    }
    public CommandLineParser setDashDashCommentEnabled(boolean enableDashDashComment) {
        this.enableDashDashComment = enableDashDashComment;
        return this;
    }
    public CommandLineParser setAutoPrintParseException(boolean autoPrintParseException) {
        this.autoPrintParseException = autoPrintParseException;
        return this;
    }
    public CommandLineParser setUseDefaultHelpOptionBehavior(boolean useDefaultHelpOptionBehavior) {
        this.useDefaultHelpOptionBehavior = useDefaultHelpOptionBehavior;
        return this;
    }
    public void addValidator(FinalParserValidator v) {
        validators.add(v);
    }
    public CommandLineParser setArgFileConfig(ArgFileConfig argFileConfig) {
        this.argFileConfig = argFileConfig;
        if(argFileConfig == null) {
            argFileOption = null;
            pathsRelToFileOption = null;
        } else {
            argFileOption = addPathOption(argFileConfig.getOptionName()).setMustBeFile(true);
            if(argFileConfig.getPathsRelToFileOptionName() != null) {
                pathsRelToFileOption = addBooleanOption(argFileConfig.getPathsRelToFileOptionName());
                pathsRelToFileOption.setHelpDescription("Whether or not the provided argument file contains paths that should be resolved relative to the file itself instead of the working directory.");
            }
            argFileOption.setHelpDescription("A location of a file out of which additional command line arguments will be read and processed.");
            argFileOption.setHelpParamName("FILE");
        }
        return this;
    }

    // Usage Message Configuration
    public CommandLineParser setCommandName(String commandName) {
        this.commandName = commandName;
        return this;
    }
    public CommandLineParser setNonOptionNames(String... nonOptNames) {
        this.nonOptNames = nonOptNames;
        return this;
    }
    public CommandLineParser setAddlUsageMessage(String addlUsageMessage) {
        this.addlUsageMessage = addlUsageMessage;
        return this;
    }
    public CommandLineParser setAddlUsageMessageIndent(int addlUsageMessageIndent) {
        this.addlUsageMessageIndent = addlUsageMessageIndent;
        return this;
    }
    public CommandLineParser setCustomUsageLine(String customUsageLine) {
        this.customUsageLine = customUsageLine;
        return this;
    }
    public CommandLineParser setPrintParamDelimiters(boolean printParamDelimiters) {
        this.printParamDelimiters = printParamDelimiters;
        return this;
    }
    public CommandLineParser setDefaultXArgsEscapeToken(String defaultXArgsEscapeToken) {
        this.defaultXArgsEscapeToken = defaultXArgsEscapeToken;
        return this;
    }

    // Mutators (Helper)

    public CommandLineParser setAddlUsageMessage(String addlUsageMessage, int indent) {
        this.addlUsageMessage  = addlUsageMessage;
        addlUsageMessageIndent = indent;
        return this;
    }


    //////////
    // MISC //
    //////////

    private void checkOptionRelated(Option option) throws OptionNotRelatedException {
        if(!allOptions.contains(option)) {
            throw new OptionNotRelatedException(option);
        }
    }
}
