package hudson.plugins.cigame.model;

/**
 * Recorded score for a rule and build.
 * 
 */
public class Score {
    private String rulesetName;
    private String ruleName;
    private double value;
    
    public Score(String rulesetName, String ruleName, double value) {
        this.rulesetName = rulesetName;
        this.ruleName = ruleName;
        this.value = value;
    }

    public String getRulesetName() {
        return rulesetName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public double getValue() {
        return value;
    }
}
