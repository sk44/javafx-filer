/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.fxfiler.interfaces.javafx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author sk
 */
public class PathModel {

    private static final String DIR_INFO = "<DIR>";
    private static final String PARENT_NAME = "..";
    private final Path path;
    private final boolean parent;

    public PathModel(Path path) {
        this(path, false);
    }

    public PathModel(Path path, boolean parent) {
        this.path = path;
        this.parent = parent;
    }

    public Path getDirectoryPath() {
        if (isDirectory() == false) {
            return null;
        }
        return path;
    }

    public String getName() {
        if (parent) {
            return PARENT_NAME;
        }
        return path.getFileName().toString() + (isDirectory() ? "/" : "");
    }

    public String getInfo() {
        if (isDirectory()) {
            return DIR_INFO;
        }
        // TODO
        return "12,345K";
    }

    public boolean isDirectory() {
        return Files.isDirectory(path);
    }

    public DateTime getLastModified() {
        try {
            return new DateTime(Files.getLastModifiedTime(path).toMillis());
        } catch (IOException ex) {
            Logger.getLogger(PathModel.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
