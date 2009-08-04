package hudson.plugins.cigame.rules.unittesting;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.rules.unittesting.IncreasingFailedTestsRule;
import hudson.tasks.test.AbstractTestResultAction;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class IncreasingFailedTestsRuleTest {
    @Test
    public void testNoTests() throws Exception {
        IncreasingFailedTestsRule rule = new IncreasingFailedTestsRule(-10);
        RuleResult result = rule.evaluate(Result.SUCCESS, Result.SUCCESS, 0, 0);
        assertNull("No new test should return null", result);
    }

    @Test
    public void testMoreFailingTests() throws Exception {
        IncreasingFailedTestsRule rule = new IncreasingFailedTestsRule(-10);
        RuleResult result = rule.evaluate(Result.UNSTABLE, Result.UNSTABLE, 2, 0);
        assertThat("2 new test should give -20 result", result.getPoints(), is((double) -20));
    }

    @Test
    public void testLessFailingTests() throws Exception {
        IncreasingFailedTestsRule rule = new IncreasingFailedTestsRule(-10);
        RuleResult result = rule.evaluate(Result.UNSTABLE, Result.UNSTABLE, 2, 4);
        assertNull("2 lost tests should return null", result);
    }

    @Test
    public void testPreviousBuildFailed() throws Exception {
        IncreasingFailedTestsRule rule = new IncreasingFailedTestsRule(-10);
        RuleResult result = rule.evaluate(Result.UNSTABLE, Result.FAILURE, 1, 0);
        assertNull("Previous failed build should return null", result);
    }

    @Test
    public void testCurrentBuildFailed() throws Exception {
        IncreasingFailedTestsRule rule = new IncreasingFailedTestsRule(-10);
        RuleResult result = rule.evaluate(Result.FAILURE, Result.UNSTABLE, 1, 0);
        assertNull("Current build failed should return null", result);
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
        when(action.getFailCount()).thenReturn(10);
        when(previousAction.getFailCount()).thenReturn(5);
        
        RuleResult ruleResult = new IncreasingFailedTestsRule(-100).evaluate(build);
        assertNull("Rule result must be null", ruleResult);
    }
}
