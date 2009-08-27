package hudson.plugins.cigame.rules.unittesting;

import hudson.plugins.cigame.model.RuleSet;

/**
 * Rule set for unit test rules.
 */
public class UnitTestingRuleSet extends RuleSet {
    public UnitTestingRuleSet() {
        super(Messages.UnitTestingRuleSet_Title()); //$NON-NLS-1$
        add(new IncreasingFailedTestsRule());
        add(new IncreasingPassedTestsRule());
    }
}
