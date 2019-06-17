package com.github.slopeoak

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
            InitTask.folderProjectName(tempFolder.root, subFolder) == expected

        where:
            paths                                                  | expected
            'subFolder'                                            | ':subFolder'
            ['subFolder1', 'subFolder2', 'subFolder3'] as String[] | ':subFolder1:subFolder2:subFolder3'
    }

    def "Path to rootFolder is just :"() {
        expect: 'project name for folder matches #expected'
            InitTask.folderProjectName(tempFolder.root, tempFolder.root) == ':'
    }
}
