package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

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
}

//
//            PreparedStatement preparedStatement = con.prepareStatement("select gehalt from angestellter where gehalt > ?");
//            preparedStatement.setInt(1, 40000);
//            resultSet = preparedStatement.executeQuery();
//
//            ResultSetMetaData metaData1 = resultSet.getMetaData();
//            metaData1.getColumnCount();
//
//
//            // Ergebnis verarbeiten
//            while (resultSet.next()) {
//                System.out.println(resultSet.getInt("gehalt"));
//            }
//
//            DatabaseMetaData metaData = con.getMetaData();
//            System.out.println(metaData.getDatabaseProductName());
//            System.out.println(metaData.getDefaultTransactionIsolation());
//            con.setAutoCommit(false);
//
//            resultSet.close();
//            statement.close();
//            preparedStatement.close();
//            con.close();