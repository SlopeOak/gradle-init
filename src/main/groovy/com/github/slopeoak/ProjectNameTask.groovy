package com.github.slopeoak

import groovy.io.FileType
import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class ProjectNameTask extends DefaultTask {

    private Project project

    @Inject
    ProjectNameTask(Project project) {
        this.project = project
        project.ext.set('projectMap', [:])
    }

    @TaskAction
    def generateProjectNames() {
        def projectMap = project.projectMap

        def root = project.rootDir
        root.traverse(type: FileType.FILES, excludeFilter: ~$/.*[\\|/](src|build|target|.git|.gradle)[\\|/].*/$) {
            if (it.name == 'pom.xml') {
                def pom = new MavenXpp3Reader().read(it.newReader())
//                def pom = new MavenXpp3Reader().read(it..newReader())

                def projectName = folderProjectName(project.rootDir, it.parentFile)
                def mavenName = "${pom.groupId}:${pom.artifactId}".toString()

                projectMap[mavenName] = [name: projectName, path: project.rootDir.relativePath(it.parentFile)]

                project.logger.debug("Storing project name $mavenName -> $projectName")
            }
        }

        project.ext.set('projectMap', projectMap)
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
