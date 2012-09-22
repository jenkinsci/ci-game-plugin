package hudson.plugins.cigame.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import hudson.model.Run;
import org.kohsuke.stapler.export.Exported;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class ScoreHistoryEntry {

    /*
     * The runs that are the cause for us giving you the award.
     */
    private SortedSet<Run<?,?>> awardingRuns;

    /*
     * The score that has been awarded.
     */
    private double awardedScore;


    public ScoreHistoryEntry() {
    }

    public ScoreHistoryEntry(Collection<? extends Run<?,?>> awardingRuns, double awardedScore) {
        this.awardingRuns = Sets.newTreeSet(awardingRuns);
        this.awardedScore = awardedScore;
    }

    public Set<Run<?,?>> getAwardingRuns() {
        return awardingRuns;
    }

    public void setAwardingRuns(Collection<? extends Run<?,?>> awardingRuns) {
        this.awardingRuns = Sets.newTreeSet(awardingRuns);
    }

    public double getAwardedScore() {
        return awardedScore;
    }

    @Exported
    public String getAwardedScoreString() {
        if(awardedScore > 0) {
            return "+" + String.valueOf(awardedScore);
        } else {
            return String.valueOf(awardedScore);
        }
    }

    public void setAwardedScore(double awardedScore) {
        this.awardedScore = awardedScore;
    }

    public static ScoreHistoryEntry fromScoreAward(@Nonnull List<? extends Run<?,?>> accountableBuilds, double accountedScore) {
        return new ScoreHistoryEntry(accountableBuilds, accountedScore);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScoreHistoryEntry that = (ScoreHistoryEntry) o;

        if (Double.compare(that.awardedScore, awardedScore) != 0) return false;
        return Objects.equal(this.awardingRuns, that.awardingRuns);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.awardedScore, this.awardingRuns);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("awardedScore", getAwardedScoreString())
                .add("awardingRuns", awardingRuns)
                .toString();
    }

    @SuppressWarnings("unused")
    public static final class ConverterImpl implements Converter {
        private RunCreationStrategy runCreationStrategy;

        public ConverterImpl() {
            this(new DefaultRunCreationStrategy());
        }

        public ConverterImpl(RunCreationStrategy runCreationStrategy) {
            this.runCreationStrategy = runCreationStrategy;
        }

        public boolean canConvert(Class type) {
            return type==ScoreHistoryEntry.class;
        }

        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            ScoreHistoryEntry entry = (ScoreHistoryEntry) source;

            writer.startNode("score");
            writer.setValue(String.valueOf(entry.getAwardedScore()));
            writer.endNode();
            writer.startNode("accountableRuns");
            for (Run run : entry.awardingRuns) {
                writer.startNode("run");
                writer.setValue(run.getExternalizableId());
                writer.endNode();
            }
            writer.endNode();
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            double score;
            reader.moveDown();
            score = Double.valueOf(reader.getValue());
            reader.moveUp();
            reader.moveDown();
            List<Run<?,?>> awardingRuns = Lists.newArrayList();

            while(reader.hasMoreChildren()) {
                reader.moveDown();
                String externalId = reader.getValue();
                awardingRuns.add(this.runCreationStrategy.createRunFromExternalId(externalId));
                reader.moveUp();
            }
            reader.moveUp();
            return ScoreHistoryEntry.fromScoreAward(awardingRuns, score);
        }


    }

    @VisibleForTesting
    protected Run<?, ?> runFromExternalId(String externalId) {
        return Run.fromExternalizableId(externalId);
    }

    public static interface RunCreationStrategy {
        Run<?,?> createRunFromExternalId(String externalId);
    }

    public static class DefaultRunCreationStrategy implements RunCreationStrategy {

        public Run<?, ?> createRunFromExternalId(String externalId) {
            return Run.fromExternalizableId(externalId);
        }
    }
}
