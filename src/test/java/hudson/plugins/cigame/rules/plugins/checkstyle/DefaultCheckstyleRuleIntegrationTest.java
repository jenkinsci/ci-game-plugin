package hudson.plugins.cigame.rules.plugins.checkstyle;

import static hudson.plugins.cigame.util.Assert.*;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;

import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.recipes.LocalData;

public class DefaultCheckstyleRuleIntegrationTest extends HudsonTestCase {

    @LocalData
    public void testNoPointsAwardedForFirstBuild() throws Exception {
        FreeStyleBuild build = ((FreeStyleProject) hudson.getItem("checkstyle-first")).scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);
        assertPointsForBuildEquals(build, 1);
        assertPointsForRuleEquals(build, Messages.CheckstyleRuleSet_Title(), 0);
    }

    @LocalData
    public void testPointsAwardedForChangeInCheckstyleErrors() throws Exception {
        FreeStyleBuild build = ((FreeStyleProject) hudson.getItem("checkstyle-change")).scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);
        assertPointsForBuildEquals(build, 1 + 2 * -1);
        assertPointsForRuleSetEquals(build, Messages.CheckstyleRuleSet_Title(), -2);
    }

    @LocalData
    public void testNoPointsAwardedAsLastBuildFailed() throws Exception {
        FreeStyleBuild build = ((FreeStyleProject) hudson.getItem("checkstyle-previous-failed")).scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);
        assertPointsForBuildEquals(build, 1);
        assertPointsForRuleEquals(build, Messages.CheckstyleRuleSet_Title(), 0);
    }

    @LocalData
    public void testNoPointsAwardedAsLastBuildDidNotContainSpecifiedReport() throws Exception {
        FreeStyleBuild build = ((FreeStyleProject) hudson.getItem("checkstyle-no-previous-report")).scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);
        assertPointsForBuildEquals(build, 1);
        assertPointsForRuleEquals(build, Messages.CheckstyleRuleSet_Title(), 0);
    }
}
