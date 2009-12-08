package hudson.plugins.cigame.rules.plugins.findbugs;

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
import hudson.plugins.findbugs.FindBugsResult;
import hudson.plugins.findbugs.FindBugsResultAction;

import java.util.Arrays;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class FixedFindBugsWarningsRuleTest {
    
    @Test
    public void assertFailedBuildsIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);

        FixedFindBugsWarningsRule rule = new FixedFindBugsWarningsRule(Priority.LOW, 100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }
    
    @Test
    public void assertNoPreviousBuildIsWorthZeroPoints() { 
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);
        when(build.getPreviousBuild()).thenReturn(null);

        FixedFindBugsWarningsRule rule = new FixedFindBugsWarningsRule(Priority.LOW, 100);
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
        FindBugsResult result = mock(FindBugsResult.class);
        FindBugsResult previosResult = mock(FindBugsResult.class);
        FindBugsResultAction action = new FindBugsResultAction(build, mock(HealthDescriptor.class), result);
        FindBugsResultAction previousAction = new FindBugsResultAction(previousBuild, mock(HealthDescriptor.class), previosResult);
        when(build.getActions(FindBugsResultAction.class)).thenReturn(Arrays.asList(action));
        when(build.getAction(FindBugsResultAction.class)).thenReturn(action);
        when(previousBuild.getAction(FindBugsResultAction.class)).thenReturn(previousAction);
        when(previousBuild.getActions(FindBugsResultAction.class)).thenReturn(Arrays.asList(previousAction));
        
        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(5);
        when(previosResult.getNumberOfAnnotations(Priority.LOW)).thenReturn(10);

        RuleResult ruleResult = new FixedFindBugsWarningsRule(Priority.LOW, -4).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
    
    @Test
    public void assertIfPreviousHasErrorsFailedResultIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        FindBugsResult result = mock(FindBugsResult.class);
        FindBugsResult previosResult = mock(FindBugsResult.class);
        when(previosResult.hasError()).thenReturn(true);
        FindBugsResultAction action = new FindBugsResultAction(build, mock(HealthDescriptor.class), result);
        FindBugsResultAction previousAction = new FindBugsResultAction(previousBuild, mock(HealthDescriptor.class), previosResult);
        when(build.getActions(FindBugsResultAction.class)).thenReturn(Arrays.asList(action));
        when(build.getAction(FindBugsResultAction.class)).thenReturn(action);
        when(previousBuild.getAction(FindBugsResultAction.class)).thenReturn(previousAction);
        when(previousBuild.getActions(FindBugsResultAction.class)).thenReturn(Arrays.asList(previousAction));
        
        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(5);
        when(previosResult.getNumberOfAnnotations(Priority.LOW)).thenReturn(10);

        RuleResult ruleResult = new FixedFindBugsWarningsRule(Priority.LOW, -4).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
}
