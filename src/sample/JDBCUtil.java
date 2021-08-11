package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

public class JDBCUtil {
    String className, URL, user, password;
    Connection connection;

    public JDBCUtil(String className, String URL, String user, String password) {
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

    // Eintraege als Matrix - [0;AnzahlZeilen]
    public ArrayList<ArrayList<String>> getEntries(Table table) {
        ArrayList<ArrayList<String>> entries = new ArrayList<>();
        try {
            // Statement erstellen
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from " + table);
            entries = new ArrayList<>();

            // Ergebnis verarbeiten
            while (resultSet.next()) {
                ArrayList<String> row = new ArrayList<>();          // einzelner Eintrag (Zeile)
                for (int i = 1; i <= 10; i++) {
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
        for (ArrayList<String> row : entries) {
            for (String s : row) {
                System.out.print(s + ", ");
            }
            System.out.println();
        }
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