package hudson.plugins.cigame.rules.plugins.checkstyle;

import java.util.Collection;
import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.checkstyle.CheckStyleResult;
import hudson.plugins.checkstyle.CheckStyleResultAction;
import hudson.plugins.checkstyle.util.model.FileAnnotation;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;

/**
 * Default rule for the Warnings plugin.
 */
public class DefaultCheckstyleRule implements Rule {

    private int pointsForAddingAWarning;
    private int pointsForRemovingAWarning;

    public DefaultCheckstyleRule(int pointsForAddingAWarning, int pointsForRemovingAWarning) {
        this.pointsForAddingAWarning = pointsForAddingAWarning;
        this.pointsForRemovingAWarning = pointsForRemovingAWarning;
    }

    public RuleResult evaluate(AbstractBuild<?, ?> build) {
        int numberOfWarnings = 0;
        if (build.getResult().isBetterOrEqualTo(Result.UNSTABLE)
                && (build.getPreviousBuild() != null)) {
            List<CheckStyleResultAction> actions = build.getActions(hudson.plugins.checkstyle.CheckStyleResultAction.class);
            for (CheckStyleResultAction action : actions) {
                if (action.hasPreviousResultAction()) {
                    CheckStyleResult result = action.getResult();
                    CheckStyleResult previousResult = action.getPreviousResultAction().getResult();
                    Collection<FileAnnotation> annotations = result.getAnnotations();
                    Collection<FileAnnotation> previousAnnotations = previousResult.getAnnotations();
                    if ((annotations != null) && (previousAnnotations != null)) {
                        numberOfWarnings += annotations.size() - previousAnnotations.size();
                    }
                }
            }
        }
        if (numberOfWarnings > 0) {
            return new RuleResult(numberOfWarnings * pointsForAddingAWarning, 
                    String.format("%d new checkstyle warningss were found", numberOfWarnings));
        }
        if (numberOfWarnings < 0) {
            return new RuleResult((numberOfWarnings * -1) * pointsForRemovingAWarning, 
                    String.format("%d checkstyle warnings were fixed", numberOfWarnings * -1));
        }
        return  new RuleResult(0, "No new or fixed checkstyle warnings found.");
    }
    
    public String getName() {
        return "Changed number of checkstyle warnings";
    }
}
