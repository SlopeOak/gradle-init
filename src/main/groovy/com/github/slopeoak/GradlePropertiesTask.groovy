package com.github.slopeoak

import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class GradlePropertiesTask extends DefaultTask {

    private Project project

    @Inject
    GradlePropertiesTask(Project project) {
        this.project = project
    }

    @TaskAction
    def createGradleProperties() {
        def root = project.rootDir

        root.traverse(type: FileType.FILES, excludeFilter: ~$/.*[\\|/](src|build|target|.git|.gradle)[\\|/].*/$) {
            if (it.name == 'pom.xml') {
                createPropertiesFile(it.parentFile)
            }
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
