package hudson.plugins.cigame.rules.plugins.checkstyle;

import hudson.plugins.cigame.rules.plugins.PluginRuleSet;

public class CheckstyleRuleSet extends PluginRuleSet {

    public CheckstyleRuleSet() {
        super("checkstyle", Messages.CheckstyleRuleSet_Title()); //$NON-NLS-1$ //$NON-NLS-2$
    } 

    @Override
    protected void loadRules() {
        add(new DefaultCheckstyleRule(-1, 1));
    }
}
