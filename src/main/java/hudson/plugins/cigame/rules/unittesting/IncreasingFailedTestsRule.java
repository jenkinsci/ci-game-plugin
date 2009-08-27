package hudson.plugins.cigame.rules.unittesting;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.tasks.test.AbstractTestResultAction;

import java.util.List;

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
        return Messages.UnitTestingRuleSet_IncreasingFailedRule_Name(); //$NON-NLS-1$
    }

    @SuppressWarnings("unchecked")
    public RuleResult evaluate(AbstractBuild<?, ?> build) {
        List<AbstractTestResultAction> actions = build.getActions(AbstractTestResultAction.class);
        for (AbstractTestResultAction action : actions) {
            if ((action != null) && (action.getPreviousResult() != null)) {
                return evaluate(build.getResult(), 
                        build.getPreviousBuild().getResult(), 
                        action.getFailCount(), 
                        action.getPreviousResult().getFailCount());
            }
        }
        return null;
    }

    RuleResult evaluate(Result currentResult, Result previousResult,
            int currentFailCount, int previousFailCount) {
        if ((previousResult.isBetterThan(Result.FAILURE))
                && (currentResult.isBetterOrEqualTo(Result.UNSTABLE))) {
            int failingTestDiff = currentFailCount - previousFailCount;
            if (failingTestDiff > 0) {
                return new RuleResult(failingTestDiff * pointsForEachNewFailure, 
                        Messages.UnitTestingRuleSet_IncreasingFailedRule_Count(failingTestDiff)); //$NON-NLS-1$
            }
        }
        return null;
    }
}
