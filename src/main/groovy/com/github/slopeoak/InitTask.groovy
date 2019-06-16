package com.github.slopeoak

import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class InitTask extends DefaultTask {

    private Project project

    @Inject
    InitTask(Project project) {
        this.project = project
    }

    @TaskAction
    def init() {
        def root = project.rootDir

        root.traverse(type: FileType.FILES) {
            if (it.name == 'pom.xml') {
                def newBuild = new File(it.parent, 'build.gradle')
                newBuild.write('')
            }
        }
    }
}
