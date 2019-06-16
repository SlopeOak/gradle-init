package com.github.slopeoak

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.PendingFeature
import spock.lang.Specification

class ProjectNameSpec extends Specification {

    @Rule
    TemporaryFolder tempFolder

    def "Each submodule contains a gradle project file"() {
        given: 'the plugin has been configured for a folder'
            def plugin = GradleRunner.create()
                    .withPluginClasspath()
                    .withProjectDir(tempFolder.root)
                    .withArguments(':gradle-init')

        and: 'the folder contains the test data'
            FileUtils.copyDirectory(new File('src/integrationTest/resources/createProject/inRootDir'), tempFolder.root)

        when: 'the init task is run'
            def outcome = plugin.build()

        then: 'the task was successful'
            outcome.task(':gradle-init').outcome == TaskOutcome.SUCCESS

        and: 'there is a gradle.properties file for each module'
            verifyAll {
                new File(tempFolder.root, 'gradle.properties').exists()
                new File(tempFolder.root, 'subFolder/gradle.properties').exists()
            }
    }

    def "Each gradle properties file contains a name based on the folder structure"() {
        given: 'the plugin has been configured for a folder'
            def plugin = GradleRunner.create()
                    .withPluginClasspath()
                    .withProjectDir(tempFolder.root)
                    .withArguments('--info', ':gradle-init')

        and: 'the folder contains the test data'
            FileUtils.copyDirectory(new File('src/integrationTest/resources/createProject/inRootDir'), tempFolder.root)

        when: 'the init task is run'
            def outcome = plugin.build()

        then: 'the task was successful'
            outcome.task(':gradle-init').outcome == TaskOutcome.SUCCESS

        and: 'the name is in the gradle.properties file'
            verifyAll {
                def root = new File(tempFolder.root, 'gradle.properties')
                root.readLines().any {
                    it == 'projectName=:'
                }

                def subFolder = new File(tempFolder.root, 'subFolder/gradle.properties')
                subFolder.readLines().any {
                    it == 'projectName=:subFolder'
                }
            }
    }

    @PendingFeature
    def "Each gradle properties file contains a name based on the pom name if the extension property is set to POM"() {

    }
}
