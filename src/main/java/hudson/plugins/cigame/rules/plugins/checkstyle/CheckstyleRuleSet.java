package hudson.plugins.cigame.rules.plugins.checkstyle;

import hudson.plugins.cigame.rules.plugins.PluginRuleSet;

public class CheckstyleRuleSet extends PluginRuleSet {

    public CheckstyleRuleSet() {
        super("checkstyle", "Checkstyle warnings");
    } 

    @Override
    protected void loadRules() {
        add(new DefaultCheckstyleRule(-1, 1));
    }
}
