package sample.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import sample.database.DataMatrix;
import sample.database.DataTextFieldNode;
import sample.database.JDBCDatabase;
import sample.database.Table;

import java.util.ArrayList;

// TODO anzahl maximaler angezeigter einträge
// TODO geänderte werte markieren und bei wechsel fragen ob die veränderung verworfen werden soll

public class UIController {
    BorderPane border; // main UI element
    Label currentSelectedTable = new Label("Default");
    Label currentStatement = new Label("Default");

    ListView dataMatrixListView = new ListView(); // TODO slider anpassen
    DataMatrix dataMatrix; // enthaelt alle daten vom aktuellen Table (falls gepullt), erste Zeile sind die Spaltennamen
    JDBCDatabase util;
    Table table;
    boolean newEntryActive = false;

    public UIController() {
        util = new JDBCDatabase("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@oracle-srv.edvsz.hs-osnabrueck.de:1521/oraclestud",
                "ojokramer", "g4Tbb3Vn0");
        dataMatrix = new DataMatrix();
    }

    public BorderPane createUI() {
        //Borderpane https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
        border = new BorderPane();

        HBox hbox = createTopNavigation();
        border.setTop(hbox);
        border.setLeft(createLeftNavigation());
        border.setRight(createRightNavigation());
        border.setBottom(createBottomNavigation());
        return border;
    }

    public void changeTable(Table table) {
        this.table = table;
        currentSelectedTable.setText(replaceUmlaute(table.toString() + " is selected")); // TODO zentralisieren mit Zeitanzeige
        pullData();
        border.setCenter(createDatabaseView());
    }

    // updated die dataMatrix
    private void refreshDatabaseView() {
        border.setCenter(createDatabaseView());
    }

    private VBox createDatabaseView() {

        dataMatrixListView = new ListView(); // TODO slider anpassen, optimieren (wird zu oft ausgeführt)
        dataMatrixListView.setPrefHeight(2160);
        dataMatrixListView.setPrefWidth(4096);
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes

        Text title = new Text("Daten");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);

        if (table != null) {
            for (int i = 0; i < dataMatrix.size(); i++) {
                dataMatrixListView.getItems().add(createDataRow(dataMatrix.getNodeEntry(i)));
            }
            vbox.getChildren().add(dataMatrixListView);
        }

        return vbox;
    }

    // TODO Varchar constraints für charlimit
    private GridPane createDataRow(ArrayList<DataTextFieldNode> entry) {
        GridPane root = new GridPane();
        int i = 0;
        for (DataTextFieldNode tf : entry) {
            root.setRowIndex(tf, 0);       // Nur nutwendig für GridPane
            root.setColumnIndex(tf, i++);        // ^
            root.getChildren().add(tf);
        }
        return root;
    }

    private void pullData() {
        dataMatrix.clear();
        if (table != null) {
            util.getConnection();
            dataMatrix.initializeColumnNames(util.getColumnNames(this.table));
            ArrayList<ArrayList<String>> eintraege = util.getEntries(this.table);
            int rowIndex = 1;
            for (ArrayList<String> entry : util.getEntries(this.table)) {
                dataMatrix.addEntry(entry, rowIndex++);
            }
        }
    }

    private HBox createTopNavigation() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #FFFFFF;");

        Button buttonAufgabe = new Button("Aufgaben");
        buttonAufgabe.setPrefSize(100, 20);
        buttonAufgabe.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeTable(Table.AUFGABE);
            }
        });

        Button buttonPersonal = new Button("Personal");
        buttonPersonal.setPrefSize(100, 20);
        buttonPersonal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeTable(Table.PERSONAL);
            }
        });

        Button buttonMaschine = new Button("Maschinen");
        buttonMaschine.setPrefSize(100, 20);
        buttonMaschine.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeTable(Table.MASCHINE);
            }
        });

        // Inventar dropdown
        String inventar[] = {"Inventargegenstand", "Vorräte", "Lagersilo", "Maschine"};
        ComboBox inventarComboBox = new ComboBox(FXCollections.observableArrayList(inventar));
        inventarComboBox.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                changeTable(Table.valueOf(replaceUmlaute(inventarComboBox.getValue().toString())));
            }
        });
        TilePane tilePane = new TilePane(inventarComboBox);
        inventarComboBox.getSelectionModel().selectFirst();
        inventarComboBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
            if (!isShowing) {
                changeTable(Table.valueOf(replaceUmlaute(inventarComboBox.getValue().toString())));
            }
        });

        hbox.getChildren().addAll(inventarComboBox, buttonAufgabe, buttonPersonal, buttonMaschine, currentSelectedTable);
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

    private VBox createRightNavigation() {
        return createLeftNavigation();
    }

    private HBox createBottomNavigation() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #FFFFFF;");

        Button buttonEinfuegen = new Button("Neuer Eintrag");
        buttonEinfuegen.setPrefSize(100, 20);
        buttonEinfuegen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //util.insertTest(Table.INVENTARGEGENSTAND, 1);
                if (!newEntryActive) {
                    newEntryActive = false; // TODO Designentscheidung
                    System.out.println(util.insertStatementColumns(table));
                    dataMatrix.addEmptyEntry();
                    printMatrix();
                    refreshDatabaseView();
                }
            }
        });

        Button buttonCommit = new Button("Commit!");
        buttonCommit.setPrefSize(100, 20);
        buttonCommit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO zur datenbank pushen
                System.out.println(dataMatrix.getLatestEntry().get(0));
                System.out.println("size" + dataMatrix.size());
                String val = dataMatrix.getLatestEntry().get(0);
                util.insert(table, val);
                currentStatement.setText(util.getInsertStatement(table, val));
            }
        });

        Button buttonLoeschen = new Button("Löschen");
        buttonLoeschen.setPrefSize(100, 20);
        buttonLoeschen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dataMatrix.removeEntry(dataMatrix.size() - 1); // TODO nur visuell! noch keine datenbankveränderung
                refreshDatabaseView();
            }
        });

        Button buttonAktualisieren = new Button("Aktualisieren");
        buttonAktualisieren.setPrefSize(100, 20);
        buttonAktualisieren.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pullData();
                refreshDatabaseView();
            }
        });

        hbox.getChildren().addAll(buttonEinfuegen, buttonCommit, buttonLoeschen, buttonAktualisieren, currentStatement);

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

    private void printMatrix() {
        for (ArrayList<String> arr : dataMatrix.getStringMatrix()) {
            for (String s : arr) {
                System.out.print(s + ", ");
            }
            System.out.println();
        }
    }

}
