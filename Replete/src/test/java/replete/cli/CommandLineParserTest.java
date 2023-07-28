package replete.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import replete.cli.argfile.ArgFileConfig;
import replete.cli.errors.CommandLineParseException;
import replete.cli.errors.InvalidOptionNameException;
import replete.cli.errors.OptionAlreadyExistsException;
import replete.cli.errors.OptionNotRelatedException;
import replete.cli.errors.ResultsNotParsedException;
import replete.cli.options.Option;
import replete.cli.validator.OptionValueValidator;
import replete.collections.Pair;
import replete.io.FileUtil;
import replete.util.OsUtil;


/**
 * @author Derek Trumbo
 */

// TODO: Add tests for option validation and aliases.
public class CommandLineParserTest {

    private CommandLineParser parser1 = constructTestParser1();
    private CommandLineParser parser2 = constructTestParser2();

    public static void main(String[] args) {
        CommandLineParserTest test = new CommandLineParserTest();
        test.testCustomArguments(test.parser1, args);
    }

    private CommandLineParser constructTestParser1() {
        CommandLineParser parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);

        Option debug, verbose, reverse, reverse2, name, count, seconds, height, length;

        debug = parser.addBooleanOption('d', "debug");
        verbose = parser.addBooleanOption('v', "verbose");
        reverse = parser.addBooleanOption('r');
        reverse2 = parser.addBooleanOption("reverse");
        name = parser.addStringOption('n', "name");
        count = parser.addIntegerOption('c', "count");
        seconds = parser.addLongOption('s', "seconds");
        height = parser.addFloatOption('h', "height");
        length = parser.addDoubleOption('l', "length");

        debug.setHelpDescription("Places the application into debug mode and activates      the variable mode for the sake of brevity.\n   \nAlso need \r\nto investigate the debugging capability.\n\n");
        verbose.setHelpDescription("Enables verbose mode.  \n");
        length.setHelpDescription("Permits a restrction on the                                  length of the objects.");
        count.setHelpDescription("Indicates how many of something to create when something hap \npens on activation.  Also, here is an even longer mesjgekkjj that will wrap yet another time.");
        seconds.setHelpDescription("");
        name.setHelpParamName("NAME");
        length.setHelpParamName("LEN");
        String methodDesc =
            "Method 1: Use <vn>...</vn> links in grouping files to decide\n" +
            "whether a grouping reference is added to a VerbNet MEMBER line.\n" +
            "Method 2: Use <wn>...</wn> links in grouping files to decide\n" +
            "whether a grouping reference is added to a VerbNet MEMBER line.";
        height.setHelpDescription("Method to use when computing grouping links.  Valid values: 'm1' or 'm2'.  " +
            "Default is 'm1' if not supplied.  Here are the descriptions of the methods:\n" + methodDesc);

        return parser;
    }

    private CommandLineParser constructTestParser2() {
        CommandLineParser parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);
        parser.addBooleanOption('d', "debug");
        return parser;
    }

    private void testCustomArguments(CommandLineParser parser, String[] args) {
        if(args.length == 1 && args[0].equals("--stdin")) {
            System.out.println("Enter arguments: ");
            args = ConsoleUtil.getLine().split(" ");
        }
        try {
            parser.parse(args);
        } catch(CommandLineParseException e) {
            fail(e.getMessage());
        }

        for(Option opt : parser.getOptions()) {
            Object val = parser.getOptionValue(opt);
            Object[] vals = parser.getOptionValues(opt);

            System.out.printf("%-12s : %s %s\n", opt.toString(),
                    "" + val + "(type=" + valWrap(val) + ")",
                    Arrays.toString(vals));
        }

        String[] rem = parser.getNonOptionArguments();
        System.out.println("Remaining    : " + Arrays.toString(rem));
        System.out.println("Map          : " + parser.getOptionValuesMap());
    }

    private String valWrap(Object o) {
        if(o == null) {
            return "NULL";
        }
        return o.getClass().getSimpleName();
    }

    @Test
    public void dashDash() {

        String cmdLine, valMapExpected, remArgsExpected;

        cmdLine = "--debug=false -- name= blah -c";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Unknown option '--'");

        parser1.setDashDashEnabled(true);

        cmdLine = "-- --reverse true -ddv -vd -r=false false --verbose true --debug true --reverse FALSE";
        valMapExpected = "{}";
        remArgsExpected = "[--reverse, true, -ddv, -vd, -r=false, false, --verbose, true, --debug, true, --reverse, FALSE]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false -- name= blah -c";
        valMapExpected = "{-d/--debug=[false]}";
        remArgsExpected = "[name=, blah, -c]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "-ddv -vd -r --verbose=true -- --debug=true";
        valMapExpected = "{-d/--debug=[true, true, true], -v/--verbose=[true, true, true], -r=[true]}";
        remArgsExpected = "[--debug=true]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false a b -- c d";
        valMapExpected = "{-d/--debug=[false]}";
        remArgsExpected = "[a, b, c, d]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);
    }

    @Test
    public void dashDashComment() {

        String cmdLine, valMapExpected, remArgsExpected;

        cmdLine = "--debug=false --// name= blah -c";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Unknown option '--//'");

        parser1.setDashDashEnabled(true);
        parser1.setDashDashCommentEnabled(true);

        cmdLine = "--// --reverse true -ddv -vd -r=false false --verbose true --debug true --reverse FALSE";
        valMapExpected = "{}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false --// a b -- c d";
        valMapExpected = "{-d/--debug=[false]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false a b --// c d";
        valMapExpected = "{-d/--debug=[false]}";
        remArgsExpected = "[a, b]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);
    }

    @Test
    public void notRelated() {
        CommandLineParser parser2 = new CommandLineParser();
        parser2.setAutoPrintParseException(false);
        Option bopt = parser2.addBooleanOption('x', "xylophone");
        try {
            parser1.getOptionValue(bopt);
            fail();
        } catch(OptionNotRelatedException e) {
            // OK
        }
    }

    @Test
    public void nonOptionCount() {
        String cmdLine, valMapExpected, remArgsExpected;

        CommandLineParser parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);
        parser.setMiniumNonOptionArguments(2);
        parser.setMaximumNonOptionArguments(4);
        parser.addBooleanOption('x', "xylophone");

        cmdLine = "-x";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, "Too few non-option arguments provided: [] (Min 2)");

        cmdLine = "-x 1 2 3 4 5";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, "Too many non-option arguments provided: [1, 2, 3, 4, 5] (Max 4)");

        cmdLine = "-x 1 2 3 4";
        valMapExpected = "{-x/--xylophone=[true]}";
        remArgsExpected = "[1, 2, 3, 4]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected);
    }

    @Test
    public void nonOptionString() {
        CommandLineParser parser;

        parser = new CommandLineParser().setPrintParamDelimiters(true);
        //parser.setMiniumNonOptionArguments(2);
        //parser.setMaximumNonOptionArguments(4);
        //parser.setNonOptionNames("One", "Two", "Three");
        testNonOption("[<argument>...]", parser);

        parser = new CommandLineParser().setPrintParamDelimiters(true);
        //parser.setMiniumNonOptionArguments(2);
        //parser.setMaximumNonOptionArguments(4);
        parser.setNonOptionNames("One", "Two", "Three");
        testNonOption("[<One>] [<Two>] [<Three>] [<argument>...]", parser);

        parser = new CommandLineParser().setPrintParamDelimiters(true);
        //parser.setMiniumNonOptionArguments(2);
        parser.setMaximumNonOptionArguments(4);
        //parser.setNonOptionNames("One", "Two", "Three");
        testNonOption("[<arg1>] [<arg2>] [<arg3>] [<arg4>]", parser);

        parser = new CommandLineParser().setPrintParamDelimiters(true);
        //parser.setMiniumNonOptionArguments(2);
        parser.setMaximumNonOptionArguments(4);
        parser.setNonOptionNames("One", "Two", "Three");
        testNonOption("[<One>] [<Two>] [<Three>] [<arg4>]", parser);

        parser = new CommandLineParser().setPrintParamDelimiters(true);
        //parser.setMiniumNonOptionArguments(2);
        parser.setMaximumNonOptionArguments(4);
        parser.setNonOptionNames("One", "Two", "Three", "Four", "Five");
        testNonOption("[<One>] [<Two>] [<Three>] [<Four>]", parser);

        parser = new CommandLineParser().setPrintParamDelimiters(true);
        parser.setMiniumNonOptionArguments(2);
        //parser.setMaximumNonOptionArguments(4);
        //parser.setNonOptionNames("One", "Two", "Three");
        testNonOption("<arg1> <arg2> [<argument>...]", parser);

        parser = new CommandLineParser().setPrintParamDelimiters(true);
        parser.setMiniumNonOptionArguments(2);
        //parser.setMaximumNonOptionArguments(4);
        parser.setNonOptionNames("One", "Two", "Three");
        testNonOption("<One> <Two> [<Three>] [<argument>...]", parser);

        parser = new CommandLineParser().setPrintParamDelimiters(true);
        parser.setMiniumNonOptionArguments(2);
        parser.setMaximumNonOptionArguments(4);
        //parser.setNonOptionNames("One", "Two", "Three");
        testNonOption("<arg1> <arg2> [<arg3>] [<arg4>]", parser);

        parser = new CommandLineParser().setPrintParamDelimiters(true);
        parser.setMiniumNonOptionArguments(2);
        parser.setMaximumNonOptionArguments(4);
        parser.setNonOptionNames("One", "Two", "Three");
        testNonOption("<One> <Two> [<Three>] [<arg4>]", parser);

        parser = new CommandLineParser().setPrintParamDelimiters(true);
        parser.setMiniumNonOptionArguments(2);
        parser.setMaximumNonOptionArguments(4);
        parser.setNonOptionNames("One", "Two", "Three", "Four", "Five");
        testNonOption("<One> <Two> [<Three>] [<Four>]", parser);

        parser = new CommandLineParser().setPrintParamDelimiters(true);
        parser.setMiniumNonOptionArguments(2);
        //parser.setMaximumNonOptionArguments(4);
        parser.setNonOptionNames("One", "Two", "Three...");
        testNonOption("<One> <Two> [<Three>...]", parser);

        parser = new CommandLineParser().setPrintParamDelimiters(true);
        parser.setMiniumNonOptionArguments(2);
        //parser.setMaximumNonOptionArguments(4);
        parser.setNonOptionNames("One", "Two", "Three..");
        testNonOption("<One> <Two> [<Three..>] [<argument>...]", parser);

        parser = new CommandLineParser().setPrintParamDelimiters(true);
        parser.setMiniumNonOptionArguments(2);
        //parser.setMaximumNonOptionArguments(4);
        parser.setNonOptionNames("One", "Two", "Three...", "Four...");
        testNonOption("<One> <Two> [<Three>...] [<Four>...]", parser);
    }

    private void testNonOption(String expected, CommandLineParser parser) {
        ParserRenderer lu = new ParserRenderer(parser);
        assertEquals(expected, lu.renderNonOptionString());
    }

    @Test
    public void primaryParser() {
        String expectedHelpDesc =
            "Options\n" +
            "-------\n" +
            "  -d, --debug       [Optional] Places the application into debug mode and \n" +
            "                    activates      the variable mode for the sake of brevity.\n" +
            "                       \n" +
            "                    Also need \n" +
            "                    to investigate the debugging capability.\n" +
            "                    \n" +
            "                    \n" +
            "  -v, --verbose     [Optional] Enables verbose mode.\n" +
            "                    \n" +
            "  -r                [Optional]\n" +
            "  --reverse         [Optional]\n" +
            "  -n NAME, --name=NAME\n" +
            "                    [Optional]\n" +
            "  -c VAL, --count=VAL\n" +
            "                    [Optional] Indicates how many of something to create when \n" +
            "                    something hap \n" +
            "                    pens on activation.  Also, here is an even longer mesjgekkjj\n" +
            "                    that will wrap yet another time.\n" +
            "  -s VAL, --seconds=VAL\n" +
            "                    [Optional]\n" +
            "  -h VAL, --height=VAL\n" +
            "                    [Optional] Method to use when computing grouping links.  \n" +
            "                    Valid values: 'm1' or 'm2'.  Default is 'm1' if not \n" +
            "                    supplied.  Here are the descriptions of the methods:\n" +
            "                    Method 1: Use <vn>...</vn> links in grouping files to decide\n" +
            "                    whether a grouping reference is added to a VerbNet MEMBER \n" +
            "                    line.\n" +
            "                    Method 2: Use <wn>...</wn> links in grouping files to decide\n" +
            "                    whether a grouping reference is added to a VerbNet MEMBER \n" +
            "                    line.\n" +
            "  -l LEN, --length=LEN\n" +
            "                    [Optional] Permits a restrction on the                      \n" +
            "                               length of the objects.";

        ParserRenderer lu = new ParserRenderer(parser1);
        String actualHelpDesc = lu.renderOptionDescriptions(80, 20);

        assertEquals(expectedHelpDesc, actualHelpDesc);

        String cmdLine, valMapExpected, remArgsExpected;

        cmdLine = null;
        valMapExpected = "{}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "";
        valMapExpected = "{}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "";
        valMapExpected = "{}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "file";
        valMapExpected = "{}";
        remArgsExpected = "[file]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, new Pair[] {
            new Pair<>(null, "file")
        });

        cmdLine = "-d";
        valMapExpected = "{-d/--debug=[true]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, new Pair[] {
            new Pair<>(parser1.getOption('d'), true)
        });

        cmdLine = "--debug=tRuE";
        valMapExpected = "{-d/--debug=[true]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, new Pair[] {
            new Pair<>(parser1.getOption('d'), true)
        });

        cmdLine = "--debug=fAlSe";
        valMapExpected = "{-d/--debug=[false]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, new Pair[] {
            new Pair<>(parser1.getOption('d'), false)
        });

        cmdLine = "-x";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Unknown option '-x'");

        cmdLine = "-xdv";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Unknown option 'x' in '-xdv'");

        cmdLine = "-dxv";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Unknown option 'x' in '-dxv'");

        cmdLine = "-dvx";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Unknown option 'x' in '-dvx'");

        cmdLine = "--xxx";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Unknown option '--xxx'");

        cmdLine = "-ddv";
        valMapExpected = "{-d/--debug=[true, true], -v/--verbose=[true]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, new Pair[] {
            new Pair<>(parser1.getOption('d'), true),
            new Pair<>(parser1.getOption('d'), true),
            new Pair<>(parser1.getOption("--verbose"), true)
        });

        cmdLine = "-ddv -vd";
        valMapExpected = "{-d/--debug=[true, true, true], -v/--verbose=[true, true]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "-ddv -vd";
        valMapExpected = "{-d/--debug=[true, true, true], -v/--verbose=[true, true]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "-ddv -vd --verbose=true --debug=true";
        valMapExpected = "{-d/--debug=[true, true, true, true], -v/--verbose=[true, true, true]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "-ddv -vd -r --verbose=true --debug=true";
        valMapExpected = "{-d/--debug=[true, true, true, true], -v/--verbose=[true, true, true], -r=[true]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "-ddv -vd -r --verbose=true --debug=true --reverse=FALSE";
        valMapExpected = "{-d/--debug=[true, true, true, true], -v/--verbose=[true, true, true], -r=[true], --reverse=[false]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--reverse -ddv -vd -r --verbose=true --debug=true --reverse=FALSE";
        valMapExpected = "{--reverse=[true, false], -d/--debug=[true, true, true, true], -v/--verbose=[true, true, true], -r=[true]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--reverse true -ddv -vd -r false --verbose true --debug true --reverse FALSE";
        valMapExpected = "{--reverse=[true, true], -d/--debug=[true, true, true, true], -v/--verbose=[true, true, true], -r=[true]}";
        remArgsExpected = "[true, false, true, true, FALSE]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, new Pair[] {
            new Pair<>(parser1.getOption("--reverse"), true),
            new Pair<>(null, "true"),
            new Pair<>(parser1.getOption("-d"), true),
            new Pair<>(parser1.getOption("-d"), true),
            new Pair<>(parser1.getOption("-v"), true),
            new Pair<>(parser1.getOption("-v"), true),
            new Pair<>(parser1.getOption("-d"), true),
            new Pair<>(parser1.getOption("-r"), true),
            new Pair<>(null, "false"),
            new Pair<>(parser1.getOption("--verbose"), true),
            new Pair<>(null, "true"),
            new Pair<>(parser1.getOption("--debug"), true),
            new Pair<>(null, "true"),
            new Pair<>(parser1.getOption("--reverse"), true),
            new Pair<>(null, "FALSE"),
        });

        cmdLine = "--reverse true -ddv -vd -r=false false --verbose true --debug true --reverse FALSE";
        valMapExpected = "{--reverse=[true, true], -d/--debug=[true, true, true, true], -v/--verbose=[true, true, true], -r=[true]}";
        remArgsExpected = "[true, false, true, true, FALSE]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Unknown option '=' in '-r=false'");

        cmdLine = "--debug=false -n blah";
        valMapExpected = "{-d/--debug=[false], -n/--name=[blah]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false --name blah";
        valMapExpected = "{-d/--debug=[false], -n/--name=[blah]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false --name=blah";
        valMapExpected = "{-d/--debug=[false], -n/--name=[blah]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false --name= blah";
        valMapExpected = "{-d/--debug=[false], -n/--name=[]}";
        remArgsExpected = "[blah]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, new Pair[] {
            new Pair<>(parser1.getOption("--debug"), false),
            new Pair<>(parser1.getOption("-n"), ""),
            new Pair<>(null, "blah"),
        });

        cmdLine = "--debug=false -n";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-n/--name': requires value");

        cmdLine = "--debug=false -n blah -c";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-c/--count': requires value");

        cmdLine = "--debug=false -ndv blah";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-n/--name' in '-ndv': requires value");

        cmdLine = "--debug=false -dvn";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-n/--name' in '-dvn': requires value");

        cmdLine = "file --debug=false -dvn blah file2";
        valMapExpected = "{-d/--debug=[false, true], -v/--verbose=[true], -n/--name=[blah]}";
        remArgsExpected = "[file, file2]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "file --debug=false -dvn blah file2";
        valMapExpected = "{-d/--debug=[false, true], -v/--verbose=[true], -n/--name=[blah]}";
        remArgsExpected = "[file, file2]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "file --debug=false -dvn=blah file2";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-n/--name' in '-dvn=blah': requires value");

        cmdLine = "file --debug=false --name blah -n blah2 -c 1 --count 2 file2";
        valMapExpected = "{-d/--debug=[false], -n/--name=[blah, blah2], -c/--count=[1, 2]}";
        remArgsExpected = "[file, file2]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "file --debug=false --name blah -n blah2 -c 1 --count 2 --seconds 12345 -s 67890 -h 4321.65 --height=9876.54 file2";
        valMapExpected = "{-d/--debug=[false], -n/--name=[blah, blah2], -c/--count=[1, 2], -s/--seconds=[12345, 67890], -h/--height=[4321.65, 9876.54]}";
        remArgsExpected = "[file, file2]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "-h 4321.65 file --count 2 --debug=false -n blah2 -c 1 --seconds=12345 --height=9876.54 --count=3 --name blah file2 -ds 67890";
        valMapExpected = "{-h/--height=[4321.65, 9876.54], -c/--count=[2, 1, 3], -d/--debug=[false, true], -n/--name=[blah2, blah], -s/--seconds=[12345, 67890]}";
        remArgsExpected = "[file, file2]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "-h 4321.65 file --count 2 --debug=false -n blah2 -c 1 x --seconds=12345 --height=9876.54 --count=3 --name blah file2 -ds 67890";
        valMapExpected = "{-h/--height=[4321.65, 9876.54], -c/--count=[2, 1, 3], -d/--debug=[false, true], -n/--name=[blah2, blah], -s/--seconds=[12345, 67890]}";
        remArgsExpected = "[file, x, file2]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=what";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-d/--debug': invalid value 'what'");

        cmdLine = "--debug=false -c one";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-c/--count': invalid value 'one'");

        cmdLine = "--debug=false --count=";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-c/--count': invalid value ''");

        cmdLine = "--debug=false --count=4.5";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-c/--count': invalid value '4.5'");

        cmdLine = "--debug=false --count=-7";
        valMapExpected = "{-d/--debug=[false], -c/--count=[-7]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false --count=4000000000";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-c/--count': invalid value '4000000000'");

        cmdLine = "--debug=false --count=2000000000";
        valMapExpected = "{-d/--debug=[false], -c/--count=[2000000000]}";
        remArgsExpected = "[]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false --count=2000000000 -s one";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-s/--seconds': invalid value 'one'");

        cmdLine = "--debug=false . --count=2000000000 -rs 3.48 ..";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-s/--seconds': invalid value '3.48'");

        cmdLine = "--debug=false . --count=2000000000 -rs -4000000000 ..";
        valMapExpected = "{-d/--debug=[false], -c/--count=[2000000000], -r=[true], -s/--seconds=[-4000000000]}";
        remArgsExpected = "[., ..]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false . --count=2000000000 -rs 1234567890123456 -height 6.9 ..";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-h/--height' in '-height': requires value");

        cmdLine = "--debug=false . --count=2000000000 -rs 1234567890123456 --height one.two ..";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-h/--height': invalid value 'one.two'");

        cmdLine = "--debug=false . --count=2000000000 -rs 1234567890123456 --height -456.77 ..";
        valMapExpected = "{-d/--debug=[false], -c/--count=[2000000000], -r=[true], -s/--seconds=[1234567890123456], -h/--height=[-456.77]}";
        remArgsExpected = "[., ..]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false . --count=2000000000 -rs 1234567890123456 --height 3.4e+38 ..";
        valMapExpected = "{-d/--debug=[false], -c/--count=[2000000000], -r=[true], -s/--seconds=[1234567890123456], -h/--height=[3.4E38]}";
        remArgsExpected = "[., ..]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false . --count=2000000000 -rs 1234567890123456 --height 3.5e38 ..";
        valMapExpected = "{-d/--debug=[false], -c/--count=[2000000000], -r=[true], -s/--seconds=[1234567890123456], -h/--height=[Infinity]}";
        remArgsExpected = "[., ..]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false . --count=2000000000 -rs 1234567890123456 --height -1e-45 ..";
        valMapExpected = "{-d/--debug=[false], -c/--count=[2000000000], -r=[true], -s/--seconds=[1234567890123456], -h/--height=[-1.4E-45]}";
        remArgsExpected = "[., ..]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false . --count=2000000000 -rs 1234567890123456 --height -10e80 ..";
        valMapExpected = "{-d/--debug=[false], -c/--count=[2000000000], -r=[true], -s/--seconds=[1234567890123456], -h/--height=[-Infinity]}";
        remArgsExpected = "[., ..]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        cmdLine = "--debug=false . --count=2000000000 -rs 1234567890123456 --height -345.3e-16 -l one.two ..";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected, "Illegal option '-l/--length': invalid value 'one.two'");

        cmdLine = "x --debug=false . --count=2000000000 -rs 1234567890123456 --height -345.3e-16 -l 2.3e+100 --length 124 ..";
        valMapExpected = "{-d/--debug=[false], -c/--count=[2000000000], -r=[true], -s/--seconds=[1234567890123456], -h/--height=[-3.453E-14], -l/--length=[2.3E100, 124.0]}";
        remArgsExpected = "[x, ., ..]";
        testParse(parser1, cmdLine, valMapExpected, remArgsExpected);

        //////////////////

        cmdLine = "aaa -s 456 --debug=false bbb -s 44 --name=blah --count=2 -c 3 --seconds 34 ccc";
        try {
            parser1.parse(cmdLine.split(" "));
        } catch(Exception e) {

        }

        Option debug = parser1.getOption("-d");
        Option verbose = parser1.getOption("-v");
        Option reverse = parser1.getOption("-r");
        Option reverse2 = parser1.getOption("--reverse");
        Option name = parser1.getOption("--name");
        Option count = parser1.getOption("--count");
        Option seconds = parser1.getOption("-s");
        Option height = parser1.getOption("-h");
        Option length = parser1.getOption("-l");

        if(parser1.getNonOptionArguments().length != 3) {
            fail();
        }
        if(parser1.getOptionValuesMap().size() != 4) {
            fail();
        }
        if(parser1.getOptionValuesMap().get(debug).length != 1) {
            fail();
        }
        if(parser1.getOptionValuesMap().get(verbose) != null) {
            fail();
        }
        if(parser1.getOptionValuesMap().get(reverse) != null) {
            fail();
        }
        if(parser1.getOptionValuesMap().get(reverse2) != null) {
            fail();
        }
        if(parser1.getOptionValuesMap().get(name).length != 1) {
            fail();
        }
        if(parser1.getOptionValuesMap().get(count).length != 2) {
            fail();
        }
        if(parser1.getOptionValuesMap().get(seconds).length != 3) {
            fail();
        }
        if(parser1.getOptionValuesMap().get(height) != null) {
            fail();
        }
        if(parser1.getOptionValuesMap().get(length) != null) {
            fail();
        }
        if(parser1.getOptionValuesMap().get(null) != null) {
            fail();
        }

        if(parser1.getOptionValues(debug).length != 1) {
            fail();
        }
        if(parser1.getOptionValues(verbose) != null) {
            fail();
        }
        if(parser1.getOptionValues(reverse) != null) {
            fail();
        }
        if(parser1.getOptionValues(reverse2) != null) {
            fail();
        }
        if(parser1.getOptionValues(name).length != 1) {
            fail();
        }
        if(parser1.getOptionValues(count).length != 2) {
            fail();
        }
        if(parser1.getOptionValues(seconds).length != 3) {
            fail();
        }
        if(parser1.getOptionValues(height) != null) {
            fail();
        }
        if(parser1.getOptionValues(length) != null) {
            fail();
        }

        try {
            parser1.getOptionValues(null);
            fail();
        } catch(NullPointerException npe) {

        }

        if(!parser1.getOptionValue(debug).equals(new Boolean(false))) {
            fail();
        }
        if(parser1.getOptionValue(verbose) != null) {
            fail();
        }
        if(parser1.getOptionValue(reverse) != null) {
            fail();
        }
        if(parser1.getOptionValue(reverse2) != null) {
            fail();
        }
        if(!parser1.getOptionValue(name).equals("blah")) {
            fail();
        }
        if(!parser1.getOptionValue(count).equals(new Integer(2))) {
            fail();
        }
        if(!parser1.getOptionValue(seconds).equals(new Long(456))) {
            fail();
        }
        if(parser1.getOptionValue(height) != null) {
            fail();
        }
        if(parser1.getOptionValue(length) != null) {
            fail();
        }

        try {
            parser1.getOptionValue(null);
            fail();
        } catch(NullPointerException npe) {

        }

        if(!parser1.getOptionValue(debug, true).equals(new Boolean(false))) {
            fail();
        }
        if(!parser1.getOptionValue(verbose, false).equals(new Boolean(false))) {
            fail();
        }
        if(!parser1.getOptionValue(reverse, true).equals(new Boolean(true))) {
            fail();
        }
        if(!parser1.getOptionValue(reverse2, false).equals(new Boolean(false))) {
            fail();
        }
        if(!parser1.getOptionValue(name, "blah2").equals("blah")) {
            fail();
        }
        if(!parser1.getOptionValue(count, 3).equals(new Integer(2))) {
            fail();
        }
        if(!parser1.getOptionValue(seconds, 777).equals(new Long(456))) {
            fail();
        }
        if(!parser1.getOptionValue(height, 123.445F).equals(new Float(123.445F))) {
            fail();
        }
        if(!parser1.getOptionValue(length, 2342.55E23).equals(new Double(2342.55E23))) {
            fail();
        }

        try {
            parser1.getOptionValue(null, "what");
            fail();
        } catch(NullPointerException npe) {

        }
    }

    @Test
    public void blankResults() {

        String expectedUsage =
            "Usage: <command> [-d|--debug] [argument...]\n" +
            "Options\n-------\n  -d, --debug       [Optional]";                             // TODO: why no [Optional] or (Required)
        String actualUsage = parser2.renderUsageMessage(null, 80, 20);
        assertEquals(expectedUsage, actualUsage);

        try {
            parser2.parse(new String[] {"-d", "a"});
            parser2.parse(new String[] {"-x", "a"});
            fail();
        } catch(CommandLineParseException e) {
            expectedUsage =
                "Invalid Arguments: Unknown option '-x'\n" +
                "Usage: <command> [-d|--debug] [argument...]\n" +
                "Options\n-------\n" +
                "  -d, --debug       [Optional]";
            actualUsage = parser2.renderUsageMessage(null, 80, 20, e);
            assertEquals(expectedUsage, actualUsage);
        }

        // Check with executable label.
        expectedUsage =
            "Usage: test [-d|--debug] [argument...]\n" +
            "Options\n-------\n  -d, --debug       [Optional]";
        actualUsage = parser2.renderUsageMessage("test", 80, 20);
        assertEquals(expectedUsage, actualUsage);

        try {
            parser2.parse(new String[] {"-d", "a"});
            parser2.parse(new String[] {"-x", "a"});
            fail();
        } catch(CommandLineParseException e) {
            expectedUsage =
                "Invalid Arguments: Unknown option '-x'\n" +
                "Usage: test [-d|--debug] [argument...]\n" +
                "Options\n-------\n" +
                "  -d, --debug       [Optional]";
            actualUsage = parser2.renderUsageMessage("test", 80, 20, e);
            assertEquals(expectedUsage, actualUsage);
        }
    }

    public void testParse(CommandLineParser parser, String cmdLine, String valMapExpected, String remArgsExpected) {
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, null, null);
    }
    public void testParse(CommandLineParser parser, String cmdLine, String valMapExpected, String remArgsExpected, Pair[] ordParsedArgsExpected) {
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, null, ordParsedArgsExpected);
    }
    public void testParse(CommandLineParser parser, String cmdLine, String valMapExpected, String remArgsExpected, String exMsgExpected) {
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, exMsgExpected, null);
    }
    public void testParse(CommandLineParser parser, String cmdLine, String valMapExpected, String remArgsExpected, String exMsgExpected, Pair[] ordParsedArgsExpected) {
        String[] args;
        if(cmdLine == null) {
            args = new String[0];
        } else {
            args = cmdLine.split(" ");
        }

        try {
            parser.parse(args);
            if(exMsgExpected != null) {
                fail("NO EXCEPTION OCCURRED");
                return;
            }
        } catch(CommandLineParseException e) {
            if(exMsgExpected == null) {
                fail("(EXCEP \"" + e.getMessage() + "\")");
                return;
            } else if(!exMsgExpected.equals(e.getMessage())){
                fail("(EXCEP \"" + e.getMessage() + "\" != \"" + exMsgExpected + "\")");
                return;
            }
            return;
        }

        String valMapActual = mapToString(parser.getOptionValuesMap());
        if(!valMapActual.equals(valMapExpected)) {
            fail(valMapActual + " != " + valMapExpected);
            return;
        }

        String remMapActual = Arrays.toString(parser.getNonOptionArguments());
        if(!remMapActual.equals(remArgsExpected)) {
            fail(remMapActual + " != " + remArgsExpected);
            return;
        }

        if(ordParsedArgsExpected != null) {
            if(!Arrays.equals(ordParsedArgsExpected, parser.getOrderedParsedArguments())) {
                fail(ordParsedArgsExpected + " != " + parser.getOrderedParsedArguments());
                return;
            }
        }
    }

    @Test
    public void resultsNotParsed() {
        String errMsg;

        Option db = parser2.getOption('d');

        errMsg = "Attempted to access the option values before a successful parse.";
        try { parser2.getOptionValuesMap(); fail(); }
        catch(ResultsNotParsedException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
        errMsg = "Attempted to access the remaining arguments before a successful parse.";
        try { parser2.getNonOptionArguments(); fail(); }
        catch(ResultsNotParsedException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
        errMsg = "Attempted to access the option values before a successful parse.";
        try { parser2.getOptionValue(db); fail(); }
        catch(ResultsNotParsedException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
        errMsg = "Attempted to access the option values before a successful parse.";
        try { parser2.getOptionValue(db, null); fail(); }
        catch(ResultsNotParsedException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
        errMsg = "Attempted to access the option values before a successful parse.";
        try { parser2.getOptionValues(db); fail(); }
        catch(ResultsNotParsedException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
    }

    @Test
    public void invalidName() {
        String errMsg;

        errMsg = "Short form options must be a single non-space character.";
        try { parser2.addBooleanOption((char) 0); fail(); }
        catch(InvalidOptionNameException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
        errMsg = "Short form options must be a single non-space character.";
        try { parser2.addBooleanOption((char) 13); fail(); }
        catch(InvalidOptionNameException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
        errMsg = "Short form options must be a single non-space character.";
        try { parser2.addBooleanOption(' '); fail(); }
        catch(InvalidOptionNameException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
        errMsg = "Short form options cannot be a dash.";
        try { parser2.addBooleanOption('-'); fail(); }
        catch(InvalidOptionNameException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
        errMsg = "Options must have either a short or long form or both.";
        try { parser2.addBooleanOption(null); fail(); }
        catch(InvalidOptionNameException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
        errMsg = "Long form options cannot begin with a dash.";
        try { parser2.addBooleanOption("--what"); fail(); }
        catch(InvalidOptionNameException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
        errMsg = "Long form options cannot contain an equal sign.";
        try { parser2.addBooleanOption("hello=there"); fail(); }
        catch(InvalidOptionNameException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
    }

    @Test
    public void alreadyAdded() {
        String errMsg;

        parser2.addBooleanOption('a', "apple");

        errMsg = "The option '-a' has already been added to the parser.";
        try { parser2.addBooleanOption('a', "orange"); fail(); }
        catch(OptionAlreadyExistsException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
        errMsg = "The option '--apple' has already been added to the parser.";
        try { parser2.addBooleanOption('x', "apple"); fail(); }
        catch(OptionAlreadyExistsException e) {
            if(!e.getMessage().equals(errMsg)) {fail("\"" + e.getMessage() + "\" != \"" + errMsg + "\"");}
        }
    }

    // Custom map toString so that Arrays.toString is called on the values instead
    // of toString.
    private static String mapToString(Map<Option, Object[]> map) {
        if(map == null) {
            return "null";
        }

        String ret = "{";

        for(Option key : map.keySet()) {
            Object[] values = map.get(key);
            ret += key + "=" + Arrays.toString(values) + ", ";
        }

        if(ret.length() != 1) {
            ret = ret.substring(0, ret.length() - 2);
        }

        ret += "}";

        return ret;
    }

    @Test
    public void finalValidator() {
        parser1.addValidator(p -> {
            Object x = p.getOptionValue(p.getOption("-d"));
            if(x != null && (Boolean) x) {
                return "debug mode not supported right now!";
            }
            return null;
        });

        try {
            String[] args = new String[]{"--debug=true"};
            parser1.parse(args);
            fail();
        } catch(CommandLineParseException e) {
//            System.out.println(parser1.getUsageMessage(e, "WHAT", 80, 20));
        }
    }

    @Test
    public void allowMulti() {
        CommandLineParser parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);
        parser.addIntegerOption('c', "count", false, false);
        String[] opts = new String[] {"-c", "10", "--count=20"};
        String expectedErrMsg = "Option '-c/--count' cannot have multiple values.";
        try {
            parser.parse(opts);
            fail();
        } catch(CommandLineParseException e) {
            if(!e.getMessage().equals(expectedErrMsg)) {
                fail("\"" + e.getMessage() + "\" != \"" + expectedErrMsg + "\"");
            }
        }
    }

    @Test
    public void optionValidation() {
        CommandLineParser parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);
        Option opt = parser.addStringOption('s', "string");
        opt.addValidator((OptionValueValidator<String>) (option, value) -> {
            if(value.equals("error")) {
                return "Invalid option value!";
            }
            return null;
        });
        String[] opts = new String[] {"-s", "error"};
        String expectedErrMsg = "Illegal option '-s/--string': invalid value 'error': Invalid option value!";
        try {
            parser.parse(opts);
            fail();
        } catch(CommandLineParseException e) {
            if(!e.getMessage().equals(expectedErrMsg)) {
                fail("\"" + e.getMessage() + "\" != \"" + expectedErrMsg + "\"");
            }
        }
        opts = new String[] {"-s", "noterror"};
        try {
            parser.parse(opts);
        } catch(CommandLineParseException e) {
            fail();
        }
    }

    @Test
    public void extensibleArgs() {

        String cmdLine, valMapExpected, remArgsExpected;

        CommandLineParser parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);
        Option optExec = parser.addBooleanOption("exec");
        Option optX = parser.addIntegerOption('x');

        cmdLine = "-x 9 --exec -----W -x 8 -y QQQ ; -W";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, "Unknown option '-----W'");

        cmdLine = "-x 9 --! --exec -----W -x 8 -y QQQ ; -W";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, "Invalid XArgs escape token ''");

        cmdLine = "-x 9 --!; --exec -----W -x 8 -y QQQ ; -W";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, "Unknown option '-----W'");

        optExec.setHasXArgs(true);     // Not usually set this late, but OK for testing.

        cmdLine = "-x 9 --exec -----W -x 8 -y QQQ ; -W";
        valMapExpected = "{-x=[9], --exec=[true]}";
        remArgsExpected = "[]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected);

        String[] args = new String[] {"-----W", "-x", "8", "-y", "QQQ", ";", "-W"};
        testXArgs(parser, optExec, optX, args, null);

        cmdLine = "a -x 9 --exec -----W -x 8 -y QQQ ; -W";
        valMapExpected = "{-x=[9], --exec=[true]}";
        remArgsExpected = "[a]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected);

        args = new String[] {"-----W", "-x", "8", "-y", "QQQ", ";", "-W"};
        testXArgs(parser, optExec, optX, args, null);

        cmdLine = "a -x 9 --!_ --exec -----W -x 8 -y QQQ ; -W";
        valMapExpected = "{-x=[9], --exec=[true]}";
        remArgsExpected = "[a]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected);

        args = new String[] {"-----W", "-x", "8", "-y", "QQQ", ";", "-W"};
        testXArgs(parser, optExec, optX, args, null);

        cmdLine = "a -x 9 --!; --exec -----W -x 8 -y QQQ ; -W";
        valMapExpected = "{-x=[9], --exec=[true]}";
        remArgsExpected = "[a]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, "Unknown option '-W'");

        cmdLine = "a -x 9 --!; --exec -----W -x 8 -y QQQ ; W";
        valMapExpected = "{-x=[9], --exec=[true]}";
        remArgsExpected = "[a, W]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected);

        args = new String[] {"-----W", "-x", "8", "-y", "QQQ"};
        testXArgs(parser, optExec, optX, args, null);

        cmdLine = "a -x 9 --!; --exec -----W -x 8 -y QQQ ; W X --!^ --exec A B";
        valMapExpected = "{-x=[9], --exec=[true, true]}";
        remArgsExpected = "[a, W, X]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected);

        args = new String[] {"-----W", "-x", "8", "-y", "QQQ"};
        String[] args2 = new String[] {"A", "B"};
        testXArgs(parser, optExec, optX, args, args2);

        cmdLine = "a -x 9 --!; --exec -----W -x 8 -y QQQ ; W X --!^ --exec A B ;";
        valMapExpected = "{-x=[9], --exec=[true, true]}";
        remArgsExpected = "[a, W, X]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected);

        args = new String[] {"-----W", "-x", "8", "-y", "QQQ"};
        args2 = new String[] {"A", "B", ";"};
        testXArgs(parser, optExec, optX, args, args2);

        Pair<Option<?>, Object>[] pairs = new Pair[] {
            new Pair<>(null, "a"),
            new Pair<>(optX, 9),
            new Pair<>(optExec, true),
            new Pair<>(null, "W"),
            new Pair<>(null, "X"),
            new Pair<>(optExec, true),
            new Pair<>(null, "h"),
            new Pair<>(optX, 77)
        };

        cmdLine = "a -x 9 --!; --exec -----W -x 8 -y QQQ ; W X --!^ --exec A B ; ^ h -x 77";
        valMapExpected = "{-x=[9, 77], --exec=[true, true]}";
        remArgsExpected = "[a, W, X, h]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, pairs);

        args = new String[] {"-----W", "-x", "8", "-y", "QQQ"};
        args2 = new String[] {"A", "B", ";"};
        testXArgs(parser, optExec, optX, args, args2);

        cmdLine = "a -x 9 --!; --exec -----W -x 8 -y QQQ ; W X --!^ --exec";
        valMapExpected = "{-x=[9], --exec=[true, true]}";
        remArgsExpected = "[a, W, X]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected);

        args = new String[] {"-----W", "-x", "8", "-y", "QQQ"};
        args2 = new String[] {};
        testXArgs(parser, optExec, optX, args, args2);

        optExec.setAllowMulti(false);

        cmdLine = "a -x 9 --!; --exec -----W -x 8 -y QQQ ; W X --!^ --exec A B ; ^ h -x 77";
        valMapExpected = "N/A";
        remArgsExpected = "N/A";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, "Option '--exec' cannot have multiple values.");

        // Test default escape token
        CommandLineParser parser2 = new CommandLineParser();
        parser2.setAutoPrintParseException(false);
        Option optExec2 = parser2.addBooleanOption("exec").setHasXArgs(true);
        Option optX2 = parser2.addIntegerOption('x');
        parser2.setDefaultXArgsEscapeToken(";");

        cmdLine = "-x 9 --exec -----W -x 8 -y QQQ ; W";
        valMapExpected = "{-x=[9], --exec=[true]}";
        remArgsExpected = "[W]";
        testParse(parser2, cmdLine, valMapExpected, remArgsExpected);

        args = new String[] {"-----W", "-x", "8", "-y", "QQQ"};
        testXArgs(parser2, optExec2, optX2, args, null);

        cmdLine = "-x 9 --exec -----W -x 8 -y QQQ ; W --exec A B ; Z";
        valMapExpected = "{-x=[9], --exec=[true, true]}";
        remArgsExpected = "[W, Z]";
        testParse(parser2, cmdLine, valMapExpected, remArgsExpected);

        args = new String[] {"-----W", "-x", "8", "-y", "QQQ"};
        args2 = new String[] {"A", "B"};
        testXArgs(parser2, optExec2, optX2, args, args2);
    }

    private void testXArgs(CommandLineParser parser, Option optExec, Option optX, String[] args, String[] args2) {
        String[]   x = parser.getXArgsArray(optX);   assertNull(x);
        String[][] x2 = parser.getXArgsArrays(optX); assertNull(x2);
        String[]   e = parser.getXArgsArray(optExec);
        String[][] e2 = parser.getXArgsArrays(optExec);
        assertTrue(Arrays.equals(args, e));
        assertTrue(Arrays.equals(args, e2[0]));

        if(args2 == null) {
            assertEquals(1, e2.length);
        } else {
            assertTrue(Arrays.equals(args2, e2[1]));
            assertEquals(2, e2.length);
        }
    }

    @Test
    public void argFile() throws IOException {
        String content = FileUtil.getTextContent(CommandLineParserTest.class.getResourceAsStream("test-argfile.txt"));
        File tmp = File.createTempFile("clpaf", null);
        tmp.deleteOnExit();
        String tmpStr = tmp.getAbsolutePath();
        FileUtil.writeTextContent(tmp, content);

        CommandLineParser parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);
        String cmdLine = "--argfile=" + tmpStr;
        String valMapExpected = "{--argfile=[" + tmpStr + "]}";
        String remArgsExpected = "[]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, "Unknown option '--argfile'");

        parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);
        parser.setArgFileConfig(new ArgFileConfig());

        cmdLine = "--argfile=" + tmpStr;
        valMapExpected = "{--argfile=[" + tmpStr + "]}";
        remArgsExpected = "[]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, "Unknown option '--age'");

        parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);
        parser.setArgFileConfig(new ArgFileConfig().setOptionName("config"));

        cmdLine = "--argfile=" + tmpStr;
        valMapExpected = "{--argfile=[" + tmpStr + "]}";
        remArgsExpected = "[]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected, "Unknown option '--argfile'");

        parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);
        parser.setArgFileConfig(new ArgFileConfig().setOptionName("config"));
        parser.addIntegerOption("age");
        parser.addBooleanOption('b');
        parser.addDoubleOption("size");
        parser.addDoubleOption("size2");

        cmdLine = "--config=" + tmpStr + " a b --age=65 --size=5.5";
        valMapExpected = "{--config=[" + tmpStr + "], --age=[34], --size=[5.5], -b=[true], --size2=[6.6, 7.7]}";
        remArgsExpected = "[a, b, some, more, words, ok?]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected);
    }

    @Test
    public void argFilePathsRelOption() throws IOException {
        String content = FileUtil.getTextContent(CommandLineParserTest.class.getResourceAsStream("test-argfile-paths.txt"));
        File path1;
        if(OsUtil.isWindows()) {
            path1 = new File("C:\\An\\Absolute\\Path.txt");
        } else {
            path1 = new File("/An/Absolute/Path.txt");
        }
        content += "\n--path1=" + path1.getAbsolutePath() + "\n";
        File tmp = File.createTempFile("clpaf", null);
        tmp.deleteOnExit();
        String tmpStr = tmp.getAbsolutePath();
        FileUtil.writeTextContent(tmp, content);

        CommandLineParser parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);
        parser.setArgFileConfig(
            new ArgFileConfig()
                .setOptionName("config")
                .setPathsRelToFileOptionName("configpathsrel")    // Not specified below (so it'll be essentially false)
        );
        parser.addIntegerOption("age");
        parser.addBooleanOption('b');
        parser.addDoubleOption("size");
        parser.addDoubleOption("size2");
        parser.addPathOption("path0");
        parser.addPathOption("path1");

        assertEquals(8, parser.getOptions().size());
        String path0Resolved = OsUtil.isWindows() ? "a\\b" : "a/b";

        String cmdLine = "--config=" + tmpStr + " a b --age=65 --size=5.5";
        String valMapExpected = "{--config=[" + tmpStr + "], --age=[34], --size=[5.5], -b=[true], --size2=[6.6, 7.7], --path0=[" + path0Resolved + "], --path1=[" + path1.getAbsolutePath() + "]}";
        String remArgsExpected = "[a, b, some, more, words, ok?]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected);

        parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);
        parser.setArgFileConfig(
            new ArgFileConfig()
                .setOptionName("config")
                .setPathsRelToFileOptionName("configpathsrel")
        );
        parser.addIntegerOption("age");
        parser.addBooleanOption('b');
        parser.addDoubleOption("size");
        parser.addDoubleOption("size2");
        parser.addPathOption("path0");
        parser.addPathOption("path1");

        assertEquals(8, parser.getOptions().size());

        File resolved = FileUtil.relativeTo(new File("a/b"), tmp.getParentFile());

        cmdLine = "--config=" + tmpStr + " a b --age=65 --size=5.5 --configpathsrel";
        valMapExpected = "{--config=[" + tmpStr + "], --age=[34], --size=[5.5], --configpathsrel=[true], -b=[true], --size2=[6.6, 7.7], --path0=[" + resolved.getAbsolutePath() + "], --path1=[" + path1.getAbsolutePath() + "]}";
        remArgsExpected = "[a, b, some, more, words, ok?]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected);
        // -b and --size2 are after --configpathsrel because they are the new
        // options from the argfile that aren't in the command line proper and
        // are added to the map afterwards.
    }

    @Test
    public void argFilePathsRelOptionInArgsFileItself() throws IOException {
        String content = FileUtil.getTextContent(CommandLineParserTest.class.getResourceAsStream("test-argfile-paths2.txt"));
        File path1;
        if(OsUtil.isWindows()) {
            path1 = new File("C:\\An\\Absolute\\Path.txt");
        } else {
            path1 = new File("/An/Absolute/Path.txt");
        }
        content += "\n--path1=" + path1.getAbsolutePath() + "\n";
        File tmp = File.createTempFile("clpaf", null);
        tmp.deleteOnExit();
        String tmpStr = tmp.getAbsolutePath();
        FileUtil.writeTextContent(tmp, content);

        CommandLineParser parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);
        parser.setArgFileConfig(
            new ArgFileConfig()
                .setOptionName("config")
                .setPathsRelToFileOptionName("configpathsrel")    // Not specified below but provided in file
        );
        parser.addIntegerOption("age");
        parser.addBooleanOption('b');
        parser.addDoubleOption("size");
        parser.addDoubleOption("size2");
        parser.addPathOption("path0");
        parser.addPathOption("path1");

        assertEquals(8, parser.getOptions().size());

        File resolved = FileUtil.relativeTo(new File("a/b"), tmp.getParentFile());

        String cmdLine = "--config=" + tmpStr + " a b --age=65 --size=5.5";
        String valMapExpected = "{--config=[" + tmpStr + "], --age=[34], --size=[5.5], --configpathsrel=[true], -b=[true], --size2=[6.6, 7.7], --path0=[" + resolved.getAbsolutePath() + "], --path1=[" + path1.getAbsolutePath() + "]}";
        String remArgsExpected = "[a, b, some, more, words, ok?]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected);
    }

    @Test
    public void argFilePathsRelOptionInArgsFileItselfPathMustExist() throws IOException {
        String content = FileUtil.getTextContent(CommandLineParserTest.class.getResourceAsStream("test-argfile-paths2.txt"));
        File path1 = File.createTempFile("clpaf-sibling", null);
        path1.deleteOnExit();
        content += "\n--path1=./" + path1.getName() + "\n";
        File tmp = File.createTempFile("clpaf", null);
        tmp.deleteOnExit();
        String tmpStr = tmp.getAbsolutePath();
        FileUtil.writeTextContent(tmp, content);

        CommandLineParser parser = new CommandLineParser();
        parser.setAutoPrintParseException(false);
        parser.setArgFileConfig(
            new ArgFileConfig()
                .setOptionName("config")
                .setPathsRelToFileOptionName("configpathsrel")    // Not specified below but provided in file
        );
        parser.addIntegerOption("age");
        parser.addBooleanOption('b');
        parser.addDoubleOption("size");
        parser.addDoubleOption("size2");
        parser.addPathOption("path0");
        parser.addPathOption("path1").setMustExist(true);

        assertEquals(8, parser.getOptions().size());

        File resolved = FileUtil.relativeTo(new File("a/b"), tmp.getParentFile());
        File path1Resolved = new File(tmp.getParentFile(), "./" + path1.getName());

        String cmdLine = "--config=" + tmpStr + " a b --age=65 --size=5.5";
        String valMapExpected = "{--config=[" + tmpStr + "], --age=[34], --size=[5.5], --configpathsrel=[true], -b=[true], --size2=[6.6, 7.7], --path0=[" + resolved.getAbsolutePath() + "], --path1=[" + path1Resolved.getAbsolutePath() + "]}";
        String remArgsExpected = "[a, b, some, more, words, ok?]";
        testParse(parser, cmdLine, valMapExpected, remArgsExpected);
    }
}
