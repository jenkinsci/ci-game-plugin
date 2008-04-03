package hudson.plugins.cigame;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.model.Descriptor;
import hudson.model.UserProperty;
import hudson.model.Descriptor.FormException;

public class UserScorePropertyDescriptor extends Descriptor<UserProperty> {

	public static final UserScorePropertyDescriptor INSTANCE = new UserScorePropertyDescriptor();
	
	public UserScorePropertyDescriptor() {
		super(UserScoreProperty.class);
	}
	
	@Override
	public String getDisplayName() {
		return null;
	}

}
