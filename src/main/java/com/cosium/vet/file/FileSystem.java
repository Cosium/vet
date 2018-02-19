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

    InputStream readFile(Path file);

    void writeFile(Path file, OutputStream outputStream);

}
