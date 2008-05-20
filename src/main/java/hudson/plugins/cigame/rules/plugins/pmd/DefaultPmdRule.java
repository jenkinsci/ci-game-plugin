package hudson.plugins.cigame.rules.plugins.pmd;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.pmd.PmdResult;
import hudson.plugins.pmd.PmdResultAction;

public class DefaultPmdRule implements Rule {

    public RuleResult evaluate(AbstractBuild<?, ?> build) {
        int delta = 0;
        List<PmdResultAction> actions = build.getActions(PmdResultAction.class);
        for (PmdResultAction action : actions) {
            PmdResultAction previousResultAction = action.getPreviousResultAction();
            if (previousResultAction != null) {
                PmdResult pmdResult = action.getResult();
                delta += pmdResult.getDelta();
            }
        }
        if (delta > 0) {
            return new RuleResult(1, String.format("%d new PMD warning(s) was found", delta));
        }
        if (delta < 0) {
            return new RuleResult(-1, String.format("%d PMD warning(s) was fixed", delta * -1));
        }
        return null;
    }

    public String getName() {
        return "PMD warnings";
    }

}
