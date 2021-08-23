package sample.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import sample.database.DataMatrix;
import sample.database.DataTextFieldNode;
import sample.database.JDBCDatabase;
import sample.database.Table;
import sample.database.TableView;
import sample.layoutTest.LayoutSample_TEST;

import java.util.*;

// TODO anzahl maximaler angezeigter einträge, seitenanzeige per Intervall (50, 25, 10)
// TODO geänderte werte markieren und bei wechsel fragen ob die veränderung verworfen werden soll

public class UIController {
    BorderPane border; // main UI element
    Label currentSelectedTable = new Label("");
    Label currentStatement = new Label("");
    Label debug = new Label("");

    // TODO textfield -> row anzeigen per col/row
    ListView<GridPane> dataMatrixListView = new ListView<>(); // TODO slider anpassen

    // pulled entries = [0,newEntriesIndex-1], new Entries = [newEntriesIndex,entries.size()]
    DataMatrix entries;   // enthaelt alle daten vom aktuellen Table (falls gepullt), erste Zeile sind die Spaltennamen
    DataMatrix deletedEntries;
    DataMatrix changedEntries;
    int newEntriesIndex;        // neue eintraege sind ab >= pulledEntries.size()

    JDBCDatabase util;
    TableView table;
    ArrayList<TableView> tableViews = new ArrayList<>();
    java.util.HashMap<String, TableView> tableMap = new java.util.HashMap();

    public UIController() {
        util = new JDBCDatabase("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@oracle-srv.edvsz.hs-osnabrueck.de:1521/oraclestud",
                "oSoSe2021_G7", "oSoSe2021_G7");
        entries = new DataMatrix();
        deletedEntries = new DataMatrix();
        changedEntries = new DataMatrix();
        createTables();
    }

    public BorderPane createUI() {
        //Borderpane https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
        border = new BorderPane();
        border.setTop(createTopNavigation());
        border.setLeft(createLeftNavigation());
        border.setRight(createRightNavigation());
        border.setBottom(createBottomNavigation());
        return border;
    }

    public void changeTable(TableView table) {
        this.table = table;
        currentSelectedTable.setText(replaceUmlaute(table.toString() + " is selected")); // TODO zentralisieren mit Zeitanzeige
        pullData();
        border.setCenter(createDatabaseView());
        border.setRight(createRightNavigation());
    }

    private void refreshDatabaseView() {
        currentStatement.setText("");
        debug.setText(dataMatrixListView.getSelectionModel().getSelectedIndex() + "");
        border.setCenter(createDatabaseView());
    }

    private VBox createDatabaseView() {
        System.out.println("PRIMARY KEYS: " + util.getKeys(table.toString()));

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
            entries.initializeColumnNames(util.getColumnNames(this.table.toString()));
            ArrayList<ArrayList<String>> eintraege = util.getEntries(this.table.toString());

            int row = 1;
            ArrayList<Boolean> nullables = util.getNullables(this.table.toString());
            for (ArrayList<String> entry : util.getEntries(this.table.toString())) {
                entries.addEntry(entry, row++);
                entries.markNullables(nullables, row - 1);
            }
            newEntriesIndex = entries.size();
        }
    }

    private VBox createTopNavigation() {
//        HBox hbox = new HBox();
//        hbox.setPadding(new Insets(15, 12, 15, 12));
//        hbox.setSpacing(10);   // Gap between nodes
//        hbox.setStyle("-fx-background-color: #FFFFFF;");
//
//        Button buttonAufgabe = new Button("Aufgaben");
//        buttonAufgabe.setPrefSize(100, 20);
//        buttonAufgabe.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                changeTable(tableMap.get("AUFGABE"));
//            }
//        });
//
//        Button buttonPersonal = new Button("Personal");
//        buttonPersonal.setPrefSize(100, 20);
//        buttonPersonal.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                changeTable(tableMap.get("PERSONAL"));
//            }
//        });
//
//        Button buttonMaschine = new Button("Maschinen");
//        buttonMaschine.setPrefSize(100, 20);
//        buttonMaschine.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                changeTable(tableMap.get("MASCHINE"));
//            }
//        });
//
//        Button buttonGeschaeftspartner = new Button("Geschaeftspartner");
//        buttonGeschaeftspartner.setPrefSize(100, 20);
//        buttonGeschaeftspartner.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                changeTable(tableMap.get("GESCHAEFTSPARTNER"));
//            }
//        });
//
//        // Inventar dropdown
//        String inventar[] = {"Inventargegenstand", "Vorräte", "Lagersilo", "Maschine"};
//        ComboBox inventarComboBox = new ComboBox(FXCollections.observableArrayList(inventar));
//        inventarComboBox.valueProperty().addListener(new ChangeListener() {
//            @Override
//            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//                changeTable(tableMap.get((replaceUmlaute(inventarComboBox.getValue().toString()))));
//            }
//        });
//        TilePane tilePane = new TilePane(inventarComboBox);
//        inventarComboBox.getSelectionModel().selectFirst();
//        inventarComboBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
//            if (!isShowing) {
//                changeTable(tableMap.get((replaceUmlaute(inventarComboBox.getValue().toString()))));
//            }
//        });
//
//        hbox.getChildren().addAll(inventarComboBox, buttonAufgabe, buttonPersonal, buttonMaschine, buttonGeschaeftspartner, currentSelectedTable);
        VBox vBox = new VBox();
        vBox.setSpacing(-10);

        HBox hbox1 = new HBox();
        hbox1.setPadding(new Insets(10)); // Set all sides to 10
        hbox1.setSpacing(8);              // Gap between nodes
        for (int i = 0; i < 11; i++) {
            TableView table = new TableView(Table.values()[i].toString(), false);
            this.table = table;
            String first = table.toString().substring(0, 1).toUpperCase();
            String after = table.toString().substring(1, table.toString().length()).toLowerCase();
            String tableName = first + after;

            Button currentButton = new Button(tableName);
            currentButton.setPrefSize(150, 20);
            currentButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTable(tableMap.get(table.toString()));
                }
            });
            hbox1.getChildren().add(currentButton);
        }

        HBox hbox2 = new HBox();
        hbox2.setPadding(new Insets(10));
        hbox2.setSpacing(8);
        for (int i = 11; i < 22; i++) {
            TableView table = new TableView(Table.values()[i].toString(), false);
            this.table = table;
            String first = table.toString().substring(0, 1).toUpperCase();
            String after = table.toString().substring(1, table.toString().length()).toLowerCase();
            String tableName = first + after;

            Button currentButton = new Button(tableName);
            currentButton.setPrefSize(150, 20);
            currentButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTable(tableMap.get(table.toString()));
                }
            });
            hbox2.getChildren().add(currentButton);
        }

        vBox.getChildren().add(hbox1);
        vBox.getChildren().add(hbox2);

        return vBox;
    }

    private VBox createLeftNavigation() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes
        // TODO Buttons groesse anpassen
        Text title = new Text("View");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);

        for (int i = 22; i < 46; i++) {
            TableView table = new TableView(Table.values()[i].toString(), false);

            String first = table.toString().substring(0, 1).toUpperCase();
            String after = table.toString().substring(1, table.toString().length()).toLowerCase();
            String tableName = first + after;

            Button currentButton = new Button(tableName);
            currentButton.setPrefSize(200, 20);
            currentButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTable(tableMap.get(table.toString()));
                }
            });
            vbox.getChildren().add(currentButton);
        }
        return vbox;
    }

    private VBox createRightNavigation() {
        VBox vbox = null;
        if (!table.editable()) {
            vbox = new VBox();
            vbox.setPadding(new Insets(10)); // Set all sides to 10
            vbox.setSpacing(8);              // Gap between nodes
            // TODO Buttons groesse anpassen
            Text title = new Text("View");
            title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            vbox.getChildren().add(title);

            FlowPane flow = new FlowPane();
            flow.setPadding(new Insets(5, 0, 5, 0));
            flow.setVgap(4);
            flow.setHgap(4);
            flow.setPrefWrapLength(170); // preferred width allows for two columns
            flow.setStyle("-fx-background-color: DAE6F3;");

            ImageView pie = new ImageView(new Image(LayoutSample_TEST.class.getResourceAsStream("../graphics/chart_1.png")));
            pie.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    showPieChart();
                }
            });
            ImageView bar = new ImageView(new Image(LayoutSample_TEST.class.getResourceAsStream("../graphics/chart_3.png")));
            bar.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    showBarChart();
                }
            });

            flow.getChildren().add(pie);
            flow.getChildren().add(bar);
            vbox.getChildren().add(flow);
        }
        return vbox;
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
                // TODO immer leeres feld
                // TODO onbuttonlistener - ENTER

                // ID_table keys
                sample.database.DataMatrix id_table = new DataMatrix();
                util.getConnection();


                // get free key value from InventarNr TODO other table too (?)
                if (table != null) {
                    util.getConnection();
                    id_table.initializeColumnNames(util.getColumnNames(table.iterableKeyValuesTables.get(0).toString()));
                    ArrayList<ArrayList<String>> eintraege = util.getEntries(table.toString());

                    int row = 1;
                    ArrayList<Boolean> nullables = util.getNullables(table.iterableKeyValuesTables.get(0).toString());
                    for (ArrayList<String> entry : util.getEntries(table.iterableKeyValuesTables.get(0).toString())) {
                        id_table.addEntry(entry, row++);
                        id_table.markNullables(nullables, row - 1);
                    }
                }

                // TODO code cleanup
                ArrayList<String> inventarKeys = new ArrayList<String>();
                inventarKeys.add("INVENTARNR");
                ArrayList<String> FREE_KEYS_TEST = id_table.getNextAvailableKeys(inventarKeys);

                entries.addEmptyEntry(util.getNullables(table.toString()), util.getColumnSize(table.toString()));
                ArrayList<String> columns = util.getColumnNames(table.toString());
                ArrayList<String> nextAvailableKeys = entries.getNextAvailableKeys(table.iterableKeyValues);
                ArrayList<String> keys = table.iterableKeyValues;

                // add next free kee to new emptry entry
                for (int i = 0; i < keys.size(); i++) {
                    if (keys.get(i).equals(inventarKeys.get(0))) nextAvailableKeys.set(i, FREE_KEYS_TEST.get(0));
                }


                for (int column = 0; column < columns.size(); column++) {
                    for (int keyIndex = 0; keyIndex < table.iterableKeyValues.size(); keyIndex++) {
                        if (columns.get(column).equals(keys.get(keyIndex)))
                            entries.getLatestNodeEntry().get(column).setText(nextAvailableKeys.get(keyIndex));
                    }
                }

                //System.out.println(availableKeys);

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
                    String insertResultText = util.insert(table.toString(), entries.getEntry(i), table.preFillTables);
                    //currentStatement.setText("Commit erfolgreich!");
                    if (!insertResultText.equals("")) currentStatement.setText(insertResultText);
                }

                for (int i = 0; i < deletedEntries.size(); i++) {
                    String val = deletedEntries.getVal(0, i);
                    util.delete(table.toString(), val);
                }

                ArrayList<Integer> changedIndices = locateChangedEntries();
                for (Integer i : changedIndices) {
                    if (i <= newEntriesIndex) {
                        //System.out.println(util.update(table, entries.getInitialEntry(i), entries.getEntry(i)));
                        ArrayList<String> initials = entries.getInitialEntry(i);
                        ArrayList<String> newEntries = entries.getEntry(i);
                        util.update(table.toString(), entries.getInitialEntry(i), entries.getEntry(i), table.preFillTables);
                    }
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
        for (int i = 1; i < newEntriesIndex; i++) {
            for (DataTextFieldNode node : entries.getNodeEntry(i)) {
                if (node.changed()) {
                    changed.add(i);
                    //break;
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

    private void showPieChart() {
        VBox vbox = new VBox();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
//                FXCollections.observableArrayList(
//                        new PieChart.Data("Grapefruit", 13),
//                        new PieChart.Data("Oranges", 25),
//                        new PieChart.Data("Plums", 10),
//                        new PieChart.Data("Pears", 22),
//                        new PieChart.Data("Apples", 30));
        ObservableList<PieChart.Data> test = new ObservableListBase<PieChart.Data>() {
            @Override
            public PieChart.Data get(int index) {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }
        };

        for (int i = 1; i < entries.size(); i++) {
            pieChartData.add(new PieChart.Data(entries.getEntry(i).get(0), 22));
        }
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle(table.toString());
        vbox.getChildren().add(chart);
        border.setCenter(vbox);
    }

    private void showBarChart() {
        final String austria = "Austria";
        final String brazil = "Brazil";
        final String france = "France";
        final String italy = "Italy";
        final String usa = "USA";
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> bc =
                new BarChart<String, Number>(xAxis, yAxis);
        bc.setTitle("Country Summary");
        xAxis.setLabel("Country");
        yAxis.setLabel("Value");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("2003");
        series1.getData().add(new XYChart.Data(austria, 25601.34));
        series1.getData().add(new XYChart.Data(brazil, 20148.82));
        series1.getData().add(new XYChart.Data(france, 10000));
        series1.getData().add(new XYChart.Data(italy, 35407.15));
        series1.getData().add(new XYChart.Data(usa, 12000));

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("2004");
        series2.getData().add(new XYChart.Data(austria, 57401.85));
        series2.getData().add(new XYChart.Data(brazil, 41941.19));
        series2.getData().add(new XYChart.Data(france, 45263.37));
        series2.getData().add(new XYChart.Data(italy, 117320.16));
        series2.getData().add(new XYChart.Data(usa, 14845.27));

        XYChart.Series series3 = new XYChart.Series();
        series3.setName("2005");
        series3.getData().add(new XYChart.Data(austria, 45000.65));
        series3.getData().add(new XYChart.Data(brazil, 44835.76));
        series3.getData().add(new XYChart.Data(france, 18722.18));
        series3.getData().add(new XYChart.Data(italy, 17557.31));
        series3.getData().add(new XYChart.Data(usa, 92633.68));

        bc.getData().addAll(series1, series2, series3);
        border.setCenter(bc);
    }

    private void createTables() {
        for (int i = 0; i < 22; i++) {
            TableView table = new TableView(Table.values()[i].toString(), true);
            tableMap.put(table.toString(), table);

            switch (table.toString()) {
                case "MASCHINE":
                    table.preFillTables.add("STANDORT");
                    table.preFillTables.add("INVENTARGEGENSTAND");
                    table.iterableKeyValues.add("SN");
                    table.iterableKeyValues.add("INVENTARNR");
                    table.iterableKeyValuesTables.add("INVENTARGEGENSTAND");
                    break;
                case "Lagersilo":
                    table.preFillTables.add("STANDORT");
                    table.preFillTables.add("INVENTARGEGENSTAND");
                    table.iterableKeyValues.add("SiloNr");
                    table.iterableKeyValues.add("INVENTARNR");
                    table.iterableKeyValuesTables.add("INVENTARGEGENSTAND");
                    table.decimalValues.add("Kapazitaet");
                    table.decimalValues.add("Fuellmenge");
                    break;
                case "Vorraete":
                    table.preFillTables.add("STANDORT");
                    table.preFillTables.add("INVENTARGEGENSTAND");
                    table.iterableKeyValuesTables.add("INVENTARGEGENSTAND");
                    break;
                case "Geschaeftspartner":
                    table.preFillTables.add("STANDORT");
                    break;
                case "Personal":
                    table.preFillTables.add("MitarbeiterNr");
                    table.defaultSysdate.add("Einstellungsdatum");
                    table.iterableKeyValues.add("MitarbeiterNr");
                    break;

            }



        }

        for (int i = 22; i < 46; i++) {
            TableView table = new TableView(Table.values()[i].toString(), false);
            tableMap.put(table.toString(), table);

            switch (table.toString()) {
                case "AUFGABENBEREICHE":
                    table.displayButtons.add("BARCHART");
                    break;
            }
        }

//        for (java.util.Map.Entry<String, TableView> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue().editable());
//        }
    }

    public void closeDatabaseConnection() {
        util.close();
    }
}
