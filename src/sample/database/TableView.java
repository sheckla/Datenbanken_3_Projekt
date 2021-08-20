package sample.database;

public class TableView {
    private String table;
    private boolean editable;

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
