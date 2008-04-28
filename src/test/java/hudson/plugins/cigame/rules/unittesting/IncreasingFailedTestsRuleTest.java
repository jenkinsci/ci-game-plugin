package hudson.plugins.cigame.rules.unittesting;

import static org.junit.Assert.*;
import hudson.model.Result;
import hudson.plugins.cigame.rules.unittesting.IncreasingFailedTestsRule;

import org.junit.Test;

public class IncreasingFailedTestsRuleTest {
	@Test
	public void testNoTests() throws Exception {
		IncreasingFailedTestsRule rule = new IncreasingFailedTestsRule(-10);
		double points = rule.evaluate(Result.SUCCESS, Result.SUCCESS, 0, 0);
		assertEquals("No new test should give 0 points", 0, points);
	}
	
	@Test
	public void testMoreFailingTests() throws Exception {
		IncreasingFailedTestsRule rule = new IncreasingFailedTestsRule(-10);
		double points = rule.evaluate(Result.UNSTABLE, Result.UNSTABLE, 2, 0);
		assertEquals("2 new test should give -20 points", -20, points);
	}
	
	@Test
	public void testLessFailingTests() throws Exception {
		IncreasingFailedTestsRule rule = new IncreasingFailedTestsRule(-10);
		double points = rule.evaluate(Result.UNSTABLE, Result.UNSTABLE, 2, 4);
		assertEquals("2 lost tests should give 0 points", 0, points);
	}
	
	@Test
	public void testPreviousBuildFailed() throws Exception {
		IncreasingFailedTestsRule rule = new IncreasingFailedTestsRule(-10);
		double points = rule.evaluate(Result.UNSTABLE, Result.FAILURE, 1, 0);
		assertEquals("Previous failed build should give 0 points", 0, points);
	}
	
	@Test
	public void testCurrentBuildFailed() throws Exception {
		IncreasingFailedTestsRule rule = new IncreasingFailedTestsRule(-10);
		double points = rule.evaluate(Result.FAILURE, Result.UNSTABLE, 1, 0);
		assertEquals("Current build failed should give 0 points", 0, points);
	}
}
