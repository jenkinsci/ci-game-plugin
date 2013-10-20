package hudson.plugins.cigame.rules.unittesting;

import hudson.plugins.cigame.model.RuleResult;

/**
 * Rule that gives points for increasing the number of passed tests. By default 1 mark given.
 * 
 * @author Unknown
 * @author <a href="www.digizol.com">Kamal Mettananda</a>
 * @since 1.20
 */
public class IncreasingPassedTestsRule extends AbstractPassedTestsRule {

    private int pointsForIncreasingOnePassingTest;

    public IncreasingPassedTestsRule() {
        this(1);
    }

    public IncreasingPassedTestsRule(int points) {
        pointsForIncreasingOnePassingTest = points;
    }

    public String getName() {
        return Messages.UnitTestingRuleSet_IncreasingPassedRule_Name();
    }

    @Override
    protected String getResultDescription(Integer testDiff) {
        return Messages.UnitTestingRuleSet_IncreasingPassedRule_Count(testDiff);
    }

    @Override
    protected RuleResult<Integer> evaluate(int passedTestDiff) {
        if (passedTestDiff > 0) {
            return new RuleResult<Integer>(passedTestDiff * pointsForIncreasingOnePassingTest,
                                           Messages.UnitTestingRuleSet_IncreasingPassedRule_Count(passedTestDiff),
                                           passedTestDiff);
        }
        return null;
    }

}
