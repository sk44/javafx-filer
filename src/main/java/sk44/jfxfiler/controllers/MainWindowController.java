/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.jfxfiler.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            leftFilerViewController.moveTo(new File(".").toPath());
            rightFilerViewController.moveTo(new File(".").toPath());
            leftFilerViewController.withOtherFilerView(rightFilerViewController);
            rightFilerViewController.withOtherFilerView(leftFilerViewController);
            leftFilerViewController.focus();
            messageArea.appendText("Ready.");
        } catch (RuntimeException ex) {
            messageArea.appendText(ex.getMessage());
        }
    }
}
