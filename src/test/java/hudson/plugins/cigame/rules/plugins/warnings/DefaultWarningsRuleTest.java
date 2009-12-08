package hudson.plugins.cigame.rules.plugins.warnings;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.warnings.WarningsResult;
import hudson.plugins.warnings.WarningsResultAction;

import java.util.Arrays;

import org.junit.Test;
    
@SuppressWarnings("unchecked")
public class DefaultWarningsRuleTest {    
    @Test
    public void assertFailedBuildsIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.FAILURE);

        DefaultWarningsRule rule = new DefaultWarningsRule(100, -100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }
    
    @Test
    public void assertNoPreviousBuildIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(build.getPreviousBuild()).thenReturn(null);

        DefaultWarningsRule rule = new DefaultWarningsRule(100, -100);
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
        WarningsResult result = mock(WarningsResult.class);
        WarningsResult previosResult = mock(WarningsResult.class);
        WarningsResultAction action = new WarningsResultAction(build, mock(HealthDescriptor.class), result);
        WarningsResultAction previousAction = new WarningsResultAction(previousBuild,mock(HealthDescriptor.class), previosResult);
        when(build.getActions(WarningsResultAction.class)).thenReturn(Arrays.asList(action));
        when(previousBuild.getActions(WarningsResultAction.class)).thenReturn(Arrays.asList(previousAction));
        
        when(result.getNumberOfAnnotations()).thenReturn(15);
        when(previosResult.getNumberOfAnnotations()).thenReturn(10);

        RuleResult ruleResult = new DefaultWarningsRule(100, -100).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
    
    @Test
    public void assertIfPreviousHasErrorsResultIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        WarningsResult result = mock(WarningsResult.class);
        WarningsResult previosResult = mock(WarningsResult.class);
        when(previosResult.hasError()).thenReturn(true);
        WarningsResultAction action = new WarningsResultAction(build, mock(HealthDescriptor.class), result);
        WarningsResultAction previousAction = new WarningsResultAction(previousBuild,mock(HealthDescriptor.class), previosResult);
        when(build.getActions(WarningsResultAction.class)).thenReturn(Arrays.asList(action));
        when(previousBuild.getActions(WarningsResultAction.class)).thenReturn(Arrays.asList(previousAction));
        
        when(result.getNumberOfAnnotations()).thenReturn(15);
        when(previosResult.getNumberOfAnnotations()).thenReturn(10);

        RuleResult ruleResult = new DefaultWarningsRule(100, -100).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
}
