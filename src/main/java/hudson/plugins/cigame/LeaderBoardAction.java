package hudson.plugins.cigame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.User;
import hudson.security.ACL;
import hudson.security.AccessControlled;
import hudson.security.Permission;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Leader board for users participaing in the game.
 * 
 * @author Erik Ramfelt
 */
@ExportedBean(defaultVisibility = 999)
public class LeaderBoardAction implements Action, AccessControlled {

    private static final long serialVersionUID = 1L;

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
     * 
     * @return list containing users.
     */
    @Exported
    public List<UserScore> getUserScores() {
        ArrayList<UserScore> list = new ArrayList<UserScore>();

        for (User user : User.getAll()) {
            UserScoreProperty property = user.getProperty(UserScoreProperty.class);
            if ((property != null) && property.isParticipatingInGame()) {
                list.add(new UserScore(property.getUser(), property.getScore()));
            }
        }

        Collections.sort(list, new Comparator<UserScore>() {
            public int compare(UserScore o1, UserScore o2) {
                if (o1.score < o2.score)
                    return 1;
                if (o1.score > o2.score)
                    return -1;
                return 0;
            }
        });

        return list;
    }

    public void doResetScores( StaplerRequest req, StaplerResponse rsp ) throws IOException {
        if (Hudson.getInstance().getACL().hasPermission(Hudson.ADMINISTER)) {
            for (User user : User.getAll()) {
                UserScoreProperty property = user.getProperty(UserScoreProperty.class);
                if (property != null) {
                    property.setScore(0);
                    user.save();
                }
            }
        }
        rsp.sendRedirect2(req.getContextPath());
    }
    
    @ExportedBean(defaultVisibility = 999)
    public class UserScore {
        private User user;
        private double score;

        public UserScore(User user, double score) {
            super();
            this.user = user;
            this.score = score;
        }

        @Exported
        public User getUser() {
            return user;
        }

        @Exported
        public double getScore() {
            return score;
        }
    }

    public ACL getACL() {
        return Hudson.getInstance().getACL();
    }

    public void checkPermission(Permission p) {
        getACL().checkPermission(p);
    }

    public boolean hasPermission(Permission p) {
        return getACL().hasPermission(p);
    }

}
