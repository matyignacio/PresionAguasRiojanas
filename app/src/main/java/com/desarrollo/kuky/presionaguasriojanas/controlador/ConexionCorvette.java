package com.desarrollo.kuky.presionaguasriojanas.controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Kuky on 01/08/2019.
 */

public class ConexionCorvette {
    public static Connection GetConnection(String database) {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexion = DriverManager.getConnection(
                    "jdbc:mysql://192.168.1.112:3306/" +
                            database,
                    "consulta",
                    "Consulta123");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            return conexion;
        }
    }
}
