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
public class NewTestFailureRule implements Rule {
	
	private double introducedNewFailure = -1;

    public NewTestFailureRule() {        
    }
    
    public String getName() {
        return "New test that failed";
    }

	public double evaluate(AbstractBuild<?, ?> build) {
        TestResultAction action = build.getAction(TestResultAction.class);
        if ((action != null) && (action.getPreviousResult() != null)) {
            TestResult currentResult = action.getResult();
            TestResult previousResult = action.getPreviousResult().getResult();

            List<CaseResult> currentFailedTests = currentResult.getFailedTests();
            List<CaseResult> previousFailedTests = previousResult.getFailedTests();
            
            //int regressionFailures = 0;
            int newFailures = 0;
            
            for (CaseResult caseResult : currentFailedTests) {
				if (!previousFailedTests.contains(caseResult)) {
					newFailures++;
				}
			}
            return newFailures * introducedNewFailure;
        }
        return 0;
	}

}
