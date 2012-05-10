package hudson.plugins.cigame.model;

import hudson.model.AbstractBuild;

import java.util.Collection;

/**
 * A {@link Rule} which is able to aggregate the scores for sub parts of a job.
 * This is e.g. used to calculate the scores for incremental maven multimodule builds.
 * 
 * @author kutzi
 */
public interface AggregatableRule<T> extends Rule {
	
	/**
	 * Aggregates several rule results calculated by the same rule into one.
	 * This is currently (only) used to aggregate build results for maven multi module
	 * builds (especially with the 'incremental build' option).
	 */
	RuleResult<?> aggregate(Collection<RuleResult<T>> results);
    
    /**
     * Evaluates the rule for the current build compared to a previous build.
     * 
     * Please note that 'previousBuild' may not necessarily be the immediate previous build
     * (i.e. with build number n-1), as builds which have been aborted (or otherwise not build)
     * are skipped. This is especially true for not-build maven modules in a Maven project
     * which the 'incremental build' option enabled.
     *  
     * @param previousBuild the previous build with usable results (may be null!)
     * @param build the current build (may be null!)
     * @return the rule result or null, if no points should be awarded
     */
    RuleResult<T> evaluate(AbstractBuild<?, ?> previousBuild, AbstractBuild<?, ?> build);

    /**
     * @deprecated this rule is not called at all for {@link AggregatableRule}s.
     * Please implement {@link #evaluate(AbstractBuild, AbstractBuild)} instead!
     */
    @Deprecated
	RuleResult<T> evaluate(AbstractBuild<?, ?> build);
}
