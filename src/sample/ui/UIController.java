package sample.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import sample.database.DataTextFieldNode;
import sample.database.JDBCDatabase;
import sample.database.Table;
import sample.database.TableView;
import sample.layoutTest.LayoutSample_TEST;
import sample.util.EntryManager;
import sample.util.TableClickHandler;
import sample.util.TableCreator;

import java.util.*;

// TODO anzahl maximaler angezeigter einträge, seitenanzeige per Intervall (50, 25, 10)
// TODO geänderte werte markieren und bei wechsel fragen ob die veränderung verworfen werden soll
// TODO nur bestimmte daten erlauben (int für nummern, zeichen für Strings etc)

public class UIController {
    private BorderPane border; // main UI element
    private Text dataTitle = new Text("");
    private Label currentSelectedTable = new Label("");
    private Label updatedTime = new Label("");
    private Label currentStatement = new Label("");
    private Label debug = new Label("");


    // TODO textfield -> row anzeigen per col/row
    private ListView<GridPane> dataMatrixListView = new ListView<>(); // TODO slider anpassen
    private EntryManager entryManager;
    private TableClickHandler tableClickHandler = new TableClickHandler();
    private JDBCDatabase jdbc;
    private TableView table;
    private ArrayList<TableView> tableViews = new ArrayList<>();
    private HashMap<String, TableView> tableMap = TableCreator.createTables();
    private int currentSelectedRow = 0;

    public UIController() {
        jdbc = new JDBCDatabase("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@oracle-srv.edvsz.hs-osnabrueck.de:1521/oraclestud",
                "oSoSe2021_G7", "g4Tbb3Vn0");
        entryManager = new EntryManager(jdbc);
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
        currentSelectedTable.setText(replaceUmlaute(table.toString() + " is selected"));
        entryManager.changeTable(table);
        entryManager.pullData();
        border.setCenter(createDatabaseView());
        border.setRight(createRightNavigation());
        debug.setText(dataMatrixListView.getSelectionModel().getSelectedIndex() + "");
        updatedTime.setText(entryManager.curTime());
    }

    private void refreshDatabaseView() {
        currentStatement.setText("");
        debug.setText(dataMatrixListView.getSelectionModel().getSelectedIndex() + "");
        border.setCenter(createDatabaseView());
    }

    private VBox createDatabaseView() {
        System.out.println("PRIMARY KEYS: " + jdbc.getKeys(table.toString()));

        dataMatrixListView = new ListView(); // TODO slider anpassen, optimieren (wird zu oft ausgeführt)
        dataMatrixListView.setPrefHeight(2160);
        dataMatrixListView.setPrefWidth(4096);
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes

        dataTitle = new Text("Daten");
        dataTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(dataTitle);

        if (table != null) {
            for (int i = 0; i < entryManager.size(); i++) {
                dataMatrixListView.getItems().add(createDataRow(entryManager.getNodeEntry(i)));
            }
            vbox.getChildren().add(dataMatrixListView);
        }
        dataMatrixListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                nToMHandler(dataMatrixListView.getSelectionModel().getSelectedIndex());
                currentSelectedRow = dataMatrixListView.getSelectionModel().getSelectedIndex();
            }
        });
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

        // click listener for row
        root.getChildren().forEach(item -> {
            item.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    nToMHandler(((DataTextFieldNode) item).getRow());
                    currentSelectedRow = ((DataTextFieldNode) item).getRow();
                }
            });
        });
        return root;
    }

    private VBox createTopNavigation() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #FFFFFF;");

        Button buttonAufgabe = new Button("Aufgaben");
        buttonAufgabe.setPrefSize(100, 20);
        buttonAufgabe.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeTable(tableMap.get("AUFGABE"));
            }
        });

        Button buttonPersonal = new Button("Personal");
        buttonPersonal.setPrefSize(100, 20);
        buttonPersonal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeTable(tableMap.get("PERSONAL"));
            }
        });

        Button buttonMaschine = new Button("Maschinen");
        buttonMaschine.setPrefSize(100, 20);
        buttonMaschine.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeTable(tableMap.get("MASCHINE"));
            }
        });

        Button buttonGeschaeftspartner = new Button("Geschaeftspartner");
        buttonGeschaeftspartner.setPrefSize(100, 20);
        buttonGeschaeftspartner.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeTable(tableMap.get(table.correspondingTables.get(0)));
            }
        });

        // Inventar dropdown
        String inventar[] = {"Inventargegenstand", "Vorräte", "Lagersilo", "Maschine"};
        ComboBox inventarComboBox = new ComboBox(FXCollections.observableArrayList(inventar));
        inventarComboBox.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                changeTable(tableMap.get((replaceUmlaute(inventarComboBox.getValue().toString()))));
            }
        });
        TilePane tilePane = new TilePane(inventarComboBox);
        inventarComboBox.getSelectionModel().selectFirst();
        inventarComboBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
//            if (!isShowing) {
//                changeTable(tableMap.get((replaceUmlaute(inventarComboBox.getValue().toString()))));
//            }
        });
        hbox.getChildren().addAll(inventarComboBox, buttonAufgabe, buttonPersonal, buttonMaschine, buttonGeschaeftspartner, currentSelectedTable);
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
        vBox.getChildren().add(hbox);
        return vBox;
    }

    private VBox createLeftNavigation() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes
        Text title = new Text("View");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);

        for (int i = 22; i < 22 + 17; i++) {
            TableView table = new TableView(Table.values()[i].toString(), false);
            String first = table.toString().substring(0, 1).toUpperCase();
            String after = table.toString().substring(1, table.toString().length()).toLowerCase();
            String tableName = capitalize(table.toString());

            Button currentButton = new Button(tableName);
            currentButton.setPrefSize(200, 20);
            currentButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTable(tableMap.get(table.toString()));
                }
            });

            String[] dropDownValues = new String[0];
            String prompt = "";
            switch (tableName) {
                case "Inventarliste":
                    // Inventar dropdown
                    dropDownValues = new String[]{"INVENTARLISTE", "VORRAETEMITSTANDORT", "LAGERSILOLISTE", "MASCHINENLISTE", "FELDERLISTE",
                            "BENOETIGTEVORRAETE", "GEERNTETEPRODUKTE"};
                    prompt = "Inventar";
                    break;
                case "Personalliste":
                    dropDownValues = new String[]{"PERSONALLISTE", "MITARBEITERAUFTEILUNG", "BEARBEITUNGSGESCHWINDIGKEIT"};
                    prompt = "Personal";
                    break;
                case "Aufgabenbereiche":
                    dropDownValues = new String[]{"AUFGABENBEREICHE", "AUFGABENLISTE", "AUFGABENVERTEILUNG", "TAETIGKEITENUEBERSICHT"};
                    prompt = "Aufgabenbereiche";
                    break;
                case "Finanzübersicht":
                    dropDownValues = new String[]{"FINANZUEBERSICHT", "KOSTENSTELLENLISTE", "GESCHAEFTSPARTNERLISTE"};
                    prompt = "Finanzen";
                    break;
            }
            if (!prompt.equals("")) {
                ComboBox buttonComboBox = createComboBox(dropDownValues, prompt);
                vbox.getChildren().add(buttonComboBox);
            }

//            if (tableName.equals("Kostenstelleliste")) {
//                buttonComboBox.getChildrenUnmodifiable().add(currentButton);
//                vbox.getChildren().add(buttonComboBox);
//            }
//            if (tableName.equals("Geschaeftspartnerliste")) {
//                vbox.getChildren().add(currentButton);
//            }
        }
        return vbox;
    }

    private ComboBox createComboBox(String[] values, String prompt) {
        for (int j = 0; j < values.length; j++) {
            values[j] = capitalize(values[j]);
        }
        ComboBox inventarComboBox = new ComboBox(FXCollections.observableArrayList(values));
        inventarComboBox.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                changeTable(tableMap.get((replaceUmlaute(inventarComboBox.getValue().toString()))));
            }
        });

        inventarComboBox.setPromptText(prompt);

        return inventarComboBox;
    }
//
//
//        for(
//    int i = 22;
//    i< 22;i++)
//
//    {
//        TableView table = new TableView(Table.values()[i].toString(), false);
//        String first = table.toString().substring(0, 1).toUpperCase();
//        String after = table.toString().substring(1, table.toString().length()).toLowerCase();
//        String tableName = first + after;
//
//        Button currentButton = new Button(tableName);
//        currentButton.setPrefSize(200, 20);
//        currentButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                changeTable(tableMap.get(table.toString()));
//                secondClick = true;
//            }
//        });
//
//
//        vbox.getChildren().add(currentButton);
//
//    }
//        return vbox;
//}

    private VBox createRightNavigation() {
        VBox vbox = null;
        // Views
        if (!table.editable() && !table.displayButtons.isEmpty()) {
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

            for (String button : table.displayButtons) {
                switch (button) {
                    case "PIECHART":
                        ImageView pie = new ImageView(new Image(LayoutSample_TEST.class.getResourceAsStream("../graphics/chart_1.png")));
                        pie.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                showPieChart();
                            }
                        });
                        flow.getChildren().add(pie);
                        break;
                    case "BARCHART":
                        ImageView bar = new ImageView(new Image(LayoutSample_TEST.class.getResourceAsStream("../graphics/chart_3.png")));
                        bar.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                showBarChart();
                            }
                        });
                        flow.getChildren().add(bar);
                        break;
                }
            }
                ImageView data = new ImageView(new Image(LayoutSample_TEST.class.getResourceAsStream("../graphics/chart_4.png")));
                data.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        refreshDatabaseView();
                    }
                });
                flow.getChildren().add(data);
                vbox.getChildren().add(flow);
            // Tables
        } else if (!table.mToNTables.isEmpty()) {
            vbox = new VBox();
            vbox.setPadding(new Insets(10)); // Set all sides to 10
            vbox.setSpacing(8);              // Gap between nodes
            // TODO Buttons groesse anpassen
            Text title = new Text("ADD N:M");
            title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            vbox.getChildren().add(title);


            // N:M Table button
            Button currentButton = new Button(table.mToNTables.get(0));
            currentButton.setPrefSize(100, 20);
            currentButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    tableClickHandler.setButtonClicked(true);
                    changeTable(tableMap.get(table.correspondingTables.get(0)));
                    dataTitle.setText("Zweiten Eintrag auswählen");
                }
            });
            vbox.getChildren().add(currentButton);

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
                // TODO onbuttonlistener - ENTER
                entryManager.createNewEntry();
                refreshDatabaseView();
            }
        });

        Button buttonCommit = new Button("Commit!");
        buttonCommit.setPrefSize(100, 20);
        buttonCommit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO Commit output texte
                entryManager.commit();
                refreshDatabaseView();
            }
        });

        Button buttonLoeschen = new Button("Löschen");
        buttonLoeschen.setPrefSize(100, 20);
        buttonLoeschen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                entryManager.removeEntry(currentSelectedRow);
                refreshDatabaseView();
            }
        });

        Button buttonAktualisieren = new Button("Aktualisieren");
        buttonAktualisieren.setPrefSize(100, 20);
        buttonAktualisieren.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                entryManager.pullData();
                refreshDatabaseView();
            }
        });

        hbox.getChildren().addAll(buttonEinfuegen, buttonCommit, buttonLoeschen, buttonAktualisieren);
        //Tabellen label einfügen
        HBox hBox2 = new HBox();
        hBox2.setPadding(new Insets(15, 12, 15, 12));
        hBox2.setSpacing(10);   // Gap between nodes
        hBox2.getChildren().add(currentSelectedTable);
        //hBox2.getChildren().add();

        // data pulled date
        hBox2.setPadding(new Insets(15, 12, 15, 12));
        hBox2.setSpacing(10);   // Gap between nodes
        hBox2.getChildren().add(updatedTime);
        //hBox2.getChildren().add();

        root.getChildren().add(hbox);
        root.getChildren().add(hBox2);
        //currentStatement.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
        root.getChildren().add(currentStatement);
        root.getChildren().add(debug);
        return root;
    }

    private void nToMHandler(int row) {
        // both corresponding n:m tables clicked -> add to actual n:m table
        if (tableClickHandler.isSecondClick()) {
            tableClickHandler.setSecondClick(false);
            tableClickHandler.setSecondEntry(entryManager.getEntry(row), jdbc.getColumnNames(table.toString()));
            changeTable(tableMap.get("BEINHALTET"));

            tableClickHandler.setnToMColumns(jdbc.getColumnNames(table.toString()));
            System.out.println("selected entries are:");
            System.out.println(tableClickHandler.getFirstRow());
            System.out.println(tableClickHandler.getSecondRow());

            tableClickHandler.reset();

            // Insert values to entry
            ArrayList<DataTextFieldNode> nodes = entryManager.getLatestNodeEntry();
            ArrayList<String> selectedUserValues = tableClickHandler.getFilledNtoMColumns();
            System.out.println(selectedUserValues);
            for (int i = 0; i < selectedUserValues.size(); i++) {
                nodes.get(i).setText(selectedUserValues.get(i));
            }
        } else {
            System.out.println("First Click");
            tableClickHandler.setFirstEntry(entryManager.getEntry(row), jdbc.getColumnNames(table.toString()));
            System.out.println(tableClickHandler.getFirstRow());
        }
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

        for (int i = 1; i < entryManager.size(); i++) {
            PieChart.Data data = new PieChart.Data(entryManager.getEntry(i).get(0), 22);
            pieChartData.add(data);
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

    private String capitalize(String s) {
        String first = s.substring(0, 1).toUpperCase();
        String after = s.substring(1, s.length()).toLowerCase();
        return first + after;
    }

    public void closeDatabaseConnection() {
        jdbc.close();
    }
}
