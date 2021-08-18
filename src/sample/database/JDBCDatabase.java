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



    public ArrayList<String> getColumnNames(Table table) {
        ArrayList<String> columns = new ArrayList<>();
        try {
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery("select * from " + table);
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                columns.add(rsmd.getColumnName(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return columns;
    }

    // Eintraege als Matrix - [0;AnzahlZeilen]
    public ArrayList<ArrayList<String>> getEntries(Table table) {
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
                    row.add(resultSet.getString(i));
                }
                entries.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }

    public ArrayList<ArrayList<String>> createView(String view) {
        ArrayList<ArrayList<String>> entries = new ArrayList<>();
        try {
            // Statement erstellen
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(view);
            ResultSetMetaData rsmd = resultSet.getMetaData();

            entries = new ArrayList<>();

            // Ergebnis verarbeiten
            while (resultSet.next()) {
                ArrayList<String> row = new ArrayList<>();          // einzelner Eintrag (Zeile)
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    row.add(resultSet.getString(i));
                }
                entries.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }

    public int getEntrySize(Table table) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalEntries;
    }

    public int getColumnSize(Table table) {
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

    public String getInsertStatement(Table table, ArrayList<String> vals) {
        return "INSERT INTO " + table.toString() + " " + columnStatement(table) + " VAlUES " +
                valueStatement(vals);
    }

    public String update(Table table, ArrayList<String> oldVals, ArrayList<String> newVals) {
        try {
            Statement st = connection.createStatement();
            String primaryKey = getColumnNames(table).get(0);
            String sql = "UPDATE " + table.toString() + " SET " + setStatement(getColumnNames(table), newVals) + " WHERE " +
                    whereStatement(table, oldVals);
            System.out.println(sql);
            st.executeQuery(sql);
        } catch (Exception e) {
            return exceptionMessageHandle(e);
        }
        return "";
    }

    public String delete(Table table, String val) {
        try {
            Statement st = connection.createStatement();
            String primaryKey = getColumnNames(table).get(0);
            String sql = "DELETE FROM " + table.toString() + " WHERE " + primaryKey + " = '" + val + "'";
            System.out.println(sql);
            st.executeQuery(sql);
        } catch (Exception e) {
            return exceptionMessageHandle(e);
        }
        return "";
    }

    public String insert(Table table, ArrayList<String> vals) {
        try {
            Statement st = connection.createStatement();
            String sql = "INSERT INTO " + table.toString() + " " + columnStatement(table) + " VAlUES " +
                    valueStatement(vals);
            System.out.println(sql);
            st.executeQuery(sql);
        } catch (Exception e) {
            return exceptionMessageHandle(e);
        }
        return "";
    }

    public String columnStatement(Table table) {
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
            if (!vals.get(i).equals("")) {
                s += "'" + vals.get(i) + "'";
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
            s += columns.get(i) + " = '" + vals.get(i) + "'";
            if (i != vals.size() - 1) s += ", ";
        }
        return s;
    }

    public String whereStatement(Table table, ArrayList<String> vals) {
        String s = "";
        ArrayList<String> keys= getKeys(table);
        ArrayList<String> columns = getColumnNames(table);
        for (int i = 0; i < vals.size(); i++) {
            if (keys.get(i).equals(columns.get(i))) {
                s += keys.get(i) + " = '" + vals.get(i) + "'";
            }
        }
        return s;
    }

    public String exceptionMessageHandle(Exception e) {
        System.out.println(e.getMessage());
        if (e instanceof SQLIntegrityConstraintViolationException) {
            return "Ein Constraint wurde verletzt!";
        }
        return e.getMessage().substring(11);
    }

    public ArrayList<String> getKeys(Table table) {
        ArrayList<String> keys = new ArrayList<>();
        try {
            Statement st = connection.createStatement();
            DatabaseMetaData dm = connection.getMetaData();
            ResultSet rs = dm.getPrimaryKeys(null, null, table.toString().toUpperCase());
            int i = 0;
            while (rs.next()) {
                keys.add(rs.getString("COLUMN_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keys;
    }

    public ArrayList<Boolean> getNullables(Table table) {
        ArrayList<Boolean> nullables = new ArrayList<>();
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + table.toString());
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= getColumnSize(table); i++) {
                if (rsmd.isNullable(i) == 1) {
                    nullables.add(true);
                } else {
                    nullables.add(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nullables;
    }
}