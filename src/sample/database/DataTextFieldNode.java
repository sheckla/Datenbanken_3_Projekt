package sample.database;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;

public class DataTextFieldNode extends TextField {
    private int col;
    private int row;

    public DataTextFieldNode() {
        super("");
    }

    public DataTextFieldNode(int col, int row) {
        super("");
        setPrefHeight(20);
        setPrefWidth(150);
        setAlignment(Pos.CENTER);
        textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("NODE(ROW-COL): " + row + " " + col + ", VAL: " + newValue);
                setText(newValue);
                //tf.setPrefWidth(tf.getWidth() + 2); //TODO dynamische verbreitung je nach textlänge
            }
        });
        this.col = col;
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
