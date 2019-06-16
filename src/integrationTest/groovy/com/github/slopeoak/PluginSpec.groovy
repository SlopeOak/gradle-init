package com.github.slopeoak

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PluginSpec extends Specification {

    @Rule
    TemporaryFolder tempFolder

    def "Plugin provides the init task"() {
        given: 'the plugin has been configured for a folder'
            def plugin = GradleRunner.create()
                    .withPluginClasspath()
                    .withProjectDir(tempFolder.root)
                    .withArguments(':init')

        when: 'the init task is run'
            def outcome = plugin.build()

        then: 'the task was successful'
            outcome.task(':init').outcome == TaskOutcome.SUCCESS
    }
}
