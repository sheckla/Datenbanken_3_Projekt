package sample.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.database.Table;

public class Main extends Application {
    final int APP_WIDTH = 1600;
    final int APP_HEIGHT = 1300;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        UIController uiController = new UIController();
        System.out.println(Table.values()[0]);

        Scene scene = new Scene(uiController.createUI());
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.setWidth(APP_WIDTH);
        primaryStage.setHeight(APP_HEIGHT);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
