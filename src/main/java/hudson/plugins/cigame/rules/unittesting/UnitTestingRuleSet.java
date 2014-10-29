package hudson.plugins.cigame.rules.unittesting;

import hudson.plugins.cigame.model.RuleSet;

/**
 * Rule set for unit test rules. There types of unit test changes are considered.<br/>
 * PassedTests<br/>
 * FailedTests<br/>
 * SkippedTests<br/>
 * 
 * @author Unknown
 * @author <a href="www.digizol.com">Kamal Mettananda</a>
 * @since 1.20
 */
public class UnitTestingRuleSet extends RuleSet {

    public UnitTestingRuleSet() {
        super(Messages.UnitTestingRuleSet_Title()); //$NON-NLS-1$
        add(new IncreasingPassedTestsRule());
        add(new DecreasingPassedTestsRule());
        add(new IncreasingFailedTestsRule());
        add(new DecreasingFailedTestsRule());
        add(new IncreasingSkippedTestsRule());
        add(new DecreasingSkippedTestsRule());
    }

}
