package hudson.plugins.cigame.rules.plugins.pmd;

import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.rules.plugins.PluginRuleSet;

public class PmdRuleSet extends PluginRuleSet {

    public PmdRuleSet() {
        super("pmd", Messages.PmdRuleSet_Title()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    protected void loadRules() {
        add(new DefaultPmdRule(Priority.HIGH, -5, 5));
        add(new DefaultPmdRule(Priority.NORMAL, -3, 3));
        add(new DefaultPmdRule(Priority.LOW, -1, 1));
    }
}
