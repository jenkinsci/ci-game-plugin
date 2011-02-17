package hudson.plugins.cigame.rules.unittesting;

import hudson.plugins.cigame.model.RuleResult;
import hudson.tasks.test.AbstractTestResultAction;

public class RemovedFailedTestsRule extends AbstractUnitTestsRule {
	
	private static final double pointsForEachRemovedFailure = 1.0;

	@Override
	protected RuleResult<Integer> evaluate(
			AbstractTestResultAction<?> testResult,
			AbstractTestResultAction<?> previousTestResult) {
		
		if (testResult != null) {
			// handled by other rules
			return null;
		}
		
		int failingTestDiff = previousTestResult.getFailCount();
        if (failingTestDiff > 0) {
            return new RuleResult<Integer>(failingTestDiff * pointsForEachRemovedFailure, 
                    getResultDescription(failingTestDiff),
                    failingTestDiff); 
        }
        return null; 
	}

	@Override
	public String getName() {
		return Messages.UnitTestingRuleSet_RemovedFailedRule_Name();
	}

	@Override
	protected String getResultDescription(Integer testDiff) {
		return Messages.UnitTestingRuleSet_RemovedFailedRule_Count(testDiff);
	}
}
