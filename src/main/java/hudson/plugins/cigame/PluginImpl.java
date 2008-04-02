package hudson.plugins.cigame;

import hudson.Plugin;
import hudson.model.Descriptor;
import hudson.tasks.Publisher;

/**
 * Entry point of the CI Game plugin.
 */
public class PluginImpl extends Plugin {
    
    private static final GameDescriptor DESCRIPTOR = new GameDescriptor(); 
    
    public void start() throws Exception {
        Publisher.PUBLISHERS.addRecorder(DESCRIPTOR);
    }

    /**
     * Returns the one and only descriptor.
     * @return the one and only descriptor.
     */
    static Descriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;        
    }
}
