package hudson.plugins.cigame.rules.plugins.pmd;

import hudson.plugins.cigame.rules.plugins.PluginRuleSet;
import hudson.plugins.pmd.util.model.Priority;

public class PmdRuleSet extends PluginRuleSet {

    public PmdRuleSet() {
        super("pmd", "PMD warnings");
    }

    @Override
    protected void loadRules() {
        add(new DefaultPmdRule(Priority.HIGH, -5, 5));
        add(new DefaultPmdRule(Priority.NORMAL, -3, 3));
        add(new DefaultPmdRule(Priority.LOW, -1, 1));
    }
}
