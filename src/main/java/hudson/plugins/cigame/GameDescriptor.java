package hudson.plugins.cigame;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.model.Descriptor;
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
import hudson.tasks.Publisher;

public class GameDescriptor extends Descriptor<Publisher> {

    public static final String ACTION_LOGO_LARGE = "/plugin/ci-game/icons/game-32x32.png";
    public static final String ACTION_LOGO_MEDIUM = "/plugin/ci-game/icons/game-22x22.png";

    private RuleBook rulebook;

    protected GameDescriptor() {
        super(GamePublisher.class);
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
        return "Continuous Integration Game";
    }

    @Override
    public Publisher newInstance(StaplerRequest req, JSONObject formData)
            throws hudson.model.Descriptor.FormException {
        return new GamePublisher();
    }
}
