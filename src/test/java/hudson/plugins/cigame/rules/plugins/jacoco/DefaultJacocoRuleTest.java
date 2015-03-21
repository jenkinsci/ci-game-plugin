package hudson.plugins.cigame.rules.plugins.jacoco;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.jacoco.JacocoBuildAction;
import hudson.plugins.jacoco.model.Coverage;
import hudson.plugins.jacoco.model.CoverageElement.Type;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for {@link DefaultJacocoRule}.
 *
 * @author Philip Aston
 */
public class DefaultJacocoRuleTest {

    @Mock
    private AbstractBuild<?, ?> build;
    @Mock
    private AbstractBuild<?, ?> previous;

    private final DefaultJacocoRule rule = new DefaultJacocoRule(-2, 3);

    @Before
    public void setUp() {
        initMocks(this);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previous.getResult()).thenReturn(Result.SUCCESS);
    }

    @Test
    public void reducedCoverageGiveNegativePoints() {
        addCoverage(previous, 5, 5);
        addCoverage(build, 6, 4);

        final RuleResult<?> ruleResult = rule.evaluate(previous, build);

        assertPoints(-20f, ruleResult);

        assertThat(ruleResult.getDescription(),
            allOf(
                containsString("reduced"),
                containsString("10.0%")));
    }

    @Test
    public void reducedCoverageCostsAtLeastOnePoint() {
        addCoverage(previous, 10, 990);
        addCoverage(build, 11, 989);

        final RuleResult<?> ruleResult = rule.evaluate(previous, build);

        assertPoints(-1f, ruleResult);

        assertThat(ruleResult.getDescription(),
            allOf(
                containsString("reduced"),
                containsString("0.1%")));
    }

    @Test
    public void increasedCoverageGivePositivePoints() {
        addCoverage(previous, 6, 4);
        addCoverage(build, 5, 5);

        final RuleResult<?> ruleResult = rule.evaluate(previous, build);

        assertPoints(30f, ruleResult);

        assertThat(ruleResult.getDescription(),
            allOf(
                containsString("increased"),
                containsString("10.0%")));
    }

    @Test
    public void increasedCoverageWinsAtLeastOnePoint() {
        addCoverage(previous, 10, 990);
        addCoverage(build, 9, 991);

        final RuleResult<?> ruleResult = rule.evaluate(previous, build);

        assertPoints(1f, ruleResult);
    }

    @Test
    public void noChangeGivesNoPoints() {
        addCoverage(previous, 6, 4);
        addCoverage(build, 6, 4);

        final RuleResult<?> ruleResult = rule.evaluate(previous, build);

        assertPoints(0f, ruleResult);
    }

    @Test
    public void sameCoverageGivesOnePoint() {
        addCoverage(previous, 5, 10);
        addCoverage(build, 4, 8);

        final RuleResult<?> ruleResult = rule.evaluate(previous, build);

        assertPoints(1f, ruleResult);
    }

    @Test
    public void previousBuildHasNoCoverage() {
        addCoverage(build, 5, 5);

        final RuleResult<?> ruleResult = rule.evaluate(previous, build);

        assertPoints(0f, ruleResult);
    }

    @Test
    public void currentBuildHasNoCoverage() {
        addCoverage(previous, 5, 5);

        final RuleResult<?> ruleResult = rule.evaluate(previous, build);

        assertPoints(0f, ruleResult);
    }

    @Test
    public void failedBuildIsWorthZeroPoints() {
        when(build.getResult()).thenReturn(Result.FAILURE);
        addCoverage(build, 5, 5);
        addCoverage(previous, 5, 7);

        final RuleResult<?> ruleResult = rule.evaluate(previous, build);

        assertPoints(0f, ruleResult);
    }

    @Test
    public void noPreviousBuildIsWorthZeroPoints() {
        addCoverage(build, 5, 5);

        final RuleResult<?> ruleResult = rule.evaluate(null, build);

        assertPoints(0f, ruleResult);
    }

    @Test
    public void noCurrentBuildIsWorthZeroPoints() {
        addCoverage(previous, 5, 5);

        final RuleResult<?> ruleResult = rule.evaluate(previous, null);

        assertPoints(0f, ruleResult);
    }

    @Test
    public void currentBuildNotCompleteIsWorthZeroPoints() {
        addCoverage(previous, 2, 5);
        addCoverage(build, 5, 5);

        when(build.getResult()).thenReturn(null);

        final RuleResult<?> ruleResult = rule.evaluate(previous, build);

        assertPoints(0f, ruleResult);
    }

    @Test
    public void previousBuildFailedResultIsWorthZeroPoints() {
        when(previous.getResult()).thenReturn(Result.FAILURE);
        addCoverage(build, 5, 5);
        addCoverage(previous, 5, 7);

        final RuleResult<?> ruleResult = rule.evaluate(previous, build);

        assertPoints(0f, ruleResult);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void aggregateIncreasingScore() {
        final RuleResult<?> aggregation = rule.aggregate(
            asList(
                new RuleResult<Double>(-2, null, -0.05),
                new RuleResult<Double>(+5, null, +0.06)
            ));

        assertEquals(3, aggregation.getPoints(), 0.1f);

        assertThat(aggregation.getDescription(),
            allOf(
                containsString("increased"),
                containsString("0.01%")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void aggregateDecreasingScore() {
        final RuleResult<?> aggregation = rule.aggregate(
            asList(
                new RuleResult<Double>(+2, null, +0.05),
                new RuleResult<Double>(-5, null, -0.06)
            ));

        assertEquals(-3, aggregation.getPoints(), 0.1f);

        assertThat(aggregation.getDescription(),
            allOf(
                containsString("reduced"),
                containsString("0.01%")));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDeprecatedEvaluateUnsupported() {
        rule.evaluate(build);
    }

    @Test
    public void testGetName() {
        assertThat(rule.getName(), containsString("coverage"));
    }

    private static void assertPoints(double expected, RuleResult<?> ruleResult) {
        assertEquals(expected, ruleResult.getPoints(), 0.1d);
    }

    private static void addCoverage(AbstractBuild<?, ?> build, int missed, int covered) {
        final Map<Type, Coverage> ratios = Collections.emptyMap();
        final JacocoBuildAction action =
            new JacocoBuildAction(build, null, ratios, null, mock(BuildListener.class), null, null);

        action.getLineCoverage().accumulate(missed, covered);

        when(build.getActions(JacocoBuildAction.class)).thenReturn(asList(action));
    }
}
