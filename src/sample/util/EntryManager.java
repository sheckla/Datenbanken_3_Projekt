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
    private final DataMatrix entries;   // enthaelt alle daten vom aktuellen Table (falls gepullt), erste Zeile sind die Spaltennamen
    private final DataMatrix deletedEntries;
    private final DataMatrix changedEntries;
    int newEntriesIndex;        // neue eintraege sind ab >= pulledEntries.size()

    public EntryManager(JDBCDatabase jdbc) {
        entries = new DataMatrix();
        deletedEntries = new DataMatrix();
        changedEntries = new DataMatrix();
        this.jdbc = jdbc;
    }

    public void changeTable(TableView table) {
        this.table = table;
    }

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

    public int size() {
        return entries.rowSize();
    }

    public ArrayList<DataTextFieldNode> getNodeEntry(int row) {
        return entries.getNodeEntry(row);
    }

    public void pullData() {
        entries.clear();
        deletedEntries.clear();
        changedEntries.clear();
        jdbc.getConnection();

        if (table.toString().equals("Aufgabenverteilung")) {
            System.out.println();
        }

        if (table != null) {
            entries.initializeColumnNames(jdbc.getColumnNames(table.toString()));
            deletedEntries.initializeColumnNames(jdbc.getColumnNames(table.toString())); // TODO not really necessary
            changedEntries.initializeColumnNames(jdbc.getColumnNames(table.toString()));
            ArrayList<ArrayList<String>> eintraege = jdbc.getEntries(table.toString());

            int row = 1;
            ArrayList<Boolean> nullables = jdbc.getNullables(table.toString());
            for (ArrayList<String> entry : jdbc.getEntries(table.toString())) {
                entries.setEditable(table.editable());
                entries.addEntry(entry, row++);
                entries.markNullables(nullables, row - 1); // not-null felder werden ohne eintrag rot angezeigt
                entries.replaceDecimalValues(table.decimalValues); // punkt zu komma
            }
            newEntriesIndex = entries.rowSize();
        }
        createNewEntry();
        //curDateTime();
    }

    public void commit() {
        for (int i = newEntriesIndex; i < entries.rowSize(); i++) {
            //System.out.println("size" + (entries.size() - newEntriesIndex));
            String insertResultText = jdbc.insert(table.toString(), entries.getEntry(i), table.preFillTables);
            //currentStatement.setText("Commit erfolgreich!");
            //if (!insertResultText.equals("")) currentStatement.setText(insertResultText);
        }

        for (int i = 0; i < deletedEntries.rowSize(); i++) {
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
        deletedEntries.addEntry(entries.getEntry(row), deletedEntries.rowSize());
        entries.removeEntry(row);
    }

    public void createNewEntry() {
        // TODO onbuttonlistener - ENTER
        // TODO code cleanup
        if (table.editable()) {
            entries.addEmptyEntry(jdbc.getNullables(table.toString()), jdbc.getColumnSize(table.toString()));
            ArrayList<String> columns = jdbc.getColumnNames(table.toString());
            ArrayList<String> nextAvailableKeys = entries.getNextAvailableKeys(table.iterableValues);
            ArrayList<String> keys = table.iterableValues;

            // ID_table keys
            sample.database.DataMatrix inventargegenstand = new DataMatrix();

            // get free key value from InventarNr TODO other table too (?)
            if (!table.iterableValuesTables.isEmpty()) {
                jdbc.getConnection();
                inventargegenstand.initializeColumnNames(jdbc.getColumnNames(table.iterableValuesTables.get(0)));
                ArrayList<ArrayList<String>> inventargegenstandEintraege = jdbc.getEntries(table.toString());


                int row = 1;
                ArrayList<Boolean> nullables = jdbc.getNullables(table.iterableValuesTables.get(0));
                for (ArrayList<String> entry : jdbc.getEntries(table.iterableValuesTables.get(0))) {
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
            entries.fillSysDateValues(table.defaultSysdate, entries.rowSize() - 1);
            // fill next iterable key-value of table
            for (int column = 0; column < columns.size(); column++) {
                for (int keyIndex = 0; keyIndex < table.iterableValues.size(); keyIndex++) {
                    if (columns.get(column).equals(keys.get(keyIndex))) {
                        entries.getLatestNodeEntry().get(column).setText(nextAvailableKeys.get(keyIndex));
                        entries.getLatestNodeEntry().get(column).initialVal = nextAvailableKeys.get(keyIndex);
                        entries.getLatestNodeEntry().get(column).setChanged(false);
                    }
                }
            }


        }
    }

    public void searchByKeyword(String keyword) {
        entries.searchByKeyword(keyword);
    }

    public boolean isChanged() {
        return !entries.locateChangedEntries(entries.rowSize()).isEmpty();
    }

    public String curTime() {
        return entries.curTime();
    }

}
