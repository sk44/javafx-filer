/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.jfxfiler.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author sk
 */
public class CommandLineViewModel {

    public enum Command {

        CREATE_DIRECTORY("Enter new directory name.") {
                @Override
                void execute(CommandLineViewModel model, FilesViewModel filesViewModel) {
                    Path newDir = filesViewModel
                    .getCurrentPath()
                    .resolve(model.commandProperty().get());
                    if (Files.exists(newDir)) {
                        MessageModel.warn(newDir.toString() + " is already exists.");
                    }
                    try {
                        Files.createDirectories(newDir);
                        MessageModel.info("directory created: " + newDir.toString());
                        filesViewModel.updateFiles();
                        model.exitCommandMode();
                    } catch (IOException ex) {
                        MessageModel.error("creating directory failed: " + newDir.toString());
                        MessageModel.error(ex);
                    }
                }
            },
        SEARCH("Enter search keyword.") {
                @Override
                void execute(CommandLineViewModel model, FilesViewModel filesViewModel) {
                    model.lastSearchPattern = model.commandProperty.get();
                    filesViewModel.selectNext(model.lastSearchPattern);
                }
            };

        private final String promptText;

        private Command(String promptText) {
            this.promptText = promptText;
        }

        abstract void execute(CommandLineViewModel model, FilesViewModel filesViewModel);
    }

    private Command command;
    private final BooleanProperty commandModeProperty = new SimpleBooleanProperty(false);
    private final StringProperty commandProperty = new SimpleStringProperty();
    private final StringProperty commandPromptTextProperty = new SimpleStringProperty();
    private String lastSearchPattern;

    public boolean handleKeyEvent(KeyEvent event, FilesViewModel filesViewModel) {
        if (commandModeProperty.get() == false) {
            return false;
        }
        switch (event.getCode()) {
            case ENTER:
                executeCommand(filesViewModel);
                return true;
            case ESCAPE:
                clearCommand();
                exitCommandMode();
                return true;
            case Q:
                return false;
            case H:
                if (event.isControlDown()) {
                    deleteLastChar();
                    return true;
                }
                addTextToCommand(event.getText());
                return true;
            case BACK_SPACE:
                deleteLastChar();
                return true;
            default:
                addTextToCommand(event.getText());
                return true;
        }
    }

    public void enterCommandMode(Command command) {
        this.command = command;
        commandModeProperty.set(true);
        commandPromptTextProperty.set(command.promptText);
        commandProperty.set("");
    }

    public void executeCommand(FilesViewModel filesViewModel) {
        if (isCommandSet() == false) {
            MessageModel.warn("please input directory name to create.");
            return;
        }
        command.execute(this, filesViewModel);
    }

    public void exitCommandMode() {
        commandModeProperty.set(false);
        // TODO enum の方に処理をもたせる
        if (command != Command.SEARCH) {
            commandProperty.set("");
            commandPromptTextProperty.set("");
        }
    }

    public void searchNext(FilesViewModel filesViewModel) {
        if (isLastSearchPatternSet() == false) {
            return;
        }

        filesViewModel.selectNext(lastSearchPattern);
    }

    public void searchPrevious(FilesViewModel filesViewModel) {
        if (isLastSearchPatternSet() == false) {
            return;
        }

        filesViewModel.selectPrevious(lastSearchPattern);
    }

    private boolean isLastSearchPatternSet() {
        if (lastSearchPattern == null || lastSearchPattern.length() == 0) {
            MessageModel.warn("search keyword does not set.");
            return true;
        }
        return false;
    }

    private void clearCommand() {
        commandProperty().set("");
    }

    private void addTextToCommand(String text) {
        commandProperty().set(commandProperty.get() + text);
    }

    private void deleteLastChar() {
        String command = commandProperty().get();
        if (command == null || command.isEmpty()) {
            return;
        }
        commandProperty().set(command.substring(0, command.length() - 1));
    }

    public boolean isCommandSet() {
        String inputValue = commandProperty.get();
        return inputValue != null && inputValue.length() > 0;
    }

    public BooleanProperty commandModeProperty() {
        return commandModeProperty;
    }

    public StringProperty commandProperty() {
        return commandProperty;
    }

    public StringProperty commandPromptTextProperty() {
        return commandPromptTextProperty;
    }

}
