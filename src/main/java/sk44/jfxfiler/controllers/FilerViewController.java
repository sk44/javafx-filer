/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.jfxfiler.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import sk44.jfxfiler.models.CommandLineViewModel;
import sk44.jfxfiler.models.FilesViewModel;
import sk44.jfxfiler.models.MessageModel;
import sk44.jfxfiler.models.PathModel;
import sk44.jfxfiler.views.TextAlignmentCellFactory;

/**
 *
 * @author sk
 */
public class FilerViewController implements Initializable {

    private static Path normalizePath(Path path) {
        return path.toAbsolutePath().normalize();
    }

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
    @FXML
    private TextField commandField;
    private FilerViewController otherFilerView;
    private final FilesViewModel filesViewModel = new FilesViewModel();
    private final CommandLineViewModel commandLineViewModel = new CommandLineViewModel();

    @FXML
    protected void handleCommandAction(ActionEvent event) {
        commandLineViewModel.executeCommand(filesViewModel);
        focus();
        refresh();
    }

    @FXML
    protected void handleCommandKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case ESCAPE:
                commandLineViewModel.exitCommandMode();
                focus();
                break;
            case TAB:
                commandLineViewModel.exitCommandMode();
                otherFilerView.focus();
                break;
        }
    }

    @FXML
    protected void handleKeyPressedInTable(KeyEvent event) {

        // TODO モードを考える必要がある（通常、検索入力中、検索中 etc ）
        // TODO フォーカスするとスラッシュが入ってしまうのでフォーカスせずに有効な入力値だけパス
        if (commandLineViewModel.handleKeyEvent(event, filesViewModel)) {
            return;
        }

        switch (event.getCode()) {
            case C:
                copy();
                break;
            case D:
                delete();
                break;
            case G:
                if (event.isShiftDown()) {
                    selectLast();
                } else {
                    selectFirst();
                }
                break;
            case H:
            case LEFT:
                moveTo(filesViewModel.getParentPath(), true);
                break;
            case J:
                selectNext();
                break;
            case K:
                selectPrevious();
                break;
            case L:
                goForward();
                break;
            case M:
                // TODO "M" が入力されてしまう
                if (event.isShiftDown()) {
//                if (event.isControlDown()) {
//                    event.consume();
                    enterCommandMode(CommandLineViewModel.Command.CREATE_DIRECTORY);
                }
                break;
            case N:
                if (event.isShiftDown()) {
                    commandLineViewModel.searchPrevious(filesViewModel);
                } else {
                    commandLineViewModel.searchNext(filesViewModel);
                }
                break;
            case O:
                if (event.isShiftDown()) {
                    otherFilerView.moveTo(filesViewModel.getCurrentPath());
                } else {
                    moveTo(otherFilerView.filesViewModel.getCurrentPath());
                }
                break;
            case Q:
                Platform.exit();
                break;
            case X:
                openAssosiated();
                break;
            case Y:
                filesViewModel.yankCurrentPath();
                break;
            case ENTER:
                if (event.isControlDown()) {
                    openAssosiated();
                }
                break;
            case SPACE:
                filesViewModel.toggleSelected();
                selectNext();
                break;
            case RIGHT:
                goForward();
                break;
            case TAB:
                otherFilerView.focus();
                break;
            case LESS:
                moveTo(filesViewModel.getRootPath());
                break;
            case SLASH:
                // TODO スラッシュがつく
                enterCommandMode(CommandLineViewModel.Command.SEARCH);
                break;
        }
    }

    void withOtherFilerView(FilerViewController other) {
        otherFilerView = other;
    }

    void copy() {
        otherFilerView.copyFrom(filesViewModel.collectMarked());
    }

    void copyFrom(List<PathModel> pathes) {
        filesViewModel.copyFrom(pathes);
        refresh();
    }

    void delete() {
        filesViewModel.deleteMarked();
        refresh();
    }

    void focus() {
        Platform.runLater(filesView::requestFocus);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        filesView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        filesView.setItems(filesViewModel.getFiles());
        // 右辺にからじゃないものをしていするひつようがある
        filesViewModel.focusModelProperty().bindBidirectional(filesView.focusModelProperty());
        filesViewModel.selectionModelProperty().bindBidirectional(filesView.selectionModelProperty());
        currentPathLabel.textProperty().bind(filesViewModel.currentPathValueProperty());

        markColumn.setCellValueFactory(p -> p.getValue().markValueProperty());
        markColumn.setCellFactory(new TextAlignmentCellFactory<>(TextAlignmentCellFactory.Alignment.RIGHT));
        nameColumn.setCellValueFactory(p -> p.getValue().nameProperty());
        typeColumn.setCellValueFactory(p -> p.getValue().typeProperty());
        sizeColumn.setCellValueFactory(p -> p.getValue().sizeValueProperty());
        sizeColumn.setCellFactory(new TextAlignmentCellFactory<>(TextAlignmentCellFactory.Alignment.RIGHT));
        lastModifiedColumn.setCellValueFactory(p -> p.getValue().lastModifiedProperty());
        // automatic width
        nameColumn.prefWidthProperty().bind(filesView.widthProperty().subtract(295));

        // TODO disabled 制御
//        commandField.disableProperty().bind(commandLineViewModel.commandModeProperty().not());
        commandField.disableProperty().set(true);

        commandField.textProperty().bindBidirectional(commandLineViewModel.commandProperty());
        commandField.promptTextProperty().bind(commandLineViewModel.commandPromptTextProperty());
        commandField.focusedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean oldValue, Boolean newValue) -> {
            if (newValue == false) {
                commandLineViewModel.exitCommandMode();
            }
        });
    }

    private void enterCommandMode(CommandLineViewModel.Command command) {
        commandLineViewModel.enterCommandMode(command);
//        commandField.requestFocus();
        MessageModel.info(commandField.getPromptText());
    }

    private void selectPrevious() {
        final int index = filesViewModel.getFocusedIndex();
        filesViewModel.select(index - 1);
        scrollToFocused();
    }

    private void selectNext() {
        final int index = filesViewModel.getFocusedIndex();
        filesViewModel.select(index + 1);
        scrollToFocused();
    }

    private void selectFirst() {
        filesViewModel.select(0);
        scrollToFocused();
    }

    private void selectLast() {
        filesViewModel.select(filesViewModel.count() - 1);
        scrollToFocused();
    }

    private void scrollToFocused() {
        // runLater 内でスクロールを呼ばないと変な位置にスクロールしてしまう
        Platform.runLater(() -> {
            filesView.scrollTo(filesViewModel.getFocusedIndex());
        });
    }

    private void goForward() {
        moveTo(filesViewModel.getFocusedModel().getDirectoryPath());
    }

    private void openAssosiated() {
        // TODO Mac で Desktop.getDesktop をサポートしてない疑い
        // http://stackoverflow.com/questions/14964376/java-getdesktop-open-works-in-windows-but-doesnt-work-in-mac
        if (Desktop.isDesktopSupported() == false) {
            MessageModel.warn("Desktop.getDesktop() does not supported.");
            return;
        }
        PathModel pathModel = filesViewModel.getFocusedModel();
        if (pathModel == null) {
            return;
        }
        try {
            Desktop.getDesktop().open(pathModel.getPath().toFile());
        } catch (IOException | RuntimeException ex) {
            MessageModel.error(ex);
        }
    }

    void refresh() {
        moveTo(filesViewModel.getCurrentPath());
    }

    void moveTo(Path path) {
        moveTo(path, false);
    }

    void moveTo(Path path, boolean goParent) {
        if (path == null) {
            return;
        }
        filesViewModel.moveTo(path, goParent);
//        selectFirst();
        scrollToFocused();
    }

}
