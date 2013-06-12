package hudson.plugins.cigame.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import hudson.model.Job;
import hudson.model.Run;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScoreHistoryEntryTest {

    private ScoreHistoryEntry entry = new ScoreHistoryEntry();

    @Mock
    @SuppressWarnings("rawtypes")
    TestRun mockRun1;

    @Mock
    @SuppressWarnings("rawtypes")
    TestRun mockRun2;

    @Before
    public void setUp() throws Exception {
        when(mockRun1.getExternalizableId()).thenReturn("MockRun#1");
        when(mockRun1.getNumber()).thenReturn(1);
        when(mockRun1.compareTo(Mockito.any(TestRun.class))).thenCallRealMethod();
        when(mockRun2.getExternalizableId()).thenReturn("MockRun#2");
        when(mockRun2.getNumber()).thenReturn(2);
        when(mockRun2.compareTo(Mockito.any(TestRun.class))).thenCallRealMethod();
        fillEntry();
    }

    private void fillEntry() throws IOException {
        this.entry.setAwardedScore(3.0);
        this.entry.setAwardingRuns(ImmutableList.of((Run<?,?>)mockRun1, (Run<?,?>)mockRun2));
    }

    @Test
    public void theAwardedScoreStringShouldBePrefixedByPlusSignForPositiveScores() throws Exception {
        entry.setAwardedScore(3.0);
        assertEquals('+', entry.getAwardedScoreString().charAt(0));
    }

    @Test
    public void theAwardedScoreStringShouldBePrefixedByMinusSignForNegativeScores() throws Exception {
        entry.setAwardedScore(-0.5);
        assertEquals('-', entry.getAwardedScoreString().charAt(0));
    }

    @Test
    public void theAwardedRunsContains() throws Exception {
        Set<TestRun> expected = ImmutableSet.of(mockRun2, mockRun1);
        assertEquals(expected, entry.getAwardingRuns());
    }

    @Test
    public void theAwardedRunsShouldBeSortedFromNewestToOldest() throws Exception {
        Set<TestRun> expected = ImmutableSet.of(mockRun2, mockRun1);
        Iterator<TestRun> expectedIter = expected.iterator();
        Iterator<Run<?, ?>> actualIter = entry.getAwardingRuns().iterator();
        while(expectedIter.hasNext() && actualIter.hasNext()) {
            assertEquals(expectedIter.next(), actualIter.next());
        }
        assertFalse(expectedIter.hasNext());
        assertFalse(actualIter.hasNext());
    }

    /*
    * Strictly speaking, this is not really a unit-test because it does involve XStream directly.
    * Since it runs very fast, we use it here anyway.
    */
    @Test
    public void itCanMarshal() throws Exception {
        final String marshaled = marshal();
        assertNotNull(marshaled);
        assertTrue(marshaled.length() > 0);
    }

    /*
     * Strictly speaking, this is not really a unit-test because it does involve XStream directly.
     * Since it runs very fast, we use it here anyway.
     */
    @Test
    public void aMarshalledScoreEntryHasAllInfoInIt() throws Exception {
        final String marshalled = marshal();
        assertTrue("score is not contained", marshalled.contains(Double.toString(this.entry.getAwardedScore())));
        assertTrue("run1 is not contained", marshalled.contains(mockRun1.getExternalizableId()));
        assertTrue("run2 is not contained", marshalled.contains(mockRun2.getExternalizableId()));
    }

    /*
     * Strictly speaking, this is not really a unit-test because it does involve XStream directly.
     * Since it runs very fast, we use it here anyway.
     */
    @Test
    public void itCanUnmarshal() throws Exception {
        final String marshaled = marshal();
        ScoreHistoryEntry unmarshaled = umarshal(marshaled);
        assertNotNull(unmarshaled);
    }

    /*
     * Strictly speaking, this is not really a unit-test because it does involve XStream directly.
     * Since it runs very fast, we use it here anyway.
     */
    @Test
    public void itCorrectlyMarshalsAndUnmarshalsAnEntry() throws Exception {
        ScoreHistoryEntry unmarshaled = umarshal(marshal());
        assertEquals(this.entry, unmarshaled);
    }

    private ScoreHistoryEntry umarshal(String marshaled) {
        XStream xStream = createXStream(new TestRunCreationStrategy());
        return (ScoreHistoryEntry) xStream.fromXML(marshaled);
    }

    private String marshal() {
        XStream xStream = createXStream(null);
        String xml = xStream.toXML(this.entry);
        return xml;
    }

    private XStream createXStream(ScoreHistoryEntry.RunCreationStrategy strategy) {
        XStream xStream = new XStream(new DomDriver());
        xStream.setClassLoader(getClass().getClassLoader());
        ScoreHistoryEntry.ConverterImpl converter;
        if(strategy != null){
            converter = new ScoreHistoryEntry.ConverterImpl(strategy);
        } else{
            converter = new ScoreHistoryEntry.ConverterImpl();
        }
        xStream.registerConverter(converter);
        return xStream;
    }

    private class TestRunCreationStrategy implements ScoreHistoryEntry.RunCreationStrategy {

        public Run<?, ?> createRunFromExternalId(String externalId) {
            if(externalId.equals(mockRun1.getExternalizableId())) {
                return mockRun1;
            }
            if(externalId.equals(mockRun2.getExternalizableId())) {
                return mockRun2;
            }
            throw new IllegalStateException("Unknown run resolution: " + externalId);
        }
    }

    public static abstract class TestRun<JobT extends Job<JobT,RunT>,RunT extends Run<JobT,RunT>> extends Run<JobT,RunT> {

        protected TestRun(JobT job) throws IOException {
            super(job);
        }

        @Override
        public int compareTo(RunT that) {
            //we implement a simple version of compareTo to make sure that the ScoreHistoryEntry can differ between two instances
            return Integer.valueOf(getNumber()).compareTo(that.getNumber());
        }
    }

}
