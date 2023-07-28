package finio.extractors.jo.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import finio.core.KeyPath;
import finio.extractors.jo.PopulateParams;

public class RuleNode implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    protected PopulateParams params;
    protected List<RuleNode> children = new ArrayList<>();
    protected List<Rule> rules = new ArrayList<>();


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public RuleNode() {
        this(null);
    }
    public RuleNode(Rule... rules) {
        this(null, rules);
    }
    public RuleNode(PopulateParams params, Rule... rules) {
        this.params = params;
        for(Rule rule : rules) {
            this.rules.add(rule);
        }
    }

    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public PopulateParams getParams() {
        return params;
    }

    // Accessors (Computed)

    public boolean rulesApplyTo(Object O, KeyPath P) {
        boolean allApplyTo = true;
        for(Rule rule : rules) {
            allApplyTo = allApplyTo && rule.appliesTo(O, P);
        }
        return allApplyTo;
    }
    protected void appendParams(Object O, KeyPath P, PopulateParams aggregateParams) {
        if(rulesApplyTo(O, P)) {
            if(params != null) {
                aggregateParams.overlay(params);
            }
            for(RuleNode child : children) {
                child.appendParams(O, P, aggregateParams);
            }
        }
    }

    // Mutator

    public void setParams(PopulateParams params) {
        this.params = params;
    }
    public void addRule(Rule rule) {
        rules.add(rule);
    }
    public void addChild(RuleNode child) {
        children.add(child);
    }
}
