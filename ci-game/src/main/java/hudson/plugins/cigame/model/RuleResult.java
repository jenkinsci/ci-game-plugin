package hudson.plugins.cigame.model;

/**
 * Result from a Rule evaluation.
 * 
 */
public class RuleResult {
    private final double points;
    private final String description;

    public RuleResult(double points, String description) {
        this.points = points;
        this.description = description;
        
    }
    /**
     * Returns the points for the result from a rule
     * @return the points
     */
    public double getPoints() {
        return points;
    }

    /**
     * Returns a description of the result.
     * Should contain a reason why the number of points was evaluated from the
     * rule. "-5 because the build failed"
     * @return a point description
     */
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return "[RuleSet description='" + description + "', points=" + points + "]"; 
    }
}
