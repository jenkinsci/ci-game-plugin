package hudson.plugins.cigame.rules.unittesting;

import hudson.plugins.cigame.model.RuleResult;

/**
 * Rule that gives points for increasing the number of skipped tests. By default 0 marks given.
 * 
 * @author <a href="www.digizol.com">Kamal Mettananda</a>
 * @since 1.20
 */
public class IncreasingSkippedTestsRule extends AbstractSkippedTestsRule {

    private int pointsForIncreasingOneSkippedTest;

    public IncreasingSkippedTestsRule() {
        this(0);
    }

    public IncreasingSkippedTestsRule(int points) {
        pointsForIncreasingOneSkippedTest = points;
    }

    public String getName() {
        return Messages.UnitTestingRuleSet_IncreasingSkippedRule_Name();
    }

    @Override
    protected String getResultDescription(Integer testDiff) {
        return Messages.UnitTestingRuleSet_IncreasingSkippedRule_Count(testDiff);
    }

    @Override
    protected RuleResult<Integer> evaluate(int passedTestDiff) {
        if (passedTestDiff > 0) {
            return new RuleResult<Integer>(passedTestDiff * pointsForIncreasingOneSkippedTest,
                                           Messages.UnitTestingRuleSet_IncreasingSkippedRule_Count(passedTestDiff),
                                           passedTestDiff);
        }
        return null;
    }

}
