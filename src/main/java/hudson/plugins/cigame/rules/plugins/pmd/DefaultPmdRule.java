package hudson.plugins.cigame.rules.plugins.pmd;

import java.util.List;

import hudson.model.AbstractBuild;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.pmd.PmdResult;
import hudson.plugins.pmd.PmdResultAction;

public class DefaultPmdRule implements Rule {

    public double evaluate(AbstractBuild<?, ?> build) {
        List<PmdResultAction> actions = build.getActions(PmdResultAction.class);
        for (PmdResultAction action : actions) {
            PmdResultAction previousResultAction = action.getPreviousResultAction();
            if (previousResultAction != null) {
                PmdResult pmdResult = action.getResult();
                if (pmdResult.getDelta() > 0) {
                    return -1;
                } else if (pmdResult.getDelta() < 0) {
                    return 1;
                }
            }
        }
        return 0;
    }

    public String getName() {
        return "PMD warnings";
    }

}
