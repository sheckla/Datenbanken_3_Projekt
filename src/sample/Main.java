package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {
    final int APP_WIDTH = 500;
    final int APP_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        //Borderpane https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
        BorderPane border = new BorderPane();

        HBox hbox = createTopNavigation();
        border.setTop(hbox);
        //border.setLeft(addVBox());

        //addStackPane(hbox);

        //border.setRight(addFlowPane());
        //border.setCenter(addAnchorPane(addGridPane()));

        Scene scene = new Scene(border);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private HBox createTopNavigation() {

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #FFFFFF;");

        Button buttonMaschinen = new Button("Maschinen");
        buttonMaschinen.setPrefSize(100, 20);

        Button buttonSilos = new Button("Silos");
        buttonSilos.setPrefSize(100, 20);

        Button buttonVorraete = new Button("Vorräte");
        buttonVorraete.setPrefSize(100,20);

        hbox.getChildren().addAll(buttonMaschinen, buttonSilos, buttonVorraete);

        return hbox;
    }
}
