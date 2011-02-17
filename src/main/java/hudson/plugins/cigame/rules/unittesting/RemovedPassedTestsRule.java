package hudson.plugins.cigame.rules.unittesting;

import java.util.Collection;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.AggregatableRule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.tasks.test.AbstractTestResultAction;

public class RemovedPassedTestsRule implements AggregatableRule<Integer> {

	private static final double pointsPerRemovedTest = -1.0;
	
	public String getName() {
		return Messages.UnitTestingRuleSet_RemovedPassedRule_Name();
	}
	
    private AbstractBuild<?, ?> getPreviousBuild(AbstractBuild<?, ?> previousBuild) {
        while(previousBuild != null) {
            if (previousBuild.getResult() != null) {
                if (previousBuild.getResult().isBetterThan(Result.FAILURE)) {
                    return previousBuild;
                } else if (previousBuild.getResult().isWorseOrEqualTo(Result.NOT_BUILT)) { 
                    // fall through
                } else {
                    return previousBuild;
                }
            }
            previousBuild = previousBuild.getPreviousBuild();
        }
        
        return null;
    }
	
   @SuppressWarnings("unchecked")
    public final RuleResult<Integer> evaluate(AbstractBuild<?, ?> previousBuild,
            AbstractBuild<?, ?> build) {

        previousBuild = getPreviousBuild(previousBuild);

        AbstractTestResultAction action; 
        AbstractTestResultAction prevAction;
        Result result;
        Result prevResult;
        
        if (previousBuild == null && build == null) {
            return null;
        }

        if (build == null) {
            action = null;
            result = Result.SUCCESS;
        } else {
            action = build.getTestResultAction();
            result = build.getResult();
        }
        
        if (previousBuild == null) {
            prevAction = AbstractUnitTestsRule.ZERO_RESULT;
            prevResult = Result.SUCCESS;
        } else {
            prevAction = previousBuild.getTestResultAction();
            prevResult = previousBuild.getResult();
        }
        
        prevAction = prevAction != null ? prevAction : AbstractUnitTestsRule.ZERO_RESULT;
        
        // sometimes (when a build is aborted?) result can be null
        prevResult = prevResult != null ? prevResult : Result.ABORTED;
        result = result != null ? result : Result.ABORTED;
        
        if ((prevResult.isBetterThan(Result.FAILURE))
                && (result.isBetterThan(Result.FAILURE))) {
            return evaluate(action, prevAction);
        } else {
            return null;
        }
    }

	protected RuleResult<Integer> evaluate(
			AbstractTestResultAction<?> testResult,
			AbstractTestResultAction<?> previousTestResult) {
		
		if (testResult != null) {
			// handled by other rules
			return null;
		}
		
		int previousPassingTests = previousTestResult.getTotalCount()
			- previousTestResult.getFailCount() - previousTestResult.getSkipCount();
		
		if (previousPassingTests > 0) {
			return new RuleResult<Integer>(previousPassingTests * pointsPerRemovedTest,
					getResultDescription(previousPassingTests),
					previousPassingTests);
					
		}

		return null;
	}

	protected String getResultDescription(Integer testDiff) {
		return Messages.UnitTestingRuleSet_RemovedPassedRule_Count(testDiff);
	}

    @Override
    public final RuleResult<?> aggregate(Collection<RuleResult<Integer>> results) {
        double score = 0.0;
        int testDiff = 0;
        for (RuleResult<Integer> result : results) {
            if (result != null) {
                score += result.getPoints();
                testDiff += result.getAdditionalData();
            }
        }
        
        if (score != 0.0) {
            return new RuleResult<Void>(score, 
                    getResultDescription(testDiff)); 
        }
        return null;
    }

    @Override
    public RuleResult<Integer> evaluate(AbstractBuild<?, ?> build) {
        throw new UnsupportedOperationException();
    }
}
