package hudson.plugins.cigame.rules.unittesting;

import hudson.plugins.cigame.model.RuleResult;
import hudson.tasks.test.AbstractTestResultAction;

/**
 * Rule for giving points if a new test is added and fails.
 */
public class IncreasingFailedTestsRule extends AbstractUnitTestsRule {

    private double pointsForEachNewFailure;

    public IncreasingFailedTestsRule() {
        this(-1);
    }

    public IncreasingFailedTestsRule(int points) {
        pointsForEachNewFailure = points;
    }

    public String getName() {
        return Messages.UnitTestingRuleSet_IncreasingFailedRule_Name(); 
    }

    RuleResult<Integer> evaluate(
        int currentFailCount, int previousFailCount) {
        int failingTestDiff = currentFailCount - previousFailCount;
        if (failingTestDiff > 0) {
            return new RuleResult<Integer>(failingTestDiff * pointsForEachNewFailure, 
                    Messages.UnitTestingRuleSet_IncreasingFailedRule_Count(failingTestDiff),
                    failingTestDiff); 
        }
        return null;
    }
    
	@Override
	@SuppressWarnings("unchecked")
	protected RuleResult evaluate(
			AbstractTestResultAction testResult,
			AbstractTestResultAction previousTestResult) {
		if (testResult == null) {
			return null;
		}
		
		return evaluate(
				testResult.getFailCount(), previousTestResult.getFailCount());
	}

	@Override
	protected String getResultDescription(Integer testDiff) {
		return Messages.UnitTestingRuleSet_IncreasingFailedRule_Count(testDiff);
	}
}
