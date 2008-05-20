package hudson.plugins.cigame.rules.plugins.warnings;

import hudson.plugins.cigame.rules.plugins.PluginRuleSet;
import hudson.plugins.warnings.util.model.Priority;

public class WarningsRuleSet extends PluginRuleSet {

    public WarningsRuleSet() {
        super("warnings", "Compiler warnings");
    } 

    @Override
    protected void loadRules() {
        add(new DefaultWarningsRule(-5, 5));
    }
}
