package sample.util;

import java.util.ArrayList;

// Handler for adding 2 row values to an N:M table
public class TableClickHandler {
    private boolean secondClick = false; // second row from other table
    private boolean buttonClicked = false; // button für adding nToMTable
    private ArrayList<String> nToMColumns;

    private ArrayList<String> firstRow; // FIRST TABLE
    private ArrayList<String> firstColumns;

    private ArrayList<String> secondColumns;
    private ArrayList<String> secondRow; // SECOND TABLE

    public TableClickHandler() {

    }

    public ArrayList<String> getFilledNtoMColumns() {
        ArrayList<String> filledVals = new ArrayList<>();
        filledVals.add(firstRow.get(findMatchingColumnIndex(firstColumns, nToMColumns)));
        filledVals.add(secondRow.get(findMatchingColumnIndex(secondColumns, nToMColumns)));

        return filledVals;
    }

    private int findMatchingColumnIndex(ArrayList<String> columns1,ArrayList<String> columns2) {
        for (int i = 0; i < columns1.size(); i++) {
            for (int j = 0; j < columns2.size(); j++) {
                if (firstColumns.get(i).equals(nToMColumns.get(j))) return i;
            }
        }
        return -1;
    }

    public boolean isSecondClick() {
        return secondClick;
    }

    public void setSecondClick(boolean secondClick) {
        this.secondClick = secondClick;
    }

    public boolean isButtonClicked() {
        return buttonClicked;
    }

    public void setButtonClicked(boolean buttonClicked) {
        secondClick = true;
        this.buttonClicked = buttonClicked;
    }

    public ArrayList<String> getFirstRow() {
        return firstRow;
    }

    public ArrayList<String> getSecondRow() {
        return secondRow;
    }


    public void setFirstEntry(ArrayList<String> row, ArrayList<String> columns) {
        firstRow = row;
        firstColumns = columns;
    }

    public void setSecondEntry(ArrayList<String> row, ArrayList<String> columns) {
        secondRow = row;
        secondColumns = columns;
    }

    public void setnToMColumns(ArrayList<String> columns) {
        nToMColumns = columns;
        System.out.println(nToMColumns);
    }

    public void reset() {
        secondClick = false;
        buttonClicked = false;
    }
}
