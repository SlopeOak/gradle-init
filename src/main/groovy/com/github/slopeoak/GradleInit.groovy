package com.github.slopeoak

import com.github.slopeoak.extensions.GradleInitExtensions
import org.gradle.api.Plugin
import org.gradle.api.Project

class GradleInit implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('gradleInit', GradleInitExtensions)
        project.tasks.create('gradleInit', InitTask, project)

        project.pluginManager.apply('com.github.slopeoak:project-writer')
    }
}
