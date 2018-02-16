package com.cosium.vet.git;

import com.cosium.vet.runtime.ProcessBuilderFactory;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Created on 16/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultGitRepositoryProvider implements GitRepositoryProvider {

  private final ProcessBuilderFactory processBuilderFactory;

  public DefaultGitRepositoryProvider(ProcessBuilderFactory processBuilderFactory) {
    requireNonNull(processBuilderFactory);
    this.processBuilderFactory = processBuilderFactory;
  }

  @Override
  public GitRepository getRepository() {
    Repository repository;
    try {
      repository = new FileRepositoryBuilder().findGitDir().build();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    if (repository.isBare()) {
      throw new RuntimeException(
          "Current repository is bare repository. A non bare git repository is required.");
    }
    return new DefaultGitRepository(repository, processBuilderFactory);
  }
}
