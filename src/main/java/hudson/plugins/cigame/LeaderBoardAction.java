package hudson.plugins.cigame;

import java.util.ArrayList;
import java.util.List;

import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.plugins.cigame.model.ScoreCard;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Leader board for users participaing in the game.
 * 
 * @author Erik Ramfelt
 */
@ExportedBean(defaultVisibility=999)
public class LeaderBoardAction implements Action{

	public String getDisplayName() {
		return "Leader board";
	}

	public String getIconFileName() {
        return GameDescriptor.ACTION_LOGO_MEDIUM;
	}

	public String getUrlName() {
        return "cigame";
	}

	/**
	 * Returns the user that are participants in the ci game
	 * @return list containing users.
	 */
    @Exported
    public List<UserScore> getUserScores() {
        ArrayList<UserScore> list = new ArrayList<UserScore>();
        
        for (User user : User.getAll()) {
        	UserScoreProperty property = user.getProperty(UserScoreProperty.class);
        	if (property != null) {
        		list.add(new UserScore(property.getUser().getDisplayName(), property.getScore()));
        	}
        }
        
        return list;
    }
    

    @ExportedBean(defaultVisibility=999)
	public class UserScore {
		private String fullName;
		private double score;
		
		public UserScore(String fullName, double score) {
			super();
			this.fullName = fullName;
			this.score = score;
		}

	    @Exported		
		public String getFullName() {
			return fullName;
		}

	    @Exported	
		public double getScore() {
			return score;
		}		
	}

}
