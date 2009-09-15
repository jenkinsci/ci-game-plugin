package hudson.plugins.cigame;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.User;
import hudson.plugins.cigame.model.Rule;
import hudson.plugins.cigame.model.RuleBook;
import hudson.plugins.cigame.model.RuleResult;
import hudson.plugins.cigame.model.RuleSet;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;

import org.junit.Test;
import org.jvnet.hudson.test.Bug;

@SuppressWarnings("unchecked")
public class GamePublisherTest {

    @Test
    public void assertScoreCardActionIsAddedToBuild() throws Exception {
        AbstractBuild build = mock(AbstractBuild.class);
        List<Action> actions = mock(List.class);
        when(build.getActions()).thenReturn(actions);

        assertThat(new GamePublisher().perform(build, new RuleBook(), true), is(false));
        
        verify(build).getActions();
        verify(actions).add(isA(ScoreCardAction.class));
        verify(build).getChangeSet();
        verifyNoMoreInteractions(build);
    }

    @Test
    public void assertPointsAreToExistingUserScoreProperty() throws Exception {
        AbstractBuild build = mock(AbstractBuild.class);
        UserScoreProperty userScoreProperty = new UserScoreProperty(10, true);
        mockChangeSetInBuild(build, createUser(userScoreProperty));

        assertThat(new GamePublisher().perform(build, createRuleBook(5d), true), is(true));
        assertThat(userScoreProperty.getScore(), is(15d));
    }

    @Test
    public void assertUserScorePropertyIsAddedToUserThatDoesNotHaveIt() throws Exception {
        AbstractBuild build = mock(AbstractBuild.class);
        User userWithoutProperty = createUser(null);        
        mockChangeSetInBuild(build, userWithoutProperty);

        assertThat(new GamePublisher().perform(build, createRuleBook(5d), true), is(true));
        verify(userWithoutProperty).addProperty(new UserScoreProperty(5, true));
    }

    @Bug(4470)
    @Test
    public void assertThatUserDoesNotReciveDoublePointsIfUserExistInSeveralChangeSetEntries() throws Exception {
        AbstractBuild build = mock(AbstractBuild.class);
        UserScoreProperty property = new UserScoreProperty(10d, true);
        User user = createUser(property);        
        mockChangeSetInBuild(build, user, user);

        assertThat(new GamePublisher().perform(build, createRuleBook(5d), true), is(true));
        assertThat(property.getScore(), is(15d));
    }
    
    @Test
    public void assertUsersNamesWithDifferentCasingIsReportedAsPointsForOneUser() throws Exception {
        AbstractBuild build = mock(AbstractBuild.class);
        UserScoreProperty propertyOne = new UserScoreProperty(10, true);
        UserScoreProperty propertyTwo = new UserScoreProperty(20, true);
        mockChangeSetInBuild(build, createUser(propertyOne, "name"), createUser(propertyTwo, "NAME"));

        assertThat(new GamePublisher().perform(build, createRuleBook(5d), false), is(true));
        assertThat(propertyOne.getScore(), is(15d));
        assertThat("Points were added to both users", propertyTwo.getScore(), is(20d));
    }
    
//    @Test
//    public void assertUsersAreLookedUpCaseInsensitive() throws Exception {
//        AbstractBuild build = mock(AbstractBuild.class);
//        UserScoreProperty propertyOne = new UserScoreProperty(10, true);
//        User existingUser = createUser(propertyOne, "name");
//        mockChangeSetInBuild(build, createUser(null, "NAME"));
//
//        assertThat(new GamePublisher().perform(build, createRuleBook(15d), false), is(true));
//        assertThat(propertyOne.getScore(), is(25d));
//    }

    private RuleBook createRuleBook(double points) {
        return new RuleBook(new RuleSet("test", new ArrayList<Rule>(Arrays.asList(new RuleImpl(new RuleResult(points, "desc"))))));
    }

    private void mockChangeSetInBuild(AbstractBuild build, User... users) {
        ChangeLogSet changeset = mock(ChangeLogSet.class);
        when(build.getActions()).thenReturn(new ArrayList<Action>());
        List<Entry> changesetList = new ArrayList();
        for (User user : users) {
            changesetList.add(createEntry(user));
        }
        when(changeset.iterator()).thenReturn(changesetList.iterator());
        when(build.getChangeSet()).thenReturn(changeset);
    }
    
    private User createUser(UserScoreProperty property) {
        return createUser(property, "ignored-" + System.currentTimeMillis());
    }
    
    private User createUser(UserScoreProperty property, String name) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(name);
        if (property != null) {
            when(user.getProperty(UserScoreProperty.class)).thenReturn(property);
        }
        return user;
    }
    
    private Entry createEntry(User author) {
        Entry entry = mock(Entry.class);
        when(entry.getAuthor()).thenReturn(author);
        return entry;
    }
    
    private static class RuleImpl implements Rule {

        private final RuleResult ruleResult;

        public RuleImpl(RuleResult ruleResult) {
            this.ruleResult = ruleResult;
        }

        public RuleResult evaluate(AbstractBuild<?, ?> build) {
            return ruleResult;
        }

        public String getName() {
            return "impl";
        }
    }
}
