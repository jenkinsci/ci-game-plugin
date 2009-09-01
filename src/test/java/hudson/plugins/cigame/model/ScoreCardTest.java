package hudson.plugins.cigame.model;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;

import hudson.model.AbstractBuild;

import org.junit.Test;

public class ScoreCardTest {

    @Test(expected=IllegalStateException.class)
    public void testIllegalStateThrownInGetScores() {
        ScoreCard sc = new ScoreCard();
        sc.getScores();
    }

    @Test
    public void assertThatEmptyRuleResultIsNotUsed() {
        Rule rule = mock(Rule.class);
        when(rule.evaluate(isA(AbstractBuild.class))).thenReturn(RuleResult.EMPTY_RESULT);
        ScoreCard card = new ScoreCard();
        card.record(mock(AbstractBuild.class), new RuleSet("test", Arrays.asList(new Rule[]{rule})));
        assertThat(card.getScores().size(), is(0));
    }
    
    @Test
    public void assertEmptyRuleBookDoesNotThrowIllegalException() {
        ScoreCard scoreCard = new ScoreCard();
        scoreCard.record(mock(AbstractBuild.class), new RuleBook());
        assertThat(scoreCard.getTotalPoints(), is(0d));
    }
}
