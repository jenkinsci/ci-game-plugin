package hudson.plugins.cigame;

import java.util.HashSet;
import java.util.Set;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.User;
import hudson.plugins.cigame.model.ScoreCard;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;

/**
 * Score card for a certain build
 * 
 * @author Erik Ramfelt
 */
@ExportedBean(defaultVisibility=999)
public class ScoreCardAction implements Action{

	private static final long serialVersionUID = 1L;

	private AbstractBuild<?, ?> build;
    
    private ScoreCard scorecard;
    
    public ScoreCardAction(ScoreCard scorecard, AbstractBuild<?,?>b) {
        build = b;
        this.scorecard = scorecard;
    }
    
    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    public String getDisplayName() {
        return "Score card";
    }

    public String getIconFileName() {
        return GameDescriptor.ACTION_LOGO_MEDIUM;
    }

    public String getUrlName() {
        return "cigame";
    }

    @Exported
    public ScoreCard getScorecard() {
        return scorecard;
    }
    
    @Exported
    public Set<User> getParticipants() {
    	Set<User> players = new HashSet<User>();
    	ChangeLogSet<? extends Entry> changeSet = build.getChangeSet();
    	for (Entry entry : changeSet) {
    		players.add(entry.getAuthor());
		}
    	return players;
    }
}
