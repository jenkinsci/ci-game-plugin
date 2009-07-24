package hudson.plugins.cigame.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Class containing one or more rules
 * 
 * @author Erik Ramfelt
 */
public class RuleSet {

    private Collection<Rule> rules;
    private String name;

    public RuleSet(String name, Collection<Rule> rules) {
        this.name = name;
        this.rules = rules;
    }

    public RuleSet(String name) {
        this.name = name;
        this.rules = new ArrayList<Rule>();
    }

    /**
     * Returns if the rule set is available or not. If a rule set is not
     * available then it will not be used for determining the score for a build
     * Default implementation returns true.
     * 
     * @return true
     */
    public boolean isAvailable() {
        return true;
    }

    /**
     * Returns the name of the rule set.
     * 
     * @return the name of the rule set.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the rules for this rule set.
     * 
     * @return the rules for this rule set.
     */
    public Collection<Rule> getRules() {
        return rules;
    }

    /**
     * Add rule to the rule set
     * 
     * @param rule rule to add to the set.
     */
    public void add(Rule rule) {
        rules.add(rule);
    }

    /**
     * Remove rule from the rule set.
     * 
     * @param rule the rule to remove.
     */
    public void removeRule(Rule rule) {
        rules.remove(rule);
    }
}
