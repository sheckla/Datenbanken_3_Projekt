package sample.database;

import java.util.ArrayList;
    //toDo: iterableKeyValuesTables entfernen. alle fälle die in dieser Liste enthalten sind stehen auch in iterableKeyValues Liste mit InventarNr

public class TableView {
    private final String table;
    private final boolean editable; // editable = true -> normal Table, false -> View
    public ArrayList<String> preFillTables = new ArrayList<>();         // tables to be filled before main insert
    public ArrayList<String> iterableValues = new ArrayList<>();        // iterable key values
    public ArrayList<String> iterableValuesTables = new ArrayList<>();  // tables for <keyValues>
    public ArrayList<String> decimalValues = new ArrayList<>();         // '.' to ','
    public ArrayList<String> displayButtons = new ArrayList<>();        // format buttons for UiController::createRightNavigation()
    public ArrayList<String> defaultSysdate = new ArrayList<>();        // values to be filled with sysdate
    public ArrayList<String> connectedTables = new ArrayList<>();
    public ArrayList<String> correspondingConnectedTables = new ArrayList<>();
    public ArrayList<String> graphicDisplayStrings = new ArrayList<>(); // 0 = Title, 1 = xAxis, 2 = yAxis, 3 = description

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
