package hudson.plugins.cigame.rules.plugins.violation;

import static hudson.plugins.cigame.util.Assert.*;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;

import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.recipes.LocalData;

public class DefaultViolationRuleIntegrationTest extends HudsonTestCase {

    @LocalData
    public void testNoPointsAwardedForFirstBuild() throws Exception {
        FreeStyleBuild build = ((FreeStyleProject) hudson.getItem("checkstyle-first")).scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);
        assertPointsForBuildEquals(build, 1);
        assertPointsForRuleSetEquals(build, Messages.ViolationRuleSet_Title(), 0);
    }

    @LocalData
    public void testPointsAwardedForChangeInViolations() throws Exception {
        FreeStyleBuild build = ((FreeStyleProject) hudson.getItem("checkstyle-change")).scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);
        assertPointsForBuildEquals(build, 1 + 3 * 1);
        assertPointsForRuleSetEquals(build, Messages.ViolationRuleSet_Title(), 3);
    }

    @LocalData
    public void testNoPointsAwardedAsLastBuildFailed() throws Exception {
        FreeStyleBuild build = ((FreeStyleProject) hudson.getItem("checkstyle-previous-failed")).scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);
        assertPointsForBuildEquals(build, 1);
        assertPointsForRuleSetEquals(build, Messages.ViolationRuleSet_Title(), 0);
    }

    @LocalData
    public void testNoPointsAwardedAsLastBuildDidNotContainSpecifiedReport() throws Exception {
        FreeStyleBuild build = ((FreeStyleProject) hudson.getItem("checkstyle-no-previous-report")).scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);
        assertPointsForBuildEquals(build, 1);
        assertPointsForRuleSetEquals(build, Messages.ViolationRuleSet_Title(), 0);
    }
}
