package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;

import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.CLAVE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.DATA_BASE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PUERTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.USER;

/**
 * Created by Kuky on 21/05/2019.
 */

public class Conexion {
    public static Connection GetConnection(Activity a) {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexion = DriverManager.getConnection(
                    "jdbc:mysql://" + HOST + ":" +
                            PUERTO + "/" +
                            DATA_BASE,
                    USER,
                    CLAVE);
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
