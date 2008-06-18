package hudson.plugins.cigame.rules.plugins.opentasks;

import java.util.Collection;
import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.tasks.TasksResult;
import hudson.plugins.tasks.TasksResultAction;
import hudson.plugins.tasks.util.model.FileAnnotation;
import hudson.plugins.tasks.util.model.Priority;

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
        int numberOfAnnotations = 0;
        if (build.getResult().isBetterOrEqualTo(Result.UNSTABLE)
                && (build.getPreviousBuild() != null)){
            List<TasksResultAction> actions = build.getActions(hudson.plugins.tasks.TasksResultAction.class);
            for (TasksResultAction action : actions) {
                if (action.hasPreviousResultAction()) {
                    TasksResult result = action.getResult();
                    TasksResult previousResult = action.getPreviousResultAction().getResult();
                    
                    Collection<FileAnnotation> annotations = result.getAnnotations(tasksPriority.name());
                    Collection<FileAnnotation> previousAnnotations = previousResult.getAnnotations(tasksPriority.name());
                    if ((annotations != null) && (previousAnnotations != null)) {
                        numberOfAnnotations += annotations.size() - previousAnnotations.size();
                    }
                }
            }
        }
        if (numberOfAnnotations > 0) {
            return new RuleResult(numberOfAnnotations * pointsForAddingAnAnnotation, 
                    String.format("%d new open %s priority tasks were found", numberOfAnnotations, tasksPriority.name()));
        }
        if (numberOfAnnotations < 0) {
            return new RuleResult((numberOfAnnotations * -1) * pointsForRemovingAnAnnotation, 
                    String.format("%d open %s priority tasks were fixed", numberOfAnnotations * -1, tasksPriority.name()));
        }
        return new RuleResult(0, String.format("No new or fixed %s priority tasks found.", tasksPriority.name()));
    }
    
    public String getName() {
        return String.format("Open %s priority tasks", tasksPriority.name());
    }
}
