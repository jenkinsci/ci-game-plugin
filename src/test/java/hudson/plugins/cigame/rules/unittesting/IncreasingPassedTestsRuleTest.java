package hudson.plugins.cigame.rules.unittesting;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.plugins.cigame.GameDescriptor;
import hudson.plugins.cigame.model.RuleResult;
import hudson.tasks.test.AbstractTestResultAction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Bug;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mockito;

@SuppressWarnings("unchecked")
public class IncreasingPassedTestsRuleTest {

    private GameDescriptor gameDescriptor;
    
    @Rule 
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    @Before 
    public void setup() {
        gameDescriptor = jenkinsRule.jenkins.getDescriptorByType(GameDescriptor.class);
    }
    
    @Test
    public void testNoTests() throws Exception {
        gameDescriptor.setPassedTestIncreasingPoints(10);
        
        IncreasingPassedTestsRule rule = new IncreasingPassedTestsRule();
        RuleResult result = rule.evaluate((0 - 0 - 0) - (0 - 0 - 0));
        Assert.assertNull("No new test should return null", result);
    }

    @Test
    public void testMorePassingTests() throws Exception {
        gameDescriptor.setPassedTestIncreasingPoints(10);
        
        IncreasingPassedTestsRule rule = new IncreasingPassedTestsRule();
        RuleResult result = rule.evaluate((2 - 0 - 0) - (0 - 0 - 0));
        Assert.assertThat("2 new test should give 20 result", result.getPoints(), is((double) 20));
    }

    @Test
    public void testLessPassingTests() throws Exception {
        gameDescriptor.setPassedTestIncreasingPoints(10);
        
        IncreasingPassedTestsRule rule = new IncreasingPassedTestsRule();
        RuleResult result = rule.evaluate((2 - 0  - 0) - (4 - 0 - 0));
        Assert.assertNull("2 lost tests should return null", result);
    }
    
    @Test
    public void testNoMorePointsThanPassingTests() throws Exception {
        gameDescriptor.setPassedTestIncreasingPoints(1);
        
        IncreasingPassedTestsRule rule = new IncreasingPassedTestsRule();
        
    	// JENKINS-6446 
    	// Previous build: Total number of tests = 71 + 67 + 2963
    	// Current build: Total number of tests = 610
    	// So 
    	// no. of new passing tests = 610 - 71
    	// no. of new failing tests = 0 - 67
    	// no. of new skipped tested = 0 - 2963
        
    	AbstractBuild<?, ?> previousBuild =
        	MavenMultiModuleUnitTestsTest.mockBuild(Result.UNSTABLE,
        			71, 67, 2963);
        AbstractBuild<?, ?> build =
        	MavenMultiModuleUnitTestsTest.mockBuild(Result.SUCCESS,
        			610, 0, 0);
        
        RuleResult result = rule.evaluate(previousBuild, build);
        Assert.assertNotNull(result);
        Assert.assertEquals((610 - 71), result.getPoints(), 0.1);
    }

    @Test
    public void testPreviousBuildFailed() throws Exception {
        gameDescriptor.setPassedTestIncreasingPoints(10);
        
        IncreasingPassedTestsRule rule = new IncreasingPassedTestsRule();
        AbstractBuild<?, ?> previousBuild =
        	MavenMultiModuleUnitTestsTest.mockBuild(Result.FAILURE,
        			1, 1, 0);
        AbstractBuild<?, ?> build =
        	MavenMultiModuleUnitTestsTest.mockBuild(Result.UNSTABLE,
        			2, 0, 0);
        
        RuleResult result = rule.evaluate(previousBuild, build);
        Assert.assertNull("Previous failed build should return null", result);
    }

    @Test
    public void testCurrentBuildFailed() throws Exception {
        gameDescriptor.setPassedTestIncreasingPoints(10);
        
        IncreasingPassedTestsRule rule = new IncreasingPassedTestsRule();
        AbstractBuild<?, ?> previousBuild =
        	MavenMultiModuleUnitTestsTest.mockBuild(Result.UNSTABLE,
        			1, 1, 0);
        AbstractBuild<?, ?> build =
        	MavenMultiModuleUnitTestsTest.mockBuild(Result.FAILURE,
        			2, 0, 0);
        
        RuleResult result = rule.evaluate(previousBuild, build);
        Assert.assertNull("Current build failed should return null", result);
    }

    @Test
    public void assertIfPreviousBuildFailedResultIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.FAILURE);
        AbstractTestResultAction action = mock(AbstractTestResultAction.class);
        AbstractTestResultAction previousAction = mock(AbstractTestResultAction.class);
        when(build.getTestResultAction()).thenReturn(action);
        when(action.getPreviousResult()).thenReturn(previousAction);
        when(action.getTotalCount()).thenReturn(10);
        when(previousAction.getTotalCount()).thenReturn(5);
        
        gameDescriptor.setPassedTestIncreasingPoints(-100);
        
        RuleResult ruleResult = new IncreasingPassedTestsRule().evaluate(previousBuild, build);
        assertNull("Rule result must be null", ruleResult);
    }

    @Test
    public void assertResultIsCalculated() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        AbstractTestResultAction action = mock(AbstractTestResultAction.class);
        AbstractTestResultAction previousAction = mock(AbstractTestResultAction.class);
        when(build.getTestResultAction()).thenReturn(action);
        when(action.getPreviousResult()).thenReturn(previousAction);
        when(previousBuild.getTestResultAction()).thenReturn(previousAction);
        when(action.getTotalCount()).thenReturn(10);
        when(previousAction.getTotalCount()).thenReturn(5);
        
        gameDescriptor.setPassedTestIncreasingPoints(100);
        
        RuleResult ruleResult = new IncreasingPassedTestsRule().evaluate(previousBuild, build);
        assertThat(ruleResult, notNullValue());
        assertThat(ruleResult.getPoints(), is(500d));
    }

    @Bug(4449)
    @Test
    public void assertSkippedTestIsntCalculated() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);
        AbstractTestResultAction action = mock(AbstractTestResultAction.class);
        AbstractTestResultAction previousAction = mock(AbstractTestResultAction.class);
        when(build.getTestResultAction()).thenReturn(action);
        when(action.getPreviousResult()).thenReturn(previousAction);
        when(previousBuild.getTestResultAction()).thenReturn(previousAction);
        when(action.getTotalCount()).thenReturn(10);
        when(action.getSkipCount()).thenReturn(5);
        when(previousAction.getTotalCount()).thenReturn(5);
        when(previousAction.getSkipCount()).thenReturn(1);
        
        gameDescriptor.setPassedTestIncreasingPoints(100);
        
        RuleResult ruleResult = new IncreasingPassedTestsRule().evaluate(previousBuild, build);
        assertThat(ruleResult, notNullValue());
        assertThat(ruleResult.getPoints(), is(100d));
    }
}
