package hudson.plugins.cigame;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.Util;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;

/**
 * Descriptor for the {@link UserScoreProperty}.
 * 
 * @author Erik Ramfelt
 */
@Extension
public class UserScorePropertyDescriptor extends UserPropertyDescriptor {

    public UserScorePropertyDescriptor() {
        super(UserScoreProperty.class);
    }

    @Override
    public String getDisplayName() {
        return Messages.User_Property_Title();
    }
    
    /**
     * Method kept for backward compability.
     * Prior to 1.222 the JSONObject formdata was always null. This method
     * should be removed in the future.
     * @param req request coming from config.jelly
     * @return a UserScoreProperty object
     */
    private UserScoreProperty newInstanceIfJSONIsNull(StaplerRequest req) throws FormException {
        String scoreStr = Util.fixEmpty(req.getParameter("game.score")); //$NON-NLS-1$
        if (scoreStr != null) {
            return new UserScoreProperty(Double.parseDouble(scoreStr), req.getParameter("game.participatingInGame") != null, null); //$NON-NLS-1$
        }
        return new UserScoreProperty();
    }

    @Override
    public UserScoreProperty newInstance(StaplerRequest req, JSONObject formData) throws hudson.model.Descriptor.FormException {
        if (formData == null) {
            return newInstanceIfJSONIsNull(req);
        }
        if (formData.has("score")) { //$NON-NLS-1$
            return req.bindJSON(UserScoreProperty.class, formData);
        }
        return new UserScoreProperty();
    }

    @Override
    public UserProperty newInstance(User arg0) {
        return null;
    }
}
