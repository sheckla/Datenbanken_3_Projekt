package sample.graphics;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import sample.JDBCDatabase;
import sample.Table;

import java.util.ArrayList;

public class UIController {
    BorderPane border;
    Table table;

    public UIController() {
    }

    public BorderPane createUI() {
        //Borderpane https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
        border = new BorderPane();

        HBox hbox = createTopNavigation();
        border.setTop(hbox);
        border.setLeft(createLeftNavigation());
        border.setCenter(createDatabaseView());
        border.setRight(createRightNavigation());
        border.setBottom(createBottomNavigation());
        return border;
    }

    public void changeTable(Table table) {
        this.table = table;
    }

    private HBox createTopNavigation() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #FFFFFF;");
        Label selected = new Label("Default");

        Button buttonAufgabe = new Button("Aufgaben");
        buttonAufgabe.setPrefSize(100, 20);
        buttonAufgabe.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                table = Table.AUFGABE;
                selected.setText(replaceUmlaute(table.toString() + " is selected"));
                border.setCenter(createDatabaseView());
            }
        });

        Button buttonPersonal = new Button("Personal");
        buttonPersonal.setPrefSize(100, 20);
        buttonPersonal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                table = Table.PERSONAL;
                selected.setText(replaceUmlaute(table.toString() + " is selected"));
                border.setCenter(createDatabaseView());
            }
        });

        Button buttonMaschine = new Button("Maschinen");
        buttonMaschine.setPrefSize(100, 20);
        buttonMaschine.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                table = Table.MASCHINE;
                selected.setText(replaceUmlaute(table.toString() + " is selected"));
                border.setCenter(createDatabaseView());
            }
        });

        // Inventar dropdown
        String inventar[] = {"Inventargegenstand", "Vorräte", "Lagersilo", "Maschine"};
        ComboBox inventarComboBox = new ComboBox(FXCollections.observableArrayList(inventar));
        inventarComboBox.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                selected.setText(replaceUmlaute(inventarComboBox.getValue().toString()) + " is selected");
                table = Table.valueOf(replaceUmlaute(inventarComboBox.getValue().toString()));
                border.setCenter(createDatabaseView());
            }
        });
        TilePane tilePane = new TilePane(inventarComboBox);
        inventarComboBox.getSelectionModel().selectFirst();
        inventarComboBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (!isShowing) {
                selected.setText(replaceUmlaute(inventarComboBox.getValue().toString()) + " is selected");
                table = Table.valueOf(replaceUmlaute(inventarComboBox.getValue().toString()));
                border.setCenter(createDatabaseView());
            }
        });

        hbox.getChildren().addAll(inventarComboBox, buttonAufgabe, buttonPersonal, buttonMaschine, selected);
        return hbox;
    }

    private VBox createLeftNavigation() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes

        Text title = new Text("Optionen");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);

        Hyperlink options[] = new Hyperlink[]{
                new Hyperlink("Option"),
                new Hyperlink("Option"),
                new Hyperlink("Option"),
                new Hyperlink("Option")};

        for (int i = 0; i < 4; i++) {
            // Add offset to left side to indent from title
            VBox.setMargin(options[i], new Insets(0, 0, 0, 8));
            vbox.getChildren().add(options[i]);
        }

        return vbox;
    }

    private VBox createDatabaseView() {
        ListView textFieldListView = new ListView(); // TODO slider anpassen
        textFieldListView.setPrefHeight(2160);
        textFieldListView.setPrefWidth(4096);
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes

        Text title = new Text("Daten");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);

        if (table != null) {
            JDBCDatabase util = new JDBCDatabase("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@oracle-srv.edvsz.hs-osnabrueck.de:1521/oraclestud",
                    "ojokramer", "g4Tbb3Vn0");
            util.getConnection();
            util.printAllFrom(util.getEntries(this.table));

            ArrayList<ArrayList<String>> eintraege = util.getEntries(this.table);
            for (ArrayList<String> entry : eintraege) {
                String row = "";
                for (String s : entry) {
                    row += s + ", ";
                }
            }

            textFieldListView.getItems().add(createColumnNames(util.getColumnNames(this.table)));
            for (int i = 0; i < eintraege.size(); i++) {
                textFieldListView.getItems().add(createRow(eintraege.get(i), i));
            }
            vbox.getChildren().add(textFieldListView);
        }
        return vbox;
    }

    private GridPane createColumnNames(ArrayList<String> columns) {
        GridPane root = new GridPane();
        for (int x = 0; x < columns.size(); x++) {

            // Create a new TextField in each Iteration
            TextField tf = new TextField();
            tf.setPrefHeight(20);
            tf.setPrefWidth(150);
            tf.setAlignment(Pos.CENTER);
            tf.setEditable(false);
            tf.setStyle("-fx-font-weight: bold");
            tf.setText(columns.get(x));
            tf.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    //tf.setPrefWidth(tf.getWidth() + 2); //TODO dynamische verbreitung je nach textlänge
                }
            });

            // Iterate the Index using the loops
            root.setRowIndex(tf, 0);
            root.setColumnIndex(tf, x);
            root.getChildren().add(tf);
        }
        return root;
    }

    // TODO Varchar constraints für charlimit
    private GridPane createRow(ArrayList<String> row, int index) {
        GridPane root = new GridPane();

        for (int x = 1; x <= row.size(); x++) {
            TextField tf = new TextField();
            tf.setPrefHeight(20);
            tf.setPrefWidth(150);
            tf.setAlignment(Pos.CENTER);
            tf.setEditable(true);
            tf.setText(row.get(x - 1));
            tf.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    //tf.setPrefWidth(tf.getWidth() + 2); //TODO dynamische verbreitung je nach textlänge
                }
            });

            // Iterate the Index using the loops
            root.setRowIndex(tf, index);
            root.setColumnIndex(tf, x);
            root.getChildren().add(tf);
        }
        return root;
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

    private String replaceUmlaute(String s) {
        s = s.toLowerCase();
        s = s.replaceAll("ä", "ae");
        s = s.replaceAll("ü", "ue");
        s = s.replaceAll("ö", "oe");
        s = s.replaceAll("ß", "ss");
        s = s.toUpperCase();
        return s;
    }
}
