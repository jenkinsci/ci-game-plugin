package hudson.plugins.cigame.model;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Recorded score for a rule and build.
 * 
 */
@ExportedBean(defaultVisibility=999)
public class Score {
    private String rulesetName;
    private String ruleName;
    private double value;

    public Score(String rulesetName, String ruleName, double value) {
        this.rulesetName = rulesetName;
        this.ruleName = ruleName;
        this.value = value;
    }

    @Exported
    public String getDescription() {
        return rulesetName + " - " + ruleName;
    }

    @Exported
    public String getRulesetName() {
        return rulesetName;
    }

    @Exported
    public String getRuleName() {
        return ruleName;
    }

    @Exported
    public double getValue() {
        return value;
    }
}
