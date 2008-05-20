package hudson.plugins.cigame.rules.unittesting;

import hudson.model.Result;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.rules.unittesting.IncreasingPassedTestsRule;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals("2 new test should give 20 result", 20, result.getPoints());
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
}
