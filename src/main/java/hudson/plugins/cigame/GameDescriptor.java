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

// Config page for the application (descriptor of the game plugin)
@Extension
public class GameDescriptor extends BuildStepDescriptor<Publisher> {

    public static final String ACTION_LOGO_LARGE = "/plugin/ci-game/icons/game-32x32.png"; //$NON-NLS-1$
    public static final String ACTION_LOGO_MEDIUM = "/plugin/ci-game/icons/game-22x22.png"; //$NON-NLS-1$
    
    private transient RuleBook rulebook;
    private boolean namesAreCaseSensitive = true;
    
    private int passedTestIncreasingPoints = 1;
    private int passedTestDecreasingPoints = 0;
    private int failedTestIncreasingPoints = -1;
    private int failedTestDecreasingPoints = 0;
    private int skippedTestIncreasingPoints = 0;
    private int skippedTestDecreasingPoints = 0;

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

    // config page heading
    @Override
    public String getDisplayName() {
        return Messages.Plugin_Title();
    }

    // creates a instance with form data; but this only creates empty new object
    @Override
    public GamePublisher newInstance(StaplerRequest req, JSONObject formData)
            throws hudson.model.Descriptor.FormException {
        return new GamePublisher();
    }

    // invoked when even properties are updated (global properties configured with global.jelly
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

    
    public int getPassedTestIncreasingPoints() {
        return passedTestIncreasingPoints;
    }

    
    public void setPassedTestIncreasingPoints(int passedTestIncreasingPoints) {
        this.passedTestIncreasingPoints = passedTestIncreasingPoints;
    }

    
    public int getPassedTestDecreasingPoints() {
        return passedTestDecreasingPoints;
    }

    
    public void setPassedTestDecreasingPoints(int passedTestDecreasingPoints) {
        this.passedTestDecreasingPoints = passedTestDecreasingPoints;
    }

    
    public int getFailedTestIncreasingPoints() {
        return failedTestIncreasingPoints;
    }

    
    public void setFailedTestIncreasingPoints(int failedTestIncreasingPoints) {
        this.failedTestIncreasingPoints = failedTestIncreasingPoints;
    }

    
    public int getFailedTestDecreasingPoints() {
        return failedTestDecreasingPoints;
    }

    
    public void setFailedTestDecreasingPoints(int failedTestDecreasingPoints) {
        this.failedTestDecreasingPoints = failedTestDecreasingPoints;
    }

    
    public int getSkippedTestIncreasingPoints() {
        return skippedTestIncreasingPoints;
    }

    
    public void setSkippedTestIncreasingPoints(int skippedTestIncreasingPoints) {
        this.skippedTestIncreasingPoints = skippedTestIncreasingPoints;
    }

    
    public int getSkippedTestDecreasingPoints() {
        return skippedTestDecreasingPoints;
    }

    
    public void setSkippedTestDecreasingPoints(int skippedTestDecreasingPoints) {
        this.skippedTestDecreasingPoints = skippedTestDecreasingPoints;
    }

}
