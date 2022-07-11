package com.sedmelluq.discord.jdaction;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.plugins.quality.CodeQualityExtension;
import org.gradle.api.plugins.quality.internal.AbstractCodeQualityPlugin;
import org.gradle.api.tasks.SourceSet;

import java.util.concurrent.Callable;

public class NoActionPlugin extends AbstractCodeQualityPlugin<NoActionVerificationTask> {
  @Override
  protected String getToolName() {
    return "jdaction";
  }

  @Override
  protected Class<NoActionVerificationTask> getTaskType() {
    return NoActionVerificationTask.class;
  }

  @Override
  protected void configureConfiguration(Configuration configuration) {

  }

  @Override
  protected CodeQualityExtension createExtension() {
    extension = project.getExtensions().create("jdaction", Extension.class, project);
    return extension;
  }

  @Override
  protected void configureForSourceSet(SourceSet sourceSet, NoActionVerificationTask task) {
    task.setSource(sourceSet.getAllJava());

    ConventionMapping taskMapping = task.getConventionMapping();
    taskMapping.map("classes", (Callable<FileCollection>) () -> project.fileTree(sourceSet.getOutput().getClassesDirs()).builtBy(sourceSet.getOutput()));
    project.getTasksByName(sourceSet.getClassesTaskName(), false).forEach(classesTask -> classesTask.finalizedBy(task.getPath()));
  }

  public static class Extension extends CodeQualityExtension {
    @SuppressWarnings("unused")
    public Extension(Project project) {

    }
  }
}
