package com.github.slopeoak.tasks

import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class ConvertDependenciesTask extends DefaultTask {

    private Project project

    @Inject
    ConvertDependenciesTask(Project project) {
        this.project = project
        description = "Adds the dependencies from the pom.xml file to the project model."
        group = 'Gradle init'
    }

    @TaskAction
    def convertDependencies() {
        def pom = project.file('pom.xml')
        def mavenModel = new MavenXpp3Reader().read(pom.newReader())

        mavenModel.dependencies.each { mvnDep ->
            def configs = scopeMap(mvnDep.scope)
            configs.each { configName ->
                project.configurations.create(configName)
                def dep = project.dependencies.add(configName, "$mvnDep.groupId:$mvnDep.artifactId:$mvnDep.version")
                if (mvnDep.type != null && mvnDep.type != 'jar') {
                    dep.targetConfiguration = mvnDep.type
                }
            }
        }
    }

    static scopeMap(def scope) {
        def config
        switch (scope) {
            case 'compile':
                config = ['compile']
                break
            case 'test':
                config = ['testCompile']
                break
            case 'runtime':
                config = ['runtime']
                break
            case 'provided':
                config = ['compileOnly', 'testCompile']
                break
            default:
                config = ['compile']
        }
        config
    }
}
