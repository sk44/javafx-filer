/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.jfxfiler.models;

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
 *import sk44.fxfiler.interfaces.javafx.FileSizeFormatter;
 @author sk
 */
public class PathModel {

    private static final String DIR_INFO = "<DIR>";
    private static final String PARENT_NAME = "..";
    private static final String MARK_VALUE = "*";
    private static final String SIZE_VALUE_FOR_DIR = "";
    private static final String LAST_MODIFIED_DATE_FORMAT = "yy/MM/dd HH:mm:ss";
    private final Path path;
    private final boolean parent;
    private final BooleanProperty markedProperty = new SimpleBooleanProperty(false);
    private final StringProperty markValueProperty = new SimpleStringProperty();
    private final StringProperty nameProperty = new SimpleStringProperty();
    private final StringProperty typeProperty = new SimpleStringProperty();
    private final StringProperty sizeValueProperty = new SimpleStringProperty();
    private final StringProperty lastModifiedProperty = new SimpleStringProperty();

    public PathModel(Path path) {
        this(path, false);
    }

    public PathModel(Path path, boolean parent) {
        this.path = path;
        this.parent = parent;
        nameProperty.set(formatNameFromPath());
        typeProperty.set(getType());
        if (isDirectory()) {
            sizeValueProperty.set(SIZE_VALUE_FOR_DIR);
        } else {
            sizeValueProperty.set(FileSizeFormatter.format(getSizeForFile()));
        }
        lastModifiedProperty.set(
            getLastModified().toString(LAST_MODIFIED_DATE_FORMAT));
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
        return nameProperty.get();
    }

    public StringProperty nameProperty() {
        return nameProperty;
    }

    private String formatNameFromPath() {
        if (parent) {
            return PARENT_NAME;
        }
        return path.getFileName().toString() + (isDirectory() ? "/" : "");
    }

    private String getType() {
        if (isDirectory()) {
            return DIR_INFO;
        }
        String fileName = getName();
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public StringProperty typeProperty() {
        return typeProperty;
    }

    private long getSizeForFile() {
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

    public StringProperty sizeValueProperty() {
        return sizeValueProperty;
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

    public final boolean isDirectory() {
        return Files.isDirectory(path);
    }

    public final DateTime getLastModified() {
        try {
            return new DateTime(Files.getLastModifiedTime(path).toMillis());
        } catch (IOException ex) {
            Logger.getLogger(PathModel.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public StringProperty lastModifiedProperty() {
        return lastModifiedProperty;
    }
}
