package hudson.plugins.cigame.rules.plugins.findbugs;

import java.util.Collection;
import hudson.model.AbstractBuild;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.model.RuleResult;

public class NewFindBugsWarningsRule extends AbstractFindBugsWarningsRule {

    private int pointsForEachNewWarning;
    
    public NewFindBugsWarningsRule(Priority priority, int pointsForEachNewWarning) {
    	super(priority);
        this.pointsForEachNewWarning = pointsForEachNewWarning;
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
        
        if (score != 0.0) {
            return new RuleResult<Void>(score, 
            		Messages.FindBugsRuleSet_NewWarningsRule_Count(Math.abs(newWarnings), priority.name())); 
        }
        return EMPTY_RESULT;
	}

	@Override
	protected RuleResult<Integer> evaluate(int previousAnnotations, int currentAnnotations) {
    	
    	if (currentAnnotations > previousAnnotations) {
    		int newWarnings = currentAnnotations - previousAnnotations;
    		return new RuleResult<Integer>(newWarnings * pointsForEachNewWarning, 
                    Messages.FindBugsRuleSet_NewWarningsRule_Count(Math.abs(newWarnings), priority.name()),
                    newWarnings);
    	}
    	
		return EMPTY_RESULT;
	}
    
    public RuleResult<Integer> evaluate(AbstractBuild<?, ?> build) {
    	throw new UnsupportedOperationException();
//        int numberOfAnnotations = 0;
//        if (new ResultSequenceValidator(Result.UNSTABLE, 2).isValid(build)) {
//            List<List<FindBugsResultAction>> actionSequence = new ActionSequenceRetriever<FindBugsResultAction>(FindBugsResultAction.class, 2).getSequence(build);
//            if ((actionSequence != null)
//                    && hasNoErrors(actionSequence.get(0)) && hasNoErrors(actionSequence.get(1))) {
//                numberOfAnnotations = getNumberOfAnnotations(actionSequence.get(0)) - getNumberOfAnnotations(actionSequence.get(1)); 
//            }       
//        }
//        if (numberOfAnnotations > 0) {
//            return new RuleResult(numberOfAnnotations * pointsForEachNewWarning, 
//                    Messages.FindBugsRuleSet_NewWarningsRule_Count( Math.abs(numberOfAnnotations), priority.name())); //$NON-NLS-1$
//        }
//        return RuleResult.EMPTY_RESULT;
    }
    
    public String getName() {
        return Messages.FindBugsRuleSet_NewWarningsRule_Title(priority.name()); //$NON-NLS-1$
    }
}
