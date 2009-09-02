package hudson.plugins.cigame.rules.plugins.violation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.violations.TypeSummary;
import hudson.plugins.violations.ViolationsBuildAction;
import hudson.plugins.violations.ViolationsReport;
import hudson.plugins.violations.ViolationsReport.TypeReport;

import org.junit.Test;
import org.jvnet.hudson.test.Bug;

@SuppressWarnings("unchecked")
public class DefaultViolationRuleTest {
    @Test
    public void assertFailedBuildsIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);

        DefaultViolationRule rule = new DefaultViolationRule("pmd", "PMD Violations", 100, -100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }

    /**
     * Asserts that the issue 1884 is fixed.
     * https://hudson.dev.java.net/issues/show_bug.cgi?id=1884
     */
    @Test
    public void assertIssue1884IsFixed() {
        final ViolationsReport previousReport = createViolationsReportStub("pmd", 200, null);
        final ViolationsReport currentReport = createViolationsReportStub("pmd", 100, previousReport);
        final ArrayList<ViolationsBuildAction> actionList = new ArrayList<ViolationsBuildAction>();
        actionList.add(new ViolationsBuildAction(null, currentReport));

        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getActions(ViolationsBuildAction.class)).thenReturn(actionList);

        DefaultViolationRule rule = new DefaultViolationRule("pmd", "PMD Violations", 100, -100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }
    
    @Test
    public void assertNoPreviousBuildIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(build.getPreviousBuild()).thenReturn(null);
        
        DefaultViolationRule rule = new DefaultViolationRule("pmd", "PMD Violations", 100, -100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }
    
    @Test
    public void assertThatPointsAreAwardedCorrectly() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);

        ViolationsBuildAction previousAction = mock(ViolationsBuildAction.class);
        ViolationsReport previousReport = createViolationsReportStub("pmd", 50, null);
        when(previousBuild.getActions(ViolationsBuildAction.class)).thenReturn(Arrays.asList(previousAction));
        when(previousAction.getReport()).thenReturn(previousReport);

        ViolationsBuildAction currentAction = mock(ViolationsBuildAction.class);
        ViolationsReport currentReport = createViolationsReportStub("pmd", 100, previousReport);
        when(build.getActions(ViolationsBuildAction.class)).thenReturn(Arrays.asList(currentAction));
        when(currentAction.getReport()).thenReturn(currentReport);

        RuleResult ruleResult = new DefaultViolationRule("pmd", "PMD violations", 100, -100).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 5000", ruleResult.getPoints(), is(5000d));
    }
    
    @Test
    public void assertIfPreviousBuildFailedResultIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.FAILURE);

        ViolationsBuildAction action = mock(ViolationsBuildAction.class);
        when(build.getActions(ViolationsBuildAction.class)).thenReturn(Arrays.asList(action));
        ViolationsReport previousReport = createViolationsReportStub("pmd", 10, null);
        ViolationsReport currentReport = createViolationsReportStub("pmd", 100, previousReport);
        when(action.getReport()).thenReturn(currentReport);

        RuleResult ruleResult = new DefaultViolationRule("pmd", "PMD violations", 100, -100).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
    
    @Bug(3726)
    @Test
    public void assertThatNonExistingPreviousReportsAreIgnored() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);

        ViolationsBuildAction previousAction = mock(ViolationsBuildAction.class);
        ViolationsReport previousReport = createViolationsReportStub("cpd", 10, null);
        when(previousBuild.getActions(ViolationsBuildAction.class)).thenReturn(Arrays.asList(previousAction));
        when(previousAction.getReport()).thenReturn(previousReport);

        ViolationsBuildAction currentAction = mock(ViolationsBuildAction.class);
        ViolationsReport currentReport = createViolationsReportStub("pmd", 100, previousReport);
        when(build.getActions(ViolationsBuildAction.class)).thenReturn(Arrays.asList(currentAction));
        when(currentAction.getReport()).thenReturn(currentReport);

        RuleResult ruleResult = new DefaultViolationRule("pmd", "PMD violations", 100, -100).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
    
    @Bug(3726)
    @Test
    public void assertThatPreviousReportsWithErrorIsIgnored() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.SUCCESS);

        ViolationsBuildAction previousAction = mock(ViolationsBuildAction.class);
        ViolationsReport previousReport = createViolationsReportStub("pmd", 50, null);
        when(previousBuild.getActions(ViolationsBuildAction.class)).thenReturn(Arrays.asList(previousAction));
        when(previousAction.getReport()).thenReturn(previousReport);
        previousReport.getTypeSummary("pmd").setErrorMessage("an error message");

        ViolationsBuildAction currentAction = mock(ViolationsBuildAction.class);
        ViolationsReport currentReport = createViolationsReportStub("pmd", 100, previousReport);
        when(build.getActions(ViolationsBuildAction.class)).thenReturn(Arrays.asList(currentAction));
        when(currentAction.getReport()).thenReturn(currentReport);

        RuleResult ruleResult = new DefaultViolationRule("pmd", "PMD violations", 100, -100).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }

    /**
     * Creates a violation report stub with one TypeReport containing the method params
     * @param type type in the report
     * @param number the number of violations
     * @param previous if there is a previous report to be returned by report.getPrevious();
     * @return mocked ViolationsReport
     */
    private ViolationsReport createViolationsReportStub(final String type, int number, final ViolationsReport previous) {
        ViolationsReport report = mock(ViolationsReport.class);
        TypeSummary typeSummary = new TypeSummary();
        
        TypeReport typeReport = report.new TypeReport(type, null, number);
        Map<String, TypeReport> typeReports = new HashMap<String, TypeReport>();
        typeReports.put(type, typeReport);
        
        when(report.getTypeSummary(type)).thenReturn(typeSummary);
        when(report.getTypeReports()).thenReturn(typeReports);
        when(report.typeCount(type)).thenReturn(number);
        if (previous != null) {
            when(report.previous()).thenReturn(previous);
        }
        return report;
    }
}
