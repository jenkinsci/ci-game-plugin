package hudson.plugins.cigame;

import java.util.LinkedList;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.model.Descriptor;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleBook;
import hudson.plugins.cigame.model.RuleSet;
import hudson.plugins.cigame.rules.basic.BuildResultRule;
import hudson.plugins.cigame.rules.basic.IncreasingFailedTestsRule;
import hudson.plugins.cigame.rules.basic.IncreasingPassedTestsRule;
import hudson.plugins.cigame.rules.plugins.PluginRuleSet;
import hudson.plugins.cigame.rules.plugins.opentasks.DefaultOpenTasksRule;
import hudson.plugins.cigame.rules.plugins.opentasks.OpenTasksRuleSet;
import hudson.plugins.cigame.rules.plugins.pmd.PmdRuleSet;
import hudson.plugins.cigame.rules.plugins.violation.DefaultViolationRule;
import hudson.plugins.cigame.rules.plugins.violation.ViolationsRuleSet;
import hudson.plugins.tasks.util.model.Priority;
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

            RuleSet ruleset = new RuleSet("Basic ruleset");
            ruleset.add(new BuildResultRule());
            ruleset.add(new IncreasingFailedTestsRule());
            ruleset.add(new IncreasingPassedTestsRule());

            rulebook.addRuleSet(ruleset);
            // addRuleSetIfAvailable(book, new OpenTasksRuleSet());
            // addRuleSetIfAvailable(rulebook, new ViolationsRuleSet());
            // addRuleSetIfAvailable(rulebook, new PmdRuleSet());
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
