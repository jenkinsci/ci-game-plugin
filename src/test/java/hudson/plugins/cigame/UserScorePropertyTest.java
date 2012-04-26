package hudson.plugins.cigame;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class UserScorePropertyTest {

	private UserScoreProperty userScoreProperty;

	@Test
    public void assertMedianValueForNoScores() throws Exception {
		givenEmptyScoreProperty();

		thenMedianScoreShouldEqual(0d);
	}
	
	@Test
    public void assertMedianValueForUnevenAmountOfScores() throws Exception {
		givenEmptyScoreProperty();
		
		whenIncrementingScoreWith(1d);
		whenIncrementingScoreWith(-1d);
		whenIncrementingScoreWith(2d);
		whenIncrementingScoreWith(-2d);
		whenIncrementingScoreWith(3d);

		thenMedianScoreShouldEqual(1d);
	}

	@Test
    public void assertMedianValueForEvenAmountOfScores() throws Exception {
		givenEmptyScoreProperty();
		
		whenIncrementingScoreWith(1d);
		whenIncrementingScoreWith(2d);
		whenIncrementingScoreWith(-2d);
		whenIncrementingScoreWith(3d);

		thenMedianScoreShouldEqual(1.5);
	}

	private void thenMedianScoreShouldEqual(double score) {
		assertThat(userScoreProperty.getMedianScore(), is(score));
	}

	private void whenIncrementingScoreWith(double score) {
		userScoreProperty.incrementScore(score);
	}

	private void givenEmptyScoreProperty() {
		userScoreProperty = new UserScoreProperty();
	}
	
}
