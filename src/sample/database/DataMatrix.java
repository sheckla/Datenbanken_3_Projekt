package sample.database;

import java.util.ArrayList;

public class DataMatrix {
    private ArrayList<ArrayList<DataTextFieldNode>> matrix;
    private ComboBox<String> comboBox;
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
                if (tf.getCol() == col && tf.getRow() == row) return tf.getText();
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
//                    for (int row = 1; row < matrix.size(); row++) {
//                        System.out.println("sysdate");
                        matrix.get(row).get(col).setText(curDateTime());
//                    }
                }
            }
        }
    }


    public ArrayList<String> getInitialEntry(int row) {
        ArrayList<String> entry = new ArrayList<>();
        for (DataTextFieldNode node : matrix.get(row)) {
            entry.add(node.initialVal);
//            if (node.initialVal.equals("")) {
//                entry.add("");
//            } else {
//                entry.add(node.initialVal);
//            }
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
                            if (text.equals("")) text = "0";
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

    public ArrayList<Integer> locateChangedEntries(int from) {
        ArrayList<Integer> changed = new ArrayList<>();
        for (int i = 1; i < from; i++) { // TODO indexfehler beime löschen,aber wirkt sich nicht auf funktionalität aus
            for (DataTextFieldNode node : matrix.get(i)) {
                if (node.changed()) {
                    changed.add(i);
                    //break;
                }
            }
        }
        return changed;
    }

    public int size() {
        return matrix.size();
    }

    public void clear() {
        matrix.clear();
    }
}
