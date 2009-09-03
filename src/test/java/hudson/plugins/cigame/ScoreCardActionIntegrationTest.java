package hudson.plugins.cigame;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.recipes.LocalData;

import com.gargoylesoftware.htmlunit.html.HtmlTable;

public class ScoreCardActionIntegrationTest extends HudsonTestCase {

    @LocalData
    public void testThatUsernameWithDifferentCasingIsDisplayedAsOne() throws Exception {
        hudson.getDescriptorByType(GameDescriptor.class).setNamesAreCaseSensitive(false);
        HtmlTable table = (HtmlTable) new WebClient().goTo("job/multiple-culprits/4/cigame/").getHtmlElementById("game.culprits");
        assertThat(table.getRowCount(), is(2));
    }
    
    @LocalData
    public void testThatUsernameWithDifferentCasingIsNotDisplayedAsOne() throws Exception {
        HtmlTable table = (HtmlTable) new WebClient().goTo("job/multiple-culprits/4/cigame/").getHtmlElementById("game.culprits");
        assertThat(table.getRowCount(), is(3));        
    }
}
