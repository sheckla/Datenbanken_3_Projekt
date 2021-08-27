package sample.database;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;

public class DataTextFieldNode extends TextField {
    private int col;
    private int row;
    private boolean nullable = false;
    private boolean changed = false;
    public String initialVal = "";

    public DataTextFieldNode() {
        super("");
    }

    public DataTextFieldNode(int col, int row) {
        super("");
        setPrefHeight(20);
        setPrefWidth(150);
        setAlignment(Pos.CENTER);
        if (isEditable()) {
            textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    changed = true;

                    setText(newValue);
                    TextField defaultTextField = new TextField();
                    if (nullable) {
                        if (!newValue.equals("")) {
                            setStyle(defaultTextField.getStyle());
                        } else {
                            mark();
                        }
                    }
                    //System.out.println("NODE(ROW-COL): " + row + " " + col + ", VAL: " + newValue);
                    //tf.setPrefWidth(tf.getWidth() + 2); //TODO dynamische verbreitung je nach textlänge
                }
            });
        }
//        setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                System.out.println("its me");
//            }
//        });
        setText("");
        this.col = col;
        this.row = row;
    }

    public void setNullable(boolean b) {
        nullable = b;
    }

    public void setChanged(boolean b) {changed = b;}

    public boolean changed() {return changed;}

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

    public void mark() {
        setStyle(";-fx-background-color: #ffd6d6;");
    }
}
