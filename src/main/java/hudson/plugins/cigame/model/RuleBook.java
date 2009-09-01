package hudson.plugins.cigame.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class containing several rule sets.
 * 
 * @author Erik Ramfelt
 */
public class RuleBook {

    private List<RuleSet> rulesets;

    public RuleBook() {
        rulesets = new LinkedList<RuleSet>();
    }

    public RuleBook(RuleSet ruleset) {
        this();
        rulesets.add(ruleset);
    }

    /**
     * Add rule set to the rule book
     * 
     * @param ruleset new rule set
     */
    public void addRuleSet(RuleSet ruleset) {
        rulesets.add(ruleset);
    }

    /**
     * Remove rule set from the rule book
     * 
     * @param ruleset rule set
     */
    public void removeRuleSet(RuleSet ruleset) {
        rulesets.remove(ruleset);
    }

    /**
     * Returns rule sets
     * 
     * @return list containing the rule sets
     */
    public List<RuleSet> getRuleSets() {
        return rulesets;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RuleBook ["); //$NON-NLS-1$
        for (Iterator<RuleSet> iterator = rulesets.iterator(); iterator.hasNext();) {
            builder.append(iterator.next().getName());
            if (iterator.hasNext()) {
                builder.append(", "); //$NON-NLS-1$
            }
        }
        builder.append("]"); //$NON-NLS-1$
        return builder.toString();
    }
}
