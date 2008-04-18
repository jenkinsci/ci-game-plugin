package hudson.plugins.cigame.rules.plugins.violation;

import hudson.plugins.cigame.rules.plugins.PluginRuleSet;

public class ViolationsRuleSet extends PluginRuleSet {

    public ViolationsRuleSet() {
        super("violations", "Violations");
    }

    @Override
    protected void loadRules() {
        add(new DefaultViolationRule("pmd", "PMD violation", -1, 1));
        add(new DefaultViolationRule("pylint", "pylint violation", -1, 1));
        add(new DefaultViolationRule("cpd", "CPD violation", -5, 5));
        add(new DefaultViolationRule("checkstyle", "Checkstyle violation", -1, 1));
        add(new DefaultViolationRule("findbugs", "FindBugs violation", -1, 1));
        add(new DefaultViolationRule("fxcop", "FXCop violation", -1, 1));
        add(new DefaultViolationRule("simian", "Simian violation", -5, 5));
    }
}
