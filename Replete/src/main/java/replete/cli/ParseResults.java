package replete.cli;

import java.util.List;
import java.util.Map;

import replete.cli.options.Option;
import replete.collections.Pair;


public class ParseResults {


    ////////////
    // FIELDS //
    ////////////

    private Map<Option, List<Object>>       parsedOptionValues;
    private List<String>                    parsedRemainingArgs;
    private List<Pair<Option<?>, Object>>   orderedParsedArguments;
    private Map<Option, List<List<String>>> xargsParsedResults;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ParseResults(Map<Option, List<Object>> parsedOptionValues,
                        List<String> parsedRemainingArgs,
                        List<Pair<Option<?>, Object>> orderedParsedArguments,
                        Map<Option, List<List<String>>> xargsParsedResults) {
        this.parsedOptionValues = parsedOptionValues;
        this.parsedRemainingArgs = parsedRemainingArgs;
        this.orderedParsedArguments = orderedParsedArguments;
        this.xargsParsedResults = xargsParsedResults;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Map<Option, List<Object>> getParsedOptionValues() {
        return parsedOptionValues;
    }
    public List<String> getParsedRemainingArgs() {
        return parsedRemainingArgs;
    }
    public List<Pair<Option<?>, Object>> getOrderedParsedArguments() {
        return orderedParsedArguments;
    }
    public Map<Option, List<List<String>>> getXargsParsedResults() {
        return xargsParsedResults;
    }


    //////////
    // MISC //
    //////////

    // TODO: We need more complex code to be able to handle how
    // arguments parsed from arg file files are integrated into
    // the main argument list.  This is a non trivial matter as
    // not all the data structures are set up to retain argument
    // placement information.  So for now, we're only going to
    // truly support --argfile being the ONLY argument on the
    // command line.  If arguments are provided before or after
    // this option, then there's no guarantee what will happen.
    // This is a ridiculous restriction that is in place only
    // because of developer time constraints.  In general there
    // could be a number of different merge strategies that
    // could be implemented and the developer could choose
    // between them.

    public void overlay(ParseResults results) {
        parsedOptionValues.putAll(results.getParsedOptionValues());
        parsedRemainingArgs.addAll(results.getParsedRemainingArgs());
        orderedParsedArguments.addAll(results.getOrderedParsedArguments());
        xargsParsedResults.putAll(results.getXargsParsedResults());
    }

    // To properly implement the merging of the primary command
    // line arguments with those within --argfile files,
    // you'd have to know and deal with the positions of all
    // arguments involved (and know exactly in the main list
    // where --argfile was provided).
//    public int indexOf(Option<?> opt) {
//        int a = 0;
//        for(Pair<Option<?>, Object> arg : orderedParsedArguments) {
//            if(arg.getValue1() == opt) {
//                return a;
//            }
//            a++;
//        }
//        return -1;
//    }
}
