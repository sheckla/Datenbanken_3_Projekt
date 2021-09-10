package sample.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import sample.layoutsample.LayoutSample;
import sample.util.EntryManager;
import sample.util.TableClickHandler;
import sample.util.TableCreator;

import java.util.*;

// TODO anzahl maximaler angezeigter einträge, seitenanzeige per Intervall (50, 25, 10)
// TODO geänderte werte markieren und bei wechsel fragen ob die veränderung verworfen werden soll
// TODO nur bestimmte daten erlauben (int für nummern, zeichen für Strings etc)

public class UIController {
    private final boolean DEVELOPER_MODE = false; // shows all tables in database in top navigation
    private BorderPane border; // main UI element
    private Text dataTitle = new Text("");
    private final Label updatedTime = new Label("");
    private final Label currentStatement = new Label("");
    private TextField searchField = new TextField("");


    private ListView<GridPane> dataMatrixListView = new ListView<>();
    private final EntryManager entryManager;
    private final TableClickHandler tableClickHandler = new TableClickHandler();
    private final JDBCDatabase jdbc;
    private TableView currentTable;
    private final HashMap<String, TableView> tableMap = TableCreator.createTables();
    private int currentSelectedRow = 0;

    public UIController() {
        jdbc = new JDBCDatabase("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@oracle-srv.edvsz.hs-osnabrueck.de:1521/oraclestud",
                "oSoSe2021_G7", "g4Tbb3Vn0");
        entryManager = new EntryManager(jdbc);
    }

    public BorderPane createUI() {
        //Borderpane https://docs.oracle.com/javafx/2/layout/builtin_l
        // layouts.htm
        border = new BorderPane();
        ScrollPane topScroll = new ScrollPane(createTopNavigation());
        topScroll.setFitToHeight(true);
        topScroll.setFitToWidth(true);
        border.setTop(topScroll);
        ScrollPane leftScroll = new ScrollPane(createLeftNavigation());
        leftScroll.setFitToWidth(true);
        topScroll.setFitToHeight(true);
        border.setLeft(leftScroll);
        border.setRight(createRightNavigation());
        border.setBottom(createBottomNavigation());
        return border;
    }

    public void changeTableDialog(TableView table) {
        // change occured -> ask if change is to be discarded
        if (entryManager.isChanged()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Änderungen verwerfen?");
            alert.setContentText("Sie haben noch ungespeicherte Änderungen.\n" +
                    "Möchten Sie diese verwerfen und zur ausgewählten Tabelle wechseln?");
            ButtonType okButton = new ButtonType("Ja", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("Nein", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(okButton, noButton);
            alert.showAndWait().ifPresent(type -> {
                if (type == okButton) changeToTable(table);
            });
        } else {
            // no change occured -> normal table switch
            changeToTable(table);
        }
    }

    // initializes all class variables and parsed table
    private void changeToTable(TableView table) {

        this.currentTable = table;
        System.out.println(table.toString());
        entryManager.changeTable(table);
        entryManager.pullData();
        border.setCenter(createDatabaseView());
        border.setRight(createRightNavigation());
        updatedTime.setText("Daten aktualisiert: " + entryManager.curTime());
        searchField.setText("");
    }

    private void refreshDatabaseView() {
        border.setCenter(createDatabaseView());
    }

    private VBox createDatabaseView() {
        System.out.println("PRIMARY KEYS: " + jdbc.getKeys(currentTable.toString()));

        dataMatrixListView = new ListView(); // TODO slider anpassen, optimieren (wird zu oft ausgeführt)
        dataMatrixListView.setPrefHeight(2160);
        dataMatrixListView.setPrefWidth(4096);
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes

        dataTitle = new Text(currentTable.toString());
        dataTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(dataTitle);

        if (currentTable != null) {
            for (int i = 0; i < entryManager.size(); i++) {
                dataMatrixListView.getItems().add(createDataRow(entryManager.getNodeEntry(i)));
            }
            vbox.getChildren().add(dataMatrixListView);
        }
        dataMatrixListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int index = dataMatrixListView.getSelectionModel().getSelectedIndex();
                if (index > 0) nToMHandler(index);
                currentSelectedRow = dataMatrixListView.getSelectionModel().getSelectedIndex();
            }
        });
        return vbox;
    }


    // TODO Varchar constraints für charlimit
    private GridPane createDataRow(ArrayList<DataTextFieldNode> entry) {
        GridPane root = new GridPane();
        for (DataTextFieldNode tf : entry) {
            GridPane.setRowIndex(tf, tf.getRow());           // Nur nutwendig für GridPane
            GridPane.setColumnIndex(tf, tf.getCol());        // ^
            root.getChildren().add(tf);
        }

        // click listener for row
        root.getChildren().forEach(item -> {
            item.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    int index = (((DataTextFieldNode) item).getRow());
                    if (index > 0) nToMHandler(index);
                    currentSelectedRow = ((DataTextFieldNode) item).getRow();
                }
            });
        });
        return root;
    }

    private VBox createTopNavigation() {
        VBox vBox = new VBox();
        vBox.setSpacing(-10);
        if (!DEVELOPER_MODE) {
            HBox hbox = new HBox();
            hbox.setPadding(new Insets(10));
            hbox.setSpacing(8);
            hbox.setStyle("-fx-background-color: #FFFFFF;");

            Button buttonAufgabe = new Button("Aufgaben");
            buttonAufgabe.setPrefSize(100, 20);
            buttonAufgabe.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTableDialog(tableMap.get(Table.AUFGABE.toString()));
                }
            });

            Button buttonPersonal = new Button("Personal");
            buttonPersonal.setPrefSize(100, 20);
            buttonPersonal.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTableDialog(tableMap.get(Table.PERSONAL.toString()));
                }
            });

            Button buttonGeschaeftspartner = new Button("Geschaeftspartner");
            buttonGeschaeftspartner.setPrefSize(100, 20);
            buttonGeschaeftspartner.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTableDialog(tableMap.get(Table.GESCHAEFTSPARTNER.toString()));
                }
            });

            Button buttonFeld = new Button("Feld");
            buttonFeld.setPrefSize(100, 20);
            buttonFeld.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTableDialog(tableMap.get(Table.FELD.toString()));
                }
            });

            Button buttonKostenstelle = new Button("Kostenstelle");
            buttonKostenstelle.setPrefSize(100, 20);
            buttonKostenstelle.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTableDialog(tableMap.get(Table.KOSTENSTELLE.toString()));
                }
            });

            Button buttonSex = new Button("Geschlecht");
            buttonSex.setPrefSize(100, 20);
            buttonSex.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTableDialog(tableMap.get(Table.SEX.toString()));
                }
            });

            Button buttonStandort = new Button("Standort");
            buttonStandort.setPrefSize(100, 20);
            buttonStandort.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTableDialog(tableMap.get(Table.STANDORT.toString()));
                }
            });


            // Inventar dropdown


            String[] inventar = {"Vorräte", "Lagersilo", "Maschine"};
            ComboBox inventarComboBox = new ComboBox(FXCollections.observableArrayList(inventar));
            inventarComboBox.valueProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    changeTableDialog(tableMap.get((replaceUmlaute(inventarComboBox.getValue().toString()))));
                }
            });
            TilePane tilePane = new TilePane(inventarComboBox);
            inventarComboBox.getSelectionModel().selectFirst();
            inventarComboBox.showingProperty().addListener((obs, wasShowing, isShowing) -> {
//            if (!isShowing) {
//                changeTable(tableMap.get((replaceUmlaute(inventarComboBox.getValue().toString()))));
//            }
            });
            hbox.getChildren().addAll(inventarComboBox, buttonAufgabe, buttonPersonal, buttonGeschaeftspartner, buttonFeld,
                    buttonKostenstelle, buttonSex, buttonStandort);

            // connected tables
            HBox hboxConnected = new HBox();
            hboxConnected.setPadding(new Insets(10));
            hboxConnected.setSpacing(8);
            hboxConnected.setStyle("-fx-background-color: #FFFFFF;");

            Button buttonBeinhaltet = new Button("Beinhaltet");
            buttonBeinhaltet.setPrefSize(100, 20);
            buttonBeinhaltet.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTableDialog(tableMap.get(Table.BEINHALTET.toString()));
                }
            });

            Button buttonDurchgefuehrt = new Button("Durchgeführt auf");
            buttonDurchgefuehrt.setPrefSize(100, 20);
            buttonDurchgefuehrt.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTableDialog(tableMap.get(Table.DURCHGEFUEHRT_AUF.toString()));
                }
            });

            Button buttonGelagert = new Button("Gelagert in");
            buttonGelagert.setPrefSize(100, 20);
            buttonGelagert.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTableDialog(tableMap.get(Table.GELAGERT_IN.toString()));
                }
            });

            Button buttonPartner = new Button("Partner bearbeitet");
            buttonPartner.setPrefSize(100, 20);
            buttonPartner.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTableDialog(tableMap.get(Table.PARTNER_BEARBEITET.toString()));
                }
            });

            Button buttonPersonalBearbeitet = new Button("Personal bearbeitet");
            buttonPersonalBearbeitet.setPrefSize(100, 20);
            buttonPersonalBearbeitet.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTableDialog(tableMap.get(Table.PERSONAL_BEARBEITET.toString()));
                }
            });
            hboxConnected.getChildren().addAll(buttonBeinhaltet, buttonDurchgefuehrt, buttonGelagert, buttonPartner, buttonPersonalBearbeitet);
            vBox.getChildren().add(hbox);
            vBox.getChildren().add(hboxConnected);
        } else {

            HBox hbox1 = new HBox();
            hbox1.setPadding(new Insets(10)); // Set all sides to 10
            hbox1.setSpacing(8);              // Gap between nodes
            for (int i = 0; i < 11; i++) {
                TableView table = new TableView(Table.values()[i].toString(), false);
                this.currentTable = table;
                String first = table.toString().substring(0, 1).toUpperCase();
                String after = table.toString().substring(1).toLowerCase();
                String tableName = first + after;

                Button currentButton = new Button(tableName);
                currentButton.setPrefSize(150, 20);
                currentButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        changeTableDialog(tableMap.get(table.toString()));
                    }
                });
                hbox1.getChildren().add(currentButton);
            }

            HBox hbox2 = new HBox();
            hbox2.setPadding(new Insets(10));
            hbox2.setSpacing(8);
            for (int i = 11; i < 22; i++) {
                TableView table = new TableView(Table.values()[i].toString(), false);
                this.currentTable = table;
                String first = table.toString().substring(0, 1).toUpperCase();
                String after = table.toString().substring(1).toLowerCase();
                String tableName = first + after;

                Button currentButton = new Button(tableName);
                currentButton.setPrefSize(150, 20);
                currentButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        changeTableDialog(tableMap.get(table.toString()));
                    }
                });
                hbox2.getChildren().add(currentButton);
            }
            vBox.getChildren().add(hbox1);
            vBox.getChildren().add(hbox2);
        }
        return vBox;
    }

    private VBox createLeftNavigation() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes
        Text title = new Text("Ansichten");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);

        for (int i = 22; i < Table.values().length; i++) {
            TableView table = new TableView(Table.values()[i].toString(), false);
            String tableName = capitalize(table.toString());

            Button currentButton = new Button(tableName);
            currentButton.setPrefSize(200, 20);
            currentButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    changeTableDialog(tableMap.get(table.toString()));
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
                    dropDownValues = new String[]{"AUFGABENUEBERSICHT", "AUFGABENLISTE", "Aufgabenverteilung", "TAETIGKEITENUEBERSICHT"};
                    prompt = "Aufgabenbereiche";
                    break;
                case "Finanzuebersicht":
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
                if (inventarComboBox.getValue().toString().equals("Aufgabenverteilung")) {
                    changeTableDialog(tableMap.get(inventarComboBox.getValue().toString()));
                } else {
                    changeTableDialog(tableMap.get((replaceUmlaute(inventarComboBox.getValue().toString()))));
                }
            }
        });

        inventarComboBox.setPromptText(prompt);

        return inventarComboBox;
    }

    private VBox createRightNavigation() {
        VBox vbox = null;
        // Views
        if (!currentTable.editable() && !currentTable.displayButtons.isEmpty()) {
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

            for (String button : currentTable.displayButtons) {
                switch (button) {
                    case "PIECHART":
                        ImageView pie = new ImageView(new Image(UIController.class.getResourceAsStream("../graphics/chart_1.png")));
                        pie.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                showPieChart();
                            }
                        });
                        flow.getChildren().add(pie);
                        break;
                    case "BARCHART":
                        ImageView bar = new ImageView(new Image(UIController.class.getResourceAsStream("../graphics/chart_3.png")));
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
            ImageView data = new ImageView(new Image(UIController.class.getResourceAsStream("../graphics/chart_4.png")));
            data.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    refreshDatabaseView();
                }
            });
            flow.getChildren().add(data);
            vbox.getChildren().add(flow);
            // Tables
        } else if (!currentTable.connectedTables.isEmpty() && !tableClickHandler.isSecondClick()) {
            vbox = new VBox();
            vbox.setPadding(new Insets(10)); // Set all sides to 10
            vbox.setSpacing(8);              // Gap between nodes
            // TODO Buttons groesse anpassen
            Text title = new Text("Verknüpfte Tabellen");
            title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            vbox.getChildren().add(title);


            // Connected Tables
            for (int i = 0; i < currentTable.connectedTables.size(); i++) {
                final int index = i;
                String buttonText = currentTable.connectedTables.get(i);
                switch (currentTable.connectedTables.get(i)) { // cases to be extended for user-friendlieness
                    case "ABWESENHEIT":
                        buttonText = "Mitarbeiter auswählen";
                        break;
                    case "BEINHALTET":
                        buttonText = "Beinhaltet Inventargegenstand";
                        break;
                    case "DURCHGEFUEHRT_AUF":
                        buttonText = "Durchgeführt auf Feld";
                        break;
                    case "PARTNER_BEARBEITET":
                        buttonText = "Partner bearbeitet";
                        break;
                    case "GELAGERT_IN":
                        buttonText = "Gelagert in";
                        break;
                    case "PERSONAL":
                        buttonText = "Geschlecht";
                    default:
                        buttonText = capitalize(buttonText);
                        break;
                }
                Button currentButton = new Button(buttonText);
                currentButton.setPrefSize(150, 20);
                currentButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (currentSelectedRow > 0) { // exclude column-titles
                            tableClickHandler.setButtonClicked(true);
                            tableClickHandler.setInitialTable(currentTable.toString());
                            tableClickHandler.setTableIndex(index);
                            changeToTable(tableMap.get(currentTable.correspondingConnectedTables.get(index)));
                            dataTitle.setText("Zweiten Eintrag auswählen");
                        }
                    }
                });
                vbox.getChildren().add(currentButton);
            }

        }
        return vbox;
    }

    private VBox createBottomNavigation() {
        VBox root = new VBox();
        root.setSpacing(8);
        root.setPadding(new Insets(15, 12, 15, 12));

        // Search field
        HBox searchBox = new HBox();
        searchField = new TextField("");
        Button searchCommitButton = new Button("Nach Wert suchen");
        searchCommitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!searchField.getText().equals("")) {
                    entryManager.pullData();
                    entryManager.searchByKeyword(searchField.getText());
                    refreshDatabaseView();
                }
            }
        });
        searchBox.getChildren().add(searchField);
        searchBox.getChildren().add(searchCommitButton);

        // Database actions buttons
        HBox databaseButtons = new HBox();
        databaseButtons.setPadding(new Insets(15, 12, 15, 12));
        databaseButtons.setSpacing(10);   // Gap between nodes
        databaseButtons.setStyle("-fx-background-color: #FFFFFF;");

        Button buttonEinfuegen = new Button("Neuer Eintrag");
        buttonEinfuegen.setPrefSize(100, 20);
        buttonEinfuegen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO onbuttonlistener - ENTER
                entryManager.createNewEntry();
                currentStatement.setText("YEPPERS");
                refreshDatabaseView();
            }
        });

        Button buttonCommit = new Button("Werte Bestätigen");
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

        databaseButtons.getChildren().addAll(buttonEinfuegen, buttonCommit, buttonLoeschen, buttonAktualisieren);

        //Tabellen label einfügen
        HBox tableLable = new HBox();
        tableLable.setPadding(new Insets(15, 12, 15, 12));
        tableLable.setSpacing(10);   // Gap between nodes

        // data pulled date
        tableLable.setPadding(new Insets(15, 12, 15, 12));
        tableLable.setSpacing(10);   // Gap between nodes
        tableLable.getChildren().add(updatedTime);

        root.getChildren().add(searchBox);
        root.getChildren().add(databaseButtons);
        root.getChildren().add(tableLable);
        root.getChildren().add(currentStatement);
        return root;
    }

    private void nToMHandler(int row) {
        // both corresponding n:m tables clicked -> add to actual n:m table
        if (tableClickHandler.isSecondClick()) {
            tableClickHandler.setSecondClick(false);
            tableClickHandler.setSecondEntry(entryManager.getEntry(row), jdbc.getColumnNames(currentTable.toString()));
            changeToTable(tableMap.get(tableMap.get(tableClickHandler.getInitialTable()).connectedTables.get(tableClickHandler.getTableIndex())));

            tableClickHandler.setnToMColumns(jdbc.getColumnNames(currentTable.toString()));
            System.out.println("selected entries are:");
            System.out.println(tableClickHandler.getFirstRow());
            System.out.println(tableClickHandler.getSecondRow());

            tableClickHandler.reset();

            // Insert values to entry
            ArrayList<DataTextFieldNode> nodes = entryManager.getLatestNodeEntry();
            ArrayList<String> selectedUserValues = tableClickHandler.getFilledNtoMColumns();
            System.out.println(selectedUserValues);
            for (int i = 0; i < selectedUserValues.size(); i++) {
                if (!selectedUserValues.get(i).equals("")) nodes.get(i).setText(selectedUserValues.get(i));
            }
        } else {
            tableClickHandler.setFirstEntry(entryManager.getEntry(row), jdbc.getColumnNames(currentTable.toString()));
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

        ArrayList<ArrayList<String>> matrix = entryManager.getEntries().getStringMatrix();
        ArrayList<String> chartNames = new ArrayList<>();
        ArrayList<Integer> chartValues = new ArrayList<>();
        for (int row = 1; row < matrix.size(); row++) {
            for (int col = 0; col < matrix.get(0).size(); col++) {
                switch (currentTable.toString()) {
                    case "MITARBEITERAUFTEILUNG":
                        if (matrix.get(0).get(col).equals("MITARBEITERNR")) {
                            chartNames.add(matrix.get(row).get(col));
                        } else {
                            int sum = 0;
                            for (int i = 4; i < matrix.get(0).size(); i++) {
                                sum += Integer.parseInt(matrix.get(row).get(i));
                            }
                            System.out.println(sum);
                            chartValues.add(sum);
                            break;
                        }
                        break;
                    case "LAGERSILOLISTE":
                        break;
                    default:
                        if (matrix.get(0).get(col).equals(currentTable.graphicDisplayStrings.get(2))) {
                            chartValues.add(Integer.parseInt(matrix.get(row).get(col)));
                        } else if (matrix.get(0).get(col).equals(currentTable.graphicDisplayStrings.get(1))) {
                            chartNames.add(matrix.get(row).get(col));
                        }
                        break;
                }
            }
        }


        for (int i = 0; i < chartNames.size(); i++) {
            PieChart.Data data = new PieChart.Data(chartNames.get(i), chartValues.get(i));
            pieChartData.add(data);
        }
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle(currentTable.graphicDisplayStrings.get(3));
        vbox.getChildren().add(chart);
        border.setCenter(vbox);
    }

    private void showBarChart() {
        final String austria = "Austria";
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        StackedBarChart barchart =
                new StackedBarChart(xAxis, yAxis);
        barchart.setTitle(currentTable.graphicDisplayStrings.get(0));
        xAxis.setLabel(currentTable.graphicDisplayStrings.get(1));
        yAxis.setLabel(currentTable.graphicDisplayStrings.get(2));

        ArrayList<ArrayList<String>> matrix = entryManager.getEntries().getStringMatrix();
        ArrayList<String> chartNames = new ArrayList<>();
        ArrayList<Integer> chartValues = new ArrayList<>();
        for (int row = 1; row < matrix.size(); row++) {
            for (int col = 0; col < matrix.get(0).size(); col++) {
                switch (currentTable.toString()) {
                    case "MITARBEITERAUFTEILUNG":
                        if (matrix.get(0).get(col).equals("MITARBEITERNR")) {
                            chartNames.add(matrix.get(row).get(col));
                        } else {
                            int sum = 0;
                            for (int i = 4; i < matrix.get(0).size(); i++) {
                                sum += Integer.parseInt(matrix.get(row).get(i));
                            }
                            System.out.println(sum);
                            chartValues.add(sum);
                            break;
                        }
                        break;
                    case "LAGERSILOLISTE":
                        break;
                    default:
                        if (matrix.get(0).get(col).equals(currentTable.graphicDisplayStrings.get(2))) {
                            chartValues.add(Integer.parseInt(matrix.get(row).get(col)));
                        } else if (matrix.get(0).get(col).equals(currentTable.graphicDisplayStrings.get(1))) {
                            chartNames.add(matrix.get(row).get(col));
                        }
                        break;
                }
            }
        }

        for (int i = 0; i < chartNames.size(); i++) {
            XYChart.Series series = new XYChart.Series<>();
            series.setName(chartNames.get(i));
            series.getData().add(new XYChart.Data(chartNames.get(i), chartValues.get(i)));
            barchart.getData().add(series);
        }
        border.setCenter(barchart);
    }

    static public String capitalize(String s) {
        String first = s.substring(0, 1).toUpperCase();
        String after = s.substring(1).toLowerCase();
        return first + after;
    }

    public void closeDatabaseConnection() {
        jdbc.close();
    }

}
