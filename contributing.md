# Contributing
When contributing to this repository, please first discuss the change you wish to make with the owners prior to making
any changes.

## Languages
This plugin has been written in Groovy, using the Groovy DSL for gradle. Unit tests and Integration tests are written
using spock.

Before adding a new language, or changing the build DSL, please contact the repository owners and explain your 
reasoning.

## Adding/modifying code
1. Ensure thorough integration tests of any new Task or modifications to an existing task. Use the GradleRunner to
validate that the task runs, with test data that accurately represents the problem you're trying to solve.
2. Write unit tests using Spock to validate methods, and to verify the state of the of the Project during task 
execution.
3. Tasks should be atomic. New behaviour should be added as new tasks, unless it directly relates to an existing 
behaviour.

## Pull requests
1. All changes should be made on a branch and a pull request should be opened against master.
2. The owners of the repository will merge the pull requests.

## Publishing the plugin
The owners of the repository will be responsible for publishing the plugin.
