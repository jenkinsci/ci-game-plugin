package hudson.plugins.cigame.rules.basic;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.plugins.cigame.model.Rule;
import hudson.tasks.junit.CaseResult;
import hudson.tasks.junit.TestResult;
import hudson.tasks.junit.TestResultAction;

/**
 * Rule that gives points for increasing the number of passed tests.
 * @author Paulina
 *
 */
public class IncreasingPassedTestsRule implements Rule {

	private int pointsForEachFixedFailure = 1;
	
	public double evaluate(AbstractBuild<?, ?> build) {
        TestResultAction action = build.getAction(TestResultAction.class);
        if ((action != null) && (action.getPreviousResult() != null)) {
            TestResult currentResult = action.getResult();
            TestResult previousResult = action.getPreviousResult().getResult();

            int passedTestDiff = currentResult.getPassCount() - previousResult.getPassCount();
            if (passedTestDiff > 0) {
            	return passedTestDiff * pointsForEachFixedFailure;
            }
        }
        return 0;
	}

	public String getName() {
		return "Increased number of passed tests";
	}

}
