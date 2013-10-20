package hudson.plugins.cigame.rules.unittesting;

import hudson.plugins.cigame.model.RuleResult;

/**
 * Rule for giving points if a test fails (new or existing). By default -1 mark given.
 * 
 * @author Unknown
 * @author <a href="www.digizol.com">Kamal Mettananda</a>
 * @since 1.20
 */
public class IncreasingFailedTestsRule extends AbstractFailedTestsRule {

    private int pointsForIncreasingOneFailedTest;

    public IncreasingFailedTestsRule() {
        this(-1);
    }

    public IncreasingFailedTestsRule(int points) {
        pointsForIncreasingOneFailedTest = points;
    }

    public String getName() {
        return Messages.UnitTestingRuleSet_IncreasingFailedRule_Name(); 
    }

	@Override
	protected String getResultDescription(Integer testDiff) {
		return Messages.UnitTestingRuleSet_IncreasingFailedRule_Count(testDiff);
	}

    @Override
    protected RuleResult<Integer> evaluate(int failingTestDiff) {
        if (failingTestDiff > 0) {
            return new RuleResult<Integer>(failingTestDiff * pointsForIncreasingOneFailedTest, 
                    Messages.UnitTestingRuleSet_IncreasingFailedRule_Count(failingTestDiff),
                    failingTestDiff); 
        }
        return null;
    }
}
