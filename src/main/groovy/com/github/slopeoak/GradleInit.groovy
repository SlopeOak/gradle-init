package com.github.slopeoak

import org.gradle.api.Plugin
import org.gradle.api.Project

class GradleInit implements Plugin<Project> {

    @Override
    void apply(Project target) {
        target.tasks.create('init', InitTask)
    }
}
