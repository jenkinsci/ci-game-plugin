package hudson.plugins.cigame.model;

/**
 * Result from a Rule evaluation.
 * 
 */
public class RuleResult {
    public static final RuleResult EMPTY_RESULT = new EmptyRuleResult();
    
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
        return "[RuleSet description='" + description + "', points=" + points + "]";  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        long temp;
        temp = Double.doubleToLongBits(points);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RuleResult other = (RuleResult) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (Double.doubleToLongBits(points) != Double.doubleToLongBits(other.points))
            return false;
        return true;
    }
    
    private static class EmptyRuleResult extends RuleResult {
        private EmptyRuleResult() {
            super(0, "");
        }

        @Override
        public String getDescription() {
            throw new UnsupportedOperationException("Empty rule result should not be used.");
        }
    }
}
