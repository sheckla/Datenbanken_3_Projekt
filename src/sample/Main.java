package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Main extends Application {
    final int APP_WIDTH = 1200;
    final int APP_HEIGHT = 1200;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        //Borderpane https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
        BorderPane border = new BorderPane();

        HBox hbox = createTopNavigation();
        border.setTop(hbox);
        border.setLeft(createLeftNavigation());
        border.setCenter(createCenterNavigation());
        border.setRight(createRightNavigation());
        border.setBottom(createBottomNavigation());
        //border.setLeft(addVBox());

        //addStackPane(hbox);

        //border.setRight(addFlowPane());
        //border.setCenter(addAnchorPane(addGridPane()));

        JDBCUtil util = new JDBCUtil("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@oracle-srv.edvsz.hs-osnabrueck.de:1521/oraclestud",
                "ojokramer", "g4Tbb3Vn0");
        util.getConnection();
        util.printAllFrom(util.getEntries(Table.AUFGABE));

        ArrayList<ArrayList<String>> eintraege = util.getEntries(Table.AUFGABE);

        TextField test = new TextField();
        test.setText(util.getAllStringsFrom(util.getEntries(Table.AUFGABE)));

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
        buttonVorraete.setPrefSize(100, 20);

        Button buttonPersonal = new Button("Personal");
        buttonPersonal.setPrefSize(100,20);

        hbox.getChildren().addAll(buttonMaschinen, buttonSilos, buttonVorraete, buttonPersonal);

        return hbox;
    }

    private VBox createLeftNavigation() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes

        Text title = new Text("Optionen");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);

        Hyperlink options[] = new Hyperlink[] {
                new Hyperlink("Option"),
                new Hyperlink("Option"),
                new Hyperlink("Option"),
                new Hyperlink("Option")};

        for (int i=0; i<4; i++) {
            // Add offset to left side to indent from title
            VBox.setMargin(options[i], new Insets(0, 0, 0, 8));
            vbox.getChildren().add(options[i]);
        }

        return vbox;
    }

    ListView<TextField> textFieldListView = new ListView<>();

    private VBox createCenterNavigation() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes

        Text title = new Text("Data");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);

        vbox.getChildren().add(textFieldListView);
        return vbox;
    }

    private VBox createRightNavigation() {
        return createLeftNavigation();
    }

    private HBox createBottomNavigation() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #FFFFFF;");

        Button buttonMaschinen = new Button("Einfügen");
        buttonMaschinen.setPrefSize(100, 20);

        Button buttonSilos = new Button("Löschen");
        buttonSilos.setPrefSize(100, 20);

        Button buttonVorraete = new Button("Aktualisieren");
        buttonVorraete.setPrefSize(100, 20);

        hbox.getChildren().addAll(buttonMaschinen, buttonSilos, buttonVorraete);

        return hbox;
    }
}
