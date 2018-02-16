package com.cosium.vet.push;

import com.cosium.vet.VetCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.IOException;

/**
 * Created on 14/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommand implements VetCommand {

  private final String branchName;

  PushCommand(String branchName) {
    this.branchName = branchName;
  }

  @Override
  public void execute() {
    Repository gitRepository;
    try {
      gitRepository = new FileRepositoryBuilder().findGitDir().build();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }


  }
}
