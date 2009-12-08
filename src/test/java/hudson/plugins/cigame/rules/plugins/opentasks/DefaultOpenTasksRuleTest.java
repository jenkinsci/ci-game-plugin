package hudson.plugins.cigame.rules.plugins.opentasks;

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
import hudson.plugins.tasks.TasksResult;
import hudson.plugins.tasks.TasksResultAction;

import java.util.Arrays;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class DefaultOpenTasksRuleTest {
    
    @Test
    public void assertFailedBuildsIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.FAILURE);

        DefaultOpenTasksRule rule = new DefaultOpenTasksRule(Priority.HIGH, 100, -100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }
    
    @Test
    public void assertNoPreviousBuildIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.FAILURE);
        when(build.getPreviousBuild()).thenReturn(null);

        DefaultOpenTasksRule rule = new DefaultOpenTasksRule(Priority.HIGH, 100, -100);
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
        TasksResult result = mock(TasksResult.class);
        TasksResult previosResult = mock(TasksResult.class);
        TasksResultAction action = new TasksResultAction(build, mock(HealthDescriptor.class), result);
        TasksResultAction previousAction = new TasksResultAction(previousBuild,mock(HealthDescriptor.class), previosResult);
        when(build.getActions(TasksResultAction.class)).thenReturn(Arrays.asList(action));
        when(build.getAction(TasksResultAction.class)).thenReturn(action);
        when(previousBuild.getAction(TasksResultAction.class)).thenReturn(previousAction);
        when(previousBuild.getActions(TasksResultAction.class)).thenReturn(Arrays.asList(previousAction));
        
        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(15);
        when(previosResult.getNumberOfAnnotations(Priority.LOW)).thenReturn(10);

        RuleResult ruleResult = new DefaultOpenTasksRule(Priority.LOW, 100, -100).evaluate(build);
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
        TasksResult result = mock(TasksResult.class);
        TasksResult previosResult = mock(TasksResult.class);
        when(previosResult.hasError()).thenReturn(true);
        TasksResultAction action = new TasksResultAction(build, mock(HealthDescriptor.class), result);
        TasksResultAction previousAction = new TasksResultAction(previousBuild,mock(HealthDescriptor.class), previosResult);
        when(build.getActions(TasksResultAction.class)).thenReturn(Arrays.asList(action));
        when(previousBuild.getActions(TasksResultAction.class)).thenReturn(Arrays.asList(previousAction));
        
        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(15);
        when(previosResult.getNumberOfAnnotations(Priority.LOW)).thenReturn(10);

        RuleResult ruleResult = new DefaultOpenTasksRule(Priority.LOW, 100, -100).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
}
