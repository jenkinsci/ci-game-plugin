package hudson.plugins.cigame.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import hudson.model.AbstractBuild;
import hudson.plugins.cigame.ScoreCardAction;
import hudson.plugins.cigame.model.Score;
import hudson.plugins.cigame.model.ScoreCard;

@SuppressWarnings("unchecked")
public class Assert {

    public static void assertPointsForBuildEquals(AbstractBuild build, double points) {
        ScoreCard scorecard = build.getAction(ScoreCardAction.class).getScorecard();
        assertThat("Points for build was incorrect", scorecard.getTotalPoints(), is(points));
    }

    public static void assertPointsForRuleSetEquals(AbstractBuild build, String rulesetName, double points) {
        ScoreCard scorecard = build.getAction(ScoreCardAction.class).getScorecard();
        double pointsForRule = 0;
        for (Score score : scorecard.getScores()) {
            if (score.getRulesetName().equalsIgnoreCase(rulesetName)) {
                pointsForRule += score.getValue();
            }
        }
        assertThat("Points for ruleset '" + rulesetName + "' was incorrect.", pointsForRule, is(points));
    }

    public static void assertPointsForRuleEquals(AbstractBuild build, String ruleName, double points) {
        ScoreCard scorecard = build.getAction(ScoreCardAction.class).getScorecard();
        double pointsForRule = 0;
        for (Score score : scorecard.getScores()) {
            if (score.getRuleName().equalsIgnoreCase(ruleName)) {
                pointsForRule += score.getValue();
            }
        }
        assertThat("Points for rule '" + ruleName + "' was incorrect.", pointsForRule, is(points));
    }
}
