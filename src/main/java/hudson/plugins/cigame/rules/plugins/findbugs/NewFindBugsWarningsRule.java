package hudson.plugins.cigame.rules.plugins.findbugs;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.findbugs.FindBugsResultAction;
import hudson.plugins.findbugs.util.model.Priority;

public class NewFindBugsWarningsRule implements Rule {

    private Priority priority;
    private int pointsForEachNewWarning;
    
    public NewFindBugsWarningsRule(Priority priority, int pointsForEachNewWarning) {
        this.priority = priority;
        this.pointsForEachNewWarning = pointsForEachNewWarning;
    }

    public RuleResult evaluate(AbstractBuild<?, ?> build) {
        int numberOfAnnotations = 0;
        if ((build.getResult().isBetterOrEqualTo(Result.UNSTABLE)) 
                && (build.getPreviousBuild() != null)) {
            List<FindBugsResultAction> actions = build.getActions(hudson.plugins.findbugs.FindBugsResultAction.class);
            for (FindBugsResultAction action : actions) {
                if (action.hasPreviousResultAction()) {
                    numberOfAnnotations = action.getResult().getNumberOfAnnotations(priority) -
                        action.getPreviousResultAction().getResult().getNumberOfAnnotations(priority);
                }
            }
        }
        if (numberOfAnnotations > 0) {
            return new RuleResult(numberOfAnnotations * pointsForEachNewWarning, 
                    String.format("%d new %s priority findbugs warnings were found", Math.abs(numberOfAnnotations), priority.name()));
        }
        return new RuleResult(0, String.format("No new %s priority findbugs warnings found.", priority.name()));
    }

    public String getName() {
        return String.format("New %s priority Findbugs warnings", priority.name());
    }

}
