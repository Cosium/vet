package com.cosium.vet.git;

import java.nio.file.Path;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GitExecutor {

  String execute(Path workingDir, String... arguments);
}
