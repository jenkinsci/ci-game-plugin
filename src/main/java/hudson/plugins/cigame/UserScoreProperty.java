package hudson.plugins.cigame;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import hudson.model.AbstractBuild;
import hudson.plugins.cigame.model.ScoreHistoryEntry;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import hudson.model.User;
import hudson.model.UserProperty;

import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Erik Ramfelt
 */
@ExportedBean(defaultVisibility = 999)
public class UserScoreProperty extends UserProperty {

    @VisibleForTesting
    protected static final int MAX_HISTORY_LENGTH = 10;

    private double score;
    
    /** Inversed name as default value is false when serializing from data that
     * has doesnt have the value. */
    private boolean isNotParticipatingInGame;

    private List<ScoreHistoryEntry> scoreHistoryEntries;

    public UserScoreProperty() {
        score = 0;
        isNotParticipatingInGame = false;
    }
    
    @DataBoundConstructor
    public UserScoreProperty(double score, boolean participatingInGame, List<ScoreHistoryEntry> scoreHistoryEntries) {
        this.score = score;
        this.isNotParticipatingInGame = !participatingInGame;
        this.scoreHistoryEntries = scoreHistoryEntries != null ? Lists.newLinkedList(scoreHistoryEntries) : null;
    }

    @Exported
    public User getUser() {
        return user;
    }

    @Exported
    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Exported
    public boolean isParticipatingInGame() {
        return !isNotParticipatingInGame;
    }

    public void rememberAccountableBuilds(List<AbstractBuild<?, ?>> accountableBuilds, double score) {
        this.addScoreHistoryEntry(ScoreHistoryEntry.fromScoreAward(accountableBuilds, score));
    }

    @VisibleForTesting
    protected void addScoreHistoryEntry(ScoreHistoryEntry scoreHistoryEntry) {
        if(this.scoreHistoryEntries == null) {
            this.scoreHistoryEntries = Lists.newLinkedList();
        }
        makeSpaceForNewEntryInHistory();
        this.scoreHistoryEntries.add(0, scoreHistoryEntry);
    }

    private void makeSpaceForNewEntryInHistory() {
        while(historyReachesOrIsAboveCapacityLimit()) {
            removeOldestHistoryEntry();
        }
    }

    private void removeOldestHistoryEntry() {
        this.scoreHistoryEntries.remove(this.scoreHistoryEntries.size()-1);
    }

    private boolean historyReachesOrIsAboveCapacityLimit() {
        return this.scoreHistoryEntries.size() >= MAX_HISTORY_LENGTH;
    }

    @Exported
    public List<ScoreHistoryEntry> getMostRecentScores() {
        if(this.scoreHistoryEntries == null) {
            return Collections.emptyList();
        }
        return Lists.newLinkedList(this.scoreHistoryEntries);
    }



    @Override
    public String toString() {
        return String.format("UserScoreProperty [isNotParticipatingInGame=%s, score=%s, user=%s, scoreHistory=%s]", isNotParticipatingInGame, score, user, scoreHistoryEntries); //$NON-NLS-1$
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isNotParticipatingInGame ? 1231 : 1237);
        long temp;
        temp = Double.doubleToLongBits(score);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + scoreHistoryEntries.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof UserScoreProperty))
            return false;
        UserScoreProperty other = (UserScoreProperty) obj;
        if (isNotParticipatingInGame != other.isNotParticipatingInGame)
            return false;
        if (Double.doubleToLongBits(score) != Double.doubleToLongBits(other.score))
            return false;
        if (!Objects.equal(this.scoreHistoryEntries, other.scoreHistoryEntries))
            return false;
        return true;
    }
}
