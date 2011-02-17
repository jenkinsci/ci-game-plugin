package hudson.plugins.cigame.rules.plugins.warnings;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.maven.MavenBuild;
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
	public void assertNewWarningsGiveNegativePoints() {
		AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.SUCCESS);
        addWarnings(build, 3);
        
        AbstractBuild previousBuild = mock(AbstractBuild.class); 
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        addWarnings(previousBuild, 1);
        
        DefaultWarningsRule rule = new DefaultWarningsRule(-2, 2);
        RuleResult ruleResult = rule.evaluate(previousBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be -4", ruleResult.getPoints(), is(-4d));
	}
	
	@Test
	public void assertRemovedWarningsGivePositivePoints() {
		AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.SUCCESS);
        addWarnings(build, 3);
        
        AbstractBuild previousBuild = mock(AbstractBuild.class); 
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        addWarnings(previousBuild, 12);
        
        DefaultWarningsRule rule = new DefaultWarningsRule(-2, 2);
        RuleResult ruleResult = rule.evaluate(previousBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 18", ruleResult.getPoints(), is(18d));
	}
	
    @Test
    public void assertFailedBuildsIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.FAILURE);
        addWarnings(build, 15);
        
        AbstractBuild previousBuild = mock(AbstractBuild.class); 
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        addWarnings(previousBuild, 10);

        DefaultWarningsRule rule = new DefaultWarningsRule(-100, 100);
        RuleResult ruleResult = rule.evaluate(previousBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }
    
    @Test
    public void assertNoPreviousBuildIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class); 
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(build.getPreviousBuild()).thenReturn(null);
        addWarnings(build, 7);

        DefaultWarningsRule rule = new DefaultWarningsRule(-100, 100);
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
        WarningsResult result = mock(WarningsResult.class);
        WarningsResult previosResult = mock(WarningsResult.class);
        WarningsResultAction action = new WarningsResultAction(build, mock(HealthDescriptor.class), result);
        WarningsResultAction previousAction = new WarningsResultAction(previousBuild,mock(HealthDescriptor.class), previosResult);
        when(build.getActions(WarningsResultAction.class)).thenReturn(Arrays.asList(action));
        when(previousBuild.getActions(WarningsResultAction.class)).thenReturn(Arrays.asList(previousAction));
        
        when(result.getNumberOfAnnotations()).thenReturn(15);
        when(previosResult.getNumberOfAnnotations()).thenReturn(10);

        RuleResult ruleResult = new DefaultWarningsRule(-100, 100).evaluate(previousBuild, build);
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

        RuleResult ruleResult = new DefaultWarningsRule(-100, 100).evaluate(previousBuild, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
    
    @Test
    public void assertNewMavenModuleGivesNegativePoints() {
    	AbstractBuild build = mock(MavenBuild.class); 
        when(build.getResult()).thenReturn(Result.SUCCESS);
        addWarnings(build, 3);
        
        RuleResult ruleResult = new DefaultWarningsRule(-1, 1).evaluate(null, build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be -3", ruleResult.getPoints(), is(-3d));
    }
    
    @Test
    public void assertRemovedMavenModuleGivesPositivePoints() {
    	AbstractBuild previousBuild = mock(MavenBuild.class); 
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        addWarnings(previousBuild, 3);
        
        RuleResult ruleResult = new DefaultWarningsRule(-1, 1).evaluate(previousBuild, null);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 3", ruleResult.getPoints(), is(3d));
    }
    
    private static void addWarnings(AbstractBuild build, int numberOfWarnings) {
    	WarningsResult result = mock(WarningsResult.class);
        WarningsResultAction action = new WarningsResultAction(build, mock(HealthDescriptor.class), result);
        when(build.getActions(WarningsResultAction.class)).thenReturn(Arrays.asList(action));
        
        when(result.getNumberOfAnnotations()).thenReturn(numberOfWarnings);
    }
}
