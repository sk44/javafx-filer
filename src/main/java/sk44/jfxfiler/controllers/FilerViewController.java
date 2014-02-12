/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.jfxfiler.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import sk44.jfxfiler.models.PathModel;
import sk44.jfxfiler.models.PathModelComparators;
import sk44.jfxfiler.views.TextAlignmentCellFactory;

/**
 *
 * @author sk
 */
public class FilerViewController implements Initializable {

    @FXML
    private Label currentPathLabel;
    @FXML
    private TableView<PathModel> filesView;
    @FXML
    private TableColumn<PathModel, String> markColumn;
    @FXML
    private TableColumn<PathModel, String> nameColumn;
    @FXML
    private TableColumn<PathModel, String> typeColumn;
    @FXML
    private TableColumn<PathModel, String> sizeColumn;
    @FXML
    private TableColumn<PathModel, String> lastModifiedColumn;
    private FilerViewController otherFilerView;
    // TODO bind
    private Path currentPath;

    @FXML
    protected void handleKeyPressedInTable(KeyEvent event) {
//		System.out.println("key pressed in table: " + event.getCode());
        switch (event.getCode()) {
            case C:
                copy();
                break;
            case G:
                if (event.isShiftDown()) {
                    moveToLast();
                } else {
                    moveToFirst();
                }
                break;
            case H:
            case LEFT:
                moveTo(currentPath.getParent());
                break;
            case J:
                moveToNext();
                break;
            case K:
                if (event.isShiftDown()) {
                    // TODO create directory
                } else {
                    moveToPrevious();
                }
                break;
            case L:
            case RIGHT:
                goForward();
                break;
            case O:
                // TODO
                break;
            case Q:
                Platform.exit();
                break;
            case TAB:
                otherFilerView.focus();
                break;
            case UP:
//                focusPrevious();
                break;
            case DOWN:
                // TODO
//                focusNext();
                break;
            case LESS:
                moveTo(currentPath.getRoot());
                break;
            case X:
            case ENTER:
                if (event.isControlDown()) {
                    openAssosiated();
                }
                break;
            case SPACE:
                toggleSelected();
                moveToNext();
                break;
        }
    }

    void withOtherFilerView(FilerViewController other) {
        otherFilerView = other;
    }

    List<PathModel> collectMarkedItems() {
        List<PathModel> results = new ArrayList<>();
        for (PathModel model : filesView.getItems()) {
            if (model.isMarked()) {
                results.add(model);
            }
        }
        return results;
    }

    void copy() {
        otherFilerView.copyFrom(collectMarkedItems());
    }

    void copyFrom(List<PathModel> pathes) {
        for (PathModel pathModel : pathes) {
            pathModel.copyTo(currentPath);
        }
        refresh();
    }

    void focus() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                filesView.requestFocus();
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        filesView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//        filesView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        markColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PathModel, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PathModel, String> p) {
                return p.getValue().markValueProperty();
            }
        });
        markColumn.setCellFactory(new TextAlignmentCellFactory<PathModel>(TextAlignmentCellFactory.Alignment.RIGHT));
        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PathModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(final TableColumn.CellDataFeatures<PathModel, String> p) {
                return p.getValue().nameProperty();
            }
        });
        typeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PathModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PathModel, String> p) {
                return p.getValue().typeProperty();
            }
        });
        sizeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PathModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PathModel, String> p) {
                return p.getValue().sizeValueProperty();
            }
        });
        sizeColumn.setCellFactory(new TextAlignmentCellFactory<PathModel>(TextAlignmentCellFactory.Alignment.RIGHT));
        lastModifiedColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PathModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PathModel, String> p) {
                return p.getValue().lastModifiedProperty();
            }
        });
        // automatic width
        nameColumn.prefWidthProperty().bind(filesView.widthProperty().subtract(295));
    }

    private void moveToPrevious() {
//        final int index = filesView.getSelectionModel().getSelectedIndex();
        final int index = getCurrentIndex();
        moveToIndex(index - 1);
        scrollToFocused();
    }

    private void moveToNext() {
//        final int index = filesView.getSelectionModel().getSelectedIndex();
        final int index = getCurrentIndex();
        moveToIndex(index + 1);
        scrollToFocused();
    }

    private void moveToFirst() {
        moveToIndex(0);
        scrollToFocused();
    }

    private void moveToLast() {
        moveToIndex(countItems() - 1);
        scrollToFocused();
    }

    private int getCurrentIndex() {
        return filesView.getFocusModel().getFocusedIndex();
    }

    private void moveToIndex(int index) {
        if (index < 0 || countItems() - 1 < index) {
            return;
        }
        filesView.getSelectionModel().clearAndSelect(index);
        filesView.getFocusModel().focus(index);
    }

    private int countItems() {
        return filesView.getItems().size();
    }

    private void scrollToFocused() {
        filesView.scrollTo(filesView.getFocusModel().getFocusedIndex());
    }

    private void goForward() {
        moveTo(filesView.getFocusModel().getFocusedItem().getDirectoryPath());
    }

    private void openAssosiated() {
        PathModel pathModel = filesView.getFocusModel().getFocusedItem();
        if (pathModel == null) {
            return;
        }
        try {
            Desktop.getDesktop().open(pathModel.getPath().toFile());
        } catch (IOException ex) {
            Logger.getLogger(FilerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void toggleSelected() {
        PathModel pathModel = filesView.getFocusModel().getFocusedItem();
        if (pathModel == null) {
            return;
        }
        filesView.getSelectionModel().select(pathModel);
        pathModel.toggleMark();
    }

    void refresh() {
        moveTo(currentPath);
    }

    void moveTo(Path path) {
        if (path == null) {
            return;
        }
        updateCurrentPath(path);
        filesView.getItems().clear();
        Path parent = currentPath.getParent();
        if (parent != null) {
            filesView.getItems().add(new PathModel(parent, true));
        }
        List<PathModel> entries = new ArrayList<>();

        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentPath)) {
                for (Path entry : stream) {
                    entries.add(new PathModel(entry));
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        Collections.sort(entries, PathModelComparators.BY_DEFAULT);
        filesView.getItems().addAll(entries);
        moveToFirst();
    }

    private void updateCurrentPath(Path path) {
        // これやらないと相対になったり parent が取れなかったり
        Path normalizedPath = path.toAbsolutePath().normalize();
        currentPath = normalizedPath;
        currentPathLabel.setText(normalizedPath.toString());
    }
}
