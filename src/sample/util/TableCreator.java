package sample.util;

import sample.database.Table;
import sample.database.TableView;
import sample.ui.UIController;

import java.util.HashMap;

public class TableCreator {

    public TableCreator() {

    }

    public static HashMap<String, TableView> createTables() {
        HashMap<String, TableView> tableMap = new HashMap();

        for (int i = 0; i < 22; i++) { //tables
            TableView table = new TableView(Table.values()[i].toString(), true);
            tableMap.put(table.toString(), table);

            //TODO Aufgabe -> Menge eingeben -> Inventargegenstand auswählen
            switch (table.toString()) {     //todo Beim einfügen von Einträgen Checkboxen und Dropdownmenüs mit zuletzt eingetragenen Daten vorschlagen
                case "MASCHINE": // TODO checkmarker -  bis hier funktionierts
                    table.preFillTables.add("STANDORT");
                    table.preFillTables.add("INVENTARGEGENSTAND");
                    table.iterableValues.add("SN");
                    table.iterableValues.add("INVENTARNR");
                    table.iterableValuesTables.add("INVENTARGEGENSTAND");
                    table.connectedTables.add("GELAGERT_IN");
                    break;
                case "LAGERSILO":
                    table.preFillTables.add("STANDORT");
                    table.preFillTables.add("INVENTARGEGENSTAND");
                    table.iterableValues.add("SILONR");
                    table.iterableValues.add("INVENTARNR");
                    table.iterableValuesTables.add("INVENTARGEGENSTAND");
                    table.decimalValues.add("KAPAZITAET");
                    table.decimalValues.add("FUELLMENGE");
                    break;
                case "VORRAETE":
                    table.preFillTables.add("STANDORT");
                    table.preFillTables.add("INVENTARGEGENSTAND");
                    table.iterableValues.add("INVENTARNR");
                    table.iterableValuesTables.add("INVENTARGEGENSTAND");
                    table.connectedTables.add("GELAGERT_IN");
                    table.correspondingConnectedTables.add("STANDORT");
                    break;
                case "GESCHAEFTSPARTNER":
                    table.preFillTables.add("STANDORT");
                    table.connectedTables.add("PARTNER_BEARBEITET");
                    table.correspondingConnectedTables.add("AUFGABE");
                    break;
                case "PERSONAL":
                    table.preFillTables.add("STANDORT");
                    table.defaultSysdate.add("EINSTELLUNGSDATUM");
                    table.iterableValues.add("MITARBEITERNR");
                    table.decimalValues.add("GEHALT");
                    table.connectedTables.add("PERSONAL");
                    table.connectedTables.add("PERSONAL_BEARBEITET");
                    table.correspondingConnectedTables.add("SEX");
                    table.correspondingConnectedTables.add("AUFGABE");
                    break;
                case "ABWESENHEIT":
                    table.defaultSysdate.add("ANFANG");
                    table.connectedTables.add("ABWESENHEIT");
                    table.correspondingConnectedTables.add("PERSONAL");
                    break;
                case "SEX":
                    table.iterableValues.add("SEXNR");
                    break;
                case "FELD":
                    table.iterableValues.add("FELDNR");
                    table.connectedTables.add("DURCHGEFUEHRT_AUF");
                    table.correspondingConnectedTables.add("AUFGABE");
                    break;
                case "AUFGABE":
                    table.preFillTables.add("KOSTENSTELLE");
                    table.preFillTables.add("FELDAUFGABE");
                    table.preFillTables.add("WARTUNG");
                    table.preFillTables.add("VERTRIEB");
                    table.preFillTables.add("SONSTIGES");
                    table.defaultSysdate.add("ERSTELLDATUM");
                    table.decimalValues.add("BETRAG");
                    table.iterableValues.add("AUFGABENNR");
                    table.connectedTables.add("BEINHALTET");
                    table.connectedTables.add("DURCHGEFUEHRT_AUF");
                    table.connectedTables.add("PARTNER_BEARBEITET");
                    table.connectedTables.add("PERSONAL_BEARBEITET");
                    table.correspondingConnectedTables.add("INVENTARLISTE");
                    table.correspondingConnectedTables.add("FELD");
                    table.correspondingConnectedTables.add("GESCHAEFTSPARTNER");
                    table.correspondingConnectedTables.add("PERSONAL");

                    break;
                case "KOSTENSTELLE":
                    table.iterableValues.add("Kostenstelle");
                    break;
                case "ZUSTAND":
                    break;
                case "INVENTARGEGENSTAND":
                    break;
                case "FELDAUFGABE":
                    break;
                case "WARTUNG":
                    break;
                case "VERTRIEB":
                    break;
                case "SONSTIGES":
                    break;
                case "DURCHGEFUEHT_AUF":
                    table.decimalValues.add("ERTRAG");
                    break;
                case "PARTNER_BEARBEITET":
                    table.defaultSysdate.add("DATUM");
                    break;
                case "PERSONAL_BEARBEITET":
                    table.defaultSysdate.add("DATUM");
                    break;
                case "BEINHALTET":
                    table.decimalValues.add("BENOETIGTE_MENGE");
                    break;
                case "GELAGERT_IN":
                    // TODO prefill udpaten
                    table.preFillTables.add("VORRAETE");
                    table.decimalValues.add("MENGE");
                    break;
                case "STANDORT":
                    table.connectedTables.add("GELAGERT_IN");
                    table.correspondingConnectedTables.add("VORRAETE");
                    break;
            }


        }

        for (int i = 22; i < Table.values().length; i++) { //views
            TableView table = new TableView(Table.values()[i].toString(), false);

            switch (table.toString()) { //todo überlegen welche diagramme für welche views relevant sind
                case "AUFGABENBEREICHE":
                    table.displayButtons.add("BARCHART");
                    break;
                case "INVENTARLISTE":
                    table.connectedTables.add("BEINHALTET");
                    table.correspondingConnectedTables.add("AUFGABE");
                    break;
                case "LAGERSILOLISTE":
                    break;
                case "FELDERLISTE":
                    table.displayButtons.add("BARCHART");
                    table.displayButtons.add("PIECHART");
                    table.graphicDisplayStrings.add("Felder Flächen");
                    table.graphicDisplayStrings.add("FELDNR");
                    table.graphicDisplayStrings.add("FLAECHE");
                    table.graphicDisplayStrings.add("Felder");
                    break;
                case "TAETIGKEITENUEBERSICHT":
                    table.displayButtons.add("BARCHART");
                    table.displayButtons.add("PIECHART");
                    table.graphicDisplayStrings.add("Tätigkeiten Anzahl");
                    table.graphicDisplayStrings.add("TAETIGKEIT");
                    table.graphicDisplayStrings.add("ANZAHL");
                    table.graphicDisplayStrings.add("Taetigkeit");
                    break;
                case "MITARBEITERAUFTEILUNG":
                    table.displayButtons.add("BARCHART");
                    table.displayButtons.add("PIECHART");
                    table.graphicDisplayStrings.add("Mitarbeiter Aufgabenverteilung");
                    table.graphicDisplayStrings.add("MITARBEITERNR");
                    table.graphicDisplayStrings.add("");
                    table.graphicDisplayStrings.add("");
                    break;
                case "Aufgabenverteilung":
                    table = new TableView(UIController.capitalize(table.toString()), false); // database name ajdustment
                    break;
                case "FINANZUEBERSICHT":
                    table.displayButtons.add("BARCHART");
                    table.displayButtons.add("PIECHART");
                    table.graphicDisplayStrings.add("Aufgaben Beträge");
                    table.graphicDisplayStrings.add("AUFGABENNR");
                    table.graphicDisplayStrings.add("BETRAG");
                    table.graphicDisplayStrings.add("Aufgabenr");
                    break;
            }
            tableMap.put(table.toString(), table);
        }
        //        for (java.util.Map.Entry<String, TableView> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue().editable());
//        }
        return tableMap;
    }
}
