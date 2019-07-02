package com.github.slopeoak

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
                    .withArguments(':convertDependencies', ':writeProject')

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

    def "Dependencies with no scope are assumed to be compile"() {
        given: 'there is a project set up with the init plugin'
            def projectRoot = tempFolder.root
            def plugin = GradleRunner.create()
                    .withPluginClasspath()
                    .withProjectDir(projectRoot)
                    .withArguments(':convertDependencies', ':writeProject')

        and: 'the directory contains a build file'
            def build = tempFolder.newFile('build.gradle')
            build << """
                plugins {
                    id 'com.github.slopeoak.gradle-init'
                }
            """

        and: 'there is a pom with a dependency'
            def pom = tempFolder.newFile('pom.xml')
            pom << """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.github.slopeoak</groupId>
                    <artifactId>somepom</artifactId>
                    <version>0.0.1</version>
                    <dependencies>
                        <dependency>
                            <groupId>com.github.slopeoak</groupId>
                            <artifactId>gradle-init</artifactId>
                            <version>0.0.2</version>
                        </dependency>
                    </dependencies>
                </project>
            """

        when: 'the init task is run'
            def outcome = plugin.build()

        then: 'the build.gradle file looks like expected-build.gradle'
            new File(tempFolder.root, 'build.gradle').readLines().any {
                it ==~ /(.*)compile 'com.github.slopeoak:gradle-init:0.0.2'(.*)/
            }

        and: 'the task was successful'
            verifyAll {
                outcome.task(':convertDependencies').outcome == TaskOutcome.SUCCESS
                outcome.task(':writeProject').outcome == TaskOutcome.SUCCESS
            }
    }

    @Unroll
    def "Maven dependency with groupId #group, artifactId #name, version #version and scope #scope becomes #expectedDependency"() {
        given: 'there is a project set up with the init plugin'
            def projectRoot = tempFolder.root
            def plugin = GradleRunner.create()
                    .withPluginClasspath()
                    .withProjectDir(projectRoot)
                    .withArguments(':convertDependencies', ':writeProject')

        and: 'the directory contains a build file'
            def build = tempFolder.newFile('build.gradle')
            build << """
                plugins {
                    id 'com.github.slopeoak.gradle-init'
                }
            """

        and: 'there is a pom with a dependency'
            def pom = tempFolder.newFile('pom.xml')
            pom << """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.github.slopeoak</groupId>
                    <artifactId>somepom</artifactId>
                    <version>0.0.1</version>
                    <dependencies>
                        <dependency>
                            <groupId>$group</groupId>
                            <artifactId>$name</artifactId>
                            <version>$version</version>
                            <scope>$scope</scope>
                        </dependency>
                    </dependencies>
                </project>
            """

        when: 'the init task is run'
            def outcome = plugin.build()

        then: 'the build.gradle file looks like expected-build.gradle'
            new File(tempFolder.root, 'build.gradle').readLines().any {
                it ==~ /(.*)$expectedDependency(.*)/
            }

        and: 'the task was successful'
            verifyAll {
                outcome.task(':convertDependencies').outcome == TaskOutcome.SUCCESS
                outcome.task(':writeProject').outcome == TaskOutcome.SUCCESS
            }

        where:
            configuration | scope     | group                 | name             | version
            'compile'     | 'compile' | 'com.github.slopeoak' | 'project-writer' | '0.0.1'
            'compile'     | 'compile' | 'com.github.slopeoak' | 'gradle-init'    | '0.0.2'
            'testCompile' | 'test'    | 'org.spockframework'  | 'spock-core'     | '1.3-groovy-2.5'

            expectedDependency = "$configuration '$group:$name:$version'"
    }

    @Unroll
    def "Dependency with targetConfiguration #type created for maven dependency with type #type"() {
        given: 'there is a project set up with the init plugin'
            def projectRoot = tempFolder.root
            def plugin = GradleRunner.create()
                    .withPluginClasspath()
                    .withProjectDir(projectRoot)
                    .withArguments(':convertDependencies', ':writeProject')

        and: 'the directory contains a build file'
            def build = tempFolder.newFile('build.gradle')
            build << """
                plugins {
                    id 'com.github.slopeoak.gradle-init'
                }
            """

        and: 'there is a pom with a dependency'
            def pom = tempFolder.newFile('pom.xml')
            pom << """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.github.slopeoak</groupId>
                    <artifactId>somepom</artifactId>
                    <version>0.0.1</version>
                    <dependencies>
                        <dependency>
                            <groupId>com.github.slopeoak</groupId>
                            <artifactId>project-writer</artifactId>
                            <version>0.0.1</version>
                            <type>$type</type>
                        </dependency>
                    </dependencies>
                </project>
            """

        when: 'the init task is run'
            def outcome = plugin.build()

        then: 'the build.gradle file looks like expected-build.gradle'
            new File(tempFolder.root, 'build.gradle').readLines().any {
                it ==~ /(.*)$expectedDependency(.*)/
            }

        and: 'the task was successful'
            verifyAll {
                outcome.task(':convertDependencies').outcome == TaskOutcome.SUCCESS
                outcome.task(':writeProject').outcome == TaskOutcome.SUCCESS
            }

        where:
            type      | _
            'all'     | _
            'sources' | _
            'javadoc' | _

            expectedDependency = "compile\\(group: 'com.github.slopeoak', name: 'project-writer', version: '0.0.1', targetConfiguration: '$type'\\)"
    }
}
