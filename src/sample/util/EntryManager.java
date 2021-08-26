package sample.util;

import sample.database.DataMatrix;
import sample.database.DataTextFieldNode;
import sample.database.JDBCDatabase;
import sample.database.TableView;

import java.util.ArrayList;

public class EntryManager {
    // pulled entries = [0,newEntriesIndex-1], new Entries = [newEntriesIndex,entries.size()]
    JDBCDatabase jdbc;
    private TableView table;
    private DataMatrix entries;   // enthaelt alle daten vom aktuellen Table (falls gepullt), erste Zeile sind die Spaltennamen
    private DataMatrix deletedEntries;
    private DataMatrix changedEntries;
    int newEntriesIndex;        // neue eintraege sind ab >= pulledEntries.size()

    public EntryManager(JDBCDatabase jdbc) {
        entries = new DataMatrix();
        deletedEntries = new DataMatrix();
        changedEntries = new DataMatrix();
        this.jdbc = jdbc;
    }

    public void changeTable(TableView table) {this.table = table;}

    public DataMatrix getEntries() {
        return entries;
    }

    public int getNewEntriesIndex() {
        return newEntriesIndex;
    }

    public ArrayList<String> getEntry(int row) {
        return entries.getEntry(row);
    }

    public ArrayList<DataTextFieldNode> getLatestNodeEntry() {
        return entries.getLatestNodeEntry();
    }

    public int size() {return entries.size();}

    public ArrayList<DataTextFieldNode> getNodeEntry(int row) {
        return entries.getNodeEntry(row);
    }

    public void pullData() {
        entries.clear();
        deletedEntries.clear();
        changedEntries.clear();
        jdbc.getConnection();

        if (table != null) {
            entries.initializeColumnNames(jdbc.getColumnNames(table.toString()));
            ArrayList<ArrayList<String>> eintraege = jdbc.getEntries(table.toString());

            int row = 1;
            ArrayList<Boolean> nullables = jdbc.getNullables(table.toString());
            for (ArrayList<String> entry : jdbc.getEntries(table.toString())) {
                entries.addEntry(entry, row++);
                entries.markNullables(nullables, row - 1); // not-null felder werden ohne eintrag rot angezeigt
                entries.replaceDecimalValues(table.decimalValues); // punkt zu komma
            }
            newEntriesIndex = entries.size();
        }
        createNewEntry();
        //curDateTime();
    }

    public void commit() {
        for (int i = newEntriesIndex; i < entries.size(); i++) {
            //System.out.println("size" + (entries.size() - newEntriesIndex));
            String insertResultText = jdbc.insert(table.toString(), entries.getEntry(i), table.preFillTables);
            //currentStatement.setText("Commit erfolgreich!");
            //if (!insertResultText.equals("")) currentStatement.setText(insertResultText);
        }

        for (int i = 0; i < deletedEntries.size(); i++) {
            String val = deletedEntries.getVal(0, i);
            jdbc.delete(table.toString(), val);
        }

        ArrayList<Integer> changedIndices = entries.locateChangedEntries(newEntriesIndex);
        for (Integer i : changedIndices) {
            if (i <= newEntriesIndex) {
                //System.out.println(util.update(table, entries.getInitialEntry(i), entries.getEntry(i)));
                ArrayList<String> initials = entries.getInitialEntry(i);
                ArrayList<String> newEntries = entries.getEntry(i);
                jdbc.update(table.toString(), entries.getInitialEntry(i), entries.getEntry(i), table.preFillTables);
            }
        }
    }

    public void removeEntry(int row) {
        deletedEntries.addEntry(entries.getEntry(row), deletedEntries.size());
        entries.removeEntry(row);
    }

    public void createNewEntry() {
        // TODO onbuttonlistener - ENTER
        // TODO code cleanup

        entries.addEmptyEntry(jdbc.getNullables(table.toString()), jdbc.getColumnSize(table.toString()));
        ArrayList<String> columns = jdbc.getColumnNames(table.toString());
        ArrayList<String> nextAvailableKeys = entries.getNextAvailableKeys(table.iterableKeyValues);
        ArrayList<String> keys = table.iterableKeyValues;

        //mark for ComboBox
//        ArrayList<String> comboBoxMarker = new ArrayList<>();
//        comboBoxMarker.add("SEX");
//
//        for(String name : columns){
//            for(String markerName : comboBoxMarker){
//                if(name.equals(markerName));
//                ComboBox comboBox= new ComboBox();
//                entries.add
//            }
//        }


        // ID_table keys
        sample.database.DataMatrix inventargegenstand = new DataMatrix();


        // get free key value from InventarNr TODO other table too (?)
        if (!table.iterableKeyValuesTables.isEmpty()) {
            jdbc.getConnection();
            inventargegenstand.initializeColumnNames(jdbc.getColumnNames(table.iterableKeyValuesTables.get(0).toString()));
            ArrayList<ArrayList<String>> inventargegenstandEintraege = jdbc.getEntries(table.toString());

            int row = 1;
            ArrayList<Boolean> nullables = jdbc.getNullables(table.iterableKeyValuesTables.get(0).toString());
            for (ArrayList<String> entry : jdbc.getEntries(table.iterableKeyValuesTables.get(0).toString())) {
                inventargegenstand.addEntry(entry, row++);
                inventargegenstand.markNullables(nullables, row - 1);
            }


            // add next free kee to new emptry entry
            ArrayList<String> inventarKeys = new ArrayList<String>();
            inventarKeys.add("INVENTARNR");
            ArrayList<String> freeKeysInventargegenstand = inventargegenstand.getNextAvailableKeys(inventarKeys);
            for (int i = 0; i < keys.size(); i++) {
                if (keys.get(i).equals(inventarKeys.get(0))) {
                    if (Integer.parseInt(freeKeysInventargegenstand.get(0)) >= Integer.parseInt(nextAvailableKeys.get(i)))
                        nextAvailableKeys.set(i, freeKeysInventargegenstand.get(0));
                }
            }
        }
        entries.fillSysDateValues(table.defaultSysdate, entries.size() - 1);
        // TODO Aufgabe -> kein eintrag vorhanden -> neue eintrag key started bei 2 anstatt 1
        // fill next iterable key-value of table
        for (int column = 0; column < columns.size(); column++) {
            for (int keyIndex = 0; keyIndex < table.iterableKeyValues.size(); keyIndex++) {
                if (columns.get(column).equals(keys.get(keyIndex))) {
                    entries.getLatestNodeEntry().get(column).setText(nextAvailableKeys.get(keyIndex));
                }
            }
        }
    }

    public String curTime() {return entries.curTime();}

}
