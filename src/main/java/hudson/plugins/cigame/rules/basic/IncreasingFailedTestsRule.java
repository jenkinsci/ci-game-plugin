package hudson.plugins.cigame.rules.basic;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.plugins.cigame.model.Rule;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;

/**
 * Rule for giving points if a new test is added and fails.
 */
public class IncreasingFailedTestsRule implements Rule {
	
	private double pointsForEachNewFailure = -1;

    public IncreasingFailedTestsRule() {        
    }
    
    public String getName() {
        return "Increased number of failed tests";
    }

	public double evaluate(AbstractBuild<?, ?> build) {
        TestResultAction action = build.getAction(TestResultAction.class);
        if ((action != null) && (action.getPreviousResult() != null)) {
            TestResult currentResult = action.getResult();
            TestResult previousResult = action.getPreviousResult().getResult();

            int passedTestDiff = currentResult.getFailCount() - previousResult.getFailCount();
            if (passedTestDiff > 0) {
            	return passedTestDiff * pointsForEachNewFailure;
            }
        }
        return 0;
	}

}
