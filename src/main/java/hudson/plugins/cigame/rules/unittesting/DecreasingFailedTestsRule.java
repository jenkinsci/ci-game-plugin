package hudson.plugins.cigame.rules.unittesting;

import hudson.plugins.cigame.model.RuleResult;

/**
 * Rule that gives points for decreasing the number of failed tests. By default 1 mark given.
 * 
 * @author <a href="www.digizol.com">Kamal Mettananda</a>
 * @since 1.20
 */
public class DecreasingFailedTestsRule extends AbstractFailedTestsRule {

    private int pointsForDecreasingOneFailedTest;

    public DecreasingFailedTestsRule() {
        this(1);
    }

    public DecreasingFailedTestsRule(int points) {
        pointsForDecreasingOneFailedTest = points;
    }

    public String getName() {
        return Messages.UnitTestingRuleSet_DecreasingFailedRule_Name();
    }

    @Override
    protected String getResultDescription(Integer testDiff) {
        return Messages.UnitTestingRuleSet_DecreasingFailedRule_Count(testDiff);
    }

    @Override
    protected RuleResult<Integer> evaluate(int failedTestDiff) {
        if (failedTestDiff < 0) {
            failedTestDiff = -failedTestDiff;
            return new RuleResult<Integer>(failedTestDiff * pointsForDecreasingOneFailedTest,
                                           Messages.UnitTestingRuleSet_DecreasingFailedRule_Count(failedTestDiff),
                                           failedTestDiff);
        }
        return null;
    }

}
