package hudson.plugins.cigame.rules.unittesting;

import jenkins.model.Jenkins;
import hudson.plugins.cigame.GameDescriptor;
import hudson.plugins.cigame.model.RuleResult;

/**
 * Rule that gives points for increasing the number of skipped tests. By default 0 marks given.
 * 
 * @author <a href="www.digizol.com">Kamal Mettananda</a>
 * @since 1.20
 */
public class IncreasingSkippedTestsRule extends AbstractSkippedTestsRule {

    private static final int DEFAULT_POINTS = 0;

    private int getPoints() {
        GameDescriptor gameDescriptor = Jenkins.getInstance().getDescriptorByType(GameDescriptor.class);
        return gameDescriptor!=null?gameDescriptor.getSkippedTestIncreasingPoints():DEFAULT_POINTS;
    }
    
    public String getName() {
        return Messages.UnitTestingRuleSet_IncreasingSkippedRule_Name();
    }

    @Override
    protected String getResultDescription(Integer testDiff) {
        return Messages.UnitTestingRuleSet_IncreasingSkippedRule_Count(testDiff);
    }

    @Override
    protected RuleResult<Integer> evaluate(int passedTestDiff) {
        if (passedTestDiff > 0) {
            return new RuleResult<Integer>(passedTestDiff * getPoints(),
                                           Messages.UnitTestingRuleSet_IncreasingSkippedRule_Count(passedTestDiff),
                                           passedTestDiff);
        }
        return null;
    }

}
