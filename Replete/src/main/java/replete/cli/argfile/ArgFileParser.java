package replete.cli.argfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

// TODO: Traditionally Java applications and thus the command line
// parser do not have to deal with parsing command lines into
// individual arguments.  Instead, they are handed String[] which
// have already been properly parsed.  Parsing of an entire command
// line string into the proper components is handled by the shell
// (bash, MS DOS Prompt, etc.).  However, the arg file feature of
// the command line parser implies that the parsing of a long string
// of command line arguments is now an internal responsibility.  The
// parsing of a command line is a non-trivial task, as specific
// rules about how the string is tokenized comes into play.  Bash,
// for example, uses both " and ' to allow the user to have different
// levels of control over which parts of a command line string is
// considered literal vs. which are meant to be evaluated and
// replaced.  Currently, this command line parser does not have
// any intelligent behavior beyond that of considering every token
// separated by whitepace as a different argument.  In other words
// you currently can't have a file with the following content:
//
//   1 # My arguments are below:
//   2 --booloption -i 76
//   3 --name "January Model"
//
// and expect it to be parsed as a shell normally would.  instead of
// the string "January Model" being given as the argument to the
// "--name" option, only the string ""January" would be provided and
// "Model"" would be considered a non-option argument.  Some day
// in the future we can make the parsing of the arg file smarter
// to at least more closely resemble the parsing performed by shells.
// We can give the developer more control over how this file is parsed
// by adding properties to the ArgFileConfig (e.g. tokenGroupingSym = "\"")
// and enabling the developer to change them.  Thus currently, this
// feature is not a fully fleshed-out feature and cannot be considered
// a perfect replacement for moving your command line arguments off of
// the main command line and into a file.  But it's a start.

public class ArgFileParser {

    public static String[] parse(File file, ArgFileConfig config) {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<String> args = new ArrayList<>();
            String line;
            while((line = reader.readLine()) != null) {
                line = line.trim();
                if(config.getLineCommentToken() != null) {
                    int ctPos = line.indexOf(config.getLineCommentToken());
                    if(ctPos != -1) {
                        line = line.substring(0, ctPos);
                    }
                }
                if(line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\s+");   // Extremely basic tokenization strategy
                for(String part : parts) {
                    args.add(part);
                }
            }
            return args.toArray(new String[0]);
        } catch(Exception e) {
            throw new RuntimeException();      // TODO: Better exception
        }
    }

    public static void main(String[] args) {
//        String a = "x#a b c\nd#y";
//        StringReader reader = new StringReader(a);
//        System.out.println(Arrays.toString(parse(reader, new ArgFileConfig())));
    }
}
