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
            if (sequence != null) {
                int numberOfWarnings = getNumberOfAnnotations(sequence.get(0)) - getNumberOfAnnotations(sequence.get(1));
                if (numberOfWarnings > 0) {
                    return new RuleResult(numberOfWarnings * pointsForAddingAWarning, 
                            String.format("%d new checkstyle warningss were found", numberOfWarnings));
                }
                if (numberOfWarnings < 0) {
                    return new RuleResult((numberOfWarnings * -1) * pointsForRemovingAWarning, 
                            String.format("%d checkstyle warnings were fixed", numberOfWarnings * -1));
                }
            }
        }
        return  new RuleResult(0, "No new or fixed checkstyle warnings found.");
    }
    
    private int getNumberOfAnnotations(List<CheckStyleResultAction> actions) {
        int numberOfAnnotations = 0;
        for (CheckStyleResultAction action : actions) {
            numberOfAnnotations += action.getResult().getNumberOfAnnotations();
        }
        return numberOfAnnotations;
    }

    public String getName() {
        return "Changed number of checkstyle warnings";
    }
}
