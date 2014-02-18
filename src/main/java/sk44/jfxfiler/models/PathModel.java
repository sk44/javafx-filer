/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.jfxfiler.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.joda.time.DateTime;

/**
 * filer path model.
 *
 * @author sk
 */
public class PathModel {

    private static final String DIR_INFO = "<DIR>";
    private static final String PARENT_NAME = "..";
    private static final String MARK_VALUE = "*";
    private static final String SIZE_VALUE_FOR_DIR = "";
    private static final String LAST_MODIFIED_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
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

    public boolean isMatch(String pattern) {
        if (pattern == null) {
            return true;
        }
        String name = nameProperty.get().toLowerCase();
        return name.contains(pattern.toLowerCase());
    }

    public void copyTo(Path directory) {
        // TODO
        if (isDirectory()) {
            MessageModel.warn("directory copy does not implemented yet.");
            return;
        }
        try {
            Files.copy(path, directory.resolve(path.getFileName()));
            MessageModel.info(
                "copy " + nameProperty.get() + " to " + directory.toString());
        } catch (IOException ex) {
            MessageModel.error(ex);
        }
    }

    public void delete() {
        // TODO 再帰的に消せるようにする
        // http://stackoverflow.com/questions/779519/delete-files-recursively-in-java
        try {
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                MessageModel.info(path.toString() + " was successfully deleted.");
            } else {
                MessageModel.warn("deleting " + path.toString() + " was failed.");
            }
        } catch (IOException ex) {
            MessageModel.error("an error occured during deleting " + path.toString());
            MessageModel.error(ex);
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
            MessageModel.error(ex);
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
            MessageModel.error(ex);
            return null;
        }
    }

    public StringProperty lastModifiedProperty() {
        return lastModifiedProperty;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PathModel other = (PathModel) obj;
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        if (this.parent != other.parent) {
            return false;
        }
        return true;
    }

}
