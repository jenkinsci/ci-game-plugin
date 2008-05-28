package hudson.plugins.cigame.model;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Recorded score for a rule and build.
 * 
 */
@ExportedBean(defaultVisibility=999)
public class Score implements Comparable<Score> {
    private final String rulesetName;
    private final String ruleName;
    private final double value;
    private final String description;

    public Score(String rulesetName, String ruleName, double points, String pointDescription) {
        this.rulesetName = rulesetName;
        this.ruleName = ruleName;
        this.value = points;
        description = pointDescription;
    }

    @Exported
    public String getDescription() {
        if (description == null)
            return rulesetName + " - " + ruleName;
        else
            return description;
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

    public int compareTo(Score o) {
        if (value == o.value) {
            return description.compareToIgnoreCase(o.description);
        } else {
            return (int) Math.round(o.value - value);
        }
    }
}
