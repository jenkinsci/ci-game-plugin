package hudson.plugins.cigame;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
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

        perform(build, getDescriptor().getRuleBook(), getDescriptor().getNamesAreCaseSensitive());
        return true;
    }

    /**
     * Calculates score from the build and rule book and adds a Game action to the build.
     * @param build build to calculate points for
     * @param ruleBook rules used in calculation
     * @param usernameIsCasesensitive user names in Hudson are case insensitive.
     * @return true, if any user scores were updated; false, otherwise
     * @throws IOException thrown if there was a problem setting a user property
     */
    boolean perform(AbstractBuild<?, ?> build, RuleBook ruleBook, boolean usernameIsCasesensitive) throws IOException {
        ScoreCard sc = new ScoreCard();
        sc.record(build, ruleBook);

        ScoreCardAction action = new ScoreCardAction(sc, build);
        build.getActions().add(action);

        return updateUserScores(build.getChangeSet(), sc.getTotalPoints(), usernameIsCasesensitive);
    }

    /**
     * Add the score to the users that have committed code in the change set
     * 
     * @param changeSet the change set, used to get users
     * @param score the score that the build was worth
     * @param usernameIsCasesensitive user names in Hudson are case insensitive.
     * @throws IOException thrown if the property could not be added to the user object.
     * @return true, if any user scores was updated; false, otherwise
     */
    private boolean updateUserScores(ChangeLogSet<? extends Entry> changeSet,
            double score, boolean usernameIsCasesensitive) throws IOException {
        Collection<User> players = new TreeSet<User>(usernameIsCasesensitive ? null : new UsernameCaseinsensitiveComparator());
        if (score != 0) {
            for (Entry entry : changeSet) {
                players.add(entry.getAuthor());
            }
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

    public class UsernameCaseinsensitiveComparator implements Comparator<User> {
        public int compare(User arg0, User arg1) {
            return arg0.getId().compareToIgnoreCase(arg1.getId());
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }
}
