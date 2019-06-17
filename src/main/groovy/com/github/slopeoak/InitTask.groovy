package com.github.slopeoak


import org.gradle.api.DefaultTask
import org.gradle.api.Project

import javax.inject.Inject

class InitTask extends DefaultTask {

    private Project project

    @Inject
    InitTask(Project project) {
        this.project = project

        def buildGradleTask = project.tasks.create('createBuildGradle', BuildGradleTask, project)
        def gradlePropertiesTask = project.tasks.create('createGradleProperties', GradlePropertiesTask, project)

        dependsOn buildGradleTask, gradlePropertiesTask
    }
}
