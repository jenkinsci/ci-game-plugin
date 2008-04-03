package hudson.plugins.cigame.model;

import java.util.Collection;
import java.util.LinkedList;

import hudson.model.AbstractBuild;

/**
 * Score card containing the results of evaluating the rules against a build.
 *
 *
 */
public class ScoreCard {
    
    private Collection<Score> scores;

    /**
     * Record points for the rules in the rule set
     * @param build build to evaluate
     * @param ruleset rule set to use for evaluation
     */
    public void record(AbstractBuild<?,?> build, RuleSet ruleset) {
    	if (scores == null) {
    		scores = new LinkedList<Score>();
    	}
        for (Rule rule: ruleset.getRules()) {            
            double points = rule.evaluate(build);
            if (points != 0) {
                Score score = new Score(ruleset.getName(), rule.getName(), points);
                scores.add(score);
            }    
        }
    }

    /**
     * Returns a collection of scores. May not be called before the score has been
     * recorded.
     * @return a collection of scores.
     * @throws IllegalStateException thrown if the method is called before the scores has been recorded.
     */
    public Collection<Score> getScores() throws IllegalStateException {
        if (scores == null) {
            throw new IllegalStateException("No scores are available");
        }
        return scores;
    }
    
    /**
     * Returns the total points for this score card
     * @return the total points for this score card
     * @throws IllegalStateException
     */
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
