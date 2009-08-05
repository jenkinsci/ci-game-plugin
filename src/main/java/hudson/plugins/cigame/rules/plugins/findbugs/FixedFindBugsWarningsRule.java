package hudson.plugins.cigame.rules.plugins.findbugs;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.util.ActionSequenceRetriever;
import hudson.plugins.cigame.util.ResultSequenceValidator;
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
        if (new ResultSequenceValidator(Result.UNSTABLE, 2).isValid(build)) {
            List<List<FindBugsResultAction>> actionSequence = new ActionSequenceRetriever<FindBugsResultAction>(FindBugsResultAction.class, 2).getSequence(build);
            if (actionSequence != null) {
                numberOfAnnotations = getNumberOfAnnotations(actionSequence.get(1)) - getNumberOfAnnotations(actionSequence.get(0)); 
            }
        }
        if (numberOfAnnotations > 0) {
            return new RuleResult(numberOfAnnotations * pointsForEachFixedWarning, 
                    String.format("%d %s priority findbugs warnings were fixed", Math.abs(numberOfAnnotations), priority.name()));
        }
        return new RuleResult(0, String.format("No fixed %s priority findbugs warnings found.", priority.name()));
    }
    
    private int getNumberOfAnnotations(List<FindBugsResultAction> list) {
        int numberOfAnnotations = 0;
        for (FindBugsResultAction action : list) {
            numberOfAnnotations += action.getResult().getNumberOfAnnotations(priority);
        }
        return numberOfAnnotations;
    }

    public String getName() {
        return String.format("Fixed %s priority Findbugs warnings", priority.name());
    }
}
