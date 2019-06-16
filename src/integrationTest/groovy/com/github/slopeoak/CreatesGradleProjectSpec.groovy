package com.github.slopeoak

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
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
}
