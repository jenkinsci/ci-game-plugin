package hudson.plugins.cigame.rules.plugins.jacoco;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.plugins.cigame.model.AggregatableRule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.util.ActionRetriever;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.model.Coverage;

import java.util.Collection;
import java.util.List;

/**
 * Default rule for the Jacoco plugin.
 *
 * @author Philip Aston
 */
public class DefaultJacocoRule implements AggregatableRule<Double> {

    private static final RuleResult<Double> EMPTY_RESULT =
        new RuleResult<Double>(0.0, "", 0d);

    private final int pointsForIncreasingCoverage;
    private final int pointsForReducingCoverage;

    public DefaultJacocoRule(
        int pointsForReducingCoveragePerCent,
        int pointsForIncreasingCoveragePerCent) {
        this.pointsForReducingCoverage = pointsForReducingCoveragePerCent;
        this.pointsForIncreasingCoverage = pointsForIncreasingCoveragePerCent;
    }

    @Override
    public RuleResult<?> aggregate(Collection<RuleResult<Double>> results) {
        double score = 0.0;
        double sumOfPercentageChanges = 0d;

        for (RuleResult<Double> result : results) {
            score += result.getPoints();
            sumOfPercentageChanges += result.getAdditionalData();
        }

        if (sumOfPercentageChanges >= 0) {
            return new RuleResult<Void>(
                score,
                Messages.JacocoRuleSet_DefaultRule_IncreasedCoverage(sumOfPercentageChanges * 0.01));
        }
        else {
            return new RuleResult<Void>(
                score,
                Messages.JacocoRuleSet_DefaultRule_ReducedCoverage(sumOfPercentageChanges * -0.01));
        }
    }

    @Override
    public RuleResult<Double> evaluate(AbstractBuild<?, ?> previousBuild,
        AbstractBuild<?, ?> build) {

        if (build == null ||
            build.getResult() == null ||
            build.getResult().isWorseOrEqualTo(Result.FAILURE)) {
            return EMPTY_RESULT;
        }

        if (previousBuild == null ||
            previousBuild.getResult().isWorseOrEqualTo(Result.FAILURE)) {
            return EMPTY_RESULT;
        }

        final List<JacocoBuildAction> currentActions =
            ActionRetriever.getResult(build, Result.UNSTABLE, JacocoBuildAction.class);

        final Coverage currentCoverage = getLineCoverage(currentActions);

        if (!currentCoverage.isInitialized()) {
            return EMPTY_RESULT;
        }

        final List<JacocoBuildAction> previousActions =
            ActionRetriever.getResult(previousBuild, Result.UNSTABLE, JacocoBuildAction.class);

        final Coverage previousCoverage = getLineCoverage(previousActions);

        if (!previousCoverage.isInitialized()) {
            return EMPTY_RESULT;
        }

        if (previousCoverage.equals(currentCoverage)) {
            return EMPTY_RESULT;
        }

        final double percentage = currentCoverage.getPercentageFloat() - previousCoverage.getPercentageFloat();

        if (percentage >= 0) {
            return new RuleResult<Double>(
                ceil(percentage * pointsForIncreasingCoverage),
                Messages.JacocoRuleSet_DefaultRule_IncreasedCoverage(percentage * 0.01),
                percentage);
        }
        else {
            return new RuleResult<Double>(
                floor(percentage * -1 * pointsForReducingCoverage),
                Messages.JacocoRuleSet_DefaultRule_ReducedCoverage(percentage * -0.01),
                percentage);
        }
    }

    @Override
    public RuleResult<Double> evaluate(AbstractBuild<?, ?> build) {
        throw new UnsupportedOperationException();
    }

    private static Coverage getLineCoverage(List<? extends JacocoBuildAction> actions) {
        final Coverage totalCoverage = new Coverage();

        for (JacocoBuildAction action : actions) {
            final Coverage lineCoverage = action.getLineCoverage();
            totalCoverage.accumulatePP(lineCoverage.getMissed(), lineCoverage.getCovered());
        }

        return totalCoverage;
    }

    @Override
    public String getName() {
        return Messages.JacocoRuleSet_DefaultRule_Name();
    }
}
