package hudson.plugins.cigame.rules.plugins.checkstyle;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.checkstyle.CheckStyleResultAction;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.util.ActionSequenceRetriever;
import hudson.plugins.cigame.util.ResultSequenceValidator;

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
        if (new ResultSequenceValidator(Result.UNSTABLE, 2).isValid(build)) {
            List<List<CheckStyleResultAction>> sequence = new ActionSequenceRetriever<CheckStyleResultAction>(CheckStyleResultAction.class, 2).getSequence(build);
            if ((sequence != null)
                    && hasNoErrors(sequence.get(0)) && hasNoErrors(sequence.get(1))) {
                int numberOfWarnings = getNumberOfAnnotations(sequence.get(0)) - getNumberOfAnnotations(sequence.get(1));
                if (numberOfWarnings > 0) {
                    return new RuleResult(numberOfWarnings * pointsForAddingAWarning, 
                            Messages.CheckstyleRuleSet_DefaultRule_NewWarningsCount(numberOfWarnings)); //$NON-NLS-1$
                }
                if (numberOfWarnings < 0) {
                    return new RuleResult((numberOfWarnings * -1) * pointsForRemovingAWarning, 
                            Messages.CheckstyleRuleSet_DefaultRule_FixedWarningsCount(numberOfWarnings * -1)); //$NON-NLS-1$
                }
            }
        }
        return RuleResult.EMPTY_RESULT;
    }
    
    private boolean hasNoErrors(List<CheckStyleResultAction> actions) {
        for (CheckStyleResultAction action : actions) {
            if (action.getResult().hasError()) {
                return false;
            }
        }
        return true;
    }
    
    private int getNumberOfAnnotations(List<CheckStyleResultAction> actions) {
        int numberOfAnnotations = 0;
        for (CheckStyleResultAction action : actions) {
            numberOfAnnotations += action.getResult().getNumberOfAnnotations();
        }
        return numberOfAnnotations;
    }

    public String getName() {
        return Messages.CheckstyleRuleSet_DefaultRule_Name(); //$NON-NLS-1$
    }
}
