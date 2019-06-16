package com.github.slopeoak

import org.gradle.api.Plugin
import org.gradle.api.Project

class GradleInit implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.tasks.create('gradle-init', InitTask, project)
    }
}
