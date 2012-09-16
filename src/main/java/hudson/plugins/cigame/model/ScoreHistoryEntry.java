package hudson.plugins.cigame.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import hudson.model.AbstractBuild;
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
    private SortedSet<Run> awardingRuns;

    /*
     * The score that has been awarded.
     */
    private double awardedScore;


    public ScoreHistoryEntry() {
    }

    public ScoreHistoryEntry(Collection<? extends Run> awardingRuns, double awardedScore) {
        this.awardingRuns = createEmptyRunSet();
        this.awardingRuns.addAll(awardingRuns);
        this.awardedScore = awardedScore;
    }

    private static SortedSet<Run> createEmptyRunSet() {
        return Sets.newTreeSet();
    }

    public Set<Run> getAwardingRuns() {
        return awardingRuns;
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

    public static ScoreHistoryEntry fromScoreAward(@Nonnull List<? extends Run> accountableBuilds, double accountedScore) {
        return new ScoreHistoryEntry(accountableBuilds, accountedScore);
    }

    public static final class ConverterImpl implements Converter {
        public ConverterImpl() {
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
            List<Run> awardingRuns = Lists.newArrayList();

            while(reader.hasMoreChildren()) {
                reader.moveDown();
                String externalId = reader.getValue();
                awardingRuns.add(Run.fromExternalizableId(externalId));
                reader.moveUp();
            }
            reader.moveUp();
            return ScoreHistoryEntry.fromScoreAward(awardingRuns, score);
        }
    }
}
