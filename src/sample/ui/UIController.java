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

// TODO anzahl maximaler angezeigter einträge, seitenanzeige per Intervall (50, 25, 10)
// TODO geänderte werte markieren und bei wechsel fragen ob die veränderung verworfen werden soll

public class UIController {
    String view = "" +
            "select Typ, Marke, Modell " +
            "from Maschine";

    BorderPane border; // main UI element
    Label currentSelectedTable = new Label("");
    Label currentStatement = new Label("");
    Label debug = new Label("");
    private String lastKey = "";

    // TODO textfield -> row anzeigen per col/row
    ListView<GridPane> dataMatrixListView = new ListView<>(); // TODO slider anpassen
    DataMatrix entries;   // enthaelt alle daten vom aktuellen Table (falls gepullt), erste Zeile sind die Spaltennamen
    DataMatrix deletedEntries;
    DataMatrix changedEntries;
    int newEntriesIndex;        // neue eintraege sind ab >= pulledEntries.size()

    JDBCDatabase util;
    Table table;

    public UIController() {
        util = new JDBCDatabase("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@oracle-srv.edvsz.hs-osnabrueck.de:1521/oraclestud",
                "ojokramer", "g4Tbb3Vn0");
        entries = new DataMatrix();
        deletedEntries = new DataMatrix();
        changedEntries = new DataMatrix();


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

    private void refreshDatabaseView() {
        currentStatement.setText("");
        debug.setText(dataMatrixListView.getSelectionModel().getSelectedIndex() + "");
        border.setCenter(createDatabaseView());
        System.out.println(util.createView(view));
    }

    private VBox createDatabaseView() {
        System.out.println("PRIMARY KEYS: " + util.getKeys(table));

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
            for (int i = 0; i < entries.size(); i++) {
                dataMatrixListView.getItems().add(createDataRow(entries.getNodeEntry(i)));
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
            root.setRowIndex(tf, tf.getRow());           // Nur nutwendig für GridPane
            root.setColumnIndex(tf, tf.getCol());        // ^
            root.getChildren().add(tf);
        }
        return root;
    }

    private void pullData() {
        entries.clear();
        deletedEntries.clear();
        changedEntries.clear();

        if (table != null) {
            util.getConnection();
            entries.initializeColumnNames(util.getColumnNames(this.table));
            ArrayList<ArrayList<String>> eintraege = util.getEntries(this.table);

            int row = 1;
            ArrayList<Boolean> nullables = util.getNullables(this.table);
            for (ArrayList<String> entry : util.getEntries(this.table)) {
                entries.addEntry(entry, row++);
                entries.markNullables(nullables, row - 1);
            }
            newEntriesIndex = entries.size();
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

    private VBox createBottomNavigation() {
        VBox root = new VBox();
        root.setSpacing(8);
        root.setPadding(new Insets(15, 12, 15, 12));

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #FFFFFF;");

        Button buttonEinfuegen = new Button("Neuer Eintrag");
        buttonEinfuegen.setPrefSize(100, 20);
        buttonEinfuegen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lastKey = entries.getLatestEntry().get(0);
                // TODO neuer eintrag automatische werte für z.B InventarNr, Erstelldatum etc.
                // TODO immer leeres feld, onbuttonlistener - ENTER
                // TODO höchsten key anders pullen - sortiert
                entries.addEmptyEntry(util.getNullables(table), util.getColumnSize(table));
                try {
                    entries.getNodeEntry(entries.size() - 1).get(0).setText("" + (1 + Integer.parseInt(lastKey)));
                    entries.getNodeEntry(entries.size()-1).get(0).setChanged(false);
                } catch (NumberFormatException e) {
                    System.out.println("Key not a number");
                }
                //printMatrix();
                refreshDatabaseView();
            }
        });

        Button buttonCommit = new Button("Commit!");
        buttonCommit.setPrefSize(100, 20);
        buttonCommit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO zur datenbank pushen
                // TODO Commit texte
                for (int i = newEntriesIndex; i < entries.size(); i++) {
                    //System.out.println("size" + (entries.size() - newEntriesIndex));
                    String insertResultText = util.insert(table, entries.getEntry(i));
                    if (!insertResultText.equals("")) currentStatement.setText(insertResultText);
                    currentStatement.setText("Commit erfolgreich!");
                    if (!insertResultText.equals("")) currentStatement.setText(insertResultText);
                }

                for (int i = 0; i < deletedEntries.size(); i++) {
                    String val = deletedEntries.getVal(0, i);
                    util.delete(table, val);
                }

                locateChangedEntries();
                for (Integer i : locateChangedEntries()) {
                    System.out.println(util.update(table, entries.getInitialEntry(i), entries.getEntry(i)));
                }
            }
        });

        Button buttonLoeschen = new Button("Löschen");
        buttonLoeschen.setPrefSize(100, 20);
        buttonLoeschen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = dataMatrixListView.getSelectionModel().getSelectedIndex();
                deletedEntries.addEntry(entries.getEntry(index), deletedEntries.size());
                entries.removeEntry(index);
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

        hbox.getChildren().addAll(buttonEinfuegen, buttonCommit, buttonLoeschen, buttonAktualisieren);

        root.getChildren().add(hbox);
        //currentStatement.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
        root.getChildren().add(currentStatement);
        root.getChildren().add(debug);
        return root;
    }

     private ArrayList<Integer> locateChangedEntries() {
        ArrayList<Integer> changed = new ArrayList<>();
        for (int i = 1; i < entries.size(); i++) {
            for (DataTextFieldNode node : entries.getNodeEntry(i)) {
                if (node.changed()) {
                    changed.add(i);
                    break;
                }
            }
        }
        return changed;
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
        for (ArrayList<String> arr : entries.getStringMatrix()) {
            for (String s : arr) {
                System.out.print(s + ", ");
            }
            System.out.println();
        }
    }

}
