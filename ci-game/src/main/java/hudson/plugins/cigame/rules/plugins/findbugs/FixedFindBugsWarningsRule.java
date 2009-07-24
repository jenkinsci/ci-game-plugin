package hudson.plugins.cigame.rules.plugins.findbugs;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.findbugs.FindBugsResultAction;
import hudson.plugins.findbugs.util.model.Priority;

public class FixedFindBugsWarningsRule implements Rule {

    private Priority priority;
    private int pointsForEachFixedWarning;
    
    public FixedFindBugsWarningsRule(Priority priority, int pointsForEachFixedWarning) {
        this.priority = priority;
        this.pointsForEachFixedWarning = pointsForEachFixedWarning;
    }

    public RuleResult evaluate(AbstractBuild<?, ?> build) {
        int numberOfAnnotations = 0;
        if (build.getResult().isBetterOrEqualTo(Result.UNSTABLE)
                && (build.getPreviousBuild() != null)) {
            List<FindBugsResultAction> actions = build.getActions(hudson.plugins.findbugs.FindBugsResultAction.class);
            for (FindBugsResultAction action : actions) {
                if (action.hasPreviousResultAction()) {
                    numberOfAnnotations = action.getPreviousResultAction().getResult().getNumberOfAnnotations(priority) -
                        action.getResult().getNumberOfAnnotations(priority);
                }
            }
        }
        if (numberOfAnnotations > 0) {
            return new RuleResult(numberOfAnnotations * pointsForEachFixedWarning, 
                    String.format("%d %s priority findbugs warnings were fixed", Math.abs(numberOfAnnotations), priority.name()));
        }
        return new RuleResult(0, String.format("No fixed %s priority findbugs warnings found.", priority.name()));
    }
    
    public String getName() {
        return String.format("Fixed %s priority Findbugs warnings", priority.name());
    }
}
