package hudson.plugins.cigame.rules.plugins.pmd;

import hudson.plugins.cigame.rules.plugins.PluginRuleSet;

public class PmdRuleSet extends PluginRuleSet {

    public PmdRuleSet() {
        super("pmd", "PMD warnings");
    }

    @Override
    protected void loadRules() {
        add(new DefaultPmdRule());
    }
}
