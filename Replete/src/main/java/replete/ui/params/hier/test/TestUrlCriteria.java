package replete.ui.params.hier.test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import replete.numbers.NumUtil;
import replete.params.hier.Criteria;
import replete.web.UrlUtil;

public class TestUrlCriteria implements Criteria<String> {


    ////////////
    // FIELDS //
    ////////////

    private String expression;
    private transient Rule[] rules;    // Not persisted


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TestUrlCriteria(String expression) {
        this.expression = expression;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getExpression() {
        return expression;
    }


    //////////
    // MISC //
    //////////

    private Rule[] constructRules(String line) {
        line = line.trim();
        String[] ruleStrings = line.split("\\s*\\s&\\s\\s*");      // TEST THIS
        Rule[] rules = new Rule[ruleStrings.length];

        for(int i = 0; i < ruleStrings.length; i++) {
            Rule rule = null;
            if(ruleStrings[i].equals("*")) {
                rule = new AllRule();
            } else {
                String[] ruleStringParts = ruleStrings[i].split("\\s*=\\s*", 2);    // TEST THIS
                String ruleType = ruleStringParts[0].toLowerCase();
                String ruleValue = ruleStringParts[1];

                Map<String, Supplier<Rule>> validRules = new HashMap<>();
                validRules.put("ip",       () -> new IpRule(ruleValue));
                validRules.put("host",     () -> new HierarchicalHostRule(ruleValue));
                validRules.put("url",      () -> new FullUrlRule(ruleValue));
                validRules.put("port",     () -> new PortRule(NumUtil.i(ruleValue)));
                validRules.put("protocol", () -> new ProtocolRule(ruleValue));

                for(String key : validRules.keySet()) {
                    if(key.equalsIgnoreCase(ruleType)) {
                        Supplier<Rule> supplier = validRules.get(key);
                        rule = supplier.get();
                    }
                }
                if(rule == null) {
                    throw new RuntimeException("Unknown rule type '" + ruleType + "'.");
                }
            }

            rules[i] = rule;
        }

        return rules;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean appliesTo(String url) {
        if(rules == null) {
            rules = constructRules(expression);
        }
        UrlUtil.parseUrl(url);
        boolean allApplyTo = true;
        for(Rule rule : rules) {
            allApplyTo = allApplyTo && rule.appliesTo(url);
        }
        return allApplyTo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expression == null) ? 0 : expression.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        TestUrlCriteria other = (TestUrlCriteria) obj;
        if(expression == null) {
            if(other.expression != null) {
                return false;
            }
        } else if(!expression.equals(other.expression)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return expression;
    }
}
