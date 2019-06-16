package com.github.slopeoak

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.PendingFeature
import spock.lang.Specification

class CreatesGradleProjectSpec extends Specification {

    @Rule
    TemporaryFolder tempFolder

    def "Creates a build gradle file in the root directory"() {
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

        and: 'a build.gradle file exists in both folders'
            verifyAll {
                new File(tempFolder.root, 'build.gradle').exists()
                new File(tempFolder.root, '/subFolder/build.gradle').exists()
            }
    }

    def "Does not overwrite existing build gradle files"() {
        given: 'the plugin has been configured for a folder'
            def plugin = GradleRunner.create()
                    .withPluginClasspath()
                    .withProjectDir(tempFolder.root)
                    .withArguments(':gradle-init')

        and: 'the folder contains the test data'
            FileUtils.copyDirectory(new File('src/integrationTest/resources/createProject/skipsIfItAlreadyExists'), tempFolder.root)

        when: 'the init task is run'
            def outcome = plugin.build()

        then: 'the task was successful'
            outcome.task(':gradle-init').outcome == TaskOutcome.SUCCESS

        and: 'a build.gradle file exists in both folders'
            verifyAll {
                new File(tempFolder.root, 'build.gradle').readLines().size() == 3
                new File(tempFolder.root, '/subFolder/build.gradle').text == "// Some text"
            }
    }

    def "Overwrites if the overwrite extension property is set"() {
        given: 'the plugin has been configured for a folder'
            def plugin = GradleRunner.create()
                    .withPluginClasspath()
                    .withProjectDir(tempFolder.root)
                    .withArguments(':gradle-init')

        and: 'the folder contains the test data'
            FileUtils.copyDirectory(new File('src/integrationTest/resources/createProject/overwritesIfExtensionEnabled'), tempFolder.root)

        when: 'the init task is run'
            def outcome = plugin.build()

        then: 'the task was successful'
            outcome.task(':gradle-init').outcome == TaskOutcome.SUCCESS

        and: 'a build.gradle file exists in both folders'
            verifyAll {
                new File(tempFolder.root, 'build.gradle').text.isEmpty()
                new File(tempFolder.root, '/subFolder/build.gradle').text.isEmpty()
            }
    }

    @PendingFeature
    def "Does not overwrite the root file"() {} // I assume this would be a bad idea? It would erase the plugin... Is that a bad thing?
}
