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
import hudson.plugins.cigame.rules.plugins.violation.DefaultViolationRule;
import hudson.tasks.Publisher;

public class GameDescriptor extends Descriptor<Publisher> {

    public static final String ACTION_LOGO_LARGE = "/plugin/ci-game/icons/game-32x32.png";
    public static final String ACTION_LOGO_MEDIUM = "/plugin/ci-game/icons/game-22x22.png";
    
    protected GameDescriptor() {
        super(GamePublisher.class);
    }
    
    /**
     * Returns the default rule book
     * @return the rule book that is configured for the game.
     */
    public RuleBook getRuleBook() {        
    	RuleSet ruleset = new RuleSet("Basic ruleset", new LinkedList<Rule>());
        ruleset.add(new BuildResultRule());
        ruleset.add(new IncreasingFailedTestsRule());
        ruleset.add(new IncreasingPassedTestsRule());
        
        PluginRuleSet violationsPluginSet = new PluginRuleSet("violations", "Violations");
        violationsPluginSet.add(new DefaultViolationRule("pmd", "PMD violation", -1, 1));
        violationsPluginSet.add(new DefaultViolationRule("pylint", "pylint violation", -1, 1));
        violationsPluginSet.add(new DefaultViolationRule("cpd", "CPD violation", -5, 5));
        violationsPluginSet.add(new DefaultViolationRule("checkstyle", "Checkstyle violation", -5, 5));
        violationsPluginSet.add(new DefaultViolationRule("findbugs", "FindBugs violation", -5, 5));
        violationsPluginSet.add(new DefaultViolationRule("fxcop", "FXCop violation", -5, 5));
        
    	RuleBook book = new RuleBook();
        book.addRuleSet(ruleset);
        if (violationsPluginSet.isAvailable()) {
        	book.addRuleSet(violationsPluginSet);
        }
        return book;
    }

    @Override
    public String getDisplayName() {
        return "Continuous Integration Game";
    }

    @Override
    public Publisher newInstance(StaplerRequest req, JSONObject formData) throws hudson.model.Descriptor.FormException {
        return new GamePublisher();
    }    
}
