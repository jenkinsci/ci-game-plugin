package hudson.plugins.cigame;

import hudson.model.User;

import org.jvnet.hudson.test.HudsonTestCase;

import com.gargoylesoftware.htmlunit.html.HtmlForm;

public class UserScorePropertyDescriptorIntegrationTest extends HudsonTestCase {

    public void testConfiguringWorksForNewUser() throws Exception {
        HtmlForm userConfigurationForm = new WebClient().goTo(User.get("test").getUrl() + "/configure").getFormByName("config");
        submit(userConfigurationForm);
    }
}
