package hudson.plugins.cigame.rules.plugins.opentasks;

import hudson.plugins.cigame.rules.plugins.PluginRuleSet;
import hudson.plugins.tasks.util.model.Priority;

public class OpenTasksRuleSet extends PluginRuleSet {

    public OpenTasksRuleSet() {
        super("tasks", "Open tasks");
    }

    @Override
    protected void loadRules() {
        add(new FixedOpenTasksRule(Priority.HIGH, 5, -5));
        add(new FixedOpenTasksRule(Priority.NORMAL, 3, -3));
        add(new FixedOpenTasksRule(Priority.LOW, 1, -1));
    }
}
