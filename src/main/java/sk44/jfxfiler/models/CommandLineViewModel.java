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

/**
 *
 * @author sk
 */
public class CommandLineViewModel {

    public enum Command {

        // TODO つかう
        CREATE_DIRECTORY;
    }

    private Command command;
    private final BooleanProperty commandModeProperty = new SimpleBooleanProperty(false);
    private final StringProperty commandProperty = new SimpleStringProperty();

    public void enterCommandMode(Command command) {
        this.command = command;
        commandModeProperty.set(true);
        commandProperty.set("");
    }

    public void executeCommand(Path currentDirectory) {
        if (isCommandSet() == false) {
            MessageModel.warn("please input directory name to create.");
            return;
        }
        Path newDir = currentDirectory.resolve(commandProperty.get());
        if (Files.exists(newDir)) {
            MessageModel.warn(newDir.toString() + " is already exists.");
        }
        try {
            Files.createDirectories(newDir);
            MessageModel.info("directory created: " + newDir.toString());
            exitCommandMode();
        } catch (IOException ex) {
            MessageModel.error("creating directory failed: " + newDir.toString());
            MessageModel.error(ex);
        }
    }

    public void exitCommandMode() {
        commandModeProperty.set(false);
        commandProperty.set("");
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

}
