package hudson.plugins.cigame.rules.plugins.findbugs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.findbugs.util.model.Priority;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;


public class FixedFindBugsWarningsRuleTest {

    private Mockery context;
    private Mockery classContext;
    private AbstractBuild<?,?> build;
    
    @Before
    public void setUp() throws Exception {
        context = new Mockery();
        classContext = new Mockery() {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        build = classContext.mock(AbstractBuild.class);
    }
    
    @Test
    public void assertFailedBuildsIsWorthZeroPoints() {
        
        final Result buildResult = Result.FAILURE;
        classContext.checking(new Expectations() {
            {
                ignoring(build).getResult(); will(returnValue(buildResult));
            }
        });

        FixedFindBugsWarningsRule rule = new FixedFindBugsWarningsRule(Priority.LOW, 100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertEquals("Points should be zero", 0, ruleResult.getPoints());
        
        classContext.assertIsSatisfied();
        context.assertIsSatisfied();
    }
    
    @Test
    public void assertNoPreviousBuildIsWorthZeroPoints() {        
        classContext.checking(new Expectations() {
            {
                ignoring(build).getResult(); will(returnValue(Result.SUCCESS));
                ignoring(build).getPreviousBuild(); will(returnValue(null));
            }
        });

        FixedFindBugsWarningsRule rule = new FixedFindBugsWarningsRule(Priority.LOW, 100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertEquals("Points should be zero", 0, ruleResult.getPoints());
        
        classContext.assertIsSatisfied();
        context.assertIsSatisfied();
    }
}
