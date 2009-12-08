package hudson.plugins.cigame.rules.plugins.pmd;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.pmd.PmdResult;
import hudson.plugins.pmd.PmdResultAction;

import java.util.Arrays;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class DefaultPmdRuleTest {
    
    @Test
    public void assertFailedBuildsIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.FAILURE);

        DefaultPmdRule rule = new DefaultPmdRule(Priority.HIGH, 100, -100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }
    
    @Test
    public void assertNoPreviousBuildIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.FAILURE);
        when(build.getPreviousBuild()).thenReturn(null);

        DefaultPmdRule rule = new DefaultPmdRule(Priority.HIGH, 100, -100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
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
        PmdResultAction action = new PmdResultAction(build, mock(HealthDescriptor.class), result);
        PmdResultAction previousAction = new PmdResultAction(previousBuild, mock(HealthDescriptor.class), previosResult);
        when(build.getActions(PmdResultAction.class)).thenReturn(Arrays.asList(action));
        when(build.getAction(PmdResultAction.class)).thenReturn(action);
        when(previousBuild.getAction(PmdResultAction.class)).thenReturn(previousAction);
        when(previousBuild.getActions(PmdResultAction.class)).thenReturn(Arrays.asList(previousAction));
        
        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(10);
        when(previosResult.getNumberOfAnnotations(Priority.LOW)).thenReturn(5);

        RuleResult ruleResult = new DefaultPmdRule(Priority.LOW, 100, -100).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
    
    @Test
    public void assertIfPreviousBuildHasErrorsResultIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        PmdResult result = mock(PmdResult.class);
        PmdResult previosResult = mock(PmdResult.class);
        when(previosResult.hasError()).thenReturn(true);
        PmdResultAction action = new PmdResultAction(build, mock(HealthDescriptor.class), result);
        PmdResultAction previousAction = new PmdResultAction(previousBuild, mock(HealthDescriptor.class), previosResult);
        when(build.getActions(PmdResultAction.class)).thenReturn(Arrays.asList(action));
        when(build.getAction(PmdResultAction.class)).thenReturn(action);
        when(previousBuild.getAction(PmdResultAction.class)).thenReturn(previousAction);
        when(previousBuild.getActions(PmdResultAction.class)).thenReturn(Arrays.asList(previousAction));
        
        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(10);
        when(previosResult.getNumberOfAnnotations(Priority.LOW)).thenReturn(5);

        RuleResult ruleResult = new DefaultPmdRule(Priority.LOW, 100, -100).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
}
