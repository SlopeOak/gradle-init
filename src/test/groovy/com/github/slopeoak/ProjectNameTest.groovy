package com.github.slopeoak

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class ProjectNameTest extends Specification {

    @Rule
    TemporaryFolder tempFolder

    @Unroll
    def "Project name for path #paths is #expected"() {
        expect: 'project name for folder matches #expected'
            def subFolder = tempFolder.newFolder(paths)
            ProjectNameTask.folderProjectName(tempFolder.root, subFolder) == expected

        where:
            paths                                                  | expected
            'subFolder'                                            | ':subFolder'
            ['subFolder1', 'subFolder2', 'subFolder3'] as String[] | ':subFolder1:subFolder2:subFolder3'
    }

    def "Path to rootFolder is just :"() {
        expect: 'project name for folder matches #expected'
            ProjectNameTask.folderProjectName(tempFolder.root, tempFolder.root) == ':'
    }

    def "ProjectMap extension property contains the maven name to gradle name"() {
        given: 'copy the test data to the directory'
            FileUtils.copyDirectory(new File('src/integrationTest/resources/createProject/inRootDir'), tempFolder.root)

        and: 'create a project in the test directory'
            def project = ProjectBuilder.builder()
                    .withProjectDir(tempFolder.root)
                    .build()

        when: 'the project names task is executed'
            def projectNamesTask = project.tasks.create('projectNames', ProjectNameTask, project)
            projectNamesTask.generateProjectNames()

        then: 'the extension property projectMap has the folder names'
            def projectMap = project.projectMap

            projectMap == [
                    'com.github.slopeoak:createProject-inRootDir': [name: ':', path: ''],
                    'com.github.slopeoak:createProject-subFolder': [name: ':subFolder', path: 'subFolder']
            ]
    }

    def "ProjectMap extension property available from subprojects"() {
        given: 'copy the test data to the directory'
            FileUtils.copyDirectory(new File('src/integrationTest/resources/createProject/inRootDir'), tempFolder.root)

        and: 'create a project in the test directory'
            def project = ProjectBuilder.builder()
                    .withProjectDir(tempFolder.root)
                    .build()

        and: 'create a subproject in the subfolder with the parent'
            def subproject = ProjectBuilder.builder()
                    .withParent(project)
                    .withProjectDir(new File(tempFolder.root, 'subFolder'))
                    .build()

        when: 'the project names task is executed on the parent'
            def projectNamesTask = project.tasks.create('projectNames', ProjectNameTask, project)
            projectNamesTask.generateProjectNames()

        then: 'the child project has access to the same state'
            verifyAll {
                subproject.projectMap == [
                        'com.github.slopeoak:createProject-inRootDir': [name: ':', path: ''],
                        'com.github.slopeoak:createProject-subFolder': [name: ':subFolder', path: 'subFolder']
                ]

                project.projectMap == subproject.projectMap
            }
    }

    @Unroll
    def "ProjectMap contains the name #name and the path #mappedPath"() {
        given: 'there is test data in the folder'
            def rootBuild = tempFolder.newFile('build.gradle')
            rootBuild << """
                plugins {
                    id 'com.github.slopeoak.gradle-init'
                }
            """

            def testFolder = tempFolder.newFolder(path)
            def buildFile = new File(testFolder, 'build.gradle')
            buildFile << ''

            def pom = new File(testFolder, 'pom.xml')
            pom << """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>$groupId</groupId>
                    <artifactId>$artifactId</artifactId>
                    <version>0.0.1-SNAPSHOT</version>
                </project>
            """

        and: 'create a project in the test directory'
            def project = ProjectBuilder.builder()
                    .withProjectDir(tempFolder.root)
                    .build()

        when: 'the project names task is executed on the parent'
            def projectNamesTask = project.tasks.create('projectNames', ProjectNameTask, project)
            projectNamesTask.generateProjectNames()

        then:
            verifyAll {
                project.projectMap.size() == 1
                project.projectMap.containsKey("$groupId:$artifactId".toString())
                project.projectMap == [("$groupId:$artifactId".toString()): [name: name, path: mappedPath]]
            }

        where:
            path                                          | groupId                  | artifactId                   || name                       | mappedPath
            ['a', 'b', 'c'] as String[]                   | 'group'                  | 'artifact'                   || ":a:b:c"                   | 'a/b/c'
            ['folder1', 'folder2', 'folder3'] as String[] | 'com.github.slopeoak'    | 'some-gradle-thing'          || ":folder1:folder2:folder3" | 'folder1/folder2/folder3'
            1..100 as String[]                            | 'long.folder.path.thing' | 'thats-some-sweet-test-data' || ":${path.join(':')}"       | path.join('/')
    }
}
