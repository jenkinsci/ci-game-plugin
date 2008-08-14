package hudson.plugins.cigame.rules.build;

import hudson.model.Result;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.rules.build.BuildResultRule;

import org.junit.Test;
import static org.junit.Assert.*;

public class BuildResultRuleTest {

    @Test
    public void testFirstBuildSuccess() {
        BuildResultRule rule = new BuildResultRule(100, -100);
        RuleResult results = rule.evaluate(Result.SUCCESS, null);
        assertEquals("Successful build should give 100 results", 100, results.getPoints());
    }

    @Test
    public void testFirstBuildFailed() {
        BuildResultRule rule = new BuildResultRule(100, -100);
        RuleResult results = rule.evaluate(Result.FAILURE, null);
        assertEquals("Failed build should give -100 results", -100, results.getPoints());
    }

    @Test
    public void testFirstBuildWasUnstable() {
        BuildResultRule rule = new BuildResultRule(100, -100);
        RuleResult results = rule.evaluate(Result.UNSTABLE, null);
        assertNull("Unstable build should return null", results);
    }

    @Test
    public void testLastBuildWasUnstable() {
        BuildResultRule rule = new BuildResultRule(100, -100);
        RuleResult results = rule.evaluate(Result.SUCCESS, Result.UNSTABLE);
        assertEquals("Fixed build should give 100 results", 100, results.getPoints());
    }

    @Test
    public void testContinuedBuildFailure() {
        BuildResultRule rule = new BuildResultRule(100, -100);
        RuleResult results = rule.evaluate(Result.FAILURE, Result.FAILURE);
        assertNull("No change in failure result should return null", results);
    }

    @Test
    public void testContinuedUnstableBuild() {
        BuildResultRule rule = new BuildResultRule(100, -100);
        RuleResult results = rule.evaluate(Result.UNSTABLE, Result.UNSTABLE);
        assertNull("No change in usntable result should return null", results);
    }

    @Test
    public void testLastBuildWasAborted() {
        BuildResultRule rule = new BuildResultRule(100, -100);
        RuleResult results = rule.evaluate(Result.FAILURE, Result.ABORTED);
        assertNull("Previous aborted build should return null", results);
    }

    @Test
    public void testContinuedBuildSuccess() {
        BuildResultRule rule = new BuildResultRule(100, -100);
        RuleResult results = rule.evaluate(Result.SUCCESS, Result.SUCCESS);
        assertEquals("No change in result should give 100 results", 100, results.getPoints());
    }

    @Test
    public void testCurrentBuildWasUnstable() {
        BuildResultRule rule = new BuildResultRule(100, -100);
        RuleResult results = rule.evaluate(Result.UNSTABLE, Result.SUCCESS);
        assertNull("Unstable builds should return null", results);
    }
}
