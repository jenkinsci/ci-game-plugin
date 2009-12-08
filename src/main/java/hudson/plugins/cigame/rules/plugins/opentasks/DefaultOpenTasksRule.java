package hudson.plugins.cigame.rules.plugins.opentasks;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.util.ActionSequenceRetriever;
import hudson.plugins.cigame.util.ResultSequenceValidator;
import hudson.plugins.tasks.TasksResultAction;

import java.util.List;

/**
 * Default rule for the Open tasks plugin.
 */
public class DefaultOpenTasksRule implements Rule {

    private int pointsForAddingAnAnnotation;
    private int pointsForRemovingAnAnnotation;

    private Priority tasksPriority;

    public DefaultOpenTasksRule(Priority tasksPriority,
            int pointsForAddingAnAnnotation, int pointsForRemovingAnAnnotation) {
        this.tasksPriority = tasksPriority;
        this.pointsForAddingAnAnnotation = pointsForAddingAnAnnotation;
        this.pointsForRemovingAnAnnotation = pointsForRemovingAnAnnotation;
    }

    public RuleResult evaluate(AbstractBuild<?, ?> build) {
        if (new ResultSequenceValidator(Result.UNSTABLE, 2).isValid(build)) {
            List<List<TasksResultAction>> actionSequence = new ActionSequenceRetriever<TasksResultAction>(TasksResultAction.class, 2).getSequence(build);
            if ((actionSequence != null)
                    && hasNoErrors(actionSequence.get(0)) && hasNoErrors(actionSequence.get(1))) {
                int numberOfAnnotations = getNumberOfAnnotations(actionSequence.get(0)) - getNumberOfAnnotations(actionSequence.get(1));
                if (numberOfAnnotations > 0) {
                    return new RuleResult(numberOfAnnotations * pointsForAddingAnAnnotation, 
                            Messages.OpenTasksRule_DefaultRule_NewTasksCount(numberOfAnnotations, tasksPriority.name())); //$NON-NLS-1$
                }
                if (numberOfAnnotations < 0) {
                    return new RuleResult((numberOfAnnotations * -1) * pointsForRemovingAnAnnotation, 
                            Messages.OpenTasksRule_DefaultRule_FixedTasksCount(numberOfAnnotations * -1, tasksPriority.name())); //$NON-NLS-1$
                }
            }
        }
        return RuleResult.EMPTY_RESULT;
    }
    
    private boolean hasNoErrors(List<TasksResultAction> actions) {
        for (TasksResultAction action : actions) {
            if (action.getResult().hasError()) {
                return false;
            }
        }
        return true;
    }
    
    private int getNumberOfAnnotations(List<TasksResultAction> actions) {
        int numberOfAnnotations = 0;
        for (TasksResultAction action : actions) {
            numberOfAnnotations += action.getResult().getNumberOfAnnotations(tasksPriority);
        }
        return numberOfAnnotations;
    }
    
    public String getName() {
        return Messages.OpenTasksRule_DefaultRule_Name(tasksPriority.name()); //$NON-NLS-1$
    }
}
