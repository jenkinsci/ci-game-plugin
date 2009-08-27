package hudson.plugins.cigame.rules.plugins.violation;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.util.ActionSequenceRetriever;
import hudson.plugins.cigame.util.ResultSequenceValidator;
import hudson.plugins.violations.ViolationsBuildAction;

public class DefaultViolationRule implements Rule {

    private int pointsForAddingViolation;
    private int pointsForRemovingViolation;
    private String typeName;
    private String violationName;

    public DefaultViolationRule(String typeName, String violationName,
            int pointsForAddingViolation, int pointsForRemovingViolation) {
        this.typeName = typeName;
        this.violationName = violationName;
        this.pointsForAddingViolation = pointsForAddingViolation;
        this.pointsForRemovingViolation = pointsForRemovingViolation;
    }

    public RuleResult evaluate(AbstractBuild<?, ?> build) {
        if (new ResultSequenceValidator(Result.UNSTABLE, 2).isValid(build)) {
            List<List<ViolationsBuildAction>> actionList = new ActionSequenceRetriever<ViolationsBuildAction>(ViolationsBuildAction.class, 2).getSequence(build);
            if (actionList != null) {
                int diff = getTypeReportCount(actionList.get(0)) - getTypeReportCount(actionList.get(1));
                if (diff > 0) {
                    return new RuleResult(diff * pointsForAddingViolation, 
                            Messages.ViolationRuleSet_DefaultRule_NewViolationsCount(diff, violationName)); //$NON-NLS-1$
                }
                if (diff < 0) {
                    return new RuleResult((diff * -1) * pointsForRemovingViolation, 
                            Messages.ViolationRuleSet_DefaultRule_FixedViolationsCount(diff * -1, violationName)); //$NON-NLS-1$
                }
            }
        }
        return RuleResult.EMPTY_RESULT;
    }

    private int getTypeReportCount(List<ViolationsBuildAction> actions) {
        int numberOfReports = 0;
        for (ViolationsBuildAction action : actions) {
            numberOfReports += action.getReport().getTypeReports().get(typeName).getNumber();
        }
        return numberOfReports;
    }
    
    public String getName() {
        return violationName;
    }
}
