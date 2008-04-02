package hudson.plugins.cigame;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
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
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleBook;
import hudson.plugins.cigame.model.RuleSet;
import hudson.plugins.cigame.model.ScoreCard;
import hudson.plugins.cigame.rules.basic.BuildResultRule;
import hudson.plugins.cigame.rules.basic.NewTestFailureRule;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import hudson.tasks.Publisher;

public class GamePublisher extends Publisher {

    public Descriptor<Publisher> getDescriptor() {
        return PluginImpl.getDescriptor();
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
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        
        RuleSet ruleset = new RuleSet("Basic ruleset", new LinkedList<Rule>());
        ruleset.add(new BuildResultRule());
        ruleset.add(new NewTestFailureRule());
        
        ScoreCard sc = new ScoreCard();
        sc.record(build, ruleset);        
        
        ScoreCardAction action = new ScoreCardAction(sc, build);
        build.getActions().add(action);
        
        updateUserScores(build, sc.getTotalPoints());
       
        
        installLeaderBoard();
        
        return true;
    }

	private void updateUserScores(AbstractBuild<?, ?> build, double score)
			throws IOException {
		Set<User> players = new HashSet<User>();
        ChangeLogSet<? extends Entry> changeSet = build.getChangeSet();
    	for (Entry entry : changeSet) {
    		players.add(entry.getAuthor());
		}
    	for (User user : players) {
    		UserScoreProperty property = user.getProperty(UserScoreProperty.class);
    		if (property == null) {
    			property = new UserScoreProperty();
    			user.addProperty(property);
    		}
    		property.setScore(property.getScore() + score);
    	}
	}

    /**
     * Installs LeaderBoardAction onto the front page.
     * If it is already installed, nothing happens.
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
