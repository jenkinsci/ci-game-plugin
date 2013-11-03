package hudson.plugins.cigame.rules.unittesting;

import jenkins.model.Jenkins;
import hudson.plugins.cigame.GameDescriptor;
import hudson.plugins.cigame.model.RuleResult;

/**
 * Rule that gives points for decreasing the number of passed tests. By default -1 mark given.
 * 
 * @author <a href="www.digizol.com">Kamal Mettananda</a>
 * @since 1.20
 */
public class DecreasingPassedTestsRule extends AbstractPassedTestsRule {

    private static final int DEFAULT_POINTS = 1;

    private int getPoints() {
        GameDescriptor gameDescriptor = Jenkins.getInstance().getDescriptorByType(GameDescriptor.class);
        return gameDescriptor!=null?gameDescriptor.getPassedTestDecreasingPoints():DEFAULT_POINTS;
    }
    
    public String getName() {
        return Messages.UnitTestingRuleSet_DecreasingPassedRule_Name();
    }

    @Override
    protected String getResultDescription(Integer testDiff) {
        return Messages.UnitTestingRuleSet_DecreasingPassedRule_Count(testDiff);
    }

    @Override
    protected RuleResult<Integer> evaluate(int passedTestDiff) {
        if (passedTestDiff < 0) {
            passedTestDiff = -passedTestDiff;
            return new RuleResult<Integer>(passedTestDiff * getPoints(),
                                           Messages.UnitTestingRuleSet_DecreasingPassedRule_Count(passedTestDiff),
                                           passedTestDiff);
        }
        return null;
    }

}
