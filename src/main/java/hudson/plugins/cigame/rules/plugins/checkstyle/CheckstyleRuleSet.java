package hudson.plugins.cigame.rules.plugins.checkstyle;

import hudson.plugins.cigame.rules.plugins.PluginRuleSet;
import hudson.plugins.warnings.util.model.Priority;

public class CheckstyleRuleSet extends PluginRuleSet {

    public CheckstyleRuleSet() {
        super("checkstyle", "Checkstyle warnings");
    } 

    @Override
    protected void loadRules() {
        add(new DefaultCheckstyleRule(-5, 5));
    }
}
