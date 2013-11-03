package hudson.plugins.cigame.rules.unittesting;

import jenkins.model.Jenkins;
import hudson.plugins.cigame.GameDescriptor;
import hudson.plugins.cigame.model.RuleResult;

/**
 * Rule that gives points for decreasing the number of skipped tests. By default 0 marks given.
 * 
 * @author <a href="www.digizol.com">Kamal Mettananda</a>
 * @since 1.20
 */
public class DecreasingSkippedTestsRule extends AbstractSkippedTestsRule {

    private static final int DEFAULT_POINTS = 0;

    private int getPoints() {
        GameDescriptor gameDescriptor = Jenkins.getInstance().getDescriptorByType(GameDescriptor.class);
        return gameDescriptor!=null?gameDescriptor.getSkippedTestDecreasingPoints():DEFAULT_POINTS;
    }
    
    public String getName() {
        return Messages.UnitTestingRuleSet_DecreasingSkippedRule_Name();
    }

    @Override
    protected String getResultDescription(Integer testDiff) {
        return Messages.UnitTestingRuleSet_DecreasingSkippedRule_Count(testDiff);
    }

    @Override
    protected RuleResult<Integer> evaluate(int skippedTestDiff) {
        if (skippedTestDiff < 0) {
            skippedTestDiff = -skippedTestDiff;
            return new RuleResult<Integer>(skippedTestDiff * getPoints(),
                                           Messages.UnitTestingRuleSet_DecreasingSkippedRule_Count(skippedTestDiff),
                                           skippedTestDiff);
        }
        return null;
    }

}
