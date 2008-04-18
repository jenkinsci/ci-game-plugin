package hudson.plugins.cigame.rules.plugins.opentasks;

import java.util.Collection;
import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
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

    public double evaluate(AbstractBuild<?, ?> build) {
        double points = 0;
        if (build.getResult().isBetterOrEqualTo(Result.UNSTABLE)) {
            List<TasksResultAction> actions = build.getActions(hudson.plugins.tasks.TasksResultAction.class);
            for (TasksResultAction action : actions) {
                if (action.getPreviousResultAction() != null) {
                    TasksResult result = action.getResult();
                    TasksResult previousResult = action.getPreviousResultAction().getResult();
                    points += evaluteTaskResult(result, previousResult);
                }
            }
        }
        return points;
    }

    double evaluteTaskResult(TasksResult result, TasksResult previousResult) {
        Collection<FileAnnotation> annotations = result.getAnnotations(tasksPriority.name());
        Collection<FileAnnotation> previousAnnotations = previousResult.getAnnotations(tasksPriority.name());
        if ((annotations != null) && (previousAnnotations != null)) {
            int diff = annotations.size() - previousAnnotations.size();
            if (diff > 0) {
                return pointsForAddingAnAnnotation;
            } else if (diff < 0) {
                return pointsForRemovingAnAnnotation;
            }
        }
        return 0;
    }

    public String getName() {
        return "Open tasks";
    }

}
