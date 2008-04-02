package hudson.plugins.cigame;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.model.Descriptor;
import hudson.tasks.Publisher;

public class GameDescriptor extends Descriptor<Publisher> {

    public static final String ACTION_LOGO_LARGE = "/plugin/ci-game/icons/game-32x32.png";
    public static final String ACTION_LOGO_MEDIUM = "/plugin/ci-game/icons/game-22x22.png";
    
    protected GameDescriptor() {
        super(GamePublisher.class);
    }

    @Override
    public String getDisplayName() {
        return "Continuous Integration Game";
    }

    @Override
    public Publisher newInstance(StaplerRequest req, JSONObject formData) throws hudson.model.Descriptor.FormException {
        return new GamePublisher();
    }    
}
