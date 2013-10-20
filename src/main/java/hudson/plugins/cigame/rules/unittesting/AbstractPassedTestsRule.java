package hudson.plugins.cigame.rules.unittesting;

import hudson.plugins.cigame.model.RuleResult;
import hudson.tasks.test.AbstractTestResultAction;

/**
 * Rule that gives points for increasing or decreasing the number of passed
 * tests. This is done by comparing the current with the previous build.
 * 
 * @author <a href="www.digizol.com">Kamal Mettananda</a>
 * @since 1.20
 */
public abstract class AbstractPassedTestsRule extends AbstractUnitTestsRule {

    protected abstract RuleResult<Integer> evaluate(int passedTestDiff);

    @Override
    protected RuleResult<Integer> evaluate(AbstractTestResultAction<?> testResult,
                                           AbstractTestResultAction<?> previousTestResult) {
        
        return evaluate((testResult.getTotalCount() - testResult.getFailCount() - testResult.getSkipCount())
                - (previousTestResult.getTotalCount() - previousTestResult.getFailCount() - previousTestResult.getSkipCount()));
    }

}
