package com.github.slopeoak

import com.github.slopeoak.tasks.ConvertDependenciesTask
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class DependenciesTest extends Specification {

    @Rule
    TemporaryFolder tempFolder

    @Unroll
    def "Adds #scope dependency with group #group, name #name and version #version"() {
        given: 'there is some project'
            def project = ProjectBuilder.builder()
                    .withProjectDir(tempFolder.root)
                    .build()

        and: 'the project contains the dependencies task'
            def task = project.tasks.create('convertDependencies', ConvertDependenciesTask, project)

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

        when: 'the dependencies task is executed'
            task.convertDependencies()

        then: 'the project contains an equivalent external dependency'
            project.configurations.getByName(configuration).dependencies.any {
                it.group == group
                it.name == name
                it.version == version
            }

        where:
            configuration | scope     | group                 | name             | version
            'compile'     | 'compile' | 'com.github.slopeoak' | 'project-writer' | '0.0.1'
            'compile'     | 'compile' | 'com.github.slopeoak' | 'gradle-init'    | '0.0.2'
            'testCompile' | 'test'    | 'org.spockframework'  | 'spock-core'     | '1.3-groovy-2.5'
    }
}
