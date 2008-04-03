package hudson.plugins.cigame.rules.basic;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.model.Run;
import hudson.plugins.cigame.model.Rule;

/**
 * Rule that gives points on the result of the build.
 */
public class BuildResultRule implements Rule {

	private int failurePoints = -10;
    private int successPoints = 1;
    
    public BuildResultRule() {
    }
    
    public BuildResultRule(int successPoints, int failurePoints) {
        this.successPoints = successPoints;
        this.failurePoints = failurePoints;
    }

    public String getName() {
        return "Build result";
    }

	public double evaluate(AbstractBuild<?,?> build) {
    	
    	Result result = build.getResult();
		if (result == Result.SUCCESS) {
    		return successPoints;
    	}

    	if (result == Result.FAILURE) {

        	Result lastResult = Result.SUCCESS;
        	Run<?,?> previousBuild = build.getPreviousBuild();
        	if (previousBuild != null) {
        		lastResult = previousBuild.getResult();
        	}
        	
        	if (result.isWorseThan(lastResult)) {
        		return failurePoints;
        	}
    	}
    	
        return 0;
	}
}
