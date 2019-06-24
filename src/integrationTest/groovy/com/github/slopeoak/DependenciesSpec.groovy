package com.github.slopeoak

import org.codehaus.plexus.util.FileUtils
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.PendingFeature
import spock.lang.Specification

class DependenciesSpec extends Specification {

    @Rule
    TemporaryFolder tempFolder

    @PendingFeature
    def "Compile dependency from pom copied to build gradle file"() {
        given: 'there is a project set up with the init plugin'
            FileUtils.copyDirectory(new File('src/integrationTest/resources/dependencies/externalDependencies'), tempFolder.root)
            def projectRoot = tempFolder.root
            def plugin = GradleRunner.create()
                    .withPluginClasspath()
                    .withProjectDir(projectRoot)
                    .withArguments(':gradleInit')

        when: 'the init task is run'
            plugin.build()

        then: 'the build.gradle file looks like expected-build.gradle'
            new File(tempFolder.root, 'build.gradle').text == new File(tempFolder.root, 'expected-build.gradle').text
    }
}
