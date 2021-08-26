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
                case "MASCHINE":
                    table.preFillTables.add("STANDORT");
                    table.preFillTables.add("INVENTARGEGENSTAND");
                    table.iterableKeyValues.add("SN");
                    table.iterableKeyValues.add("INVENTARNR");
                    table.iterableKeyValuesTables.add("INVENTARGEGENSTAND");
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
                    break;
                case "GESCHAEFTSPARTNER":
                    table.preFillTables.add("STANDORT");
                    break;
                case "PERSONAL": // TODO checkmarker -  bis hier funktionierts
                    table.preFillTables.add("STANDORT");
                    table.defaultSysdate.add("EINSTELLUNGSDATUM");
                    table.iterableKeyValues.add("MITARBEITERNR");
                    table.decimalValues.add("GEHALT");
                    break;
                case "ABWESENHEIT":
                    table.defaultSysdate.add("ANFANG");
                    break;
                case "SEX":
                    table.iterableKeyValues.add("SEXNR");
                    break;
                case "AUFGABE": //todo Fertiggestellt bei Aufgabe ist ein boolean der in der Anwendung direkt als Fertiggestellt(1) oder inBearbeitung(0) dargestellt werden könnte. Beim erstellen vlt. auch einfach als checkbox anwählbar.
                    table.preFillTables.add("KOSTENSTELLE");
                    table.preFillTables.add("FELDAUFGABE");
                    table.preFillTables.add("WARTUNG");
                    table.preFillTables.add("VERTIEB");
                    table.preFillTables.add("SONSTIGES");
                    table.defaultSysdate.add("ERSTELLDATUM");
                    table.decimalValues.add("BETRAG");
                    table.iterableKeyValues.add("AUFGABENNR");
                    table.mToNTables.add("BEINHALTET");
                    table.correspondingTables.add("INVENTARGEGENSTAND");
                    break;
                case "KOSTENSTELLE":
                    table.iterableKeyValues.add("Kostenstelle");
                    break;
                case "ZUSTAND":
                    break;
                case "INVENTARGEGENSTAND":
                    table.mToNTables.add("BEINHALTET");
                    table.correspondingTables.add("AUFGABE");
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


            }


        }

        for (int i = 22; i < 22; i++) { //views
            TableView table = new TableView(Table.values()[i].toString(), false);
            tableMap.put(table.toString(), table);
            table.displayButtons.add("BARCHART");
            table.displayButtons.add("PIECHART");

            switch (table.toString()) { //todo überlegen welche diagramme für welche views relevant sind
                case "AUFGABENBEREICHE":
                    table.displayButtons.add("BARCHART");
                    break;
            }
        }
        //        for (java.util.Map.Entry<String, TableView> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue().editable());
//        }
        return tableMap;
    }
}
