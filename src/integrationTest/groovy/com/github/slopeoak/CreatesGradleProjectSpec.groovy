package com.github.slopeoak

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.PendingFeature
import spock.lang.Specification
import spock.lang.Unroll

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
                    .withArguments('--debug', ':gradle-init')

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
                    .withArguments('--debug', ':gradle-init')

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
    def "Does not overwrite the root file"() {}
    // I assume this would be a bad idea? It would erase the plugin... Is that a bad thing?

    @Unroll
    def "Skip folders under #skipFolder for path #path"() {
        given: 'the plugin has been configured for a folder'
            def projectRoot = tempFolder.root
            def buildFile = tempFolder.newFile('build.gradle')
            buildFile << """
                plugins {
                    id 'com.github.slopeoak.gradle-init'
                }
            """

            def testFolder = tempFolder.newFolder(path)
            def plugin = GradleRunner.create()
                    .withPluginClasspath()
                    .withProjectDir(projectRoot)
                    .withArguments('--info', ':gradle-init')

        and: 'the folder contains the test data'
            FileUtils.copyDirectory(new File('src/integrationTest/resources/createProject/skipPomsInSrc'), testFolder)

        when: 'the init task is run'
            def outcome = plugin.build()

        then: 'the task was successful'
            outcome.task(':gradle-init').outcome == TaskOutcome.SUCCESS

        and: 'a build.gradle file exists in both folders'
            !new File(tempFolder.root, "${path.join('/')}/build.gradle").exists()

        where:
            skipFolder | path
            'src'      | [skipFolder] as String[]
            'src'      | ['someOtherFolder', skipFolder] as String[]
            'src'      | ['skip', 'at', 'the', 'end', 'of', 'a', 'long', 'ish', 'path', skipFolder] as String[]
            'src'      | [skipFolder, 'at', 'the', 'beginning', 'of', 'a', 'long', 'ish', 'path'] as String[]
            'src'      | ['skip', 'in', 'the', 'middle', skipFolder, 'of', 'a', 'long', 'ish', 'path'] as String[]
            'build'    | [skipFolder] as String[]
            'target'   | [skipFolder] as String[]
            '.git'     | [skipFolder] as String[]
            '.gradle'  | [skipFolder] as String[]
    }
}
