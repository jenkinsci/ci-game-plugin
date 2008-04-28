package hudson.plugins.cigame.rules.build;

import hudson.model.Result;
import hudson.plugins.cigame.rules.build.BuildResultRule;

import org.junit.Test;
import static org.junit.Assert.*;

public class BuildResultRuleTest {

	@Test
	public void testFirstBuildSuccess() {
		BuildResultRule rule = new BuildResultRule(100, -100);		
		double points = rule.evaluate(Result.SUCCESS, null);
		assertEquals("Successful build should give 100 points", 100, points);
	}
	
	@Test
	public void testFirstBuildFailed() {
		BuildResultRule rule = new BuildResultRule(100, -100);		
		double points = rule.evaluate(Result.FAILURE, null);
		assertEquals("Failed build should give -100 points", -100, points);
	}
	
	@Test
	public void testFirstBuildWasUnstable() {
		BuildResultRule rule = new BuildResultRule(100, -100);		
		double points = rule.evaluate(Result.UNSTABLE, null);
		assertEquals("Unstable build should give 0 points", 0, points);
	}
	
	@Test
	public void testLastBuildWasUnstable() {
		BuildResultRule rule = new BuildResultRule(100, -100);		
		double points = rule.evaluate(Result.SUCCESS, Result.UNSTABLE);
		assertEquals("Fixed build should give 100 points", 100, points);
	}
	
	@Test
	public void testContinuedBuildFailure() {
		BuildResultRule rule = new BuildResultRule(100, -100);		
		double points = rule.evaluate(Result.FAILURE, Result.FAILURE);
		assertEquals("No change in result should give 0 points", 0, points);
	}
	
	@Test
	public void testContinuedUnstableBuild() {
		BuildResultRule rule = new BuildResultRule(100, -100);		
		double points = rule.evaluate(Result.UNSTABLE, Result.UNSTABLE);
		assertEquals("No change in result should give 0 points", 0, points);
	}
	
	@Test
	public void testLastBuildWasAborted() {
		BuildResultRule rule = new BuildResultRule(100, -100);		
		double points = rule.evaluate(Result.FAILURE, Result.ABORTED);
		assertEquals("Previous aborted build should give 0 points", 0, points);
	}
	
	@Test
	public void testContinuedBuildSuccess() {
		BuildResultRule rule = new BuildResultRule(100, -100);		
		double points = rule.evaluate(Result.SUCCESS, Result.SUCCESS);
		assertEquals("No change in result should give 100 points", 100, points);
	}
	
	@Test
	public void testCurrentBuildWasUnstable() {
		BuildResultRule rule = new BuildResultRule(100, -100);		
		double points = rule.evaluate(Result.UNSTABLE, Result.SUCCESS);
		assertEquals("Unstable builds should give 0 points", 0, points);
	}
}
