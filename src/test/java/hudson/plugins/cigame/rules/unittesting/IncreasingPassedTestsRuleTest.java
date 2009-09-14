package hudson.plugins.cigame.rules.unittesting;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.rules.unittesting.IncreasingPassedTestsRule;
import hudson.tasks.test.AbstractTestResultAction;

import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Bug;

@SuppressWarnings("unchecked")
public class IncreasingPassedTestsRuleTest {

    @Test
    public void testNoTests() throws Exception {
        IncreasingPassedTestsRule rule = new IncreasingPassedTestsRule(10);
        RuleResult result = rule.evaluate(Result.SUCCESS, Result.SUCCESS, 0, 0);
        Assert.assertNull("No new test should return null", result);
    }

    @Test
    public void testMorePassingTests() throws Exception {
        IncreasingPassedTestsRule rule = new IncreasingPassedTestsRule(10);
        RuleResult result = rule.evaluate(Result.UNSTABLE, Result.UNSTABLE, 2, 0);
        Assert.assertThat("2 new test should give 20 result", result.getPoints(), is((double) 20));
    }

    @Test
    public void testLessPassingTests() throws Exception {
        IncreasingPassedTestsRule rule = new IncreasingPassedTestsRule(10);
        RuleResult result = rule.evaluate(Result.UNSTABLE, Result.UNSTABLE, 2, 4);
        Assert.assertNull("2 lost tests should return null", result);
    }

    @Test
    public void testPreviousBuildFailed() throws Exception {
        IncreasingPassedTestsRule rule = new IncreasingPassedTestsRule(10);
        RuleResult result = rule.evaluate(Result.UNSTABLE, Result.FAILURE, 1, 0);
        Assert.assertNull("Previous failed build should return null", result);
    }

    @Test
    public void testCurrentBuildFailed() throws Exception {
        IncreasingPassedTestsRule rule = new IncreasingPassedTestsRule(10);
        RuleResult result = rule.evaluate(Result.FAILURE, Result.UNSTABLE, 1, 0);
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
        when(build.getActions(AbstractTestResultAction.class)).thenReturn(Arrays.asList(action));
        when(action.getPreviousResult()).thenReturn(previousAction);
        when(action.getTotalCount()).thenReturn(10);
        when(previousAction.getTotalCount()).thenReturn(5);
        
        RuleResult ruleResult = new IncreasingPassedTestsRule(-100).evaluate(build);
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
        when(build.getActions(AbstractTestResultAction.class)).thenReturn(Arrays.asList(action));
        when(action.getPreviousResult()).thenReturn(previousAction);
        when(action.getTotalCount()).thenReturn(10);
        when(previousAction.getTotalCount()).thenReturn(5);
        
        RuleResult ruleResult = new IncreasingPassedTestsRule(100).evaluate(build);
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
        when(build.getActions(AbstractTestResultAction.class)).thenReturn(Arrays.asList(action));
        when(action.getPreviousResult()).thenReturn(previousAction);
        when(action.getTotalCount()).thenReturn(10);
        when(action.getSkipCount()).thenReturn(5);
        when(previousAction.getTotalCount()).thenReturn(5);
        when(previousAction.getSkipCount()).thenReturn(1);
        
        RuleResult ruleResult = new IncreasingPassedTestsRule(100).evaluate(build);
        assertThat(ruleResult, notNullValue());
        assertThat(ruleResult.getPoints(), is(100d));
    }
}
