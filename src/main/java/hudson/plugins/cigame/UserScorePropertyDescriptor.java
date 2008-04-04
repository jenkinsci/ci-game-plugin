package hudson.plugins.cigame;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;

/**
 * Descriptor for the {@link UserScoreProperty}.
 * 
 * @author Erik Ramfelt
 */
public class UserScorePropertyDescriptor extends UserPropertyDescriptor {

	public static final UserScorePropertyDescriptor INSTANCE = new UserScorePropertyDescriptor();
	
	public UserScorePropertyDescriptor() {
		super(UserScoreProperty.class);
	}
	
	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public UserProperty newInstance(StaplerRequest req, JSONObject formData)
			throws FormException {
		return new UserScoreProperty(formData.getDouble("game.score"));
	}

	@Override
	public UserProperty newInstance(User arg0) {
		return new UserScoreProperty();
	}
}
