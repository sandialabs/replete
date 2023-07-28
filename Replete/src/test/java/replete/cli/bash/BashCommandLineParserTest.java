package replete.cli.bash;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import replete.text.StringUtil;

public class BashCommandLineParserTest {

    private static BashCommandLineParser parser = new BashCommandLineParser();

    private static Map<String, String> env1 = new HashMap<>();

    static {
        env1.put("L", "lion");
        env1.put("T", "tiger");
    }

    @Test
    public void blank() {
        test(null,
            null,
            NullPointerException.class,
            null);
        test("",
            null,
            ParseState.SPACE,
            null,
            null,
            null,
            new String[0],
            true,
            "",
            "");
        test("    ",
            null,
            ParseState.SPACE,
            null,
            null,
            null,
            new String[0],
            true,
            "",
            "");
        test(" \t   ",
            null,
            ParseState.SPACE,
            null,
            null,
            null,
            new String[0],
            true,
            "",
            "");
    }

    @Test
    public void oneArgumentSimple() {
        test("test",
            null,
            ParseState.FREE,
            "test",
            "test",
            null,
            new String[] {"test"},
            true,
            "0=[0,4]",
            "");
        test(" test",
            null,
            ParseState.FREE,
            "test",
            "test",
            null,
            new String[] {"test"},
            true,
            "0=[1,5]",
            "");
        test("\ttest",
            null,
            ParseState.FREE,
            "test",
            "test",
            null,
            new String[] {"test"},
            true,
            "0=[1,5]",
            "");
        test("  test  ",
            null,
            ParseState.SPACE,
            null,
            null,
            null,
            new String[] {"test"},
            true,
            "0=[2,6]",
            "");
    }

    @Test
    public void invalidEscapeSequence() {

    }

    @Test
    public void oneArgumentEscape() {
        test("te\\\\st",
            null,
            ParseState.FREE,
            "te\\st",
            "te\\st",
            null,
            new String[] {"te\\st"},
            true,
            "0=[0,6]",
            "");
        test(" te\\\"st",
            null,
            ParseState.FREE,
            "te\"st",
            "te\"st",
            null,
            new String[] {"te\"st"},
            true,
            "0=[1,7]",
            "");
        test("\ttest\\\'",
            null,
            ParseState.FREE,
            "test'",
            "test'",
            null,
            new String[] {"test'"},
            true,
            "0=[1,7]",
            "");
        test("  test\\ \\  ",
            null,
            ParseState.SPACE,
            null,
            null,
            null,
            new String[] {"test  "},
            true,
            "0=[2,10]",
            "");
    }

    @Test
    public void validEscapeChars() {
        test("cmd test\\\"\\'\\\\\\ foo",
            null,
            ParseState.FREE,
            "test\"'\\ foo",
            "test\"'\\ foo",
            null,
            new String[] {"cmd", "test\"'\\ foo"},
            true,
            "0=[0,3];1=[4,19]",
            "");
        test("cmd \"test\\\"\\'\\\\\\ foo\"",
            null,
            ParseState.FREE,
            "test\"\\'\\\\ foo",
            "",                         // FREE ARG DESIRED BEHAVIOR?
            null,
            new String[] {"cmd", "test\"\\'\\\\ foo"},
            true,
            "0=[0,3];1=[4,21]",
            "");
        test("cmd 'test\\\"\\'\\\\\\ foo",    // After Compile => [cmd 'test\"\'\\\ foo] => ["cmd", "test\"\\ foo"]
            null,
            ParseState.FREE,
            "test\\\"\\\\ foo",               // After Compile => [test\"\\ foo]
            "\\ foo",                         // After Compile => [\ foo]
            null,
            new String[] {"cmd", "test\\\"\\\\ foo"},
            true,
            "0=[0,3];1=[4,20]",
            "");
        test("cmd 'test\\\"\\'\\\\\\ foo'",    // After Java => [cmd 'test\"\'\\\ foo] => ["cmd", "test\"\\ foo"]
            null,
            ParseState.SINGLE_QUOTES,
            "test\\\"\\\\ foo",
            null,
            "",
            new String[] {"cmd"},
            false,
            "0=[0,3];1=[4,-1]",
            "");
        // derek, next step, complete this section, and keep refactoring
        // the escape sequences, to make sure impl correctly
    }

    @Test
    public void oneArgumentQuotes() {
        test("\"test\"",
            null,
            ParseState.FREE,
            "test",
            "",                         // FREE ARG DESIRED BEHAVIOR?
            null,
            new String[] {"test"},
            true,
            "0=[0,6]",
            "");
        test(" \"test\"",
            null,
            ParseState.FREE,
            "test",
            "",                         // FREE ARG DESIRED BEHAVIOR?
            null,
            new String[] {"test"},
            true,
            "0=[1,7]",
            "");
        test("\"\ttest\"",
            null,
            ParseState.FREE,
            "\ttest",
            "",                         // FREE ARG DESIRED BEHAVIOR?
            null,
            new String[] {"\ttest"},
            true,
            "0=[0,7]",
            "");
        test("  \"test  \"",
            null,
            ParseState.FREE,
            "test  ",
            "",                         // FREE ARG DESIRED BEHAVIOR?
            null,
            new String[] {"test  "},
            true,
            "0=[2,10]",
            "");
        test("  \"test foo\"",
            null,
            ParseState.FREE,
            "test foo",
            "",                         // FREE ARG DESIRED BEHAVIOR?
            null,
            new String[] {"test foo"},
            true,
            "0=[2,12]",
            "");
    }

    @Test
    public void multipleArgumentQuotes() {
        test("\"cmd here\" 'and here'",
            null,
            ParseState.FREE,
            "and here",
            "",
            null,
            new String[] {"cmd here", "and here"},
            true,
            "0=[0,10]; 1=[11,21]",
            "");
        test("\"cmd here \" 'and here ' nonquote\\  ",
            null,
            ParseState.SPACE,
            null,
            null,
            null,
            new String[] {"cmd here ", "and here ", "nonquote "},
            true,
            "0=[0,11]; 1=[12,23]; 2=[24, 34]",
            "");
    }

    @Test
    public void uncategorized() {
        test("test hi $A",
            args("A", "blue knight"),
            ParseState.FREE,
            "knight",
            "knight",
            null,
            new String[] {"test", "hi", "blue", "knight"},
            true,
            "0=[0,4]; 1=[5,7]; 2=[8,10]",
            "");
        test("test hi $A",
            args("A", "blue\\ knight"),
            ParseState.FREE,
            "knight",
            "knight",
            null,
            new String[] {"test", "hi", "blue\\", "knight"},
            true,
            "0=[0,4]; 1=[5,7]; 2=[8,10]",
            "");
        test("test hi\\ $A",
            args("A", "blue\\ knight"),
            ParseState.FREE,
            "knight",
            "knight",
            null,
            new String[] {"test", "hi blue\\", "knight"},
            true,
            "0=[0,4]; 1=[5,11]",
            "");
        test("test \"abc-$unk\"",
            null,
            ParseState.FREE,
            "abc-",
            "",
            null,
            new String[] {"test", "abc-"},
            true,
            "0=[0,4]; 1=[5,15]",
            "");
        test("echo \"blue  knight\"",
            null,
            ParseState.FREE,
            "blue  knight",
            "",
            null,
            new String[] {"echo", "blue  knight"},
            true,
            "0=[0,4]; 1=[5,19]",
            "");
        test("test abc",
            args("AB", "BK"),
            ParseState.FREE,
            "abc",
            "abc",
            null,
            new String[] {"test", "abc"},
            true,
            "0=[0,4]; 1=[5,8]",
            "");
        test("test abc ",
            args("AB", "BK"),
            ParseState.SPACE,
            null,
            null,
            null,
            new String[] {"test", "abc"},
            true,
            "0=[0,4]; 1=[5,8]",
            "");
        test("test 'what'",
            args("AB", "BK"),
            ParseState.FREE,
            "what",
            "",
            null,
            new String[] {"test", "what"},
            true,
            "0=[0,4]; 1=[5,11]",
            "");
        test("test 'what'",
            args("AB", "BK"),
            ParseState.FREE,
            "what",
            "",
            null,
            new String[] {"test", "what"},
            true,
            "0=[0,4]; 1=[5,11]",
            "");
        test("test \"what$unk",
            null,
            ParseState.DOUBLE_QUOTES,
            null,
            null,
            "what$unk",
            new String[] {"test"},
            false,
            "0=[0,4]; 1=[5,-1]",
            "");
        test("test \"what\" what\"$AB\"where\"y",
            args("AB", "BK"),
            ParseState.DOUBLE_QUOTES,
            "whatBKwhere",
            null,
            "y",
            new String[] {"test", "what"},
            false,
            "0=[0,4]; 1=[5,11]; 2=[12,-1]",
            "");
        test("test \"what\" abc'123''$xyz''what\"$AB\"hi\"y",
            args("AB", "BK"),
            ParseState.SINGLE_QUOTES,
            "abc123$xyz",
            null,
            "what\"$AB\"hi\"y",
            new String[] {"test", "what"},
            false,
            "0=[0,4]; 1=[5,11]; 2=[12,-1]",
            "");
        test("echo $AB\"hi\"",
            args("AB", "blue knight"),
            ParseState.FREE,
            "knighthi",
            "",
            null,
            new String[] {"echo", "blue", "knighthi"},
            true,
            "0=[0,4]; 1=[5,12]",
            "");
        test("echo $ABx\"hi\"",
            args("AB", "blue knight"),
            ParseState.FREE,
            "hi",
            "",
            null,
            new String[] {"echo", "hi"},
            true,
            "0=[0,4]; 1=[5,13]",
            "");
        test("\\\\",
            null,
            ParseState.FREE,
            "\\",
            "\\",
            null,
            new String[] {"\\"},
            true,
            "0=[0,2]",
            "");
        test("\\\\",
            null,
            ParseState.FREE,
            "\\",
            "\\",
            null,
            new String[] {"\\"},
            true,
            "0=[0,2]",
            "");
        test("$another",
            args("another", "why   are you here"),
            ParseState.FREE,
            "here",
            "here",
            null,
            new String[] {"why", "are", "you", "here"},
            true,
            "0=[0,8]",
            "");
        test("echo ${ABC}",
            args("ABC", null),
            ParseState.FREE,
            null,                 // TODO WHY IS THIS NULL?
            "${ABC}",
            null,
            new String[] {"echo"},
            true,
            "0=[0,4]; 1=[5,-1]",  // TODO WHY???
            "");
        test("echo \"${ABC}\"",
            args("ABC", null),
            ParseState.FREE,
            "",
            "",
            null,
            new String[] {"echo", ""},
            true,
            "0=[0,4]; 1=[5,13]",
            "");
        test("find $another",
            args("another", "why   are you here"),
            ParseState.FREE,
            "here",
            "here",
            null,
            new String[] {"find", "why", "are", "you", "here"},
            true,
            "0=[0,4];1=[5,13]",
            "");
        test("test \"$another\"",
            args("another", "why   are you here"),
            ParseState.FREE,
            "why   are you here",
            "",
            null,
            new String[] {"test", "why   are you here"},
            true,
            "0=[0,4];1=[5,15]",
            "");
        test("./mv '$another'",
            args("another", "why   are you here"),
            ParseState.FREE,
            "$another",
            "",
            null,
            new String[] {"./mv", "$another"},
            true,
            "0=[0,4];1=[5,15]",
            "");
        test("mv 'john\\'s first name'",
            null,
            ParseState.SINGLE_QUOTES,
            "name",
            null,
            "",
            new String[] {"mv", "john\\s", "first"},
            false,
            "0=[0,2];1=[3,11];2=[12,17];3=[18,-1]",
            "");
        test("mv ${NAME}",
            args("NAME", "Tony!"),
            ParseState.FREE,
            "Tony!",
            "Tony!",
            null,
            new String[] {"mv", "Tony!"},
            true,
            "0=[0,2];1=[3,10]",
            "");
        test("cs \"My World\"",
            null,
            ParseState.FREE,
            "My World",
            "",
            null,
            new String[] {"cs", "My World"},
            true,
            "0=[0,2];1=[3,13]",
            "");
        test("cs 'My World'",
            null,
            ParseState.FREE,
            "My World",
            "",
            null,
            new String[] {"cs", "My World"},
            true,
            "0=[0,2];1=[3,13]",
            "");
        test("echo \"I'd say: \\\"Go for it!\\\"\"",
            null,
            ParseState.FREE,
            "I'd say: \"Go for it!\"",
            "",
            null,
            new String[] {"echo", "I'd say: \"Go for it!\""},
            true,
            "0=[0,4];1=[5,30]",
            "");
    }


    // test somewhere
    //        envMap.put("A", "derek trumbo");
    // System.out.println(parser.parseWithResult("test hi\\ $A"));
    //originalLine = test hi\ $A
    //state = FREE
    //currentArg = trumbo            // check this behavior
    //freeArg = trumbo               // check this behavior
    //quoteArg = null
    //arguments = [test, hi derek, trumbo]
    //complete = true
    //topLevelArgumentRanges = {0=ArgumentRange[start=0,endNonIncl=4], 1=ArgumentRange[start=5,endNonIncl=11]}
    //topLevelArgumentMap = {test=null, hi\ $A=null}

    //export A="derek\ trumbo"
    //echo $A   =>   "derek\ trumbo"
    // ./test hi\ $A
    // [hi derek\]  & [trumbo]   basically bash does not recognize "\ " within " ".
    // "\"" and "\$" are still recognized


    ////////////
    // HELPER //
    ////////////

    private Map<String, String> args(String... args) {
        Map<String, String> map = new LinkedHashMap<>();
        for(int i = 0; i < args.length; i += 2) {
            if(i == args.length - 1) {
                map.put(args[i], null);
            } else {
                map.put(args[i], args[i + 1]);
            }
        }
        return map;
    }

    private void test(
            String originalLine,
            Map<String, String> env,
            Class<? extends Exception> expectedExceptionType,
            String expectedExceptionMessage) {

        test(
            originalLine,
            env,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            null,
            expectedExceptionType,
            expectedExceptionMessage);

    }

    private void test(
            String originalLine,
            Map<String, String> env,
            ParseState expectedState,
            String expectedCurrentArg,
            String expectedFreeArg,
            String expectedQuoteArg,
            String[] expectedArguments,
            boolean expectedComplete,
            String expectedTopLevelArgRangesStr,
            String expectedTopLevelArgMapStr) {

        test(
            originalLine,
            env,
            expectedState,
            expectedCurrentArg,
            expectedFreeArg,
            expectedQuoteArg,
            expectedArguments,
            expectedComplete,
            expectedTopLevelArgRangesStr,
            expectedTopLevelArgMapStr,
            null,
            null);

    }

    private void test(
            String originalLine,
            Map<String, String> env,
            ParseState expectedState,
            String expectedCurrentArg,
            String expectedFreeArg,
            String expectedQuoteArg,
            String[] expectedArguments,
            boolean expectedComplete,
            String expectedTopLevelArgRangesStr,
            String expectedTopLevelArgMapStr,
            Class<? extends Exception> expectedExceptionType,
            String expectedExceptionMessage) {

        parser.setEnvironment(env);

        ParseResult result = null;
        try {
            result = parser.parseWithResult(originalLine);
        } catch(Exception e) {
            if(expectedExceptionType != null) {
                assertEquals(expectedExceptionType, e.getClass());
                assertEquals(expectedExceptionMessage, e.getMessage());
                return;
            }
            fail("Exception occured: " + e);
        }

        // A non-null state variable will signal to check result.
        if(expectedState != null) {
            assertEquals("{ORIG-LINE}", originalLine, result.getOriginalLine());
            assertEquals("{STATE}", expectedState, result.getState());
            assertEquals("{CURRENT-ARG}", expectedCurrentArg, result.getCurrentArgument());
            assertEquals("{FREE-ARG}", expectedFreeArg, result.getFreeArgument());
            assertEquals("{QUOTE-ARG}", expectedQuoteArg, result.getQuoteArgument());
            assertArrayEquals("{ARGUMENTS}", expectedArguments, result.getArguments());
            assertEquals("{COMPLETE}", expectedComplete, result.isComplete());
            if(expectedTopLevelArgRangesStr == null) {
                assertEquals("{TOP-LEVEL-ARG-RANGES}", expectedTopLevelArgRangesStr, result.getTopLevelArgumentRanges());
            } else {
                ArgumentRangeMap expectedTopLevelArgumentRanges =
                    parseTopLevelArgumentRangesStr(originalLine, expectedTopLevelArgRangesStr);
                if(!expectedTopLevelArgumentRanges.equals(result.getTopLevelArgumentRanges())) {
                    System.out.println("Expected: " + expectedTopLevelArgumentRanges);
                    System.out.println("Actual:   " + result.getTopLevelArgumentRanges());
                }
                assertEquals("{TOP-LEVEL-ARG-RANGES}", expectedTopLevelArgumentRanges, result.getTopLevelArgumentRanges());
            }
        }
    }

    private ArgumentRangeMap parseTopLevelArgumentRangesStr(String originalLine, String rangesStr) {
        ArgumentRangeMap topLevelArgumentRanges = new ArgumentRangeMap();
        if(!rangesStr.equals("")) {
            String[] ranges = rangesStr.split("\\s*;\\s*");
            for(String range : ranges) {
                String[] kv = range.split("\\s*=\\s*");
                int key = Integer.parseInt(kv[0]);
                String v = StringUtil.cut(StringUtil.snip(kv[1], 1), 1);
                String[] rangeParts = v.split("\\s*,\\s*");
                int start = Integer.parseInt(rangeParts[0]);
                int end = rangeParts[1].equals("#") ? originalLine.length() : Integer.parseInt(rangeParts[1]);
                ArgumentRange argRange = new ArgumentRange(start);
                argRange.setEndNonIncl(end);
                topLevelArgumentRanges.put(key, argRange);
            }
        }
        return topLevelArgumentRanges;
    }
}
