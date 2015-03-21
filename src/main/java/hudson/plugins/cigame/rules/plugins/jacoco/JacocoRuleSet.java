package hudson.plugins.cigame.rules.plugins.jacoco;

import hudson.plugins.cigame.rules.plugins.PluginRuleSet;

public class JacocoRuleSet extends PluginRuleSet {

    public JacocoRuleSet() {
        super("jacoco", Messages.JacocoRuleSet_Title()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    protected void loadRules() {
        add(new DefaultJacocoRule(-10, +10));
    }
}
