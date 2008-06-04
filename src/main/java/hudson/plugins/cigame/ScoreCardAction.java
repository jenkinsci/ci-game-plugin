package hudson.plugins.cigame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
@ExportedBean(defaultVisibility = 999)
public class ScoreCardAction implements Action {

    private static final long serialVersionUID = 1L;

    private AbstractBuild<?, ?> build;

    private ScoreCard scorecard;

    public ScoreCardAction(ScoreCard scorecard, AbstractBuild<?, ?> b) {
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
    public Collection<User> getParticipants() {
        List<User> players = new ArrayList<User>();
        ChangeLogSet<? extends Entry> changeSet = build.getChangeSet();
        for (Entry entry : changeSet) {
            User user = entry.getAuthor();
            UserScoreProperty property = user.getProperty(UserScoreProperty.class);
            if ((property != null) && property.isParticipatingInGame()) {
                players.add(user);
            }
        }
        Collections.sort(players, new Comparator<User>() {
            public int compare(User arg0, User arg1) {
                return arg0.getDisplayName().compareToIgnoreCase(arg1.getDisplayName());
            }            
        });
        return players;
    }
}
