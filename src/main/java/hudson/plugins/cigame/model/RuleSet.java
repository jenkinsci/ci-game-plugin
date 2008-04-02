package hudson.plugins.cigame.model;

import java.util.Collection;

/**
 * Class containing one or more rules
 * @author Erik Ramfelt
 */
public class RuleSet {

    private Collection<Rule> rules;    
    private String name;
    
    public RuleSet(String name, Collection<Rule> rules) {
        this.name = name;
        this.rules = rules;
    }

    /**
     * Returns the name of the rule set.
     * @return the name of the rule set.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the rules for this rule set.
     * @return the rules for this rule set.
     */
    Collection<Rule> getRules() {
        return rules;
    }

    /**
     * Add rule to the rule set
     * @param rule rule to add to the set.
     */
    public void add(Rule rule) {
        rules.add(rule);
    }
    
    /**
     * Remove rule from the rule set.
     * @param rule the rule to remove.
     */
    public void removeRule(Rule rule) {
    	rules.remove(rule);
    }
}
