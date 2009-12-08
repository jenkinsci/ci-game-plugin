package hudson.plugins.cigame.rules.plugins.opentasks;

import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.cigame.rules.plugins.PluginRuleSet;

public class OpenTasksRuleSet extends PluginRuleSet {

    public OpenTasksRuleSet() {
        super("tasks", Messages.OpenTasksRuleSet_Title()); //$NON-NLS-1$
    } 

    @Override
    protected void loadRules() {
        add(new DefaultOpenTasksRule(Priority.HIGH, -5, 5));
        add(new DefaultOpenTasksRule(Priority.NORMAL, -3, 3));
        add(new DefaultOpenTasksRule(Priority.LOW, -1, 1));
    }
}
