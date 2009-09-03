package hudson.plugins.cigame;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.plugins.cigame.model.RuleBook;
import hudson.plugins.cigame.model.RuleSet;
import hudson.plugins.cigame.rules.build.BuildRuleSet;
import hudson.plugins.cigame.rules.plugins.checkstyle.CheckstyleRuleSet;
import hudson.plugins.cigame.rules.plugins.findbugs.FindBugsRuleSet;
import hudson.plugins.cigame.rules.plugins.opentasks.OpenTasksRuleSet;
import hudson.plugins.cigame.rules.plugins.pmd.PmdRuleSet;
import hudson.plugins.cigame.rules.plugins.violation.ViolationsRuleSet;
import hudson.plugins.cigame.rules.plugins.warnings.WarningsRuleSet;
import hudson.plugins.cigame.rules.unittesting.UnitTestingRuleSet;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

@Extension
public class GameDescriptor extends BuildStepDescriptor<Publisher> {

    public static final String ACTION_LOGO_LARGE = "/plugin/ci-game/icons/game-32x32.png"; //$NON-NLS-1$
    public static final String ACTION_LOGO_MEDIUM = "/plugin/ci-game/icons/game-22x22.png"; //$NON-NLS-1$
    
    private transient RuleBook rulebook;
    private boolean namesAreCaseSensitive = true;

    public GameDescriptor() {
        super(GamePublisher.class);
        load();
    }

    /**
     * Returns the default rule book
     * 
     * @return the rule book that is configured for the game.
     */
    public RuleBook getRuleBook() {
        if (rulebook == null) {
            rulebook = new RuleBook();

            addRuleSetIfAvailable(rulebook, new BuildRuleSet());
            addRuleSetIfAvailable(rulebook, new UnitTestingRuleSet());
            addRuleSetIfAvailable(rulebook, new OpenTasksRuleSet());
            addRuleSetIfAvailable(rulebook, new ViolationsRuleSet());
            addRuleSetIfAvailable(rulebook, new PmdRuleSet());
            addRuleSetIfAvailable(rulebook, new FindBugsRuleSet());
            addRuleSetIfAvailable(rulebook, new WarningsRuleSet());
            addRuleSetIfAvailable(rulebook, new CheckstyleRuleSet());
        }
        return rulebook;
    }

    private void addRuleSetIfAvailable(RuleBook book, RuleSet ruleSet) {
        if (ruleSet.isAvailable()) {
            book.addRuleSet(ruleSet);
        }
    }

    @Override
    public String getDisplayName() {
        return Messages.Plugin_Title();
    }

    @Override
    public GamePublisher newInstance(StaplerRequest req, JSONObject formData)
            throws hudson.model.Descriptor.FormException {
        return new GamePublisher();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }

    public boolean getNamesAreCaseSensitive() {
        return namesAreCaseSensitive;
    }

    public void setNamesAreCaseSensitive(boolean namesAreCaseSensitive) {
        this.namesAreCaseSensitive = namesAreCaseSensitive;
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> arg0) {
        return true;
    }
}
