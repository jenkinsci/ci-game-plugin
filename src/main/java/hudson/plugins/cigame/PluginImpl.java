package hudson.plugins.cigame;

import hudson.Plugin;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.UserProperties;
import hudson.model.UserPropertyDescriptor;
import hudson.tasks.Publisher;

/**
 * Entry point of the CI Game plugin.
 */
public class PluginImpl extends Plugin {
    
    private static final GameDescriptor DESCRIPTOR = new GameDescriptor(); 
    
    @Override
    public void start() throws Exception {
        Publisher.PUBLISHERS.addRecorder(DESCRIPTOR);
        UserProperties.LIST.add(UserScorePropertyDescriptor.INSTANCE);
        Hudson.getInstance().getActions().add(new LeaderBoardAction());
        /*List<UserInfo> users = Hudson.getInstance().getPeople().users;
        System.out.println("USERS = " + users.size());
        for (UserInfo userInfo : users) {
			UserScoreProperty property = userInfo.getUser().getProperty(UserScoreProperty.class);
			if (property != null) {
				Hudson.getInstance().getActions().add(new LeaderBoardAction());
				break;
			}
		}
        */
    }

    /**
     * Returns the one and only descriptor.
     * @return the one and only descriptor.
     */
    static Descriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;        
    }
}
