package hudson.plugins.cigame;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import hudson.model.User;
import hudson.plugins.cigame.LeaderBoardAction.UserScore;

import org.junit.Test;

public class LeaderBoardActionTest {

    @Test
    public void assertUserScoreAreRestWhenResettingScore() throws Exception {
        User userWithProperty = mock(User.class);
        User userWithoutProperty = mock(User.class);
        UserScoreProperty property = new UserScoreProperty(2.0, true);
        when(userWithProperty.getProperty(UserScoreProperty.class)).thenReturn(property);
        when(userWithoutProperty.getProperty(UserScoreProperty.class)).thenReturn(null);
        
        new LeaderBoardAction().doResetScores(Arrays.asList(userWithoutProperty, userWithProperty));        
        assertThat(property.getScore(), is(0d));
        
        verify(userWithoutProperty).getProperty(UserScoreProperty.class);
        verifyNoMoreInteractions(userWithoutProperty);
        verify(userWithProperty).getProperty(UserScoreProperty.class);
        verify(userWithProperty).save();
        verifyNoMoreInteractions(userWithProperty);
    }
    
    @Test
    public void assertUsernameWithDifferentCasingIsShownAsOne() throws Exception {
        User userOne = mock(User.class);
        User userTwo = mock(User.class);
        when(userOne.getId()).thenReturn("ID");
        when(userTwo.getId()).thenReturn("id");
        when(userOne.getProperty(UserScoreProperty.class)).thenReturn(new UserScoreProperty(2.0, true));
        when(userTwo.getProperty(UserScoreProperty.class)).thenReturn(new UserScoreProperty(5.0, true));
        
        List<UserScore> scores = new LeaderBoardAction().getUserScores(Arrays.asList(userOne, userTwo), false);
        assertThat(scores.size(), is(1));
        assertThat(scores.get(0).getScore(), is(2d));
    }
    
    @Test
    public void assertUsernameWithDifferentCasingIsShownAsTwo() throws Exception {
        User userOne = mock(User.class);
        User userTwo = mock(User.class);
        when(userOne.getId()).thenReturn("ID");
        when(userTwo.getId()).thenReturn("id");
        when(userOne.getProperty(UserScoreProperty.class)).thenReturn(new UserScoreProperty(5.0, true));
        when(userTwo.getProperty(UserScoreProperty.class)).thenReturn(new UserScoreProperty(2.0, true));
        
        List<UserScore> scores = new LeaderBoardAction().getUserScores(Arrays.asList(userOne, userTwo), true);
        assertThat(scores.size(), is(2));
        assertThat(scores.get(0).getScore(), is(5d));
        assertThat(scores.get(1).getScore(), is(2d));
    }
    
    @Test
    public void assertScoresListIsSortedAccordingToPoints() throws Exception {
        User userOne = mock(User.class);
        User userTwo = mock(User.class);
        when(userOne.getId()).thenReturn("Andy");
        when(userTwo.getId()).thenReturn("John");
        when(userOne.getProperty(UserScoreProperty.class)).thenReturn(new UserScoreProperty(1.0, true));
        when(userTwo.getProperty(UserScoreProperty.class)).thenReturn(new UserScoreProperty(2.0, true));
        
        List<UserScore> scores = new LeaderBoardAction().getUserScores(Arrays.asList(userOne, userTwo), true);
        assertThat(scores.size(), is(2));
        assertThat(scores.get(0).getScore(), is(2d));
        assertThat(scores.get(0).getUser().getId(), is("John"));
        assertThat(scores.get(1).getScore(), is(1d));
        assertThat(scores.get(1).getUser().getId(), is("Andy"));
    }
}
