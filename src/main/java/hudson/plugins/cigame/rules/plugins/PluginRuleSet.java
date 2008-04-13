
package hudson.plugins.cigame.rules.plugins;

import hudson.model.Hudson;
import hudson.plugins.cigame.model.RuleSet;

/**
 * Rule set for plugins rules that will check if the plugin is installed or not.
 */
public class PluginRuleSet extends RuleSet {

	private String pluginName;
	
	/**
	 * Construct a rule set for a plugin.
	 * @param pluginName the name of the plugin as it is known by Hudson.
	 * @param name the text name of the rule set, as it will be displayed.
	 */
	public PluginRuleSet(String pluginName, String name) {
		super(name);
		this.pluginName = pluginName;
	}

	/**
	 * Returns if the plugin is installed or not.
	 * @return true, if the plugin is installed; false otherwise.
	 */
	@Override
	public boolean isAvailable() {
		return (Hudson.getInstance().getPlugin(pluginName) != null);
	}
}
