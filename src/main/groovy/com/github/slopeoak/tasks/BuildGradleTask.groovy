package com.github.slopeoak.tasks

import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class BuildGradleTask extends DefaultTask {

    private Project project

    @Inject
    BuildGradleTask(Project project) {
        this.project = project
    }

    @TaskAction
    def createBuildGradleFile() {
        def root = project.rootDir

        root.traverse(type: FileType.FILES, excludeFilter: ~$/.*[\\|/](src|build|target|.git|.gradle)[\\|/].*/$) {
            if (it.name == 'pom.xml') {
                createBuildGradle(it.parentFile)
            }
        }
    }

    def createBuildGradle(File parent) {
        project.logger.info("Writing project files under $parent")
        def buildFile = new File(parent, 'build.gradle')
        if (project.extensions.'gradleInit'.overwrite || !buildFile.exists()) {
            project.logger.info("Creating a build.gradle file at $parent/build.gradle")
            buildFile.write('')
        }
    }
}
