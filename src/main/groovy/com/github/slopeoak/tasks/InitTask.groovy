package com.github.slopeoak.tasks

import com.github.slopeoak.WriteProjectTask
import org.gradle.api.DefaultTask
import org.gradle.api.Project

import javax.inject.Inject

class InitTask extends DefaultTask {

    private Project project

    @Inject
    InitTask(Project project) {
        this.project = project
        description = "Initialise a gradle project, creating empty build.gradle files and gradle.properties files for " +
                "the folders containing a pom.xml file."
        group = 'Gradle init'

        project.tasks.create('projectNames', ProjectNameTask, project)
        project.tasks.create('convertDependencies', ConvertDependenciesTask, project)

        def buildGradleTask = project.tasks.create('createBuildGradle', BuildGradleTask, project)
        def gradlePropertiesTask = project.tasks.create('createGradleProperties', GradlePropertiesTask, project)

        dependsOn buildGradleTask, gradlePropertiesTask
        doLast { WriteProjectTask }
    }
}
