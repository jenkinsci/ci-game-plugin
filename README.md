Jenkins ci-game plugin
==============

This plugin introduces a game where users gets point on improving the builds.The plugin is a simple proof of concept of the Build game that was created by [Clint Shank](http://clintshank.javadevelopersjournal.com/ci_build_game.htm). The idea behind the game is to decrease the number of times a build becomes broken. To win the game the developers have to commit code that is compiling and keep adding unit tests.

##Usage

##Configuration

####Version requirements
Note that the plugin version 1.13+ requires Hudson 1.320 to work and the following plugin versions:
 - Checkstyle 3.1
 - Findbugs 4.0
 - PMD 3.1
 - Tasks 4.0
 - Warnings 3.0
 - Violations 0.5.4
 - analysis-core 1.0

The plugin is not activated for all jobs at start, each separate job has to activate the game. The game can also be de-activated in one job if some large merge activity is going to take place. To activate the game for a job, go to the job configuration page, click "Add post-build action" and select "Continuous integration game" from the list of available post-build actions.

####Build score card
For each build the game calculates, according to the rules, the number of points that it is worth. Each user that is responsible for the build (ie comitters) receives the points.

![build score total](https://wiki.jenkins-ci.org/download/attachments/19070977/summary.png?version=1&modificationDate=1207714737000)

Clicking on the link shows what rules that were involved in the point calculation.

![build score card](https://wiki.jenkins-ci.org/download/attachments/19070977/scorecard.png?version=1&modificationDate=1207715499000)

####Leader board
To see who is winning the build game, there is a leader board that is shown on the front page.

![leader board](https://wiki.jenkins-ci.org/download/attachments/19070977/leaderboard.png?version=1&modificationDate=1207714737000)


####Rules
The rules of the game are:

 - -10 points for breaking a build
 - 0 points for breaking a build that already was broken
 - +1 points for doing a build with no failures (unstable builds gives no points)
 - -1 points for each new test failures
 - +1 points for each new test that passes
Rules that depend on other plugins:

#####PMD Plugin. [link](https://wiki.jenkins-ci.org/display/JENKINS/PMD+Plugin)
 - Adding/removing a HIGH priority warning = -5/+5. 
 - Adding/removing a MEDIUM priority warning = -3/+3. 
 - Adding/removing a LOW priority warning = -1/+1.

#####Task Scanner Plugin. [link](https://wiki.jenkins-ci.org/display/JENKINS/Task+Scanner+Plugin)
 - Adding/removing a HIGH priority task = -5/+5. 
 - Adding/removing a MEDIUM priority task = -3/+3. 
 - Adding/removing a LOW priority task = -1/+1

#####Violations Plugin. [link](https://wiki.jenkins-ci.org/display/JENKINS/Violations+Plugin)
 - Adding/removing a violation = -1/+1. 
 - Adding/removing a duplication violation = -5/+5.

#####FindBugs Plugin. [link](https://wiki.jenkins-ci.org/display/JENKINS/FindBugs+Plugin)
 - Adding/removing a HIGH priority findbugs warning = -5/+5. 
 - Adding/removing a MEDIUM priority findbugs warning = -3/+3. 
 - Adding/removing a LOW priority findbugs warning = -1/+1

#####Warnings Plugin. [link](https://wiki.jenkins-ci.org/display/JENKINS/Warnings+Plugin)
 - Adding/removing a compiler warning = -1/+1.

#####Checkstyle Plugin. [link](https://wiki.jenkins-ci.org/display/JENKINS/Checkstyle+Plugin)
 - Adding/removing a checkstyle warning = -1/+1.


###Adding rules to the game

Currently there are three ways to add rules to the game.

#####Patch
Send me a patch for the rule implementation, and I can include it in the main game plugin.

#####Including rules in another plugin
You are a maintainer of a plugin and would like to add rules to the game with data from your plugin. To do this you should declare the game plugin as an [optional dependency](https://wiki.jenkins-ci.org/display/JENKINS/Dependencies+among+plugins#Dependenciesamongplugins-Optionaldependencies) to your plugin. To create rules implement the interface `Rule`, and group them together in a `RuleSet`. To add a `RuleSet` to the game, add it to the game's `RuleBook`.

`hudson.plugins.cigame.PluginImpl.GAME_PUBLISHER_DESCRIPTOR.getRuleBook().addRuleSet(pluginruleset);`

If there are already rules for your plugin in the game plugin, let me know so they can be removed from the plugin.

#####Include rules in a third plugin
You would like to add rules to the game that are only valid for your organization and do not want to share them. To do this you should declare the game plugin as a mandatory dependency to your plugin. Then implement the Rule and add them to the RuleBook as in above point.
