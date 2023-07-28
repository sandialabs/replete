package replete.equality.similarity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO: This class is not fully implemented. It got somewhat complicated
// trying to deal with the situation when one of the two maps has keys that
// the other one doesn't. That's not an issue that the ObjectMemberAccessRule
// has to deal with.
public class MapRule<T extends Map> implements AcceptableSimilarityRule<T> {


    ////////////
    // FIELDS //
    ////////////

    private List<AcceptableSimilarityRule<T>> wholeRules = new ArrayList<>();
    private List<AcceptableSimilarityRule> defaultEntryRules = new ArrayList<>();
    private Map<Object, List<AcceptableSimilarityRule>> entryRules = new LinkedHashMap<>();


    //////////////
    // MUTATORS //
    //////////////

    public MapRule<T> addRule(AcceptableSimilarityRule<T> rule) {
        wholeRules.add(rule);
        return this;
    }
    public MapRule<T> addEntryRule(Object key, AcceptableSimilarityRule rule) {
        List<AcceptableSimilarityRule> rules = entryRules.get(key);
        if(rules == null) {
            rules = new ArrayList<>();
            entryRules.put(key, rules);
        }
        rules.add(rule);
        return this;
    }

    public MapRule<T> addDefaultEntryRule(AcceptableSimilarityRule rule) {
        defaultEntryRules.add(rule);
        return this;
    }


    //////////
    // TEST //
    //////////

    @Override
    public boolean test(T o1, T o2) {
        for(AcceptableSimilarityRule<T> rule : wholeRules) {
            if(!rule.test(o1, o2)) {
                return false;
            }
        }
        if(!checkDefaultMemberRules(o1, o2, defaultEntryRules,
                o1.keySet(),               // TODO????????
                entryRules.keySet())) {
            return false;
        }
        if(!checkEntryRules(o1, o2, entryRules)) {
            return false;
        }
        return true;
    }

    private boolean checkDefaultMemberRules(T o1, T o2,
            List<AcceptableSimilarityRule> ruleList,
            Set<Object> allKeys, Set<Object> excludeKeys) {
        if(!ruleList.isEmpty()) {
            for(Object key : allKeys) {
                if(excludeKeys.contains(key)) {     // Not the most efficient but no need for speed here
                    continue;
                }
                Object v1 = o1.get(key);
                Object v2 = o2.get(key);
                for(AcceptableSimilarityRule rule : ruleList) {
                    if(!rule.test(v1, v2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkEntryRules(T o1, T o2,
             Map<Object, List<AcceptableSimilarityRule>> ruleLists) {
        for(Object key : ruleLists.keySet()) {
            Object v1 = o1.get(key);
            Object v2 = o2.get(key);
            List<AcceptableSimilarityRule> rules = ruleLists.get(key);
            for(AcceptableSimilarityRule rule : rules) {
                if(!rule.test(v1, v2)) {
                    return false;
                }
            }
        }
        return true;
    }
}
