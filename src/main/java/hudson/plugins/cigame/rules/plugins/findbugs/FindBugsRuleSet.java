package hudson.plugins.cigame.rules.plugins.findbugs;

import hudson.plugins.cigame.rules.plugins.PluginRuleSet;

public class FindBugsRuleSet extends PluginRuleSet {

    public FindBugsRuleSet() {
        super("findbugs", "Find bugs");
    }

    @Override
    protected void loadRules() {
        add(new NewFindBugsWarningsRule());
        add(new FixedFindBugsWarningsRule());
    }

}
