package hudson.plugins.cigame.plugins.findbugs;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.findbugs.FindBugsResultAction;

public class FixedFindBugsWarningsRule implements Rule {

    public double evaluate(AbstractBuild<?, ?> build) {
        double points = 0;
        if (build.getResult().isBetterOrEqualTo(Result.UNSTABLE)) {
            List<FindBugsResultAction> actions = build.getActions(hudson.plugins.findbugs.FindBugsResultAction.class);
            for (FindBugsResultAction action : actions) {
                if (action.getPreviousResultAction() != null) {
                    points += action.getResult().getFixedWarnings().size();
                }
            }
        }
        return points;
    }
    
    public String getName() {
        return "Fixed Findbugs warnings";
    }
}
