package hudson.plugins.cigame.rules.plugins.violation;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import hudson.model.AbstractBuild;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.plugins.cigame.ScoreCardAction;
import hudson.plugins.cigame.model.Score;
import hudson.plugins.cigame.model.ScoreCard;

import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.recipes.LocalData;

@SuppressWarnings("unchecked")
public class DefaultViolationRuleIntegrationTest extends HudsonTestCase {

    @LocalData
    public void testNoPointsAwardedForFirstBuild() throws Exception {
        FreeStyleBuild build = ((FreeStyleProject) hudson.getItem("checkstyle-first")).scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);
        assertPoints(build, 1);
        assertPointsForRuleSet(build, Messages.ViolationRuleSet_Title(), 0);
    }

    @LocalData
    public void testPointsAwardedForChangeInViolations() throws Exception {
        FreeStyleBuild build = ((FreeStyleProject) hudson.getItem("checkstyle-change")).scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);
        assertPoints(build, 1 + 3 * 1);
        assertPointsForRuleSet(build, Messages.ViolationRuleSet_Title(), 3);
    }

    @LocalData
    public void testNoPointsAwardedAsLastBuildFailed() throws Exception {
        FreeStyleBuild build = ((FreeStyleProject) hudson.getItem("checkstyle-previous-failed")).scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);
        assertPoints(build, 1);
        assertPointsForRuleSet(build, Messages.ViolationRuleSet_Title(), 0);
    }

    @LocalData
    public void testNoPointsAwardedAsLastBuildDidNotContainSpecifiedReport() throws Exception {
        FreeStyleBuild build = ((FreeStyleProject) hudson.getItem("checkstyle-no-previous-report")).scheduleBuild2(0).get();
        assertBuildStatusSuccess(build);
        assertPoints(build, 1);
        assertPointsForRuleSet(build, Messages.ViolationRuleSet_Title(), 0);
    }

    private void assertPoints(AbstractBuild build, double points) {
        ScoreCard scorecard = build.getAction(ScoreCardAction.class).getScorecard();
        assertThat("Points for build was incorrect", scorecard.getTotalPoints(), is(points));
    }

    private void assertPointsForRuleSet(AbstractBuild build, String rulesetName, double points) {
        ScoreCard scorecard = build.getAction(ScoreCardAction.class).getScorecard();
        double pointsForRule = 0;
        for (Score score : scorecard.getScores()) {
            if (score.getRulesetName().equalsIgnoreCase(rulesetName)) {
                pointsForRule += score.getValue();
            }
        }
        assertThat("Points for ruleset '" + rulesetName + "' was incorrect.", pointsForRule, is(points));
    }
}
