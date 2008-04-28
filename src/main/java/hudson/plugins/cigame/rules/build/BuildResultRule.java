package hudson.plugins.cigame.rules.build;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;

/**
 * Rule that gives points on the result of the build.
 */
public class BuildResultRule implements Rule {

    private int failurePoints;
    private int successPoints;

    public BuildResultRule() {
        this(1, -10);
    }

    public BuildResultRule(int successPoints, int failurePoints) {
        this.successPoints = successPoints;
        this.failurePoints = failurePoints;
    }

    public String getName() {
        return "Build result";
    }

    public double evaluate(AbstractBuild<?, ?> build) {
        Result result = build.getResult();
        Result lastResult = null;
        if (build.getPreviousBuild() != null) {
            lastResult = build.getPreviousBuild().getResult();
        }
        return evaluate(result, lastResult);
    }

    double evaluate(Result result, Result lastResult) {
        if (result == Result.SUCCESS) {
            return successPoints;
        }
        if (result == Result.FAILURE) {
            if ((lastResult == null)
                    || (lastResult.isBetterThan(Result.FAILURE))) {
                return failurePoints;
            }
        }
        return 0;
    }
}
