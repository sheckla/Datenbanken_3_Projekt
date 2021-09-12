package sample.database;

import javafx.scene.control.ComboBox;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DataMatrix {
    private ArrayList<ArrayList<DataTextFieldNode>> matrix; // column-titles are row: 0
    private boolean editable = true;

    // TODO Views nicht editierbar
    public DataMatrix() {
        matrix = new ArrayList<>();
    }

    public void initializeColumnNames(ArrayList<String> arr) {
        ArrayList<DataTextFieldNode> entry = new ArrayList<>();
        int colIndex = 0;
        for (String s : arr) {
            DataTextFieldNode tf = new DataTextFieldNode(colIndex++, 0);
            tf.setText(s);
            tf.setEditable(false);
            tf.setStyle("-fx-font-weight: bold");
            entry.add(tf);
        }
        matrix.add(entry);
    }

    public void searchByKeyword(String keyword) {
        ArrayList<ArrayList<DataTextFieldNode>> containing = new ArrayList<>();
        containing.add(matrix.get(0)); // add columns names as first row
        for (int row = 1; row < matrix.size(); row++) {
            for (int col = 0; col < matrix.get(0).size(); col++) {
                if (matrix.get(row).get(col).getText().toLowerCase().contains(keyword.toLowerCase())) {
                    containing.add(matrix.get(row));
                    break;
                }
            }
        }

        // update col/row for each DataTextFieldNode
        for (int row = 1; row < containing.size(); row++) {
            for (int col = 0; col < containing.get(0).size(); col++) {
                containing.get(row).get(col).setCol(col);
                containing.get(row).get(col).setRow(row);
            }
        }
        matrix = containing;
    }

    public ArrayList<String> getEntry(int row) {
        ArrayList<String> entry = new ArrayList<>();
        for (int i = 0; i < matrix.get(row).size(); i++) {
            entry.add(matrix.get(row).get(i).getText());
        }
        return entry;
    }

    public ArrayList<String> getLatestEntry() {
        ArrayList<String> entry = new ArrayList<>();
        for (int i = 0; i < matrix.get(matrix.size() - 1).size(); i++) {
            entry.add(matrix.get(matrix.size() - 1).get(i).getText());
        }
        return entry;
    }

    public void addEntry(ArrayList<String> arr, int row) {
        ArrayList<DataTextFieldNode> textFields = new ArrayList<>();
        int col = 0;
        for (String s : arr) {
            DataTextFieldNode tf = new DataTextFieldNode(col++, row);
            for (int i = 0; i < matrix.get(0).size()-1; i++) { // not all matrixes have initialized .get(0) (deletedEntries, changedEntries)
                if (matrix.get(0).get(i).getText().equals("FERTIGGESTELLT")) {
                    if (s.equals("0")) {
                        s = "nein";
                    } else if (s.equals("1")) {
                        s = "ja";
                    }
                }
            }
            tf.setText(s);
            tf.initialVal = s;
            tf.setEditable(editable);
            textFields.add(tf);
            tf.setChanged(false);
        }
        matrix.add(textFields);
    }

    public void setMatrix(ArrayList<ArrayList<String>> matrix) {
        for (int row = 0; row < matrix.size(); row++) {
            addEntry(matrix.get(row), row);
        }
    }

    public void removeEntry(int index) {
        matrix.remove(index);
    }

    public ArrayList<ArrayList<String>> getStringMatrix() {
        ArrayList<ArrayList<String>> stringMatrix = new ArrayList<>();
        for (ArrayList<DataTextFieldNode> arr : matrix) {
            ArrayList<String> stringRow = new ArrayList<>();
            for (DataTextFieldNode tf : arr) {
                stringRow.add(tf.getText());
            }
            stringMatrix.add((stringRow));
        }
        return stringMatrix;
    }

    public void addEmptyEntry(ArrayList<Boolean> nullables, int size) {
        ArrayList<DataTextFieldNode> entry = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            DataTextFieldNode tf = new DataTextFieldNode(i, matrix.size());
            entry.add(tf);
            tf.setChanged(false);
        }
        matrix.add(entry);
        markNullables(nullables, matrix.size() - 1);
    }

    public String getVal(int col, int row) {
        String result = "";
        for (ArrayList<DataTextFieldNode> arr : matrix) {
            for (DataTextFieldNode tf : arr) {
                if (tf.getCol() == col && tf.getRow() == row) return tf.getText(); //TODO unecessary, just get via matrix
            }
        }
        return result;
    }

    public DataTextFieldNode getNode(int col, int row) {
        DataTextFieldNode result = new DataTextFieldNode();
        for (ArrayList<DataTextFieldNode> arr : matrix) {
            for (DataTextFieldNode tf : arr) {
                if (tf.getCol() == col && tf.getRow() == row) return tf;
            }
        }
        return result;
    }

    public ArrayList<DataTextFieldNode> getNodeEntry(int row) {
        return matrix.get(row);
    }

    public ArrayList<DataTextFieldNode> getLatestNodeEntry() {
        return getNodeEntry(matrix.size() - 1);
    }

    public void markNullables(ArrayList<Boolean> nullables, int row) {
        for (int i = 0; i < matrix.get(row).size(); i++) {
            if (!nullables.get(i)) {
                DataTextFieldNode node = matrix.get(row).get(i);
                node.setNullable(true);
                if (node.getText().equals("")) node.mark();
            }
        }
    }

    public void replaceDecimalValues(ArrayList<String> decimalValues) {
        ArrayList<String> columns = getEntry(0);
        for (int col = 0; col < columns.size(); col++) {
            for (int j = 0; j < decimalValues.size(); j++) {
                if (columns.get(col).equals(decimalValues.get(j))) {
                    for (int row = 1; row < matrix.size(); row++) {
                        String s = matrix.get(row).get(col).getText();
                        s = s.replace(".", ",");
                        matrix.get(row).get(col).setText(s);
                        matrix.get(row).get(col).initialVal = s;
                        matrix.get(row).get(col).setChanged(false);
                    }
                }
            }
        }
    }

    public void fillSysDateValues(ArrayList<String> sysDateValues, int row) {
        ArrayList<String> columns = getEntry(0);
        for (int col = 0; col < columns.size(); col++) {
            for (int j = 0; j < sysDateValues.size(); j++) {
                if (columns.get(col).equals(sysDateValues.get(j))) {
                    matrix.get(row).get(col).setText(curDateTime());
                    matrix.get(row).get(col).initialVal = curDateTime();
                    matrix.get(row).get(col).setChanged(false);
                }
            }
        }
    }


    public ArrayList<String> getInitialEntry(int row) {
        ArrayList<String> entry = new ArrayList<>();
        for (DataTextFieldNode node : matrix.get(row)) {
            entry.add(node.initialVal);
        }
        return entry;
    }

    public ArrayList<String> getNextAvailableKeys(ArrayList<String> keys) {
        if (!keys.isEmpty()) {
            ArrayList<Integer> keyValues = new ArrayList<>();
            for (int column = 0; column < matrix.get(0).size(); column++) { // iterate columns
                for (int keyIndex = 0; keyIndex < keys.size(); keyIndex++) {
                    // iterate through each row with corresponding column name
                    if (matrix.get(0).get(column).getText().equals(keys.get(keyIndex))) {
                        keyValues.add(0);
                        for (int row = 1; row < matrix.size(); row++) {
                            String text = matrix.get(row).get(column).getText();
                            if (text.equals("") || text.equals("ja")) text = "0";
                            int val = Integer.parseInt(text);
                            if (keyValues.get(keyIndex) <= val) keyValues.set(keyIndex, val);
                        }
                        keyValues.set(keyIndex, 1 + keyValues.get(keyIndex));
                    }
                }
            }

            ArrayList<String> keyStrings = new ArrayList<>();
            for (Integer i : keyValues) {
                keyStrings.add(i + "");
            }
            return keyStrings;
        }
        return null;
    }

    public ArrayList<Integer> locateChangedEntries(int to) {
        ArrayList<Integer> changed = new ArrayList<>();
        for (int i = 1; i < to; i++) { // TODO indexfehler beime löschen,aber wirkt sich nicht auf funktionalität aus
            for (DataTextFieldNode node : matrix.get(i)) {
                if (node.changed()) {
                    changed.add(i);
                    //break;
                }
            }
        }
        return changed;
    }

    public int rowSize() {
        return matrix.size();
    }

    public void clear() {
        matrix.clear();
    }

    public void setEditable(Boolean b) {
        this.editable = b;
    }

    public String curTime() {
        LocalDateTime date = LocalDateTime.now();
        String hour = Integer.toString(date.getHour());
        String minutes = Integer.toString(date.getMinute());
        if (hour.length() <= 1) {
            hour = 0 + hour;
        }
        if (minutes.length() <= 1) {
            minutes = 0 + minutes;
        }
        String time = hour + ":" + minutes;
        return time;
    }

    // formats date to fit for oracle database insert
    public String curDateTime() {
        LocalDateTime date = LocalDateTime.now();
        String month = date.getMonthValue() + "";
        String day = date.getDayOfMonth() + "";
        String hour = date.getHour() + "";
        String minute = date.getMinute() + "";
        String second = date.getSecond() + "";
        String dateTime = date.getYear() + "-" + formatDateValue(month, 2) + "-" + formatDateValue(day, 2) + " " + formatDateValue(hour, 2) +
                ":" + formatDateValue(minute, 2) + ":" + formatDateValue(second, 2) + ".0";
        return dateTime;
    }

    // fills date value with zeros in front of it (fillTo)
    // 6
    // becomes
    // 06
    private String formatDateValue(String val, int fillTo) {
        for (int i = 0; i < fillTo - val.length(); i++) {
            val = "0" + val;
        }
        return val;
    }


    public void setComboBox(ComboBox comboBox) {
        //this.comboBox = comboBox;
    }

    public void setTextWithCB(ComboBox<String> comboBox) {
        //addEntry(comboBox.getValue());
    }


}
