package hudson.plugins.cigame.rules.plugins.findbugs;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.findbugs.FindBugsResult;
import hudson.plugins.findbugs.FindBugsResultAction;
import hudson.plugins.findbugs.util.model.Priority;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class FixedFindBugsWarningsRuleTest {
    
    @Test
    public void assertFailedBuildsIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);

        FixedFindBugsWarningsRule rule = new FixedFindBugsWarningsRule(Priority.LOW, 100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }
    
    @Test
    public void assertNoPreviousBuildIsWorthZeroPoints() { 
        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getResult()).thenReturn(Result.FAILURE);
        when(build.getPreviousBuild()).thenReturn(null);

        FixedFindBugsWarningsRule rule = new FixedFindBugsWarningsRule(Priority.LOW, 100);
        RuleResult ruleResult = rule.evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be zero", ruleResult.getPoints(), is((double) 0));
    }
    
    @Test
    public void assertIfPreviousBuildFailedResultIsWorthZeroPoints() {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractBuild previousBuild = mock(AbstractBuild.class);
        when(build.getPreviousBuild()).thenReturn(previousBuild);
        when(build.getResult()).thenReturn(Result.SUCCESS);
        when(previousBuild.getResult()).thenReturn(Result.FAILURE);
        FindBugsResult result = mock(FindBugsResult.class);
        FindBugsResult previosResult = mock(FindBugsResult.class);
        FindBugsResultAction action = new FindBugsResultAction(build, null, result);
        FindBugsResultAction previousAction = new FindBugsResultAction(previousBuild,null, previosResult);
        when(build.getActions(FindBugsResultAction.class)).thenReturn(Arrays.asList(action));
        when(build.getAction(FindBugsResultAction.class)).thenReturn(action);
        when(previousBuild.getAction(FindBugsResultAction.class)).thenReturn(previousAction);
        when(previousBuild.getActions(FindBugsResultAction.class)).thenReturn(new ArrayList<FindBugsResultAction>());
        
        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(5);
        when(previosResult.getNumberOfAnnotations(Priority.LOW)).thenReturn(10);

        RuleResult ruleResult = new FixedFindBugsWarningsRule(Priority.LOW, -4).evaluate(build);
        assertNotNull("Rule result must not be null", ruleResult);
        assertThat("Points should be 0", ruleResult.getPoints(), is(0d));
    }
}
