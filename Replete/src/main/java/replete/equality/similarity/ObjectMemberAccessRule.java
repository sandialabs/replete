package replete.equality.similarity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import replete.util.ReflectionUtil;

// By default assumes similar unless child rules say otherwise.
// Technically could start out with defaulting to a strict/full-reflection
// comparison, and rules can... undo that?
public class ObjectMemberAccessRule<T> implements AcceptableSimilarityRule<T> {


    ////////////
    // FIELDS //
    ////////////

    private List<AcceptableSimilarityRule<T>> wholeRules = new ArrayList<>();
    private List<AcceptableSimilarityRule> defaultFieldValueRules = new ArrayList<>();
    private List<AcceptableSimilarityRule> defaultMethodCallRules = new ArrayList<>();
    private Map<String, List<AcceptableSimilarityRule>> fieldValueRules = new LinkedHashMap<>();
    private Map<String, List<AcceptableSimilarityRule>> methodCallRules = new LinkedHashMap<>();


    //////////////
    // MUTATORS //
    //////////////

    public ObjectMemberAccessRule<T> addRule(AcceptableSimilarityRule<T> rule) {
        wholeRules.add(rule);
        return this;
    }
    public ObjectMemberAccessRule<T> addFieldRule(String name, AcceptableSimilarityRule rule) {
        return addToRuleList(fieldValueRules, name, rule);
    }
    public ObjectMemberAccessRule<T> addMethodRule(String name, AcceptableSimilarityRule rule) {
        return addToRuleList(methodCallRules, name, rule);
    }
    private ObjectMemberAccessRule<T> addToRuleList(
            Map<String, List<AcceptableSimilarityRule>> ruleLists,
            String name, AcceptableSimilarityRule rule) {
        List<AcceptableSimilarityRule> rules = ruleLists.get(name);
        if(rules == null) {
            rules = new ArrayList<>();
            ruleLists.put(name, rules);
        }
        rules.add(rule);
        return this;
    }

    public ObjectMemberAccessRule<T> addDefaultFieldRule(AcceptableSimilarityRule rule) {
        return addToDefaultRuleList(defaultFieldValueRules, rule);
    }
    public ObjectMemberAccessRule<T> addDefaultMethodRule(AcceptableSimilarityRule rule) {
        return addToDefaultRuleList(defaultMethodCallRules, rule);
    }

    private ObjectMemberAccessRule<T> addToDefaultRuleList(
           List<AcceptableSimilarityRule> ruleList,
           AcceptableSimilarityRule rule) {
        ruleList.add(rule);
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
        if(!checkDefaultMemberRules(o1, o2, defaultFieldValueRules,
                (o, member) -> ReflectionUtil.get(o, member),
                ReflectionUtil.getAllFieldsByName(o1.getClass(), false).keySet(),
                fieldValueRules.keySet())) {
            return false;
        }
        if(!checkDefaultMemberRules(o1, o2, defaultMethodCallRules,
                (o, member) -> ReflectionUtil.invoke(o, member),
                ReflectionUtil.getAllMethodsByName(o1.getClass(), false).keySet(),    // TODO - wrong - needs only get/is/has from Scrutinize
                methodCallRules.keySet())) {
            return false;
        }
        if(!checkMemberRules(o1, o2, fieldValueRules,
                (o, member) -> ReflectionUtil.get(o, member))) {
            return false;
        }
        if(!checkMemberRules(o1, o2, methodCallRules,
                (o, member) -> ReflectionUtil.invoke(o, member))) {
            return false;
        }
        return true;
    }

    private boolean checkDefaultMemberRules(T o1, T o2,
            List<AcceptableSimilarityRule> ruleList,
            BiFunction<T, String, Object> accessMember,
            Set<String> allMembers, Set<String> excludeMembers) {
        if(!ruleList.isEmpty()) {
            for(String member : allMembers) {
                if(excludeMembers.contains(member)) {     // Not the most efficient but no need for speed here
                    continue;
                }
                Object f1 = accessMember.apply(o1, member);
                Object f2 = accessMember.apply(o2, member);
                for(AcceptableSimilarityRule rule : ruleList) {
                    if(!rule.test(f1, f2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkMemberRules(T o1, T o2,                     // Handles both fields and methods
             Map<String, List<AcceptableSimilarityRule>> ruleLists,
             BiFunction<T, String, Object> accessMember) {
        for(String member : ruleLists.keySet()) {
            Object f1 = accessMember.apply(o1, member);
            Object f2 = accessMember.apply(o2, member);
            List<AcceptableSimilarityRule> rules = ruleLists.get(member);
            for(AcceptableSimilarityRule rule : rules) {
                if(!rule.test(f1, f2)) {
                    return false;
                }
            }
        }
        return true;
    }
}
