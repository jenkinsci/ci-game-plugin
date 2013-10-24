package hudson.plugins.cigame.rules.unittesting;

import jenkins.model.Jenkins;
import hudson.plugins.cigame.GameDescriptor;
import hudson.plugins.cigame.model.RuleResult;

/**
 * Rule that gives points for decreasing the number of failed tests. By default 0 mark given.
 * 
 * @author <a href="www.digizol.com">Kamal Mettananda</a>
 * @since 1.20
 */
public class DecreasingFailedTestsRule extends AbstractFailedTestsRule {

    private static final int DEFAULT_POINTS = 0;

    private int getPoints() {
        GameDescriptor gameDescriptor = Jenkins.getInstance().getDescriptorByType(GameDescriptor.class);
        return gameDescriptor!=null?gameDescriptor.getFailedTestDecreasingPoints():DEFAULT_POINTS;
    }

    public String getName() {
        return Messages.UnitTestingRuleSet_DecreasingFailedRule_Name();
    }

    @Override
    protected String getResultDescription(Integer testDiff) {
        return Messages.UnitTestingRuleSet_DecreasingFailedRule_Count(testDiff);
    }

    @Override
    protected RuleResult<Integer> evaluate(int failedTestDiff) {
        if (failedTestDiff < 0) {
            failedTestDiff = -failedTestDiff;
            return new RuleResult<Integer>(failedTestDiff * getPoints(),
                                           Messages.UnitTestingRuleSet_DecreasingFailedRule_Count(failedTestDiff),
                                           failedTestDiff);
        }
        return null;
    }

}
