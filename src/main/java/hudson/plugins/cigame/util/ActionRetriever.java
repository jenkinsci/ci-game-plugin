package hudson.plugins.cigame.util;

import java.util.Collections;
import java.util.List;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Result;

public class ActionRetriever {
	
	public static <T extends Action> List<T> getResult(AbstractBuild<?, ?> build,
			Result resultThreshold, Class<T> actionClass) {
		if (build != null && build.getResult() != null
		    && build.getResult().isBetterOrEqualTo(resultThreshold)) {
			return build.getActions(actionClass);
		}
		return Collections.emptyList();
	}
}
