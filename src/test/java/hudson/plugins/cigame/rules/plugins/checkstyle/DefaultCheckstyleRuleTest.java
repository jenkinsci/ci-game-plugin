package hudson.plugins.cigame.rules.plugins.checkstyle;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.checkstyle.CheckStyleResult;
import hudson.plugins.checkstyle.CheckStyleResultAction;
import hudson.plugins.checkstyle.util.model.FileAnnotation;
import hudson.plugins.cigame.model.RuleResult;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class DefaultCheckstyleRuleTest {
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
        classContext.checking(new Expectations() {
            {
                ignoring(build).getResult(); will(returnValue(Result.FAILURE));
            }
        });

        DefaultCheckstyleRule rule = new DefaultCheckstyleRule(100, -100);
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

        DefaultCheckstyleRule rule = new DefaultCheckstyleRule(100, -100);
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
        CheckStyleResult result = mock(CheckStyleResult.class);
        CheckStyleResult previosResult = mock(CheckStyleResult.class);
        CheckStyleResultAction action = new CheckStyleResultAction(build, null, result);
        CheckStyleResultAction previousAction = new CheckStyleResultAction(previousBuild,null, previosResult);
        when(build.getActions(CheckStyleResultAction.class)).thenReturn(Arrays.asList(action));
        when(build.getAction(CheckStyleResultAction.class)).thenReturn(action);
        when(previousBuild.getAction(CheckStyleResultAction.class)).thenReturn(previousAction);
        when(previousBuild.getActions(CheckStyleResultAction.class)).thenReturn(new ArrayList<CheckStyleResultAction>());
        
        FileAnnotation fileannotationOne = mock(FileAnnotation.class);
        FileAnnotation fileannotationTwo = mock(FileAnnotation.class);
        when(result.getAnnotations()).thenReturn(Arrays.asList(fileannotationOne, fileannotationTwo));
        when(previosResult.getAnnotations()).thenReturn(Arrays.asList(fileannotationOne));

        RuleResult ruleResult = new DefaultCheckstyleRule(100, -100).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
}
