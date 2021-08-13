package sample.database;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;

import javax.xml.crypto.Data;
import javax.xml.soap.Text;
import java.util.ArrayList;

public class DataMatrix {
    private ArrayList<ArrayList<DataTextFieldNode>> matrix;

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
        for (int i = 0; i < matrix.get(matrix.size()-1).size(); i++) {
            entry.add(matrix.get(matrix.size()-1).get(i).getText());
        }
        return entry;
    }

    public void addEntry(ArrayList<String> arr, int row) {
        ArrayList<DataTextFieldNode> textFields = new ArrayList<>();
        int col = 0;
        for (String s : arr) {
            DataTextFieldNode tf = new DataTextFieldNode(col++,row);
            tf.setText(s);
            tf.setEditable(true);
            textFields.add(tf);
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

    public void addEmptyEntry() {
        ArrayList<DataTextFieldNode> entry = new ArrayList<>();
        for (int i = 0; i < matrix.get(0).size(); i++) {
            DataTextFieldNode tf = new DataTextFieldNode(matrix.size(), i);
            entry.add(tf);
        }
        matrix.add(entry);
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
        DataTextFieldNode result = null;
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

    public int size() {return matrix.size();}

    public void clear() {matrix.clear();}
}
