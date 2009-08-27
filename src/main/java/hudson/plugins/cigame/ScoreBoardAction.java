package hudson.plugins.cigame;

import hudson.model.Action;

/**
 * Score card for a job.
 * 
 * @author Erik Ramfelt
 */
public class ScoreBoardAction implements Action {

    private static final long serialVersionUID = 1L;

    public String getDisplayName() {
        return Messages.Scorecard_Title();
    }

    public String getIconFileName() {
        return "Scorecard.gif"; //$NON-NLS-1$
    }

    public String getUrlName() {
        return "cigame"; //$NON-NLS-1$
    }

}
