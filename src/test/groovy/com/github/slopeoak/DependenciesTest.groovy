package com.github.slopeoak

import com.github.slopeoak.tasks.ConvertDependenciesTask
import org.gradle.api.artifacts.ExternalDependency
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

    /* See http://andresalmiray.com/maven-scopes-vs-gradle-configurations/ */
    @Unroll
    def "Maven dependency with scope #scope becomes dependency with configuration(s) #configurations"() {
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
            configurations.each { configuration ->
                project.configurations.getByName(configuration).dependencies.any {
                    it.group == group
                    it.name == name
                    it.version == version
                }
            }

        where:
            configurations                 | scope      | group                 | name             | version
            ['compile']                    | 'compile'  | 'com.github.slopeoak' | 'project-writer' | '0.0.1'
            ['testCompile']                | 'test'     | 'com.github.slopeoak' | 'project-writer' | '0.0.1'
            ['compileOnly', 'testCompile'] | 'provided' | 'com.github.slopeoak' | 'project-writer' | '0.0.1'
            ['runtime']                    | 'runtime'  | 'com.github.slopeoak' | 'project-writer' | '0.0.1'
    }

    @Unroll
    def "Maven dependency with type #type becomes dependency with classifier #type"() {
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
                            <groupId>com.github.slopeoak</groupId>
                            <artifactId>project-writer</artifactId>
                            <version>0.0.1</version>
                            <type>$type</type>
                        </dependency>
                    </dependencies>
                </project>
            """

        when: 'the dependencies task is executed'
            task.convertDependencies()

        then: 'the project contains an equivalent external dependency'
            project.configurations.compile.dependencies.any { ExternalDependency dependency ->
                dependency.group == 'com.github.slopeoak'
                dependency.name == 'project-writer'
                dependency.version == '0.0.1'
                dependency.targetConfiguration == type
            }

        where:
            type      | _
            'all'     | _
            'sources' | _
            'javadoc' | _
    }
}