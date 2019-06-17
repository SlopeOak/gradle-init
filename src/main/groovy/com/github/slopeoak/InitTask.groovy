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

        root.traverse(type: FileType.FILES, excludeFilter: ~/.*src.*/) {
            if (it.name == 'pom.xml') {
                createBuildGradle(it.parentFile)
                createPropertiesFile(it.parentFile)
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

    def createPropertiesFile(File parent) {
        project.logger.info("Writing properties files under $parent")
        def propertiesFile = new File(parent, 'gradle.properties')
        def relativeProjectName = "${folderProjectName(project.rootDir, parent)}"
        project.logger.debug("Relative project from $project.rootDir to $parent is $relativeProjectName")

        project.logger.info("Creating a gradle.properties file at $parent/gradle.properties")
        propertiesFile.write("projectName=" + relativeProjectName)
    }

    static folderProjectName(File root, File projectDir) {
        def relativePath = root.relativePath(projectDir)
        StringBuilder builder = new StringBuilder()
        relativePath.split('/').each {
            builder.append(':')
            builder.append(it)
        }
        builder.toString()
    }
}
