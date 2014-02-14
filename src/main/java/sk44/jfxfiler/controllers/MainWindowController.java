/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.jfxfiler.controllers;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import sk44.jfxfiler.models.MessageModel;

/**
 *
 * @author sk
 */
public class MainWindowController implements Initializable {

    @FXML
    private Pane rightFilerView;
    @FXML
    private FilerViewController rightFilerViewController;
    @FXML
    private Pane leftFilerView;
    @FXML
    private FilerViewController leftFilerViewController;
    @FXML
    private TextArea messageArea;
    private final MessageModel messageModel = new MessageModel("Ready.");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Path initialPath = initialPath();
        leftFilerViewController.moveTo(initialPath);
        rightFilerViewController.moveTo(initialPath);
        leftFilerViewController.withOtherFilerView(rightFilerViewController);
        rightFilerViewController.withOtherFilerView(leftFilerViewController);
        leftFilerViewController.focus();
        // TODO bind だと自動でスクロールしない
        messageArea.appendText("Ready.");
//        messageArea.textProperty().bind(messageModel.messageProperty());
        MessageModel.addObserver(new MessageModel.Observer() {

            @Override
            public void update(String message) {
//                messageModel.appendMessage(message);
                messageArea.appendText("\n" + message);
            }
        });
    }

    private Path initialPath() {
        // TODO home とか外部設定とか
        return new File(".").toPath();
    }
}
