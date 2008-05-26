package hudson.plugins.cigame.rules.plugins.findbugs;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.findbugs.FindBugsResultAction;

public class FixedFindBugsWarningsRule implements Rule {

    public RuleResult evaluate(AbstractBuild<?, ?> build) {
        int fixedWarnings = 0;
        if (build.getResult().isBetterOrEqualTo(Result.UNSTABLE)) {
            List<FindBugsResultAction> actions = build.getActions(hudson.plugins.findbugs.FindBugsResultAction.class);
            for (FindBugsResultAction action : actions) {
                if (action.getPreviousResultAction() != null) {
                    fixedWarnings += action.getResult().getFixedWarnings().size();
                }
            }
        }
        return new RuleResult(fixedWarnings, String.format("%d findbugs warnings were fixed", fixedWarnings));
    }
    
    public String getName() {
        return "Fixed Findbugs warnings";
    }
}
