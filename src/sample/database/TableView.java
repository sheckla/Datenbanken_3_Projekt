package sample.database;

import java.util.ArrayList;

public class TableView {
    private String table;
    private boolean editable;
    public ArrayList<String> preFillTables = new ArrayList<>(); // Tabellen welche vor dem eigentlichen insert gefuellt werden
    public ArrayList<String> iterableKeyValues = new ArrayList<>(); // iterable key values
    //toDo: iterableKeyValuesTables entfernen. alle fälle die in dieser Liste enthalten sind stehen auch in iterableKeyValues Liste mit InventarNr
    public ArrayList<String> iterableKeyValuesTables = new ArrayList<>(); // tables for <keyValues>
    public ArrayList<String> decimalValues = new ArrayList<>(); //punkt durch komma ersetzen
    public ArrayList<String> displayButtons = new ArrayList<>(); // format buttons -> createRightNavigation()
    public ArrayList<String> defaultSysdate = new ArrayList<>(); //in diesen spalten wird standardmäßig schon das sysdate eingetragen
    public ArrayList<String> mToNTables = new ArrayList<>(); // zwischentabellen
    public ArrayList<String> correspondingTables = new ArrayList<>(); // andere Tabelle der Zwischentabelle

    public TableView(String table, boolean b) {
        this.table = table;
        editable = b;
    }

    public boolean editable() {return editable;}

    @Override
    public String toString() {
        return table;
    }
}
