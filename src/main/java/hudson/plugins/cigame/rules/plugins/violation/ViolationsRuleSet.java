package hudson.plugins.cigame.rules.plugins.violation;

import hudson.plugins.cigame.rules.plugins.PluginRuleSet;

public class ViolationsRuleSet extends PluginRuleSet {

    public ViolationsRuleSet() {
        super("violations", Messages.ViolationRuleSet_Title()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    protected void loadRules() {
        add(new DefaultViolationRule("pmd", Messages.ViolationRuleSet_PmdRule_Name(), -1, 1)); //$NON-NLS-1$ //$NON-NLS-2$
        add(new DefaultViolationRule("pylint", Messages.ViolationRuleSet_PylintRule_Name(), -1, 1)); //$NON-NLS-1$ //$NON-NLS-2$
        add(new DefaultViolationRule("cpd", Messages.ViolationRuleSet_CpdRule_Name(), -5, 5)); //$NON-NLS-1$ //$NON-NLS-2$
        add(new DefaultViolationRule("checkstyle", Messages.ViolationRuleSet_CheckstyleRule_Name(), -1, 1)); //$NON-NLS-1$ //$NON-NLS-2$
        add(new DefaultViolationRule("findbugs", Messages.ViolationRuleSet_FindBugsRule_Name(), -1, 1)); //$NON-NLS-1$ //$NON-NLS-2$
        add(new DefaultViolationRule("fxcop", Messages.ViolationRuleSet_FxcopRule_Name(), -1, 1)); //$NON-NLS-1$ //$NON-NLS-2$
        add(new DefaultViolationRule("simian", Messages.ViolationRuleSet_SimianRule_Name(), -5, 5)); //$NON-NLS-1$ //$NON-NLS-2$
        add(new DefaultViolationRule("stylecop", Messages.ViolationRuleSet_StylecopRule_Name(), -1, 1)); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
