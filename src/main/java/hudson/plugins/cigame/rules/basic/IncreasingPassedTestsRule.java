package hudson.plugins.cigame.rules.basic;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.tasks.junit.TestResultAction;

/**
 * Rule that gives points for increasing the number of passed tests.
 */
public class IncreasingPassedTestsRule implements Rule {

    private int pointsForEachFixedFailure;

    public IncreasingPassedTestsRule() {
        this(1);
    }

    public IncreasingPassedTestsRule(int points) {
        pointsForEachFixedFailure = points;
    }

    public double evaluate(AbstractBuild<?, ?> build) {
        List<TestResultAction> actions = build.getActions(TestResultAction.class);
        for (TestResultAction action : actions) {
            if ((action != null) && (action.getPreviousResult() != null)) {
                return evaluate(build.getResult(), 
                        build.getPreviousBuild().getResult(), 
                        action.getResult().getPassCount(), 
                        action.getPreviousResult().getResult().getPassCount());
            }
        }
        return 0;
    }

    double evaluate(Result currentResult, Result previousResult,
            int currentPassCount, int previousPassCount) {
        if ((previousResult.isBetterThan(Result.FAILURE))
                && (currentResult.isBetterOrEqualTo(Result.UNSTABLE))) {
            int passedTestDiff = currentPassCount - previousPassCount;
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
