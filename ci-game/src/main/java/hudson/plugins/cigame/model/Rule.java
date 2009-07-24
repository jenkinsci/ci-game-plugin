package hudson.plugins.cigame.model;

import hudson.model.AbstractBuild;

/**
 * Rule interface.
 * 
 * @author Erik Ramfelt
 */
public interface Rule {
    /**
     * Returns the name of the rule
     * 
     * @return name of the rule
     */
    String getName();

    /**
     * Evaluate the build and return the points for it
     * 
     * @param build build to calculate points for
     * @return the result of the rule; null if the rule should be ignored.
     */
    RuleResult evaluate(AbstractBuild<?, ?> build);
}
