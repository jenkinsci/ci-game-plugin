package hudson.plugins.cigame.rules.unittesting;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import hudson.maven.MavenBuild;
import hudson.model.Result;
import hudson.plugins.cigame.GameDescriptor;
import hudson.plugins.cigame.model.RuleResult;
import hudson.tasks.test.AbstractTestResultAction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


@SuppressWarnings("unchecked")
public class MavenMultiModuleUnitTestsTest  {

    private GameDescriptor gameDescriptor;
    
    @Rule 
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    @Before 
    public void setup() {
        gameDescriptor = jenkinsRule.jenkins.getDescriptorByType(GameDescriptor.class);
    }
    
// Doesn't work, because MavenModule is final ...	
//	
//	private MavenModuleSetBuild getMultiModuleBuild() {
//		MavenModuleSetBuild build = mock(MavenModuleSetBuild.class);
//		
//		MavenModule module1 = mock(MavenModule.class);
//		MavenBuild module1Build1 = mock(MavenBuild.class);
//		AbstractTestResultAction testResult11 = mock(AbstractTestResultAction.class);
//		when(testResult11.getTotalCount()).thenReturn(3);
//		when(module1Build1.getTestResultAction()).thenReturn(testResult11);
//		
//		MavenModule module2 = mock(MavenModule.class);
//		MavenBuild module2Build1 = mock(MavenBuild.class);
//		AbstractTestResultAction testResult21 = mock(AbstractTestResultAction.class);
//		when(testResult21.getTotalCount()).thenReturn(5);
//		when(module2Build1.getTestResultAction()).thenReturn(testResult21);
//		
//		when(build.getModuleLastBuilds()).thenReturn(
//				new ImmutableMap.Builder<MavenModule, MavenBuild>()
//					.put(module1, module1Build1)
//					.put(module2, module2Build1)
//					.build());
//		return build;
//	}
//	
//	@Test
//	public void testAllTestsCountIfPreviousBuildIsNull() {
//		ScoreCard scoreCard = new ScoreCard();
//		scoreCard.record(getMultiModuleBuild(), new UnitTestingRuleSet());
//
//		Collection<Score> scores = scoreCard.getScores();
//		Assert.assertEquals(2, scores.size());
//	}
	
	/**
	 * Test that the 1st ever build will yield points for passing and
	 * failing tests.
	 */
	@Test
	public void testCountPointsOnFirstBuild() {
		MavenBuild currentBuild = mockBuild(Result.SUCCESS, 7, 3, 0);
		
		RuleResult ruleResult = getIncreasingPassedTestsRule().evaluate(null, currentBuild);
		Assert.assertEquals(7, ruleResult.getPoints(), 0.1);
		
		ruleResult = getIncreasingFailedTestsRule().evaluate(null, currentBuild);
		Assert.assertEquals(-3, ruleResult.getPoints(), 0.1);
	}
	
	/**
	 * Tests that previously passing tests are counted as negative if a module
	 * is removed.
	 * Analogue, previously failing tests should be counted as positive.
	 */
	@Test
	public void testCountNegativeIfModuleRemoved() {
		MavenBuild currentBuild = null;
		MavenBuild previousBuild = mockBuild(Result.SUCCESS, 6, 2, 1);

        gameDescriptor.setPassedTestDecreasingPoints(-1);
        
		RuleResult<Integer> ruleResult = new DecreasingPassedTestsRule().evaluate(
				previousBuild, currentBuild);
		Assert.assertEquals(-6, ruleResult.getPoints(), 0.1);
		
		gameDescriptor.setFailedTestDecreasingPoints(1);
        
        ruleResult = new DecreasingFailedTestsRule().evaluate(
				previousBuild, currentBuild);
		Assert.assertEquals(2, ruleResult.getPoints(), 0.1);
	}
	
	/**
	 * Tests that on multiple consecutive builds without any test results
	 * only the first will get negative points.
	 * I've seen this happen with the maven-jspc-plugin that
	 * when JSP compilation fails the multimodule build
	 * itself failed, but the submodule was marked as success
	 * - only its unit tests weren't executed.
	 */
	@Test
	public void testCountNegativePointsOnlyOnceOnMultipleBuildsWithoutTestResults() {
	    MavenBuild currentBuild = mockBuildWithoutTestResults(Result.SUCCESS);
	    MavenBuild previousBuild = mockBuildWithoutTestResults(Result.SUCCESS);
	    MavenBuild prevPrevBuild = mockBuild(Result.SUCCESS, 6, 0, 0);
	    when(previousBuild.getPreviousBuild()).thenReturn(prevPrevBuild);
	    
        gameDescriptor.setPassedTestDecreasingPoints(-1);
	    
	    RuleResult<Integer> ruleResult = new DecreasingPassedTestsRule().evaluate(
                previousBuild, currentBuild);
	    
	    // After the first build with 6 passing tests, next build has not got the Test Results. 
	    // So current build must be given the negative marks.
//      Assert.assertNull(ruleResult);
        Assert.assertEquals(-6, ruleResult.getPoints(), 0.1);
	    
	}
	
	/**
	 * Tests the build from before the previous build is
	 * taken into account for comparison if the previous build
	 * is NOT_BUILT or ABORTED.
	 */
	@Test
	public void testSkipUnbuiltAndAbortedBuilds() {
		MavenBuild currentBuild = mockBuild(Result.UNSTABLE,
				10, 7, 0);
		MavenBuild previousBuild = mockBuild(Result.NOT_BUILT,
				0, 0, 0);
		MavenBuild previouspreviousBuild = mockBuild(Result.UNSTABLE,
				8, 4, 0);
		when(currentBuild.getPreviousBuild()).thenReturn(previousBuild);
		when(previousBuild.getPreviousBuild()).thenReturn(previouspreviousBuild);
		
		RuleResult ruleResult = getIncreasingPassedTestsRule().evaluate(previousBuild, currentBuild);
		Assert.assertEquals(2, ruleResult.getPoints(), 0.1);
		
		ruleResult = getIncreasingFailedTestsRule().evaluate(previousBuild, currentBuild);
		Assert.assertEquals(-3, ruleResult.getPoints(), 0.1);
		
		// and the same for an ABORTED build
		previousBuild = mockBuild(Result.ABORTED,
				0, 0, 0);
		when(currentBuild.getPreviousBuild()).thenReturn(previousBuild);
		when(previousBuild.getPreviousBuild()).thenReturn(previouspreviousBuild);
		
		ruleResult = getIncreasingPassedTestsRule().evaluate(previousBuild, currentBuild);
		Assert.assertEquals(2, ruleResult.getPoints(), 0.1);
		
		ruleResult = getIncreasingFailedTestsRule().evaluate(previousBuild, currentBuild);
		Assert.assertEquals(-3, ruleResult.getPoints(), 0.1);
	}
	
	// TODO ckutz: add test case that check that no NPEs occur when a result is null
	
	
	private IncreasingFailedTestsRule getIncreasingFailedTestsRule() {
        gameDescriptor.setFailedTestIncreasingPoints(-1);
		return new IncreasingFailedTestsRule();
	}
	
	private IncreasingPassedTestsRule getIncreasingPassedTestsRule() {
	    gameDescriptor.setPassedTestIncreasingPoints(1);
        return new IncreasingPassedTestsRule();
	}
	
	static MavenBuild mockBuild(Result buildResult, int passedTestCount,
			int failedTestCount, int skippedTestCount) {
		MavenBuild build = mock(MavenBuild.class);
		AbstractTestResultAction testResult = mock(AbstractTestResultAction.class);
		when(testResult.getTotalCount()).thenReturn(passedTestCount + failedTestCount + skippedTestCount);
		when(testResult.getFailCount()).thenReturn(failedTestCount);
		when(testResult.getSkipCount()).thenReturn(skippedTestCount);
		when(build.getTestResultAction()).thenReturn(testResult);
		when(build.getResult()).thenReturn(buildResult);
		
		return build;
	}
	
	static MavenBuild mockBuildWithoutTestResults(Result buildResult) {
        MavenBuild build = mock(MavenBuild.class);
        when(build.getResult()).thenReturn(buildResult);
        
        return build;
    }
}
