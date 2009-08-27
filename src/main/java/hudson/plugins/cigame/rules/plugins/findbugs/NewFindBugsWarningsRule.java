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

public class NewFindBugsWarningsRule implements Rule {

    private Priority priority;
    private int pointsForEachNewWarning;
    
    public NewFindBugsWarningsRule(Priority priority, int pointsForEachNewWarning) {
        this.priority = priority;
        this.pointsForEachNewWarning = pointsForEachNewWarning;
    }

    public RuleResult evaluate(AbstractBuild<?, ?> build) {
        int numberOfAnnotations = 0;
        if (new ResultSequenceValidator(Result.UNSTABLE, 2).isValid(build)) {
            List<List<FindBugsResultAction>> actionSequence = new ActionSequenceRetriever<FindBugsResultAction>(FindBugsResultAction.class, 2).getSequence(build);
            if (actionSequence != null) {
                numberOfAnnotations = getNumberOfAnnotations(actionSequence.get(0)) - getNumberOfAnnotations(actionSequence.get(1)); 
            }       
        }
        if (numberOfAnnotations > 0) {
            return new RuleResult(numberOfAnnotations * pointsForEachNewWarning, 
                    Messages.FindBugsRuleSet_NewWarningsRule_Count( Math.abs(numberOfAnnotations), priority.name())); //$NON-NLS-1$
        }
        return new RuleResult(0, Messages.FindBugsRuleSet_NewWarningsRule_None( priority.name())); //$NON-NLS-1$
    }
    
    private int getNumberOfAnnotations(List<FindBugsResultAction> list) {
        int numberOfAnnotations = 0;
        for (FindBugsResultAction action : list) {
            numberOfAnnotations += action.getResult().getNumberOfAnnotations(priority);
        }
        return numberOfAnnotations;
    }

    public String getName() {
        return Messages.FindBugsRuleSet_NewWarningsRule_Title(priority.name()); //$NON-NLS-1$
    }

}
