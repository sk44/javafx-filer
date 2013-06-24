package sk44.fxfiler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FilerApplication extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Filer");
        Scene scene = new Scene((Pane) FXMLLoader
            .load(getClass().getResource("interfaces/javafx/mainWindow.fxml")));
        stage.setScene(scene);
        stage.show();
    }
}
