# Gradle init
An alternative approach to the official gradle init script.

Find this plugin in the [Gradle plugins repository](https://plugins.gradle.org/plugin/com.github.slopeoak.gradle-init)!

## What this project is about
The goal of this project is to use gradle to create a gradle project from a maven project. It operates similar to 
`gradle init` but is more task oriented. The goal of this plugin is to apply `gradle-init` at the project root level
and the entire monolithic project will be converted to a reasonable, and functional, gradle project.

This project came about due to our need to move to a better build system but with little luck of the current scripted
approaches at converting.

## Why is it a plugin?
Gradle's task oriented architecture is perfect for executing individual migration scripts in parallel. This project
will provide tasks which handle individual migration steps. One task which will pull in the maven dependencies. Another
task which will configure the test extensions. Etc. These scripts can get quite complex when built into a large groovy
or shell script. But as tasks on a plugin, they can be thoroughly tested and run very quickly.

Besides, I needed an excuse to learn more gradle! What better way to learn than to write a plugin?

## Does this support _XYZ_ DSL?
The current goal is to support maven to groovy-gradle-DSL. I have plans (and have designed the code to support) other
DSL's being added.

The bottleneck for implementing Kotlin is the writer library I'll be using to create the `build.gradle` or 
`build.gradle.kts` files. I'm currently implementing a writer library which uses mustache templates to format the 
`build.gradle` file correctly. Getting this right will be time consuming, but once I do, I'll put in some effort to
support Kotlin.

## Can I contribute?
Contributors are welcome. Please follow the contributing guide before making any changes. Check out the project page
for the current goals. 

If in doubt, please contact the repository owners.
