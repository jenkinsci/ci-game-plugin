package hudson.plugins.cigame.rules.unittesting;

import hudson.plugins.cigame.model.RuleResult;
import hudson.tasks.test.AbstractTestResultAction;

/**
 * Rule that gives points for increasing the number of passed tests.
 */
public class IncreasingPassedTestsRule extends AbstractUnitTestsRule {

    private int pointsForEachFixedFailure;

    public IncreasingPassedTestsRule() {
        this(1);
    }

    public IncreasingPassedTestsRule(int points) {
        pointsForEachFixedFailure = points;
    }

    RuleResult<Integer> evaluate(
        int currentTotalCount, int currentFailCount, int currentSkipCount,
        int previousTotalCount, int previousFailCount, int previousSkipCount) {
        
        int passedTestDiff = (currentTotalCount - currentFailCount - currentSkipCount)
        	- (previousTotalCount - previousFailCount - previousSkipCount);
        
        // ignore any tests which were just 'unskipped'
        if (currentSkipCount < previousSkipCount) {
        	passedTestDiff = passedTestDiff - (previousSkipCount - currentSkipCount);
        }
        
        // passedTestDiff may now be 0 or even negative. Count at least all
        // those tests which were fixed
        passedTestDiff = Math.max(passedTestDiff, previousFailCount - currentFailCount);
        
        if (passedTestDiff > 0) {
            return new RuleResult<Integer>(passedTestDiff * pointsForEachFixedFailure, 
                    Messages.UnitTestingRuleSet_IncreasingPassedRule_Count(passedTestDiff),
                    passedTestDiff); 
        }
        return null;
    }

    public String getName() {
        return Messages.UnitTestingRuleSet_IncreasingPassedRule_Name(); 
    }

	@Override
	protected RuleResult<Integer> evaluate(
			AbstractTestResultAction<?> testResult,
			AbstractTestResultAction<?> previousTestResult) {
		if (testResult == null) {
			return null;
		}
		
		return evaluate(
				testResult.getTotalCount(), testResult.getFailCount(), testResult.getSkipCount(),
				previousTestResult.getTotalCount(), previousTestResult.getFailCount(), previousTestResult.getSkipCount());
	}

	@Override
	protected String getResultDescription(Integer testDiff) {
		return Messages.UnitTestingRuleSet_IncreasingPassedRule_Count(testDiff);
	}
}
