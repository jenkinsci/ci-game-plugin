package hudson.plugins.cigame.rules.basic;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.tasks.junit.TestResultAction;

/**
 * Rule for giving points if a new test is added and fails.
 */
public class IncreasingFailedTestsRule implements Rule {
	
	private double pointsForEachNewFailure;

    public IncreasingFailedTestsRule() {
    	this(-1);
    }

    public IncreasingFailedTestsRule(int points) {
    	pointsForEachNewFailure = points;
    }
    
    public String getName() {
        return "Increased number of failed tests";
    }

	public double evaluate(AbstractBuild<?, ?> build) {
        TestResultAction action = build.getAction(TestResultAction.class);
        if ((action != null) && (action.getPreviousResult() != null)) {
            return evaluate(build.getResult(), build.getPreviousBuild().getResult(),
            		action.getResult().getFailCount(), action.getPreviousResult().getResult().getFailCount());
        }
        return 0;
	}
	
	double evaluate(Result currentResult, Result previousResult, int currentFailCount, int previousFailCount) {
		if ((previousResult.isBetterThan(Result.FAILURE)) 
				&& (currentResult.isBetterOrEqualTo(Result.UNSTABLE))) {
			int failingTestDiff = currentFailCount - previousFailCount;
			if (failingTestDiff > 0) {
				return failingTestDiff * pointsForEachNewFailure;
			}
		}
		return 0;
	}
}
