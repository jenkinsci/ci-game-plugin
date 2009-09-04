package hudson.plugins.cigame.rules.plugins.warnings;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.util.ActionSequenceRetriever;
import hudson.plugins.cigame.util.ResultSequenceValidator;
import hudson.plugins.warnings.WarningsResultAction;

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
        if (new ResultSequenceValidator(Result.UNSTABLE, 2).isValid(build)) {
            List<List<WarningsResultAction>> sequence = new ActionSequenceRetriever<WarningsResultAction>(WarningsResultAction.class, 2).getSequence(build);
            if ((sequence != null)
                    && hasNoErrors(sequence.get(0)) && hasNoErrors(sequence.get(1))) {
                int numberOfAnnotations = getNumberOfAnnotations(sequence.get(0)) - getNumberOfAnnotations(sequence.get(1));
                if (numberOfAnnotations > 0) {
                    return new RuleResult(numberOfAnnotations * pointsForAddingAWarning, 
                            Messages.WarningsRuleSet_DefaultRule_NewWarningsCount(numberOfAnnotations)); //$NON-NLS-1$
                }
                if (numberOfAnnotations < 0) {
                    return new RuleResult((numberOfAnnotations * -1) * pointsForRemovingAWarning, 
                            Messages.WarningsRuleSet_DefaultRule_FixedWarningsCount(numberOfAnnotations * -1)); //$NON-NLS-1$
                }
            }
        }
        return RuleResult.EMPTY_RESULT;
    }
    
    private boolean hasNoErrors(List<WarningsResultAction> actions) {
        for (WarningsResultAction action : actions) {
            if (action.getResult().hasError()) {
                return false;
            }
        }
        return true;
    }
    
    private int getNumberOfAnnotations(List<WarningsResultAction> actions) {
        int numberOfAnnotations = 0;
        for (WarningsResultAction action : actions) {
            numberOfAnnotations += action.getResult().getNumberOfAnnotations();
        }
        return numberOfAnnotations;
    }

    public String getName() {
        return Messages.WarningsRuleSet_DefaultRule_Name(); //$NON-NLS-1$
    }
}
