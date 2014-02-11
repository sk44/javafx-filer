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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.joda.time.DateTime;

/**
 *
 * @author sk
 */
public class PathModel {

    private static final String DIR_INFO = "<DIR>";
    private static final String PARENT_NAME = "..";
    private static final String MARK_VALUE = "*";
    private final Path path;
    private final boolean parent;
//	private boolean marked;
    private final BooleanProperty markedProperty = new SimpleBooleanProperty(false);
    private final StringProperty markValueProperty = new SimpleStringProperty();

    public PathModel(Path path) {
        this(path, false);
    }

    public PathModel(Path path, boolean parent) {
        this.path = path;
        this.parent = parent;
    }

    public void toggleMark() {
        if (parent) {
            return;
        }
        updateMark(markedProperty.get() == false);
    }

    public void mark() {
        updateMark(true);
    }

    private void updateMark(boolean mark) {
        markedProperty.set(mark);
        markValueProperty.set(mark ? MARK_VALUE : "");
    }

    public void copyTo(Path directory) {
        // TODO
        if (isDirectory()) {
            System.out.println("directory copy is not supported.");
            return;
        }
        try {
            Files.copy(path, directory.resolve(path.getFileName()));
        } catch (IOException ex) {
            Logger.getLogger(PathModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Path getDirectoryPath() {
        if (isDirectory() == false) {
            return null;
        }
        return path;
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        if (parent) {
            return PARENT_NAME;
        }
        return path.getFileName().toString() + (isDirectory() ? "/" : "");
    }

    public String getType() {
        if (isDirectory()) {
            return DIR_INFO;
        }
        String fileName = getName();
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public long getSize() {
        if (isDirectory()) {
            return -1;
        }
        try {
            return Files.size(path);
        } catch (IOException ex) {
            Logger.getLogger(PathModel.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public boolean isMarked() {
        return markedProperty.get();
    }

    public BooleanProperty markedProperty() {
        return markedProperty;
    }

    public StringProperty markValueProperty() {
        return markValueProperty;
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
