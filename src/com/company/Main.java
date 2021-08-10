package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Connection con;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver"); // Treiber bekanntgemacht
            // Verbindung zur DB herstellen
            con = DriverManager.getConnection("jdbc:oracle:thin:@oracle-srv.edvsz.hs-osnabrueck.de:1521/oraclestud",
                    "htapken", "htapken");

            // Statement erstellen
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("select gehalt from angestellter where gehalt > 40000");


            // Ergebnis verarbeiten
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("gehalt"));
            }

            ArrayList<String> al = new ArrayList();
            Iterator iterator = al.iterator();
            while (iterator.hasNext()) {
                String next = (String) iterator.next();
            }


            PreparedStatement preparedStatement = con.prepareStatement("select gehalt from angestellter where gehalt > ?");
            preparedStatement.setInt(1, 40000);
            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData1 = resultSet.getMetaData();
            metaData1.getColumnCount();


            // Ergebnis verarbeiten
            while (resultSet.next()) {
                System.out.println(resultSet.getInt("gehalt"));
            }

            DatabaseMetaData metaData = con.getMetaData();
            System.out.println(metaData.getDatabaseProductName());
            System.out.println(metaData.getDefaultTransactionIsolation());
            con.setAutoCommit(false);

            resultSet.close();
            statement.close();
            preparedStatement.close();
            con.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
