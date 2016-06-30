package hudson.plugins.cigame.rules.plugins.jacoco;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import hudson.plugins.cigame.model.Rule;

import java.util.Collection;

import org.junit.Test;

/**
 * Unit tests for {@link JacocoRuleSet}.
 *
 * @author Philip Aston
 */
public class JacocoRuleSetTest {

    @Test
    public void getRules() {
        final Collection<Rule> rules = new JacocoRuleSet().getRules();

        assertThat(rules, contains(instanceOf(DefaultJacocoRule.class)));
    }
}
