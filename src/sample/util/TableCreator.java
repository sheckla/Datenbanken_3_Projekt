package sample.util;

import sample.database.Table;
import sample.database.TableView;

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
                    table.iterableKeyValues.add("SN");
                    table.iterableKeyValues.add("INVENTARNR");
                    table.iterableKeyValuesTables.add("INVENTARGEGENSTAND");
                    table.connectedTables.add("GELAGERT_IN");
                    break;
                case "LAGERSILO":
                    table.preFillTables.add("STANDORT");
                    table.preFillTables.add("INVENTARGEGENSTAND");
                    table.iterableKeyValues.add("SILONR");
                    table.iterableKeyValues.add("INVENTARNR");
                    table.iterableKeyValuesTables.add("INVENTARGEGENSTAND");
                    table.decimalValues.add("KAPAZITAET");
                    table.decimalValues.add("FUELLMENGE");
                    break;
                case "VORRAETE":
                    table.preFillTables.add("STANDORT");
                    table.preFillTables.add("INVENTARGEGENSTAND");
                    table.iterableKeyValues.add("INVENTARNR");
                    table.iterableKeyValuesTables.add("INVENTARGEGENSTAND");
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
                    table.iterableKeyValues.add("MITARBEITERNR");
                    table.decimalValues.add("GEHALT");
                    table.connectedTables.add("PERSONAL");
                    table.correspondingConnectedTables.add("SEX");
                    break;
                case "ABWESENHEIT":
                    table.defaultSysdate.add("ANFANG");
                    table.connectedTables.add("ABWESENHEIT");
                    table.correspondingConnectedTables.add("PERSONAL");
                    break;
                case "SEX":
                    table.iterableKeyValues.add("SEXNR");
                    break;
                case "FELD":
                    table.iterableKeyValues.add("FELDNR");
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
                    table.iterableKeyValues.add("AUFGABENNR");
                    table.connectedTables.add("BEINHALTET");
                    table.connectedTables.add("DURCHGEFUEHRT_AUF");
                    table.connectedTables.add("PARTNER_BEARBEITET");
                    table.correspondingConnectedTables.add("INVENTARLISTE");
                    table.correspondingConnectedTables.add("FELD");
                    table.correspondingConnectedTables.add("GESCHAEFTSPARTNER");

                    break;
                case "KOSTENSTELLE":
                    table.iterableKeyValues.add("Kostenstelle");
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

        for (int i = 22; i < 22+18; i++) { //views
            TableView table = new TableView(Table.values()[i].toString(), false);
            tableMap.put(table.toString(), table);

            switch (table.toString()) { //todo überlegen welche diagramme für welche views relevant sind
                case "AUFGABENBEREICHE":
                    table.displayButtons.add("BARCHART");
                    break;
                case "INVENTARLISTE":
                    table.connectedTables.add("BEINHALTET");
                    table.correspondingConnectedTables.add("AUFGABE");
                    break;
                case"LAGERSILOLISTE":
                    //table.displayButtons.add("BARCHART");
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
            }
        }
        //        for (java.util.Map.Entry<String, TableView> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue().editable());
//        }
        return tableMap;
    }
}
