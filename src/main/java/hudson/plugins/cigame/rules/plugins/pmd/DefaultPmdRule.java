package hudson.plugins.cigame.rules.plugins.pmd;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.pmd.PmdResult;
import hudson.plugins.pmd.PmdResultAction;
import hudson.plugins.pmd.util.model.Priority;

public class DefaultPmdRule implements Rule {
    private int pointsForAddingAnAnnotation;
    private int pointsForRemovingAnAnnotation;

    private Priority tasksPriority;

    public DefaultPmdRule(Priority tasksPriority, int pointsForAddingAnAnnotation, int pointsForRemovingAnAnnotation) {
        this.tasksPriority = tasksPriority;
        this.pointsForAddingAnAnnotation = pointsForAddingAnAnnotation;
        this.pointsForRemovingAnAnnotation = pointsForRemovingAnAnnotation;
    }

    public RuleResult evaluate(AbstractBuild<?, ?> build) {
        int delta = 0;
        if ((build.getResult().isBetterOrEqualTo(Result.UNSTABLE)) 
                && (build.getPreviousBuild() != null)) {
            List<PmdResultAction> actions = build.getActions(PmdResultAction.class);
            for (PmdResultAction action : actions) {
                if (action.hasPreviousResultAction()) {
                    PmdResult result = action.getResult();
                    PmdResult previousResult = action.getPreviousResultAction().getResult();
                    
                    int annotations = result.getNumberOfAnnotations(tasksPriority);
                    int previousAnnotations = previousResult.getNumberOfAnnotations(tasksPriority);
                    delta += annotations - previousAnnotations;
                }
            }
        }
        if (delta < 0) {
            return new RuleResult(Math.abs(delta) * pointsForRemovingAnAnnotation, 
                    String.format("%d %s priority PMD warnings were fixed", Math.abs(delta), tasksPriority.name()));
        }
        if (delta > 0) {
            return new RuleResult(Math.abs(delta) * pointsForAddingAnAnnotation, 
                    String.format("%d new %s priority PMD warnings were found", Math.abs(delta), tasksPriority.name()));
        }
        return new RuleResult(0, String.format("No new or fixed %s priority PMD warnings found.", tasksPriority.name()));
    }

    public String getName() {
        return String.format("%s priority PMD warnings", tasksPriority.name());
    }
}
