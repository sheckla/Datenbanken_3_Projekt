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

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

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

        JDBC_Test();

        Scene scene = new Scene(border);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void JDBC_Test() {
        // Treiber in IDE laden
        // Variante 1: maven: Pom.xml ausfüllen, maven tool -> Install
        // Variante 2:Teiber koperen

        // Treiber bekannt machen

        Connection con;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver"); // Treiber bekanntgemacht
            // Verbindung zur DB herstellen
            con = DriverManager.getConnection("jdbc:oracle:thin:@oracle-srv.edvsz.hs-osnabrueck.de:1521/oraclestud",
                    "ojokramer", "g4Tbb3Vn0");

            // Statement erstellen
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("select gehalt from angestellter where gehalt > 40000");


            // Ergebnis verarbeiten
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("gehalt"));
            }

            ArrayList<String> al = new ArrayList();
            Iterator iterator = al.iterator();
            while (iterator.hasNext()) {
                String next = (String) iterator.next();
            }


            PreparedStatement preparedStatement = con.prepareStatement("select gehalt from angestellter where gehalt > ?");
            preparedStatement.setInt(1, 40000);
            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData1 = resultSet.getMetaData();
            metaData1.getColumnCount();


            // Ergebnis verarbeiten
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("gehalt"));
            }

            DatabaseMetaData metaData = con.getMetaData();
            System.out.println(metaData.getDatabaseProductName());
            System.out.println(metaData.getDefaultTransactionIsolation());
            con.setAutoCommit(false);

            resultSet.close();
            statement.close();
            preparedStatement.close();
            con.close();

        } catch (ClassNotFoundException | SQLException e) {
             e.printStackTrace();
        }
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
