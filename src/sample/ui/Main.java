package sample.ui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.database.Table;

public class Main extends Application {
    final int APP_WIDTH = 1100;
    final int APP_HEIGHT = 500;
    UIController uiController;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        uiController = new UIController();

        Scene scene = new Scene(uiController.createUI());
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.setWidth(APP_WIDTH);
        primaryStage.setHeight(APP_HEIGHT);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                uiController.closeDatabaseConnection();
                System.out.println("See you next time");
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }

}
