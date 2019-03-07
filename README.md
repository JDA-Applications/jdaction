# JDAction - Correct JDA RestAction usage enforcer

This is a Gradle plugin which makes sure that the return values of all methods which return a RestAction are used. Since it is a common mistake to forget to `.queue()`/`.complete()`/`.submit()` RestActions, and it is often only discovered after noticing that something doesn't work, this plugin will help catch those cases quickly as it will cause a build failure in such case.

Link to its Gradle plugin repository page:
* https://plugins.gradle.org/plugin/com.sedmelluq.jdaction

## Usage

The plugin first needs to be included:

```groovy
plugins {
  id 'com.sedmelluq.jdaction' version '1.0.3'
}
```

This makes it automatically run after the `classes` task and scans the produced class files to make sure all return values of RestAction type were used. In case it finds some, something like this will appear in the build log:

```
:demo-jda:compileJava
:demo-jda:processResources UP-TO-DATE
:demo-jda:classes
com/sedmelluq/discord/lavaplayer/demo/jda/Main$1.java:85: Return value is unused. This action is not performed.
:demo-jda:jdactionMain FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':demo-jda:jdactionMain'.
> A total of 1 unused RestActions detected.
```

From this log you can see the file names and line numbers where the unused RestActions were found.
