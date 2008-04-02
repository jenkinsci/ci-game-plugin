package hudson.plugins.cigame;

import hudson.model.Action;

/**
 * Score card for a job.
 * 
 * @author Erik Ramfelt
 */
public class ScoreBoardAction implements Action {

    public String getDisplayName() {
        return "Score card";
    }

    public String getIconFileName() {
        return "Scorecard.gif";
    }

    public String getUrlName() {
        return "cigame";
    }

}
