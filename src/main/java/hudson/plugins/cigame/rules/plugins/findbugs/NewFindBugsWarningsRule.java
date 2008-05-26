package hudson.plugins.cigame.rules.plugins.findbugs;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.findbugs.FindBugsResultAction;

public class NewFindBugsWarningsRule implements Rule {

    public RuleResult evaluate(AbstractBuild<?, ?> build) {
        double points = 0;
        if (build.getResult().isBetterOrEqualTo(Result.UNSTABLE)) {
            List<FindBugsResultAction> actions = build.getActions(hudson.plugins.findbugs.FindBugsResultAction.class);
            for (FindBugsResultAction action : actions) {
                if (action.getPreviousResultAction() != null) {
                    points -= action.getResult().getNewWarnings().size();
                }
            }
        }
        return new RuleResult(points, String.format("%f new findbugs warnings were found", points));
    }

    public String getName() {
        return "New Findbugs warnings";
    }

}
