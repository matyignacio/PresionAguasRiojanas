package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.desarrollo.kuky.presionaguasriojanas.objeto.HistorialPuntos;
import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class HistorialPuntosControlador {

    private ProgressDialog pDialog;
    private SyncMysqlToSqlite syncMysqlToSqlite;
    private Integer check;
    private ArrayList<HistorialPuntos> historiales;

    private class SyncMysqlToSqlite extends AsyncTask<String, Float, String> {

        Activity a;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Estableciendo conexion...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        public SyncMysqlToSqlite(Activity a) {
            this.a = a;
            check = 0;
        }

        @Override
        protected String doInBackground(String... strings) {
            Connection conn;
            PreparedStatement ps;
            ResultSet rs;
            try {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                conn = Conexion.GetConnection(a);
                String consultaSql = "SELECT * FROM historial_puntos_presion ";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM historial_puntos_presion");
                while (rs.next()) {
                    String sql = "INSERT INTO `historial_puntos_presion`" +
                            "(`id`," +
                            "`latitud`," +
                            "`longitud`," +
                            "`pendiente`," +
                            "`presion`," +
                            "`fecha`," +
                            "`id_punto_presion`)" +
                            "VALUES" +
                            "('" + rs.getInt(1) + "','" + // id
                            rs.getDouble(2) + "','" + // latitud
                            rs.getDouble(3) + "','" + // longitud
                            "0','" + // pendiente
                            rs.getFloat(4) + "','" + // presion
                            rs.getTimestamp(5) + "','" + // fecha
                            rs.getInt(6) + "');"; // id_tipo_presion
                    db.execSQL(sql);
                }
                db.close();
                check++;
                rs.close();
                ps.close();
                conn.close();
                if (check == 1) {
                    return "EXITO";
                } else {
                    return "ERROR";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s.equals("EXITO")) {
                Toast.makeText(a, "Se copio el historial de forma exitosa", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(a, "Error en el checkHistorial", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int sincronizarDeMysqlToSqlite(Activity a) {
        try {
            syncMysqlToSqlite = new SyncMysqlToSqlite(a);
            syncMysqlToSqlite.execute();
            return Util.EXITOSO;
        } catch (Exception e) {
            Toast.makeText(a, "Eror SyncMysqlToSqlite HPC" + e.toString(), Toast.LENGTH_SHORT).show();
            return Util.ERROR;
        }
    }

    public ArrayList<HistorialPuntos> extraerTodosPorPunto(Activity a, int id) {
        historiales = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM historial_puntos_presion " +
                "WHERE id_punto_presion = " + id + " " +
                "ORDER BY fecha DESC", null);
        while (c.moveToNext()) {
            HistorialPuntos historialPuntos = new HistorialPuntos();
            PuntoPresion puntoPresion = new PuntoPresion();
            historialPuntos.setId(c.getInt(0));
            historialPuntos.setLatitud(c.getDouble(1));
            historialPuntos.setLongitud(c.getDouble(2));
            historialPuntos.setPresion(c.getFloat(4));
            historialPuntos.setFecha(Timestamp.valueOf(c.getString(5)));
            puntoPresion.setId(c.getInt(6));
            historialPuntos.setPuntoPresion(puntoPresion);
            historiales.add(historialPuntos);
        }
        c.close();
        db.close();
        return historiales;
    }

    public int insertar(HistorialPuntos historialPuntos, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "INSERT INTO `historial_puntos_presion`" +
                    "(`latitud`," +
                    "`longitud`," +
                    "`pendiente`," +
                    "`presion`," +
                    "`id_punto_presion`)" +
                    "VALUES" +
                    "('" + historialPuntos.getLatitud() + "','" + // latitud
                    historialPuntos.getLongitud() + "','" + // longitud
                    "1','" + // pendiente
                    historialPuntos.getPresion() + "','" + // presion
                    historialPuntos.getPuntoPresion().getId() + "');"; // id_punto_presion
            db.execSQL(sql);
            sql = "UPDATE puntos_presion " +
                    "SET presion = '" + historialPuntos.getPresion() + "', pendiente = 1 " +
                    "WHERE id=" + historialPuntos.getPuntoPresion().getId();
            db.execSQL(sql);
            db.close();
            return Util.EXITOSO;
        } catch (Exception e) {
            Toast.makeText(a, "Error insertar HPC " + e.toString(), Toast.LENGTH_SHORT).show();
            return Util.ERROR;
        }
    }
}
