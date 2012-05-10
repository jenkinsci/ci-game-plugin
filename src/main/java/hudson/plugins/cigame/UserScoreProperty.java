package hudson.plugins.cigame;

import java.util.ArrayList;
import java.util.Collections;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import hudson.model.User;
import hudson.model.UserProperty;

/**
 * 
 * @author Erik Ramfelt
 */
@ExportedBean(defaultVisibility = 999)
public class UserScoreProperty extends UserProperty {

    private double score;
    private ArrayList<Double> listOfScores = new ArrayList<Double>();

    /**
     * Inversed name as default value is false when serializing from data that
     * has doesnt have the value.
     */
    private boolean isNotParticipatingInGame;

    public UserScoreProperty() {
        score = 0;
        isNotParticipatingInGame = false;
    }

    @DataBoundConstructor
    public UserScoreProperty(double score, boolean participatingInGame) {
        this.score = score;
        this.isNotParticipatingInGame = !participatingInGame;
        getListOfScores().add(score);
    }

    @Exported
    public User getUser() {
        return user;
    }

    @Exported
    public double getScore() {
        return score;
    }

    @Exported
    public ArrayList<Double> getListOfScores() {
        if (listOfScores == null) {
            listOfScores = new ArrayList<Double>();
        }

        return listOfScores;
    }

    public double getMedianScore() {
        ArrayList<Double> scoreList = getListOfScores();
        int sizeOfScoreList = scoreList.size();

        if (sizeOfScoreList == 0) {
            return 0.0;
        }

        Collections.sort(scoreList);

        if (sizeOfScoreList % 2 == 1)
            return scoreList.get((sizeOfScoreList + 1) / 2 - 1);
        else {
            double lower = scoreList.get(sizeOfScoreList / 2 - 1);
            double upper = scoreList.get(sizeOfScoreList / 2);

            return (lower + upper) / 2.0;
        }
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void incrementScore(double score) {
        setScore(getScore() + score);
        getListOfScores().add(score);
    }

    public void resetScoreList() {
        getListOfScores().clear();
    }

    @Exported
    public boolean isParticipatingInGame() {
        return !isNotParticipatingInGame;
    }

    @Override
    public String toString() {
        return String.format("UserScoreProperty [isNotParticipatingInGame=%s, score=%s, user=%s]", isNotParticipatingInGame, score, user); //$NON-NLS-1$
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isNotParticipatingInGame ? 1231 : 1237);
        long temp;
        temp = Double.doubleToLongBits(score);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        return true;
    }
}
