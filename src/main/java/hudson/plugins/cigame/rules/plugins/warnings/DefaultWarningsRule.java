package hudson.plugins.cigame.rules.plugins.warnings;

import java.util.Collection;
import java.util.List;

import hudson.maven.MavenBuild;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.AggregatableRule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.util.ActionRetriever;
import hudson.plugins.warnings.WarningsResultAction;

/**
 * Default rule for the Warnings plugin.
 */
public class DefaultWarningsRule implements AggregatableRule<Integer> {

    private int pointsForAddingAWarning;
    private int pointsForRemovingAWarning;

    public DefaultWarningsRule(int pointsForAddingAWarning, int pointsForRemovingAWarning) {
        this.pointsForAddingAWarning = pointsForAddingAWarning;
        this.pointsForRemovingAWarning = pointsForRemovingAWarning;
    }
    
    @Override
	public RuleResult<?> aggregate(Collection<RuleResult<Integer>> results) {
    	double score = 0.0;
        int newWarnings = 0;
        for (RuleResult<Integer> result : results) {
            if (result != null) {
                score += result.getPoints();
                newWarnings += result.getAdditionalData();
            }
        }
        
        if (newWarnings > 0) {
            return new RuleResult<Void>(score, 
            		Messages.WarningsRuleSet_DefaultRule_NewWarningsCount(newWarnings)); 
        } else if (newWarnings < 0) {
        	return new RuleResult<Integer>(score, 
        			Messages.WarningsRuleSet_DefaultRule_FixedWarningsCount(newWarnings * -1));
        }
        return RuleResult.EMPTY_INT_RESULT;
	}

	@Override
	public RuleResult<Integer> evaluate(AbstractBuild<?, ?> previousBuild,
			AbstractBuild<?, ?> build) {
		if (build != null && build.getResult() != null && build.getResult().isWorseOrEqualTo(Result.FAILURE)) {
    		return RuleResult.EMPTY_INT_RESULT;
    	}
    	
    	if (previousBuild == null) {
    		if ( !(build instanceof MavenBuild)) {
    			// backward compatibility
    			return RuleResult.EMPTY_INT_RESULT;
    		}
    	} else if (previousBuild.getResult().isWorseOrEqualTo(Result.FAILURE)) {
    		return RuleResult.EMPTY_INT_RESULT;
    	}
    	
    	List<WarningsResultAction> currentActions = ActionRetriever.getResult(build, Result.UNSTABLE, WarningsResultAction.class);
    	if (!hasNoErrors(currentActions)) {
    		return RuleResult.EMPTY_INT_RESULT;
    	}
    	int currentAnnotations = getNumberOfAnnotations(currentActions);
    		
    	List<WarningsResultAction> previousActions = ActionRetriever.getResult(previousBuild, Result.UNSTABLE, WarningsResultAction.class);
    	if (!hasNoErrors(previousActions)) {
    		return RuleResult.EMPTY_INT_RESULT; 
    	}
    	int previousAnnotations = getNumberOfAnnotations(previousActions);
    	
    	int numberOfNewWarnings = currentAnnotations - previousAnnotations;
    	
    	if (numberOfNewWarnings > 0) {
            return new RuleResult<Integer>(numberOfNewWarnings * pointsForAddingAWarning, 
                    Messages.WarningsRuleSet_DefaultRule_NewWarningsCount(numberOfNewWarnings),
                    numberOfNewWarnings);
        }
        if (numberOfNewWarnings < 0) {
            return new RuleResult<Integer>((numberOfNewWarnings * -1) * pointsForRemovingAWarning, 
                    Messages.WarningsRuleSet_DefaultRule_FixedWarningsCount(numberOfNewWarnings * -1),
                    numberOfNewWarnings);
        }
        
        return RuleResult.EMPTY_INT_RESULT;
	}


	public RuleResult<Integer> evaluate(AbstractBuild<?, ?> build) {
		throw new UnsupportedOperationException();
//        if (new ResultSequenceValidator(Result.UNSTABLE, 2).isValid(build)) {
//            List<List<WarningsResultAction>> sequence = new ActionSequenceRetriever<WarningsResultAction>(WarningsResultAction.class, 2).getSequence(build);
//            if ((sequence != null)
//                    && hasNoErrors(sequence.get(0)) && hasNoErrors(sequence.get(1))) {
//                int numberOfAnnotations = getNumberOfAnnotations(sequence.get(0)) - getNumberOfAnnotations(sequence.get(1));
//                if (numberOfAnnotations > 0) {
//                    return new RuleResult(numberOfAnnotations * pointsForAddingAWarning, 
//                            Messages.WarningsRuleSet_DefaultRule_NewWarningsCount(numberOfAnnotations)); //$NON-NLS-1$
//                }
//                if (numberOfAnnotations < 0) {
//                    return new RuleResult((numberOfAnnotations * -1) * pointsForRemovingAWarning, 
//                            Messages.WarningsRuleSet_DefaultRule_FixedWarningsCount(numberOfAnnotations * -1)); //$NON-NLS-1$
//                }
//            }
//        }
//        return RuleResult.EMPTY_RESULT;
    }
    
    private boolean hasNoErrors(List<WarningsResultAction> actions) {
        for (WarningsResultAction action : actions) {
            if (action.getResult().hasError()) {
                return false;
            }
        }
        return true;
    }
    
    private int getNumberOfAnnotations(List<WarningsResultAction> actions) {
        int numberOfAnnotations = 0;
        for (WarningsResultAction action : actions) {
            numberOfAnnotations += action.getResult().getNumberOfAnnotations();
        }
        return numberOfAnnotations;
    }

    public String getName() {
        return Messages.WarningsRuleSet_DefaultRule_Name(); //$NON-NLS-1$
    }
}
