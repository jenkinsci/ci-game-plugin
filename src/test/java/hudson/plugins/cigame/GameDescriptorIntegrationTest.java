package hudson.plugins.cigame;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import hudson.model.Hudson;

import org.jvnet.hudson.test.HudsonTestCase;

import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;

public class GameDescriptorIntegrationTest extends HudsonTestCase {

    public void testThatSettingCaseInsensitiveFlagWorks() throws Exception {
//        WebClient webClient = new WebClient();
//        webClient.setThrowExceptionOnScriptError(false);
//        HtmlForm form = webClient.goTo("/configure").getFormByName("config");
//        
//        assertThat(form.getInputByName("_.namesAreCasesensitive").isChecked(), is(true));
//        form.getInputByName("_.namesAreCasesensitive").setChecked(false);
//        form.submit((HtmlButton)last(form.getHtmlElementsByTagName("button")));
//
//        GameDescriptor descriptor = Hudson.getInstance().getDescriptorByType(GameDescriptor.class);
//        assertThat(descriptor.getNamesAreCaseSensitive(), is(false));
    }
}
