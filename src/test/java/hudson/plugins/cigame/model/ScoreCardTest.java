package hudson.plugins.cigame.model;

import org.junit.Test;

public class ScoreCardTest {

    @Test(expected=IllegalStateException.class)
    public void testIllegalStateThrownInGetScores() {
        ScoreCard sc = new ScoreCard();
        sc.getScores();
    }

}
