package hudson.plugins.cigame.util;

import java.util.ArrayList;
import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Action;

/**
 * Utility class to retrieve a list of actions from a build seequence.
 * Use the class to get a list of all actions for the last 5 builds. If a build
 * within the sequence does not have the action, the class will not return a list
 * at all.
 *
 * @param <T> Action class that is required to exist in all builds.
 */
public class ActionSequenceRetriever<T extends Action>  {

    private final int sequenceLength;
    private final Class<T> actionClass;

    public ActionSequenceRetriever(Class<T> actionClass, int sequenceLength) {
        this.actionClass = actionClass;
        this.sequenceLength = sequenceLength;
    }

    /**
     * Returns a list of actions that meets the requirements in the constructor.
     * @param build latest build
     * @return a list of actions, or null if there was not enough builds or a build does not contain the action
     */
    public List<List<T>> getSequence(AbstractBuild<?,?> build) {
        List<List<T>> actionSequence = new ArrayList<List<T>>();
        int buildCount = 0;
        while ((build != null)
                && (buildCount < sequenceLength))  {
            List<T> actionsInBuild = build.getActions(actionClass);
            if ((actionsInBuild != null) && (! actionsInBuild.isEmpty())) {
                actionSequence.add(actionsInBuild);
                build = build.getPreviousBuild();
            } else {
                break;
            }
            buildCount++;
        }
        if (actionSequence.size() == sequenceLength) {
            return actionSequence;
        }
        return null;
    }
}
