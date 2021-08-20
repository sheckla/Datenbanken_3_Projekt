package sample.database;

import java.util.ArrayList;

public class DataMatrix {
    private ArrayList<ArrayList<DataTextFieldNode>> matrix;

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
            tf.setEditable(true);
            textFields.add(tf);
            tf.setChanged(false);
        }
        matrix.add(textFields);
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

    public void markNullables(ArrayList<Boolean> nullables, int row) {
        for (int i = 0; i < matrix.get(row).size(); i++) {
            if (!nullables.get(i)) {
                DataTextFieldNode node = matrix.get(row).get(i);
                node.setNullable(true);
                if (node.getText().equals("")) node.mark();
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

    public String getNextKey() {
        int key = 0;
        try {
            for (int i = 1; i < matrix.size(); i++) {
                int val = Integer.parseInt(matrix.get(i).get(0).getText());
                if (val > key) key = val;
            }
        } catch(NumberFormatException e) {
            System.out.println(e.getMessage());
            return "";
        }
        return Integer.toString(key);
    }

    public int size() {
        return matrix.size();
    }

    public void clear() {
        matrix.clear();
    }
}
