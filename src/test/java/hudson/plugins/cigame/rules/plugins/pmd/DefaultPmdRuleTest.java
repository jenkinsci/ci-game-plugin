package hudson.plugins.cigame.rules.plugins.pmd;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.pmd.PmdResult;
import hudson.plugins.pmd.PmdResultAction;
import hudson.plugins.pmd.util.model.Priority;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class DefaultPmdRuleTest {
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

        DefaultPmdRule rule = new DefaultPmdRule(Priority.HIGH, 100, -100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
        
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

        DefaultPmdRule rule = new DefaultPmdRule(Priority.HIGH, 100, -100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
        
        classContext.assertIsSatisfied();
        context.assertIsSatisfied();
    }
    
    @Test
    public void assertIfPreviousBuildFailedResultIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.FAILURE);
        PmdResult result = mock(PmdResult.class);
        PmdResult previosResult = mock(PmdResult.class);
        PmdResultAction action = new PmdResultAction(build, null, result);
        PmdResultAction previousAction = new PmdResultAction(previousBuild,null, previosResult);
        when(build.getActions(PmdResultAction.class)).thenReturn(Arrays.asList(action));
        when(build.getAction(PmdResultAction.class)).thenReturn(action);
        when(previousBuild.getAction(PmdResultAction.class)).thenReturn(previousAction);
        when(previousBuild.getActions(PmdResultAction.class)).thenReturn(new ArrayList<PmdResultAction>());
        
        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(10);
        when(previosResult.getNumberOfAnnotations(Priority.LOW)).thenReturn(5);

        RuleResult ruleResult = new DefaultPmdRule(Priority.LOW, 100, -100).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
}
