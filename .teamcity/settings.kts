import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.04"

project {

    subProject(ExampleTeamcity)
}


object ExampleTeamcity : Project({
    name = "Example Teamcity"

    vcsRoot(ExampleTeamcity_HttpsGithubComUralhouseExampleTeamcityGitRefsHeadsMaster)

    buildType(ExampleTeamcity_Build)
})

object ExampleTeamcity_Build : BuildType({
    name = "Build"

    artifactRules = "target/*.jar"

    vcs {
        root(ExampleTeamcity_HttpsGithubComUralhouseExampleTeamcityGitRefsHeadsMaster)
    }

    steps {
        maven {
            enabled = false
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        maven {
            name = "mvn clean package"

            conditions {
                equals("teamcity.build.branch", "master")
            }
            goals = "clean package"
        }
        maven {
            name = "mvn clean test"

            conditions {
                doesNotEqual("teamcity.build.branch", "master")
            }
            goals = "clean test"
        }
    }

    triggers {
        vcs {
        }
    }
})

object ExampleTeamcity_HttpsGithubComUralhouseExampleTeamcityGitRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/uralhouse/example-teamcity.git#refs/heads/master"
    url = "https://github.com/uralhouse/example-teamcity.git"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "uralhouse"
        password = "credentialsJSON:9eeeed40-03f1-48f4-addf-1a0844770924"
    }
})
