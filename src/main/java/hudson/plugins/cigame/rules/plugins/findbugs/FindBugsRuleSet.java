package hudson.plugins.cigame.rules.plugins.findbugs;

import hudson.plugins.cigame.rules.plugins.PluginRuleSet;
import hudson.plugins.findbugs.util.model.Priority;

public class FindBugsRuleSet extends PluginRuleSet {

    public FindBugsRuleSet() {
        super("findbugs", "Find bugs");
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
