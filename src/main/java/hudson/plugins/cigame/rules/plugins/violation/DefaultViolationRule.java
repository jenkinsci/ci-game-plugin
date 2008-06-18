package hudson.plugins.cigame.rules.plugins.violation;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.violations.ViolationsBuildAction;
import hudson.plugins.violations.ViolationsReport;
import hudson.plugins.violations.ViolationsReport.TypeReport;

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
        int diff = 0;
        if (build.getResult().isBetterOrEqualTo(Result.UNSTABLE)
                && (build.getPreviousBuild() != null)) {
            List<ViolationsBuildAction> actions = build.getActions(ViolationsBuildAction.class);
            for (ViolationsBuildAction action : actions) {
                ViolationsReport violationReport = action.getReport();
                if (violationReport.previous() != null) {
                    TypeReport typeReport = violationReport.getTypeReports().get(typeName);
                    TypeReport previousTypeReport = violationReport.previous().getTypeReports().get(typeName);
                    if ((typeReport != null) && (previousTypeReport != null)) {
                        diff += typeReport.getNumber() - previousTypeReport.getNumber();
                    }
                }
            }
        }
        if (diff > 0) {
            return new RuleResult(diff * pointsForAddingViolation, 
                    String.format("%d new %ss were found", diff, violationName));
        }
        if (diff < 0) {
            return new RuleResult((diff * -1) * pointsForRemovingViolation, 
                    String.format("%d %ss were fixed", diff * -1, violationName));
        }
        return new RuleResult(0, 
                String.format("There was no change for %ss", violationName));
    }

    /**
     * Get the score for the current report
     * 
     * @param report can not be null
     * @param previousReport can not be null
     * @return
     */
    protected double evaluateReport(ViolationsReport report,
            ViolationsReport previousReport) {
        TypeReport typeReport = report.getTypeReports().get(typeName);
        TypeReport previousTypeReport = previousReport.getTypeReports().get(typeName);
        if ((typeReport != null) && (previousReport != null)) {
            int diff = typeReport.getNumber() - previousTypeReport.getNumber();
            if (diff > 0) {
                return pointsForAddingViolation;
            } else if (diff < 0) {
                return pointsForRemovingViolation;
            }
        }
        return 0;
    }

    public String getName() {
        return violationName;
    }
}
