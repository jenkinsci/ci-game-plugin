package hudson.plugins.cigame;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

import com.google.common.collect.Lists;
import hudson.plugins.cigame.model.ScoreHistoryEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

public class UserScorePropertyTest {

    public static final int ENTRY_COUNT = 15;
    UserScoreProperty property = new UserScoreProperty();

    List<ScoreHistoryEntry> entries = Lists.newArrayListWithExpectedSize(ENTRY_COUNT);

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < ENTRY_COUNT; i++) {
            entries.add(mock(ScoreHistoryEntry.class));
        }
    }

    @Test
    public void itShouldNotReturnNullWhenNoHistoryEntriesAreAdded() throws Exception {
        assertNotNull(property.getMostRecentScores());
    }

    @Test
    public void itShouldNotContainAnythingWhenNoEntriesAreAdded() throws Exception {
        assertEquals(0, property.getMostRecentScores().size());
    }

    @Test
    public void itShouldContainExactlyTheNumberOfElementsWhileBelowLimit() throws Exception {
        addHistoryEntriesToProperty(UserScoreProperty.MAX_HISTORY_LENGTH);
        assertEquals(UserScoreProperty.MAX_HISTORY_LENGTH, property.getMostRecentScores().size());
    }

    @Test
    public void itShouldContainMaxElementsWhenAboveLimits() throws Exception {
        addHistoryEntriesToProperty(ENTRY_COUNT);
        assertEquals(UserScoreProperty.MAX_HISTORY_LENGTH, property.getMostRecentScores().size());
    }

    @Test
    public void itShouldContainTheMostRecentElementsWhenAboveLimits() throws Exception {
        addHistoryEntriesToProperty(ENTRY_COUNT);
        assertTrue(entries.subList(ENTRY_COUNT - UserScoreProperty.MAX_HISTORY_LENGTH,
                ENTRY_COUNT).containsAll(property.getMostRecentScores()));
    }

    @Test
    public void itShouldOrderElementsInReverseAddOrder() throws Exception {
        addHistoryEntriesToProperty(2);
        assertEquals(entries.get(1), property.getMostRecentScores().get(0));
    }

    private void addHistoryEntriesToProperty(int numberOfEntries) {
        for (int i = 0; i < numberOfEntries; i++) {
            property.addScoreHistoryEntry(entries.get(i));
        }
    }
}
