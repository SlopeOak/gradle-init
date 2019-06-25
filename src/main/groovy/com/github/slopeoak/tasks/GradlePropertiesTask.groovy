package com.github.slopeoak.tasks

import groovy.io.FileType
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class GradlePropertiesTask extends DefaultTask {

    private Project project

    @Inject
    GradlePropertiesTask(Project project) {
        this.project = project

        dependsOn project.tasks.'projectNames'
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

        def pom = new MavenXpp3Reader().read(new File(parent, 'pom.xml').newReader())

        project.logger.debug("ProjectMap: $project.projectMap")
        def relativeProjectName = project.projectMap["${pom.groupId}:${pom.artifactId}".toString()].name
        project.logger.debug("Relative project for ${"${pom.groupId}:${pom.artifactId}".toString()} is $relativeProjectName")

        project.logger.info("Creating a gradle.properties file at $parent/gradle.properties")
        propertiesFile.write("projectName=" + relativeProjectName)
    }
}
