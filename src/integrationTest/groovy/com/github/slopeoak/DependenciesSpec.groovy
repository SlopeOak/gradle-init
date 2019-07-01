package com.github.slopeoak

import groovy.transform.NotYetImplemented
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class DependenciesSpec extends Specification {

    @Rule
    TemporaryFolder tempFolder

    def "Defines a convertDependencies task"() {
        given: 'the project applies the gradle init plugin'
            def plugin = GradleRunner.create()
                    .withPluginClasspath()
                    .withProjectDir(tempFolder.root)
                    .withArguments(':tasks')

            def build = tempFolder.newFile('build.gradle')
            build << """
                plugins {
                    id 'com.github.slopeoak.gradle-init'
                }
            """

        when: 'the tasks are retrieved'
            def outcome = plugin.build()

        then: 'the task is listed'
            outcome.output.contains("convertDependencies - Adds the dependencies from the pom.xml file to the project model.")
    }

    @Unroll
    def "Compile dependency from pom copied to build gradle file"() {
        given: 'there is a project set up with the init plugin'
            FileUtils.copyDirectory(new File(testFilePath), tempFolder.root)
            def projectRoot = tempFolder.root
            def plugin = GradleRunner.create()
                    .withPluginClasspath()
                    .withProjectDir(projectRoot)
                    .withArguments('--stacktrace', ':convertDependencies', ':writeProject')

        when: 'the init task is run'
            def outcome = plugin.build()

        then: 'the build.gradle file looks like expected-build.gradle'
            new File(tempFolder.root, 'build.gradle').text == new File(tempFolder.root, 'expected-build.gradle').text

        and: 'the task was successful'
            verifyAll {
                outcome.task(':convertDependencies').outcome == TaskOutcome.SUCCESS
                outcome.task(':writeProject').outcome == TaskOutcome.SUCCESS
            }

        where:
            testFilePath                                                               | _
            'src/integrationTest/resources/dependencies/externalDependencies/example1' | _
            'src/integrationTest/resources/dependencies/externalDependencies/example2' | _
    }

    @NotYetImplemented
    def "Dependencies are pulled in from the pom"() {}

    @NotYetImplemented
    def "Dependencies with no scope are assumed to be compile"() {}

    @Unroll
    @NotYetImplemented
    def "Dependencies with #mvnScope scope become #gradleConfiguration"() {}
}
