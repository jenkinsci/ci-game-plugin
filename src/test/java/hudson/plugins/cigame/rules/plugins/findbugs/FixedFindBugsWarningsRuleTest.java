package hudson.plugins.cigame.rules.plugins.findbugs;

import java.util.Arrays;

import org.junit.Test;

import hudson.maven.MavenBuild;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.findbugs.FindBugsMavenResultAction;
import hudson.plugins.findbugs.FindBugsResult;
import hudson.plugins.findbugs.FindBugsResultAction;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class FixedFindBugsWarningsRuleTest {
    
    @Test
    public void assertFailedBuildsIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);
        addFindBugsWarnings(build, 0);
        
        AbstractBuild prevBuild = mock(AbstractBuild.class);
        when(prevBuild.getResult()).thenReturn(Result.SUCCESS);
        addFindBugsWarnings(prevBuild, 7);

        FixedFindBugsWarningsRule rule = new FixedFindBugsWarningsRule(Priority.LOW, 100);
        RuleResult ruleResult = rule.evaluate(prevBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }
    
    @Test
    public void assertFailedMavenBuildsIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);
        addMavenFindBugsWarnings(build, 0);
        
        AbstractBuild prevBuild = mock(AbstractBuild.class);
        when(prevBuild.getResult()).thenReturn(Result.SUCCESS);
        addMavenFindBugsWarnings(prevBuild, 7);

        FixedFindBugsWarningsRule rule = new FixedFindBugsWarningsRule(Priority.LOW, 100);
        RuleResult ruleResult = rule.evaluate(prevBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }
    
    @Test
    public void assertNoPreviousBuildIsWorthZeroPoints() { 
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);
        when(build.getPreviousBuild()).thenReturn(null);
        addFindBugsWarnings(build, 0);

        FixedFindBugsWarningsRule rule = new FixedFindBugsWarningsRule(Priority.LOW, 100);
        RuleResult ruleResult = rule.evaluate(null, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }
    
    @Test
    public void assertNoPreviousMavenBuildIsWorthZeroPoints() { 
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);
        when(build.getPreviousBuild()).thenReturn(null);
        addMavenFindBugsWarnings(build, 0);

        FixedFindBugsWarningsRule rule = new FixedFindBugsWarningsRule(Priority.LOW, 100);
        RuleResult ruleResult = rule.evaluate(null, build);
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

        RuleResult ruleResult = new FixedFindBugsWarningsRule(Priority.LOW, -4).evaluate(previousBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
    
    @Test
    public void assertIfPreviousMavenBuildFailedResultIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.FAILURE);
        FindBugsResult result = mock(FindBugsResult.class);
        FindBugsResult previosResult = mock(FindBugsResult.class);
        FindBugsMavenResultAction action = new FindBugsMavenResultAction(build, mock(HealthDescriptor.class), "UTF-8", result);
        FindBugsMavenResultAction previousAction = new FindBugsMavenResultAction(previousBuild, mock(HealthDescriptor.class), "UTF-8", previosResult);
        when(build.getActions(FindBugsMavenResultAction.class)).thenReturn(Arrays.asList(action));
        when(build.getAction(FindBugsMavenResultAction.class)).thenReturn(action);
        when(previousBuild.getAction(FindBugsMavenResultAction.class)).thenReturn(previousAction);
        when(previousBuild.getActions(FindBugsMavenResultAction.class)).thenReturn(Arrays.asList(previousAction));
        
        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(5);
        when(previosResult.getNumberOfAnnotations(Priority.LOW)).thenReturn(10);

        RuleResult ruleResult = new FixedFindBugsWarningsRule(Priority.LOW, -4).evaluate(previousBuild, build);
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

        RuleResult ruleResult = new FixedFindBugsWarningsRule(Priority.LOW, -4).evaluate(previousBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
    
    @Test
    public void assertRemovedMavenModuleCountsAsFixed() {
    	AbstractBuild previousBuild = mock(MavenBuild.class);
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        addMavenFindBugsWarnings(previousBuild, 6);
        
        RuleResult ruleResult= new FixedFindBugsWarningsRule(Priority.LOW, 1).evaluate(previousBuild, null);
        assertNotNull(ruleResult);
        assertThat("Points should be 6", ruleResult.getPoints(), is(6d));
    }
    
    private static void addFindBugsWarnings(AbstractBuild<?, ?> build, int numberOfWarnings) {
    	FindBugsResult result = mock(FindBugsResult.class);
        FindBugsResultAction action = new FindBugsResultAction(build, mock(HealthDescriptor.class), result);
        when(build.getActions(FindBugsResultAction.class)).thenReturn(Arrays.asList(action));
        
        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(numberOfWarnings);
    }
    
    private static void addMavenFindBugsWarnings(AbstractBuild<?, ?> build, int numberOfWarnings) {
    	FindBugsResult result = mock(FindBugsResult.class);
        FindBugsMavenResultAction action = new FindBugsMavenResultAction(build, mock(HealthDescriptor.class), "UTF-8", result);
        when(build.getActions(FindBugsMavenResultAction.class)).thenReturn(Arrays.asList(action));
        
        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(numberOfWarnings);
    }
}
