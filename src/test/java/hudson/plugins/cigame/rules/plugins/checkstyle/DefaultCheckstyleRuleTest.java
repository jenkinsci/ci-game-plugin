package hudson.plugins.cigame.rules.plugins.checkstyle;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import hudson.maven.MavenBuild;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.checkstyle.CheckStyleResult;
import hudson.plugins.checkstyle.CheckStyleResultAction;
import hudson.plugins.cigame.model.RuleResult;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class DefaultCheckstyleRuleTest {
	
	@Test
	public void assertNewWarningsGiveNegativePoints() {
		AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.SUCCESS);
        addCheckstyleWarnings(build, 3);
        
        AbstractBuild previousBuild = mock(AbstractBuild.class); 
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        addCheckstyleWarnings(previousBuild, 1);
        
        DefaultCheckstyleRule rule = new DefaultCheckstyleRule(-2, 2);
        RuleResult ruleResult = rule.evaluate(previousBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be -4", ruleResult.getPoints(), is(-4d));
	}
	
	@Test
	public void assertRemovedWarningsGivePositivePoints() {
		AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.SUCCESS);
        addCheckstyleWarnings(build, 3);
        
        AbstractBuild previousBuild = mock(AbstractBuild.class); 
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        addCheckstyleWarnings(previousBuild, 12);
        
        DefaultCheckstyleRule rule = new DefaultCheckstyleRule(-2, 2);
        RuleResult ruleResult = rule.evaluate(previousBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 18", ruleResult.getPoints(), is(18d));
	}
    
    @Test
    public void assertFailedBuildsIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.FAILURE);
        addCheckstyleWarnings(build, 1);
        
        AbstractBuild previousBuild = mock(AbstractBuild.class); 
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        addCheckstyleWarnings(previousBuild, 0);

        DefaultCheckstyleRule rule = new DefaultCheckstyleRule(-100, 100);
        RuleResult ruleResult = rule.evaluate(previousBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is(0d));
    }
    
    @Test
    public void assertNoPreviousBuildIsWorthZeroPoints() {        
        AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.FAILURE);
        when(build.getPreviousBuild()).thenReturn(null);
        addCheckstyleWarnings(build, 3);

        DefaultCheckstyleRule rule = new DefaultCheckstyleRule(-100, 100);
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
        CheckStyleResult result = mock(CheckStyleResult.class);
        CheckStyleResult previosResult = mock(CheckStyleResult.class);
        CheckStyleResultAction action = new CheckStyleResultAction(build, mock(HealthDescriptor.class), result);
        CheckStyleResultAction previousAction = new CheckStyleResultAction(previousBuild, mock(HealthDescriptor.class), previosResult);
        when(build.getActions(CheckStyleResultAction.class)).thenReturn(Arrays.asList(action));
        when(build.getAction(CheckStyleResultAction.class)).thenReturn(action);
        when(previousBuild.getAction(CheckStyleResultAction.class)).thenReturn(previousAction);
        when(previousBuild.getActions(CheckStyleResultAction.class)).thenReturn(new ArrayList<CheckStyleResultAction>());

        when(result.getNumberOfAnnotations()).thenReturn(10);
        when(previosResult.getNumberOfAnnotations()).thenReturn(15);

        RuleResult ruleResult = new DefaultCheckstyleRule(-100, 100).evaluate(previousBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
    
    @Test
    public void assertIfPreviousBuildHasErrorsIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        CheckStyleResult result = mock(CheckStyleResult.class);
        CheckStyleResult previosResult = mock(CheckStyleResult.class);
        when(previosResult.hasError()).thenReturn(true);
        CheckStyleResultAction action = new CheckStyleResultAction(build, mock(HealthDescriptor.class), result);
        CheckStyleResultAction previousAction = new CheckStyleResultAction(previousBuild, mock(HealthDescriptor.class), previosResult);
        when(build.getActions(CheckStyleResultAction.class)).thenReturn(Arrays.asList(action));
        when(previousBuild.getAction(CheckStyleResultAction.class)).thenReturn(previousAction);
        when(previousBuild.getActions(CheckStyleResultAction.class)).thenReturn(Arrays.asList(previousAction));

        when(result.getNumberOfAnnotations()).thenReturn(10);
        when(previosResult.getNumberOfAnnotations()).thenReturn(15);
        
        RuleResult ruleResult = new DefaultCheckstyleRule(-100, 100).evaluate(previousBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
    
    @Test
    public void assertNewMavenModuleGivesNegativePoints() {
    	AbstractBuild build = mock(MavenBuild.class); 
        when(build.getResult()).thenReturn(Result.SUCCESS);
        addCheckstyleWarnings(build, 3);
        
        RuleResult ruleResult = new DefaultCheckstyleRule(-1, 1).evaluate(null, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be -3", ruleResult.getPoints(), is(-3d));
    }
    
    @Test
    public void assertRemovedMavenModuleGivesPositivePoints() {
    	AbstractBuild previousBuild = mock(MavenBuild.class); 
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        addCheckstyleWarnings(previousBuild, 3);
        
        RuleResult ruleResult = new DefaultCheckstyleRule(-1, 1).evaluate(previousBuild, null);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 3", ruleResult.getPoints(), is(3d));
    }
    
    private static void addCheckstyleWarnings(AbstractBuild<?, ?> build, int numberOfWarnings) {
    	CheckStyleResult result = mock(CheckStyleResult.class);
        CheckStyleResultAction action = new CheckStyleResultAction(build, mock(HealthDescriptor.class), result);
        when(build.getActions(CheckStyleResultAction.class)).thenReturn(Arrays.asList(action));
        when(result.getNumberOfAnnotations()).thenReturn(numberOfWarnings);
    }
}
