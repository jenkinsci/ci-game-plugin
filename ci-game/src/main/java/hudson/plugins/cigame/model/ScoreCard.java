package hudson.plugins.cigame.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import hudson.model.AbstractBuild;

/**
 * Score card containing the results of evaluating the rules against a build.
 * 
 * 
 */
@ExportedBean(defaultVisibility=999)
public class ScoreCard {

    private List<Score> scores;

    /**
     * Record points for the rules in the rule set
     * 
     * @param build build to evaluate
     * @param ruleset rule set to use for evaluation
     */
    public void record(AbstractBuild<?, ?> build, RuleSet ruleset) {
        if (scores == null) {
            scores = new LinkedList<Score>();
        }
        for (Rule rule : ruleset.getRules()) {
            RuleResult result = rule.evaluate(build);
            if ((result != null) && (result.getPoints() != 0)) {
                Score score = new Score(ruleset.getName(), rule.getName(), result.getPoints(), result.getDescription());
                scores.add(score);
            }
        }
        Collections.sort(scores);
    }

    /**
     * Record points for the rules in the rule book
     * 
     * @param build build to evaluate
     * @param ruleset rule book to use for evaluation
     */
    public void record(AbstractBuild<?, ?> build, RuleBook ruleBook) {
        for (RuleSet set : ruleBook.getRuleSets()) {
            record(build, set);
        }
    }

    /**
     * Returns a collection of scores. May not be called before the score has
     * been recorded.
     * 
     * @return a collection of scores.
     * @throws IllegalStateException thrown if the method is called before the scores has been recorded.
     */
    @Exported
    public Collection<Score> getScores() throws IllegalStateException {
        if (scores == null) {
            throw new IllegalStateException("No scores are available");
        }
        return scores;
    }

    /**
     * Returns the total points for this score card
     * 
     * @return the total points for this score card
     * @throws IllegalStateException
     */
    @Exported
    public double getTotalPoints() throws IllegalStateException {
        if (scores == null) {
            throw new IllegalStateException("No scores are available");
        }
        double value = 0;
        for (Score score : scores) {
            value += score.getValue();
        }
        return value;
    }
}
