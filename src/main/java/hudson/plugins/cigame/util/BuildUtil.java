package hudson.plugins.cigame.util;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.model.Run;

/**
 * Various utilities for builds.
 * 
 * @author kutzi
 */
public class BuildUtil {
	
	/**
	 * Returns the previous build for a given build which was actually build.
	 * 
	 * Note that we are not using {@link Run#getPreviousBuiltBuild()}
	 * as for incremental Maven builds {@link Run#getResult()} sometimes returns
	 * null which would result in a NPE in that method.
	 */
	public static AbstractBuild<?, ?> getPreviousBuiltBuild(AbstractBuild<?, ?> build) {
		
		if (build == null) {
			return null;
		}
		
		AbstractBuild<?, ?> r = build.getPreviousBuild();
        while( r != null && ( r.getResult() == null || r.getResult() == Result.NOT_BUILT )) {
            r = r.getPreviousBuild();
        }
        return r;
	}
}
