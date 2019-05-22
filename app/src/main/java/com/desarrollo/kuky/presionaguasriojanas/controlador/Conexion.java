package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;

import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Kuky on 21/05/2019.
 */

public class Conexion {
    public static Connection GetConnection(Activity a) {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexion = DriverManager.getConnection(
                    "jdbc:mysql://" + Util.DIRECCION_IP + ":" +
                            Util.PUERTO + "/" +
                            Util.DATA_BASE,
                    Util.USER,
                    Util.CLAVE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            conexion = null;
        } catch (SQLException e) {
            e.printStackTrace();
            conexion = null;
        } finally {
            return conexion;
        }
    }
}
