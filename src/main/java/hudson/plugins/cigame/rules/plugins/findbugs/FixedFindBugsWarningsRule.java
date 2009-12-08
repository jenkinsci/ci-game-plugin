package hudson.plugins.cigame.rules.plugins.findbugs;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.util.ActionSequenceRetriever;
import hudson.plugins.cigame.util.ResultSequenceValidator;
import hudson.plugins.findbugs.FindBugsResultAction;

import java.util.List;

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
            if ((actionSequence != null)
                    && hasNoErrors(actionSequence.get(0)) && hasNoErrors(actionSequence.get(1))) {
                numberOfAnnotations = getNumberOfAnnotations(actionSequence.get(1)) - getNumberOfAnnotations(actionSequence.get(0)); 
            }
        }
        if (numberOfAnnotations > 0) {
            return new RuleResult(numberOfAnnotations * pointsForEachFixedWarning, 
                    Messages.FindBugsRuleSet_FixedWarningsRule_Count(Math.abs(numberOfAnnotations), priority.name())); //$NON-NLS-1$
        }
        return RuleResult.EMPTY_RESULT;
    }
    
    private boolean hasNoErrors(List<FindBugsResultAction> actions) {
        for (FindBugsResultAction action : actions) {
            if (action.getResult().hasError()) {
                return false;
            }
        }
        return true;
    }
    
    private int getNumberOfAnnotations(List<FindBugsResultAction> list) {
        int numberOfAnnotations = 0;
        for (FindBugsResultAction action : list) {
            numberOfAnnotations += action.getResult().getNumberOfAnnotations(priority);
        }
        return numberOfAnnotations;
    }

    public String getName() {
        return Messages.FindBugsRuleSet_FixedWarningsRule_Title(priority.name()); //$NON-NLS-1$
    }
}
