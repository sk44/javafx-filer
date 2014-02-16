/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.jfxfiler.models;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 *
 * @author sk
 */
public class FilesViewModel {

    private static final int HISTORY_BUFFER_SIZE = 24;

    private static Path normalizePath(Path path) {
        return path.toAbsolutePath().normalize();
    }

    private final LinkedHashMap<String, PathModel> historiesMap = new LinkedHashMap<String, PathModel>(HISTORY_BUFFER_SIZE, 0.75f, false) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, PathModel> eldest) {
            return size() > HISTORY_BUFFER_SIZE;
        }
    };

    private final ObjectProperty<Path> currentPathProperty = new SimpleObjectProperty<>();
    private final ReadOnlyStringProperty currentPathValueProperty = new ReadOnlyStringPropertyBase() {
        {
            currentPathProperty.addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable o) {
                    fireValueChangedEvent();
                }
            });
        }

        @Override
        public String get() {
            return currentPathProperty.get() == null ? "" : currentPathProperty.get().toString();
        }

        @Override
        public Object getBean() {
            return FilesViewModel.this;
        }

        @Override
        public String getName() {
            return "currentPathValueProperty";
        }
    };
    private final ObservableList<PathModel> files = FXCollections.observableArrayList();
    private final ObjectProperty<TableView.TableViewFocusModel<PathModel>> focusModelProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<TableView.TableViewSelectionModel<PathModel>> selectionModelProperty = new SimpleObjectProperty<>();

    public ReadOnlyStringProperty currentPathValueProperty() {
        return currentPathValueProperty;
    }

    public Path getCurrentPath() {
        return currentPathProperty.get();
    }

    public Path getParentPath() {
        return getCurrentPath().getParent();
    }

    public Path getRootPath() {
        return getCurrentPath().getRoot();
    }

    public ObservableList<PathModel> getFiles() {
        return files;
    }

    public ObjectProperty<TableView.TableViewFocusModel<PathModel>> focusModelProperty() {
        return focusModelProperty;
    }

    public ObjectProperty<TableView.TableViewSelectionModel<PathModel>> selectionModelProperty() {
        return selectionModelProperty;
    }

    public int getFocusedIndex() {
        return focusModelProperty.get().getFocusedIndex();
    }

    public PathModel getFocusedModel() {
        return focusModelProperty.get().getFocusedItem();
    }

    public void select(int index) {
        if (index < 0 || count() - 1 < index) {
            return;
        }
        selectionModelProperty.get().clearAndSelect(index);
        focusModelProperty.get().focus(index);
    }

    private void select(PathModel model) {
        SelectionModel<PathModel> selectionModel = selectionModelProperty.get();
        selectionModel.select(model);
        int index = selectionModel.getSelectedIndex();
        focusModelProperty.get().focus(index);
    }

    public void copyFrom(List<PathModel> pathes) {
        for (PathModel pathModel : pathes) {
            pathModel.copyTo(getCurrentPath());
        }
    }

    public void moveTo(Path newPath, boolean goParent) {

        Path currentPath = getCurrentPath();
        if (currentPath != null) {
            PathModel focused = getFocusedModel();
            historiesMap.put(currentPath.toString(), focused);
        }

        Path normalizedPath = normalizePath(newPath);
        currentPathProperty.set(normalizedPath);
        updateFiles();

        PathModel lastSelected = historiesMap.get(normalizedPath.toString());
        if (lastSelected != null) {
            select(lastSelected);
        } else {
            if (goParent) {
                // 初めて上に上がってきた場合は遷移元を初期選択
                PathModel currentPathModel = new PathModel(currentPath);
                select(currentPathModel);
                return;
            }
            select(0);
        }
    }

    public int count() {
        return files.size();
    }

    public void deleteMarked() {
        for (PathModel pathModel : collectMarked()) {
            pathModel.delete();
        }
    }

    public void toggleSelected() {
        PathModel pathModel = getFocusedModel();
        if (pathModel == null) {
            return;
        }
        pathModel.toggleMark();
    }

    private void updateFiles() {

        files.clear();
        Path parent = getParentPath();
        if (parent != null) {
            files.add(new PathModel(parent, true));
        }
        List<PathModel> entries = new ArrayList<>();
        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(getCurrentPath())) {
                for (Path entry : stream) {
                    entries.add(new PathModel(entry));
                }
            }
        } catch (IOException ex) {
            MessageModel.error(ex);
            return;
        }
        Collections.sort(entries, PathModelComparators.BY_DEFAULT);
        files.addAll(entries);
    }

    public List<PathModel> collectMarked() {
        List<PathModel> results = new ArrayList<>();
        for (PathModel model : files) {
            if (model.isMarked()) {
                results.add(model);
            }
        }
        return results;
    }

    public void yankCurrentPath() {
        ClipboardContent content = new ClipboardContent();
        String pathValue = normalizePath(getFocusedModel().getPath()).toString();
        content.putString(pathValue);
        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.setContent(content);
        MessageModel.info("yanked path: " + pathValue);
    }

}
