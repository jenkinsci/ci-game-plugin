package hudson.plugins.cigame;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.plugins.cigame.model.RuleBook;
import hudson.plugins.cigame.model.ScoreCard;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import hudson.tasks.Publisher;

public class GamePublisher extends Publisher {

    public Descriptor<Publisher> getDescriptor() {
        return PluginImpl.GAME_PUBLISHER_DESCRIPTOR;
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

        RuleBook ruleBook = PluginImpl.GAME_PUBLISHER_DESCRIPTOR.getRuleBook();

        ScoreCard sc = new ScoreCard();
        sc.record(build, ruleBook);

        ScoreCardAction action = new ScoreCardAction(sc, build);
        build.getActions().add(action);

        if (updateUserScores(build.getChangeSet(), sc.getTotalPoints())) {
            installLeaderBoard();
        }

        return true;
    }

    /**
     * Add the score to the users that have committed code in the change set
     * 
     * @param changeSet the change set, used to get users
     * @param score the score that the build was worth
     * @throws IOException thrown if the property could not be added to the user object.
     * @return true, if any user scores was updated; false, otherwise
     */
    private boolean updateUserScores(ChangeLogSet<? extends Entry> changeSet,
            double score) throws IOException {
        Set<User> players = new HashSet<User>();
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

    /**
     * Installs LeaderBoardAction onto the front page. If it is already
     * installed, nothing happens.
     */
    private void installLeaderBoard() {
        boolean isInstalled = false;
        List<Action> installedActions = Hudson.getInstance().getActions();
        for (Action installedAction : installedActions) {
            if (installedAction instanceof LeaderBoardAction) {
                isInstalled = true;
                break;
            }
        }
        if (!isInstalled) {
            LeaderBoardAction action = new LeaderBoardAction();
            Hudson.getInstance().getActions().add(action);
        }
    }
}
