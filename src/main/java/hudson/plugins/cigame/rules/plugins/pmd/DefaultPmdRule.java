package hudson.plugins.cigame.rules.plugins.pmd;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.util.ActionSequenceRetriever;
import hudson.plugins.cigame.util.ResultSequenceValidator;
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
        
        if (new ResultSequenceValidator(Result.UNSTABLE, 2).isValid(build)) {
            List<List<PmdResultAction>> sequence = new ActionSequenceRetriever<PmdResultAction>(PmdResultAction.class, 2).getSequence(build);
            if (sequence != null) {
                int delta = getNumberOfAnnotations(sequence.get(0)) - getNumberOfAnnotations(sequence.get(1));

                if (delta < 0) {
                    return new RuleResult(Math.abs(delta) * pointsForRemovingAnAnnotation, 
                            String.format("%d %s priority PMD warnings were fixed", Math.abs(delta), tasksPriority.name()));
                }
                if (delta > 0) {
                    return new RuleResult(Math.abs(delta) * pointsForAddingAnAnnotation, 
                            String.format("%d new %s priority PMD warnings were found", Math.abs(delta), tasksPriority.name()));
                }
            }
        }
        return new RuleResult(0, String.format("No new or fixed %s priority PMD warnings found.", tasksPriority.name()));
    }

    private int getNumberOfAnnotations(List<PmdResultAction> actions) {
        int numberOfAnnotations = 0;
        for (PmdResultAction action : actions) {
            numberOfAnnotations += action.getResult().getNumberOfAnnotations(tasksPriority);
        }
        return numberOfAnnotations;
    }

    public String getName() {
        return String.format("%s priority PMD warnings", tasksPriority.name());
    }
}
