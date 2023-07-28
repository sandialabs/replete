package replete.text;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import replete.collections.Pair;

// Although compiling regular expression patterns is not "super" expensive by
// themselves, it can be one of the more unexpected and underrated sources of
// slow down if you're not careful.  Let's say you have some sort of keyword
// matching/search software and the user can specify any number of search terms.
// You just start out using String.contains() or String.indexOf() and everything
// is fine.  You might even have these comparisons nested in multiple loops of
// processing and everything runs fast - no need to prematurely optimize!  However,
// if the user requirements increase even slightly to wanting to match at the start
// or end of candidate strings, fields, or documents, then it the developer would
// probably consider switching over to regular expressions.  But if we merely
// replace in situ the existing matching strategy with the regex compilation and
// matching code, this is one case where we would most likely actually notice a
// tangible slow down in the software.  Thus, we want to compile pattern objects
// as few times as necessary, and we also don't want to overly complicate our
// own code to make this happen.  Or, put another way, we want to help minimize
// potential performance problems regardless of how client code is structured.
//
// If compiling new patterns, even simple ones, is more expensive than the O(1)
// lookup operations in this class, then the following pattern pool could be
// considered *always* better than (or at least having zero down sides compared
// to) calling Pattern.compile() in your own code.  Pattern objects are also
// immutable so any identical pair of (regex, flags) will always result in the
// same Pattern object.
//
// This class is an experimental idea, but if the above logic holds up, then this
// class might be so generally useful that it might warrant an internal singleton
// instance and regular use by every software component needing regex support
// (e.g. StringUtil and PatternUtil in Replete).

public class PatternPool {

    // TODO: Could add a size limit option to this pool for
    // memory concerns (if for example this instance of the
    // pool is being used indiscriminately across large parts
    // of a system), but this may not always be desired.
    // If you are using the pool for a fixed set of regex
    // strings and do need to have access to all Pattern
    // objects, you may not want a limit.

    private Map<Pair<String, Integer>, Pattern> compiledPatterns = new ConcurrentHashMap<>();

    public Pattern getPattern(String regex) {
        return getPatternInner(regex, 0);
    }
    public Pattern getPattern(String regex, int flags) {
        return getPatternInner(regex, flags);
    }

    private Pattern getPatternInner(String regex, int flags) {
        Pair<String, Integer> input = new Pair<>(regex, flags);
        Pattern pattern = compiledPatterns.get(input);
        if(pattern == null) {
            pattern = Pattern.compile(input.getValue1(), input.getValue2());
            compiledPatterns.put(input, pattern);
        }
        return pattern;
    }
}
