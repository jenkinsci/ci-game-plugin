package hudson.plugins.cigame.rules.plugins.warnings;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.warnings.WarningsResult;
import hudson.plugins.warnings.WarningsResultAction;
import hudson.plugins.warnings.util.model.FileAnnotation;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class DefaultWarningsRuleTest {

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

        DefaultWarningsRule rule = new DefaultWarningsRule(100, -100);
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

        DefaultWarningsRule rule = new DefaultWarningsRule(100, -100);
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
        WarningsResult result = mock(WarningsResult.class);
        WarningsResult previosResult = mock(WarningsResult.class);
        WarningsResultAction action = new WarningsResultAction(build, null, result);
        WarningsResultAction previousAction = new WarningsResultAction(previousBuild,null, previosResult);
        when(build.getActions(WarningsResultAction.class)).thenReturn(Arrays.asList(action));
        when(build.getAction(WarningsResultAction.class)).thenReturn(action);
        when(previousBuild.getAction(WarningsResultAction.class)).thenReturn(previousAction);
        when(previousBuild.getActions(WarningsResultAction.class)).thenReturn(new ArrayList<WarningsResultAction>());
        
        FileAnnotation fileannotationOne = mock(FileAnnotation.class);
        FileAnnotation fileannotationTwo = mock(FileAnnotation.class);
        when(result.getAnnotations()).thenReturn(Arrays.asList(fileannotationOne, fileannotationTwo));
        when(previosResult.getAnnotations()).thenReturn(Arrays.asList(fileannotationOne));

        RuleResult ruleResult = new DefaultWarningsRule(100, -100).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
}
