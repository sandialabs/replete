package replete.cli;

import replete.cli.options.Option;
import replete.text.StringUtil;

/**
 * @author Derek Trumbo
 */

public class ParserRenderer {


    ///////////
    // FIELD //
    ///////////

    private CommandLineParser parser;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ParserRenderer(CommandLineParser parser) {
        this.parser = parser;
    }


    ///////////////
    // RENDERING //
    ///////////////

    // Options in short/long form

    private String renderShortFormOption(Option opt, String shortForm) {
        String optText = CommandLineParser.DASH + shortForm;
        if(opt.wantsValue()) {
            boolean print = parser.isPrintParamDelimiters();
            String ld = print ? "<" : "";
            String rd = print ? ">" : "";
            String pName = opt.getHelpParamName();
            pName = (pName == null) ? "VAL" : pName;
            optText += " " + ld + pName + rd;
        }
        return optText;
    }

    private String renderLongFormOption(Option opt, String longForm) {
        String optText = CommandLineParser.DASH_DASH + longForm;
        if(opt.wantsValue()) {
            boolean print = parser.isPrintParamDelimiters();
            String ld = print ? "<" : "";
            String rd = print ? ">" : "";
            String pName = opt.getHelpParamName();
            pName = (pName == null) ? "VAL" : pName;
            optText += "=" + ld + pName + rd;
        }
        return optText;
    }

    // Usage line parts

    public String renderOptionUsageString() {
        String result = "";
        for(Option opt : parser.getOptions()) {
            if(!opt.isRequired()) {
                result += "[";
            }
            if(opt.getShortForm() != null) {
                result += renderShortFormOption(opt, opt.getShortForm());
            }
            if(opt.getLongForm() != null) {
                if(opt.getShortForm() != null) {
                    result += "|";
                }
                result += renderLongFormOption(opt, opt.getLongForm());
            }
            if(!opt.isRequired()) {
                result += "]";
            }
            if(opt.hasXArgs()) {
                result += " [XARGS...]";
            }
            result += " ";
        }
        return result.trim();
    }

    public String renderNonOptionString() {

        // Params:
        //   minNonOptions is 0 or >0
        //   maxNonOptions is INF or <INF
        //   nonOptNames is null or not
        //   nonOptNames.length is <min, >max or between min & max

        // if maxNonOptions is not INF, we can show options,
        // if minNonOptions is not 0, we can show options,
        // if nonOptNames is not null, we can show options

        String ret = "";
        int minNonOptions = parser.getMiniumNonOptionArguments();
        int maxNonOptions = parser.getMaximumNonOptionArguments();
        String[] nonOptNames = parser.getNonOptNames();

        boolean print = parser.isPrintParamDelimiters();
        String ld = print ? "<" : "";
        String rd = print ? ">" : "";

        String dfltVar = "argument";
        String optDfltVar = "[" + ld + dfltVar + rd + "...]";

        if(minNonOptions == 0) {
            if(maxNonOptions == Integer.MAX_VALUE) {
                if(nonOptNames == null) {
                    // [<argument>...]
                    ret += optDfltVar;
                } else {
                    // [<one>] [<two>] [<three>] [<argument>...]
                    for(String s : nonOptNames) {
                        ret += "[" + ld + s + rd + "] ";
                    }
                    ret += optDfltVar;
                }
            } else {
                if(nonOptNames == null) {
                    // [<arg1>] [<arg2>] [<arg3>] [<arg4>] [<arg5>]
                    for(int i = 0; i < maxNonOptions; i++) {
                        ret += "[" + ld + "arg" + (i + 1) + rd + "] ";
                    }
                    ret = ret.trim();
                } else {
                    // [<one>] [<two>] [<three>] [<arg4>] [<arg5>]
                    // [<one>] [<two>] [<three>]
                    // Max Options Trumps Names
                    for(int i = 0; i < maxNonOptions; i++) {
                        if(i < nonOptNames.length) {
                            ret += "[" + ld + nonOptNames[i] + rd + "] ";
                        } else {
                            ret += "[" + ld + "arg" + (i + 1) + rd + "] ";
                        }
                    }
                    ret = ret.trim();
                }
            }
        } else {
            if(maxNonOptions == Integer.MAX_VALUE) {
                if(nonOptNames == null) {
                    // <arg1> <arg2> <arg3> [<argument>...]
                    for(int i = 0; i < minNonOptions; i++) {
                        ret += ld + "arg" + (i + 1) + rd + " ";
                    }
                    ret += optDfltVar;
                } else {
                    // <one> <two> <three> <arg4> [<argument>...]
                    // <one> <two> [<three>] [<argument>...]
                    // <one> <two> [<three>...]
                    for(int i = 0; i < minNonOptions; i++) {
                        if(i < nonOptNames.length) {
                            ret += ld + nonOptNames[i] + rd + " ";
                        } else {
                            ret += ld + "arg" + (i+1) + rd + " ";
                        }
                    }
                    boolean rest = false;
                    for(int i = minNonOptions; i < nonOptNames.length; i++) {
                        String ddd = "";
                        String add = nonOptNames[i];
                        if(add.endsWith("...")) {
                            add = StringUtil.cut(add, 3);
                            ddd = "...";
                            rest = true;
                        }
                        ret += "[" + ld + add + rd + ddd + "] ";
                    }
                    if(!rest) {
                        ret += optDfltVar;
                    }
                    ret = ret.trim();
                }
            } else {
                if(nonOptNames == null) {
                    for(int i = 0; i < minNonOptions; i++) {
                        ret += ld + "arg" + (i + 1) + rd + " ";
                    }
                    for(int i = minNonOptions; i < maxNonOptions; i++) {
                        ret += "[" + ld + "arg" + (i + 1) + rd + "] ";
                    }
                    ret = ret.trim();
                } else {
                    for(int i = 0; i < minNonOptions; i++) {
                        if(i < nonOptNames.length) {
                            ret += ld + nonOptNames[i] + rd + " ";
                        } else {
                            ret += ld + "arg" + (i + 1) + rd + " ";
                        }
                    }
                    for(int i = minNonOptions; i < maxNonOptions; i++) {
                        if(i < nonOptNames.length) {
                            ret += "[" + ld + nonOptNames[i] + rd + "] ";
                        } else {
                            ret += "[" + ld + "arg" + (i + 1) + rd + "] ";
                        }
                    }
                    ret = ret.trim();
                }
            }
        }

        return ret;
    }

    // Usage line

    public String renderUsageLine() {
        return renderUsageLine(null, null);
    }
    public String renderUsageLine(String commandName, String customUsageLine) {
        commandName = (commandName == null) ? "<command>" : commandName;
        String line =
            "Usage: " + commandName + " " +
            (customUsageLine == null ? renderOptionUsageString() + " " + renderNonOptionString() : customUsageLine);
        return line.trim();             // To remind me when someday this is a multi-line thing, needs to be trimmed.
    }

    // Option descriptions

    public String renderOptionDescriptions(int totalLength, int helpMsgIndent) {
        int minBetweenSpacing = 3;
        int firstIndent = 2;

        String result = "Options\n-------\n";
        for(Option opt : parser.getOptions()) {
            String optLine = StringUtil.spaces(firstIndent);
            if(opt.getShortForm() != null) {
                optLine += renderShortFormOption(opt, opt.getShortForm());
            }
            if(opt.getLongForm() != null) {
                if(opt.getShortForm() != null) {
                    optLine += ", ";
                }
                optLine += renderLongFormOption(opt, opt.getLongForm());
            }
            if(opt.getAliases().size() != 0) {
                for(Object o : opt.getAliases()) {
                    if(o instanceof Character) {
                        optLine += ", " + renderShortFormOption(opt, "" + o);
                    } else {
                        optLine += ", " + renderLongFormOption(opt, (String) o);
                    }
                }
            }
            String defExtra = "";
            if(opt.getDefaultLabel() != null || opt.getDefaultValue() != null) {
                defExtra = ", Default:";
                if(opt.getDefaultLabel() != null) {
                    defExtra += " " + opt.getDefaultLabel();
                }
                if(opt.getDefaultValue() != null) {
                    defExtra += " " + opt.getDefaultValue();
                }
            }
            String prefix = opt.isRequired() ? "(Required)" : "[Optional" + defExtra + "]";
            if(opt.hasXArgs()) {
                prefix += " {XArgs}";
            }
            String helpDesc = opt.getHelpDescription();
            helpDesc = (helpDesc == null) ? prefix : prefix + " " + helpDesc;
            helpDesc = trimSpacesOnly(helpDesc);
            if(!helpDesc.equals("")) {
                if(optLine.length() <= helpMsgIndent - minBetweenSpacing) {
                    optLine += StringUtil.spaces(helpMsgIndent - optLine.length());
                } else {
                    optLine += "\n" + StringUtil.spaces(helpMsgIndent);
                }
                optLine += applyWordWrap(helpDesc, totalLength, helpMsgIndent);
            }
            result += optLine + "\n";
        }
        return result.trim();
    }


    //////////
    // MISC //
    //////////

    // Handles word-wrapping of the help message.
    private String applyWordWrap(String s, int totalLength, int helpMsgIndent) {
        String result = "";

        // This variable holds the column position into which the next
        // character will be printed.
        int cp = helpMsgIndent + 1;

        for(int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            // If the current character is a newline character...
            if(ch == '\n' || ch == '\r') {

                // Add a newline and proper spaces to the result and
                // reset the column position.
                result += "\n" + StringUtil.spaces(helpMsgIndent);
                cp = helpMsgIndent + 1;

                // If this was a CRLF, move past the LF.
                if(ch == '\r' && i + 1 < s.length() && s.charAt(i + 1) == '\n') {
                    i++;
                }

            } else {
                int j = i + 1;
                int more = 0;

                // A space character essentially is allowed to be a
                // 1-character word that is allowed to take up space
                // like a normal word.  The only difference is that
                // there cannot be any "more" characters attached
                // to it - it stands alone.
                if(ch != ' ') {
                    while(j < s.length() && (s.charAt(j) != ' ' && s.charAt(j) != '\n' && s.charAt(j) != '\r')) {
                        more++;
                        j++;
                    }
                }

                // Extract the word.
                String word = s.substring(i, i + more + 1);

                // If the number of characters required by the word would
                // go past the last column, then reset the line.
                if(cp + more > totalLength) {
                    result += "\n" + StringUtil.spaces(helpMsgIndent);
                    cp = helpMsgIndent + 1;
                }

                // Now add the word, increment the column position, and
                // move the loop forward (increase i).
                result += word;
                cp += word.length();
                i += more;

                // If the column position now lies past the end, we want
                // to absorb exactly one whitespace character.
                if(cp == totalLength + 1) {
                    int afterWord = i + 1;
                    if(afterWord < s.length()) {
                        char afterWordCh =  s.charAt(afterWord);
                        if(afterWordCh == ' ' || afterWordCh == '\n' || afterWordCh == '\r') {
                            i++;

                            result += "\n" + StringUtil.spaces(helpMsgIndent);
                            cp = helpMsgIndent + 1;

                            // If this was a CRLF, move past the LF.
                            if(afterWordCh == '\r' && i + 1 < s.length() && s.charAt(i + 1) == '\n') {
                                i++;
                            }
                        }
                    }
                }

            }
        }

        return result;
    }

    // Don't remove tabs or newlines.
    private String trimSpacesOnly(String s) {
        return s.replaceFirst("^ *", "").replaceFirst(" *$", "");
    }
}
