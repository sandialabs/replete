package replete.text.tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import replete.numbers.NumUtil;
import replete.text.StringUtil;

public class LocationAwareTokenizer {

    private static List<LocationAwareToken<?>> getTokens(String src) {
        List<LocationAwareToken<?>> finalTokens = new ArrayList<LocationAwareToken<?>>();

        List<MatchLocation> matches = getMatchLocations(src, "\\s+");

        // Split into segments separated by whitespace.
        // " Hi there Jean-Pier!re's dog.\t" => ["Hi", "there", "Jean-Pier!re's", "dog."]
        for(MatchLocation wsMatch : matches) {
            String wsToken = src.substring(wsMatch.start, wsMatch.endNonIncl);
            StripResult wsTokenNoEndPunct = stripEndPunct(wsToken);

            // Capture immediate negative numbers without end punctuation
            // but leaving start punctuation in case there's a hypen there.
            if(NumUtil.isDouble(wsTokenNoEndPunct.changed)) {
                Double tokenObject = Double.parseDouble(wsTokenNoEndPunct.changed);
                LocationAwareToken<Double> latToken =
                    new LocationAwareToken<Double>(
                        tokenObject, wsMatch.start, wsMatch.endNonIncl - wsTokenNoEndPunct.fromRight);
                finalTokens.add(latToken);
                continue;
            }

            // Split into segments separated by hyphens.
            // "Jean-Pier!re's" => ["Jean", "Pier!re's"]
            List<MatchLocation> hyMatches = getMatchLocations(wsToken, "-+", wsMatch.start);
            for(MatchLocation hyMatch : hyMatches) {
                String hyToken = src.substring(hyMatch.start, hyMatch.endNonIncl);

                // Take off certain punctuation from both sides.
                StripResult stripResult = stripEndPunctExceptDot(hyToken);

                // If current sans-hyphen segment is now a number, add that.
                if(NumUtil.isDouble(stripResult.changed)) {
                    Double tokenObject = Double.parseDouble(stripResult.changed);
                    LocationAwareToken<Double> latToken =
                        new LocationAwareToken<Double>(
                            tokenObject,
                            hyMatch.start + stripResult.fromLeft,
                            hyMatch.endNonIncl - stripResult.fromRight);
                    finalTokens.add(latToken);

                // Else split sans-hyphen segment into segments separated by all remaining
                // punctuation except the apostrophe (').
                // "Pier!re's" => ["Pier", "re's"]
                } else {
                    List<MatchLocation> punctMatches = getMatchLocations(hyToken, "[[\\p{Punct}]&&[^']]+", hyMatch.start);
                    for(MatchLocation punctMatch : punctMatches) {
                        String punctToken = src.substring(punctMatch.start, punctMatch.endNonIncl);

                        // Remove all apostrophes from final string.
                        StripResult stripResultApos = stripApos(punctToken);
                        String tokenObject = stripResultApos.changed;
                        LocationAwareToken<String> latToken =
                            new LocationAwareToken<String>(tokenObject,
                                punctMatch.start, punctMatch.endNonIncl);
                        finalTokens.add(latToken);
                    }
                }
            }
        }
        return finalTokens;
    }

    // Get Match Locations

    public static List<MatchLocation> getMatchLocations(String source, String delimPattern) {
        return getMatchLocations(source, delimPattern, 0);
    }
    public static List<MatchLocation> getMatchLocations(String source, String delimPattern, int offset) {
        List<MatchLocation> matches = new ArrayList<MatchLocation>();
        Pattern p = Pattern.compile(delimPattern);
        Matcher m = p.matcher(source);
        int prevEnd = 0;
        while(m.find()) {
            if(m.start() > 0) {
                matches.add(new MatchLocation(offset + prevEnd, offset + m.start()));
            }
            prevEnd = m.end();
        }
        if(prevEnd != source.length()) {
            matches.add(new MatchLocation(offset + prevEnd, offset + source.length()));
        }
        return matches;
    }

    // Strippers

    private static StripResult stripEndPunct(String str) {
        int before = str.length();
        str = str.replaceAll("\\p{Punct}*$", "");
        return new StripResult(str, 0, before - str.length());
    }
    private static StripResult stripEndPunctExceptDot(String str) {
        int before = str.length();
        str = str.replaceAll("^[\\p{Punct}&&[^\\.]]*", "");
        int end = str.length();
        str = str.replaceAll("\\p{Punct}*$", "");
        return new StripResult(str, before - end, end - str.length());
    }
    private static StripResult stripApos(String str) {
        int before = str.length();
        str = str.replaceAll("'", "");
        return new StripResult(str, before - str.length(), 0);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        String src = "The \"ants go\" marching  1 by 1. Hurrah! Hurrah!  blah-don't-blah Don't   talk to me about the number 1.0 or -1.0. Derek is an ass-hole. He has 14% of $5.00. I want $.50 & momma-dont-let-your-babies-grow-up-to-be-derek's. the function call is blah_hello(myvar, foo).";
//        String src = "Hi there hurrah!---hurrah! blah-don't-blah -1.000 to 1.0. And then abc-def. Also there-was-an-old-maid.... and then some";
//        String src = "  Hi there hurrah! \t\n"; //hurrah! blah-don't-blah -1.0 to 1.0. And then abc-def. Also there-was-an-old-maid.... and then some";
        List<LocationAwareToken<?>> finalTokens = getTokens(src);
        StringUtil.printStringIdxs(src);
        System.out.println();
        printFinalTokens(src, finalTokens);
        System.out.println();
        StringUtil.printStringIdxs(src);
    }

    public static void printFinalTokens(String src, List<LocationAwareToken<?>> matches) {
        if(matches.size() == 0) {
            System.out.println("(none)");
        } else {
            int width = (matches.get(matches.size() - 1).endNonIncl + "").length();
            String code = "%" + width + "d";

            int maxTokenLength = -1;
            for(LocationAwareToken<?> match : matches) {
                String s = "[token=" + match.getToken().toString() + "]";
                if(s.length() > maxTokenLength) {
                    maxTokenLength = s.length();
                }
            }

            for(LocationAwareToken<?> match : matches) {
                int s = match.start;
                int e = match.endNonIncl;
                String t = "[token=" + match.getToken().toString() + "]";
                String sp = StringUtil.spaces(maxTokenLength - t.length());
                System.out.printf("(" + code + " - " + code + ") [token=%s]" + sp + "{all=%s}\n",
                    s, e, match.getToken().toString(),
                    src.substring(s, e));
            }
        }
    }
}
