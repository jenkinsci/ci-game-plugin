package hudson.plugins.cigame.rules.plugins.findbugs;

import java.util.List;

import hudson.maven.MavenBuild;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.model.AggregatableRule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.util.ActionRetriever;
import hudson.plugins.findbugs.FindBugsMavenResultAction;
import hudson.plugins.findbugs.FindBugsResultAction;

public abstract class AbstractFindBugsWarningsRule implements AggregatableRule<Integer> {
	
	protected static final RuleResult<Integer> EMPTY_RESULT = new RuleResult<Integer>(0.0, "", Integer.valueOf(0));
	
	protected Priority priority;

	protected AbstractFindBugsWarningsRule(Priority priority) {
		this.priority = priority;
	}
	
    protected boolean hasNoErrors(List<FindBugsResultAction> actions) {
        for (FindBugsResultAction action : actions) {
            if (action.getResult().hasError()) {
                return false;
            }
        }
        return true;
    }
    
    protected int getNumberOfAnnotations(List<FindBugsResultAction> list) {
        int numberOfAnnotations = 0;
        for (FindBugsResultAction action : list) {
            numberOfAnnotations += action.getResult().getNumberOfAnnotations(priority);
        }
        return numberOfAnnotations;
    }
    
	protected int getNumberOfMavenAnnotations(List<FindBugsMavenResultAction> list) {
        int numberOfAnnotations = 0;
        for (FindBugsMavenResultAction action : list) {
            numberOfAnnotations += action.getResult().getNumberOfAnnotations(priority);
        }
        return numberOfAnnotations;
    }
    
	@Override
	public final RuleResult<Integer> evaluate(AbstractBuild<?, ?> previousBuild,
			AbstractBuild<?, ?> build) {
    	if (build != null && build.getResult() != null && build.getResult().isWorseOrEqualTo(Result.FAILURE)) {
    		return EMPTY_RESULT;
    	}
    	
    	if (previousBuild == null) {
    		if ( !(build instanceof MavenBuild)) {
    			// backward compatibility
    			return EMPTY_RESULT;
    		}
    	} else if (previousBuild.getResult().isWorseOrEqualTo(Result.FAILURE)) {
    		return EMPTY_RESULT;
    	}
    	
    	List<FindBugsResultAction> currentActions = ActionRetriever.getResult(build, Result.UNSTABLE, FindBugsResultAction.class);
		if (currentActions.isEmpty()) {
			return evaluateMaven(previousBuild, build);
		} else {
			if (!hasNoErrors(currentActions)) {
				return EMPTY_RESULT;
			}
			int currentAnnotations = getNumberOfAnnotations(currentActions);
			
			List<FindBugsResultAction> previousActions = ActionRetriever.getResult(previousBuild, Result.UNSTABLE, FindBugsResultAction.class);
			if (!hasNoErrors(previousActions)) {
				return EMPTY_RESULT;
			}
			int previousAnnotations = getNumberOfAnnotations(previousActions);
			
			return evaluate(previousAnnotations, currentAnnotations);
		}
	}

	private RuleResult<Integer> evaluateMaven(AbstractBuild<?, ?> previousBuild, AbstractBuild<?, ?> build) {
		List<FindBugsMavenResultAction> currentActions = ActionRetriever.getResult(build, Result.UNSTABLE,
				FindBugsMavenResultAction.class);
		if (currentActions.isEmpty()) {
			return null;
		}
		int currentAnnotations = getNumberOfMavenAnnotations(currentActions);
		
		List<FindBugsMavenResultAction> previousActions = ActionRetriever.getResult(previousBuild, Result.UNSTABLE, FindBugsMavenResultAction.class);
		int previousAnnotations = getNumberOfMavenAnnotations(previousActions);
		
		return evaluate(previousAnnotations, currentAnnotations);
	}
	
	protected abstract RuleResult<Integer> evaluate(int previousAnnotations, int currentAnnotations);
}
