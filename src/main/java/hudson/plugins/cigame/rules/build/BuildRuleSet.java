package hudson.plugins.cigame.rules.build;

import hudson.plugins.cigame.model.RuleSet;

/**
 * Rule set for common build rules.
 */
public class BuildRuleSet extends RuleSet {
    public BuildRuleSet() {
        super("Build result");
        add(new BuildResultRule());
    }
}
