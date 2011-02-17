package hudson.plugins.cigame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.User;
import hudson.plugins.cigame.model.RuleBook;
import hudson.plugins.cigame.model.ScoreCard;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;

public class GamePublisher extends Notifier {

    @Override
    public GameDescriptor getDescriptor() {
        return (GameDescriptor) super.getDescriptor();
    }

    @Override
    public boolean needsToRunAfterFinalized() {
        return true;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return null;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
            BuildListener listener) throws InterruptedException, IOException {

        perform(build, getDescriptor().getRuleBook(), getDescriptor().getNamesAreCaseSensitive(), listener);
        return true;
    }

    /**
     * Calculates score from the build and rule book and adds a Game action to the build.
     * @param build build to calculate points for
     * @param ruleBook rules used in calculation
     * @param usernameIsCasesensitive user names in Hudson are case insensitive.
     * @param listener the build listener
     * @return true, if any user scores were updated; false, otherwise
     * @throws IOException thrown if there was a problem setting a user property
     */
    boolean perform(AbstractBuild<?, ?> build, RuleBook ruleBook, boolean usernameIsCasesensitive, BuildListener listener) throws IOException {
        ScoreCard sc = new ScoreCard();
        sc.record(build, ruleBook, listener);

        ScoreCardAction action = new ScoreCardAction(sc, build);
        build.getActions().add(action);
        
        List<AbstractBuild<?, ?>> accountableBuilds = new ArrayList<AbstractBuild<?,?>>();
        accountableBuilds.add(build);
        
        // also add all previous aborted builds:
        AbstractBuild<?, ?> previousBuild = build.getPreviousBuild();
        while (previousBuild != null && previousBuild.getResult() == Result.ABORTED) {
        	accountableBuilds.add(previousBuild);
        	previousBuild = previousBuild.getPreviousBuild();
        }
        
        Set<User> players = new TreeSet<User>(usernameIsCasesensitive ? null : new UsernameCaseinsensitiveComparator());
        for (AbstractBuild<?, ?> b : accountableBuilds) {
        	ChangeLogSet<? extends Entry> changeSet = b.getChangeSet();
        	if (changeSet != null) {
	        	for (Entry e : changeSet) {
	        		players.add(e.getAuthor());
	        	}
        	}
        }
        
        return updateUserScores(players, sc.getTotalPoints());
    }

    /**
     * Add the score to the users that have committed code in the change set
     * 
     * @param changeSet the change set, used to get users
     * @param score the score that the build was worth
     * @throws IOException thrown if the property could not be added to the user object.
     * @return true, if any user scores was updated; false, otherwise
     */
    private boolean updateUserScores(Set<User> players, double score) throws IOException {
        if (score != 0) {
            for (User user : players) {
                UserScoreProperty property = user.getProperty(UserScoreProperty.class);
                if (property == null) {
                    property = new UserScoreProperty();
                    user.addProperty(property);
                }
                if (property.isParticipatingInGame()) {
                    property.setScore(property.getScore() + score);
                }
                user.save();
            }
        }
        return (!players.isEmpty());
    }

    public static class UsernameCaseinsensitiveComparator implements Comparator<User> {
        public int compare(User arg0, User arg1) {
            return arg0.getId().compareToIgnoreCase(arg1.getId());
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }
}
