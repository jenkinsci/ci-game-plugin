package hudson.plugins.cigame.rules.unittesting;

import static org.junit.Assert.*;
import hudson.model.Result;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.rules.unittesting.IncreasingFailedTestsRule;

import org.junit.Test;

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
        assertEquals("2 new test should give -20 result", -20, result.getPoints());
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
}
