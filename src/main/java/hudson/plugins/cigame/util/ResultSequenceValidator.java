package hudson.plugins.cigame.util;

import hudson.model.AbstractBuild;
import hudson.model.Result;

/**
 * Class that simplifies checking if a build sequence is above a certain result threshold.
 * Use the class to check for example if there has been five builds in a row that is
 * Unstable or better.
 */
public class ResultSequenceValidator {

    private final int sequenceLength;
    private final Result resultThreshold;
    
    public ResultSequenceValidator(Result resultThreshold, int sequenceLength) {
        this.resultThreshold = resultThreshold;
        this.sequenceLength = sequenceLength;
    }

    /**
     * Returns true if the build sequence meets the requirements
     * @param build latest build in sequence
     * @return true, if the build sequence conforms to the requirements set in constructor; false, otherwise.
     */
    public boolean isValid(AbstractBuild<?,?> build) {
        int buildCount = 0;
        while (buildCount < sequenceLength) {
            if ((build == null) || build.getResult().isWorseThan(resultThreshold)) {
                return false;
            }
            build = build.getPreviousBuild();
            buildCount++;
        }
        return true;
    }
}
