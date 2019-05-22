package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Kuky on 15/11/2016.
 */

public class Conexion {
    public static Connection GetConnection(Activity a) {
        Connection conexion = null;
        String direccionIP = "192.168.1.4",
                puerto = "3306",
                nombreBase = "presion_aguas",
                user = "root",
                pass = "root";
//        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
//        Cursor c = db.rawQuery("SELECT * FROM configuracion", null);
//        if (c.moveToFirst()) {
//            do {
//                direccionIP = c.getString(0);
//                puerto = c.getString(1);
//                nombreBase = c.getString(2);
//                user = c.getString(3);
//                pass = c.getString(4);
//            } while (c.moveToNext());
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexion = DriverManager.getConnection(
                    "jdbc:mysql://" + direccionIP + ":" + puerto + "/" + nombreBase, user, pass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            conexion = null;
        } catch (SQLException e) {
            e.printStackTrace();
            conexion = null;
        } finally {
            return conexion;
        }
//        } else {
//            Toast.makeText(a, "Debe configurar la conexion al servidor", Toast.LENGTH_SHORT).show();
//        }
//        c.close();
//        db.close();
//        return conexion;
    }
}
