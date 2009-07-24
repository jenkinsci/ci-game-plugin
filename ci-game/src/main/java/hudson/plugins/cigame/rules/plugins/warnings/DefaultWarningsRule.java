package hudson.plugins.cigame.rules.plugins.warnings;

import java.util.Collection;
import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.warnings.WarningsResult;
import hudson.plugins.warnings.WarningsResultAction;
import hudson.plugins.warnings.util.model.FileAnnotation;

/**
 * Default rule for the Warnings plugin.
 */
public class DefaultWarningsRule implements Rule {

    private int pointsForAddingAWarning;
    private int pointsForRemovingAWarning;

    public DefaultWarningsRule(int pointsForAddingAWarning, int pointsForRemovingAWarning) {
        this.pointsForAddingAWarning = pointsForAddingAWarning;
        this.pointsForRemovingAWarning = pointsForRemovingAWarning;
    }

    public RuleResult evaluate(AbstractBuild<?, ?> build) {
        int numberOfAnnotations = 0;
        if (build.getResult().isBetterOrEqualTo(Result.UNSTABLE)
                && (build.getPreviousBuild() != null)){
            List<WarningsResultAction> actions = build.getActions(hudson.plugins.warnings.WarningsResultAction.class);
            for (WarningsResultAction action : actions) {
                if (action.hasPreviousResultAction()) {
                    WarningsResult result = action.getResult();
                    WarningsResult previousResult = action.getPreviousResultAction().getResult();
                    Collection<FileAnnotation> annotations = result.getAnnotations();
                    Collection<FileAnnotation> previousAnnotations = previousResult.getAnnotations();
                    if ((annotations != null) && (previousAnnotations != null)) {
                        numberOfAnnotations += annotations.size() - previousAnnotations.size();
                    }
                }
            }
        }
        if (numberOfAnnotations > 0) {
            return new RuleResult(numberOfAnnotations * pointsForAddingAWarning, 
                    String.format("%d new compiler warnings were found", numberOfAnnotations));
        }
        if (numberOfAnnotations < 0) {
            return new RuleResult((numberOfAnnotations * -1) * pointsForRemovingAWarning, 
                    String.format("%d compiler warnings were fixed", numberOfAnnotations * -1));
        }
        return new RuleResult(0, "No new or fixed compiler warnings found.");
    }
    
    public String getName() {
        return "Changed number of compiler warnings";
    }
}
