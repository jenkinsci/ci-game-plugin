package hudson.plugins.cigame;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.jvnet.hudson.test.HudsonTestCase;

import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;

public class GameDescriptorIntegrationTest extends HudsonTestCase {

    public void testThatSettingCaseInsensitiveFlagWorks() throws Exception {
        GameDescriptor descriptor = hudson.getDescriptorByType(GameDescriptor.class);
        WebClient webClient = new WebClient();
        webClient.setThrowExceptionOnScriptError(false);
        
        HtmlForm form = webClient.goTo("configure").getFormByName("config");
        assertThat(form.getInputByName("_.namesAreCaseSensitive").isChecked(), is(true));
        form.getInputByName("_.namesAreCaseSensitive").setChecked(false);
        form.submit((HtmlButton)last(form.getHtmlElementsByTagName("button")));
        assertThat(descriptor.getNamesAreCaseSensitive(), is(false));
        
        form = webClient.goTo("configure").getFormByName("config");
        assertThat(form.getInputByName("_.namesAreCaseSensitive").isChecked(), is(false));
        form.getInputByName("_.namesAreCaseSensitive").setChecked(true);
        form.submit((HtmlButton)last(form.getHtmlElementsByTagName("button")));
        assertThat(descriptor.getNamesAreCaseSensitive(), is(true));
    }
}
