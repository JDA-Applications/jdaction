package com.sedmelluq.discord.jdaction;

import org.gradle.api.GradleException;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.VerificationTask;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class NoActionVerificationTask extends SourceTask implements VerificationTask {
  private boolean ignoreFailures;
  private FileCollection classes;

  @TaskAction
  public void run() {
    analyzeClassFiles();
  }

  @Override
  public void setIgnoreFailures(boolean ignoreFailures) {
    this.ignoreFailures = ignoreFailures;
  }

  @Override
  public boolean getIgnoreFailures() {
    return ignoreFailures;
  }

  @SkipWhenEmpty
  @PathSensitive(PathSensitivity.RELATIVE)
  @InputFiles
  public FileCollection getClasses() {
    return classes;
  }

  public void setClasses(FileCollection classes) {
    this.classes = classes;
  }

  private void analyzeClassFiles() {
    int totalIssueCount = 0;

    for (File file : getClasses()) {
      if (file.getName().endsWith(".class")) {
        try {
          totalIssueCount += analyzeClassFile(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
          getLogger().warn("Could not read class file {}", file.getAbsolutePath(), e);
        }
      }
    }

    if (totalIssueCount == 0) {
      getLogger().info("No unused RestActions detected.");
    } else {
      String message = "A total of " + totalIssueCount + " unused RestActions detected.";

      if (ignoreFailures) {
        getLogger().warn(message);
      } else {
        throw new GradleException(message);
      }
    }
  }

  private int analyzeClassFile(byte[] data) {
    ClassReader reader = new ClassReader(data);
    NoActionClassVisitor visitor = new NoActionClassVisitor(getLogger(), ignoreFailures);
    reader.accept(visitor, 0);
    return visitor.getIssueCount();
  }
}
