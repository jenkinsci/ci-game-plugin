package hudson.plugins.cigame;

import hudson.model.Descriptor;
import hudson.model.UserProperty;

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
