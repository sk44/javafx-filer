/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.fxfiler.interfaces.javafx;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

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
    private TableColumn<PathModel, String> nameColumn;
    @FXML
    private TableColumn<PathModel, String> infoColumn;
    @FXML
    private TableColumn<PathModel, String> lastModifiedColumn;
    private FilerViewController otherFilerView;
    // TODO bind
    private Path currentPath;

    @FXML
    protected void handleKeyPressedInTable(KeyEvent event) {
        System.out.println("key pressed in table: " + event.getCode());
        switch (event.getCode()) {
            case G:
                if (event.isShiftDown()) {
                    focusLast();
                } else {
                    focusFirst();
                }
                break;
            case H:
            case LEFT:
                moveTo(currentPath.getParent());
                break;
            case J:
                focusNext();
                break;
            case K:
                focusPrevious();
                break;
            case L:
            case RIGHT:
                goForward();
                break;
            case O:
                // TODO
                break;
            case TAB:
                otherFilerView.focus();
                break;
            case DOWN:
                // TODO
                break;
            case LESS:
                moveTo(currentPath.getRoot());
                break;
        }
    }

    void withOtherFilerView(FilerViewController other) {
        otherFilerView = other;
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
        filesView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PathModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PathModel, String> p) {
                return new SimpleStringProperty(p.getValue().getName());
            }
        });
        infoColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PathModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PathModel, String> p) {
                return new SimpleStringProperty(p.getValue().getInfo());
            }
        });
        lastModifiedColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PathModel, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PathModel, String> p) {
                return new SimpleStringProperty(p.getValue().getLastModified().toString("yy/MM/dd HH:mm:ss"));
            }
        });
        // automatic width
        nameColumn.prefWidthProperty().bind(filesView.widthProperty().subtract(240));
    }

    private void focusPrevious() {
        filesView.getFocusModel().focusPrevious();
        scrollToFocused();
    }

    private void focusNext() {
        filesView.getFocusModel().focusNext();
        scrollToFocused();
    }

    private void focusFirst() {
        filesView.getFocusModel().focus(0);
        scrollToFocused();
    }

    private void focusLast() {
        filesView.getFocusModel().focus(filesView.getItems().size() - 1);
        scrollToFocused();
    }

    private void scrollToFocused() {
        filesView.scrollTo(filesView.getFocusModel().getFocusedIndex());
    }

    private void goForward() {
        moveTo(filesView.getFocusModel().getFocusedItem().getDirectoryPath());
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
//        filesView.getSelectionModel().clearAndSelect(0);
        filesView.getFocusModel().focus(0);
    }

    private void updateCurrentPath(Path path) {
        // これやらないと相対になったり parent が取れなかったり
        Path normalizedPath = path.toAbsolutePath().normalize();
        currentPath = normalizedPath;
        currentPathLabel.setText(normalizedPath.toString());
    }
}
