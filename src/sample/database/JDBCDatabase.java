package sample.database;

import java.sql.*;
import java.util.ArrayList;

public class JDBCDatabase {
    String className, URL, user, password;
    Connection connection;

    public JDBCDatabase(String className, String URL, String user, String password) {
        this.className = className;
        this.URL = URL;
        this.user = user;
        this.password = password;
        this.connection = null;
    }

    public void getConnection() {
        //Load the driver class
        try {
            Class.forName(className);
        } catch (ClassNotFoundException ex) {
            System.out.println("Unable to load the class. Terminating the program");
            System.exit(-1);
        }
        //get the connection
        try {
            connection = DriverManager.getConnection(URL, user, password);
        } catch (SQLException ex) {
            System.out.println("Error getting connection: " + ex.getMessage());
            System.exit(-1);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            System.exit(-1);
        }
        System.out.println("Connection successful");
    }


    public ArrayList<String> getColumnNames(String table) {
        ArrayList<String> columns = new ArrayList<>();
        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("select * from " + table);
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                columns.add(rsmd.getColumnName(i));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            exceptionMessageHandle(e);
        }
        return columns;
    }

    // Eintraege als Matrix - [0;AnzahlZeilen]
    public ArrayList<ArrayList<String>> getEntries(String table) {
        ArrayList<ArrayList<String>> entries = new ArrayList<>();
        try {
            // Statement erstellen
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + table);
            ResultSetMetaData rsmd = resultSet.getMetaData();

            entries = new ArrayList<>();

            // Ergebnis verarbeiten
            while (resultSet.next()) {
                ArrayList<String> row = new ArrayList<>();          // einzelner Eintrag (Zeile)
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String val = resultSet.getString(i);
                    if (val == null) {
                        row.add("");
                    } else {
                        row.add(val);
                    }
                }
                entries.add(row);
            }
            statement.close();
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }

    public int getEntrySize(String table) {
        ArrayList<ArrayList<String>> entries = new ArrayList<>();
        int totalEntries = 0;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + table);
            ResultSetMetaData rsmd = resultSet.getMetaData();

            entries = new ArrayList<>();

            while (resultSet.next()) {
                totalEntries++;
            }
            statement.close();
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalEntries;
    }

    public int getColumnSize(String table) {
        return getColumnNames(table).size();
    }

    public void printAllFrom(ArrayList<ArrayList<String>> entries) {
        System.out.println(getAllStringsFrom(entries));
    }

    public String getAllStringsFrom(ArrayList<ArrayList<String>> entries) {
        String result = "";
        for (ArrayList<String> row : entries) {
            for (String s : row) {
                result += s + ", ";
            }
            result += "\n";
        }
        return result;
    }

    public String getInsertStatement(String table, ArrayList<String> vals) {
        return "INSERT INTO " + table + " " + columnStatement(table) + " VAlUES " +
                valueStatement(vals);
    }

    public String update(String table, ArrayList<String> oldVals, ArrayList<String> newVals, ArrayList<String> autoFill) {
        try {
            insertAutoFillValues(table, newVals, autoFill);
            Statement st = connection.createStatement();
            String primaryKey = getColumnNames(table).get(0);
            String sql = "UPDATE " + table + " SET " + setStatement(getColumnNames(table), newVals) + " WHERE " +
                    whereStatement(table, oldVals);
            System.out.println(sql);
            st.executeQuery(sql);
            st.close();
        } catch (Exception e) {
            return exceptionMessageHandle(e);
        }
        return "Eintrag wurde aktualisiert!";
    }

    public String delete(String table, String val) {
        try {
            Statement st = connection.createStatement();
            String primaryKey = getColumnNames(table).get(0);
            String sql = "DELETE FROM " + table + " WHERE " + primaryKey + " = '" + val + "'";
            System.out.println(sql);
            st.executeQuery(sql);
            st.close();
        } catch (Exception e) {
            return exceptionMessageHandle(e);
        }
        return "Eintrag wurde gel??scht!";
    }

    // table immer mit .toString als Parameter!
    public String insert(String table, ArrayList<String> vals, ArrayList<String> autoFill) {
        System.out.println("\n MAIN INSERT\n");
        try {
            insertAutoFillValues(table, vals, autoFill);
            insertToDatabase(table, vals);
        } catch (Exception e) {
            return exceptionMessageHandle(e);
        }
        return "Eintrag wurde eingef??gt!";
    }

    private void insertAutoFillValues(String table, ArrayList<String> vals, ArrayList<String> autoFill) {
        if (!autoFill.isEmpty()) {
            for (int i = 0; i < autoFill.size(); i++) {
                ArrayList<String> toFilledColumnNames = getColumnNames(autoFill.get(i));
                ArrayList<String> currentColumnNames = getColumnNames(table);

                ArrayList<String> toFilledColumnValues = new ArrayList<>();
                for (int n = 0; n < currentColumnNames.size(); n++) {
                    for (String filledColumn : toFilledColumnNames) {
                        if (currentColumnNames.get(n).equals(filledColumn)) {
                            toFilledColumnValues.add(vals.get(n));
                        }
                    }
                }

                insertToDatabase(autoFill.get(i), toFilledColumnValues);
            }
        }
    }

    // single row add
    private String insertToDatabase(String table, ArrayList<String> vals) {
        try {
            Statement st = connection.createStatement();
            String sql = "INSERT INTO " + table + " " + columnStatement(table) + " VAlUES " +
                    valueStatement(vals);
            System.out.println(sql);
            st.executeQuery(sql);
            st.close();
        } catch (Exception e) {
            return exceptionMessageHandle(e);
        }
        return "";
    }

    public String columnStatement(String table) {
        String s = "(";
        ArrayList<String> columns = getColumnNames(table);
        for (int i = 0; i < columns.size(); i++) {
            s += columns.get(i);
            if (i != columns.size() - 1) s += ", ";
        }
        return s + ")";
    }

    public String valueStatement(ArrayList<String> vals) {
        String s = "(";
        for (int i = 0; i < vals.size(); i++) {
            if (isDate(vals.get(i))) {
                s += "TO_DATE('" + vals.get(i).substring(0,vals.get(i).length()-2) + "', 'YYYY-MM-DD HH24:MI:SS')";
            } else if (!vals.get(i).equals("")) {
                s += "'" + vals.get(i) + "'";
            } else if(vals.get(i).equalsIgnoreCase("nein")) {
                s += "'0'";
            } else if (vals.get(i).equalsIgnoreCase("ja")) {
                s += "'1'";
            } else {
                s += "NULL";
            }
            if (i != vals.size() - 1) s += ", ";
        }
        return s + ")";
    }

    public String setStatement(ArrayList<String> columns, ArrayList<String> vals) {
        String s = "";
        for (int i = 0; i < vals.size(); i++) {
            s += columns.get(i) + " = ";
            if (isDate(vals.get(i))) {
                s += "TO_DATE('" + vals.get(i).substring(0,vals.get(i).length()-2) + "', 'YYYY-MM-DD HH24:MI:SS')";
            } else if(vals.get(i).equals("nein")) {
                s += "'0'";
            } else if (vals.get(i).equals("ja")) {
                s += "'1'";
            } else if (!vals.get(i).equals("")) {
                s += "'" + vals.get(i) + "'";
            } else {
                s += "NULL";
            }
            if (i != vals.size() - 1) s += ", ";
        }
        return s;
    }

    public String whereStatement(String table, ArrayList<String> vals) {
        String s = "";
        ArrayList<String> keys = getKeys(table);
        ArrayList<String> columns = getColumnNames(table);
        for (int i = 0; i < vals.size(); i++) {
            if (keys.get(0).equals(columns.get(i))) { // TODO keys abfragen, aktuell nur der erste
                s += keys.get(i) + " = '" + vals.get(i) + "'";
            }
        }
        return s;
    }

    public String exceptionMessageHandle(Exception e) {
        if (e.getMessage() == null) return "";
        System.out.println(e.getMessage());
        //e.printStackTrace();
        if (e instanceof SQLIntegrityConstraintViolationException) {
            return "Ein Constraint wurde verletzt!";
        }
        return e.getMessage().substring(11);
    }

    public ArrayList<String> getKeys(String table) {
        ArrayList<String> keys = new ArrayList<>();
        try {
            Statement st = connection.createStatement();
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getPrimaryKeys(null, null, table.toUpperCase());
            int i = 0;
            while (rs.next()) {
                keys.add(rs.getString("COLUMN_NAME"));
            }
            st.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keys;
    }

    public ArrayList<Boolean> getNullables(String table) {
        ArrayList<Boolean> nullables = new ArrayList<>();
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + table);
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= getColumnSize(table); i++) {
                if (rsmd.isNullable(i) == 1) {
                    nullables.add(true);
                } else {
                    nullables.add(false);
                }
            }
            st.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nullables;
    }

    private boolean isDate(String s) {
        // format YYYY-MM-DD HH24:MI:SS
        // TODO to be extended for accuracy
        if (s.length() > 7) {
            return s.charAt(4) == '-' && s.charAt(7) == '-';
        }
        return false;
    }

    public void close() {
        try {
            connection.close();
            System.out.println("Connection closed");
        } catch (Exception e) {
            exceptionMessageHandle(e);
        }
    }
}