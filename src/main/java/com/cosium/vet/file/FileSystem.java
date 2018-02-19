package com.cosium.vet.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface FileSystem {

  /**
   * @param file The relative path of the file
   * @return The input stream to use to read the file content
   */
  InputStream newAppFileInputStream(Path file);

  /**
   * @param file The relative path of the file
   * @return The output stream to use to write in the file content
   */
  OutputStream newAppFileOutputStream(Path file);
}
