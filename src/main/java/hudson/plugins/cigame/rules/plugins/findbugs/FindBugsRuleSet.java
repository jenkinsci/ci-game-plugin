package hudson.plugins.cigame.rules.plugins.findbugs;

import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.rules.plugins.PluginRuleSet;

public class FindBugsRuleSet extends PluginRuleSet {

    public FindBugsRuleSet() {
        super("findbugs", Messages.FindBugsRuleSet_Title()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    protected void loadRules() {
        add(new NewFindBugsWarningsRule(Priority.HIGH, -5));
        add(new NewFindBugsWarningsRule(Priority.NORMAL, -3));
        add(new NewFindBugsWarningsRule(Priority.LOW, -1));
        add(new FixedFindBugsWarningsRule(Priority.HIGH, 5));
        add(new FixedFindBugsWarningsRule(Priority.NORMAL, 3));
        add(new FixedFindBugsWarningsRule(Priority.LOW, 1));
    }

}
