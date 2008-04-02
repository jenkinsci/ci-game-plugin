package hudson.plugins.cigame;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import hudson.model.Descriptor;
import hudson.model.User;
import hudson.model.UserProperty;

@ExportedBean(defaultVisibility=999)
public class UserScoreProperty extends UserProperty {

	private double score;
	
	public UserScoreProperty() {
		score = 0;
	}
	
	public Descriptor<UserProperty> getDescriptor() {
		return UserScorePropertyDescriptor.INSTANCE;
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
}
