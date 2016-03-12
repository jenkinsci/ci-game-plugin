package hudson.plugins.cigame.rules.plugins.findbugs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.findbugs.FindBugsMavenResultAction;
import hudson.plugins.findbugs.FindBugsResult;
import hudson.plugins.findbugs.FindBugsResultAction;

/**
 * Utility class to perform common tasks required by FindBugs tests.
 * 
 * @author lewisvail3
 *
 */
public class FindBugsWarningsRuleTestUtils {

    /**
     * Adds FindBugsResltActions to the passed in build
     * 
     * @param build
     * @param numberOfWarnings
     */
    public static void addFindBugsWarnings(AbstractBuild<?, ?> build, int numberOfWarnings) {
        FindBugsResult result = mock(FindBugsResult.class);
        FindBugsResultAction action = new FindBugsResultAction(build, mock(HealthDescriptor.class), result);
        when(build.getActions(FindBugsResultAction.class)).thenReturn(Arrays.asList(action));
        
        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(numberOfWarnings);
    }
    
    /**
     * Adds FindBugsMavenResultActions to the passed in build
     * @param build
     * @param numberOfWarnings
     */
    public static void addMavenFindBugsWarnings(AbstractBuild<?, ?> build, int numberOfWarnings) {
        FindBugsResult result = mock(FindBugsResult.class);
        FindBugsMavenResultAction action = new FindBugsMavenResultAction(build, mock(HealthDescriptor.class), "UTF-8", result);
        when(build.getActions(FindBugsMavenResultAction.class)).thenReturn(Arrays.asList(action));
        
        when(result.getNumberOfAnnotations(Priority.LOW)).thenReturn(numberOfWarnings);
    }
}
