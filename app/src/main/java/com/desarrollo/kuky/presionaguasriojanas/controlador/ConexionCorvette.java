package com.desarrollo.kuky.presionaguasriojanas.controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionCorvette {
    private static final String DATA_BASE = "CAPITALPRUEBA";
    private static final String HOST = "192.168.1.112";
    private static final String USER = "root";
    private static final String CLAVE = "sqlibmlr963*";
    private static final String PUERTO = "3306";

    public static Connection GetConnection() {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexion = DriverManager.getConnection(
                    "jdbc:mysql://" + HOST + ":" + PUERTO + "/" +
                            DATA_BASE, USER, CLAVE);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            return conexion;
        }
    }
}
