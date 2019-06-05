package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.desarrollo.kuky.presionaguasriojanas.objeto.HistorialPuntos;
import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.ui.MapActivity;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class HistorialPuntosControlador {

    private SyncMysqlToSqlite syncMysqlToSqlite;
    private SyncSqliteToMysql syncSqliteToMysql;
    private ArrayList<HistorialPuntos> historiales;
    private ProgressDialog pDialog;


    private class SyncSqliteToMysql extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;
        private ArrayList<HistorialPuntos> historiales;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Enviando historial...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        public SyncSqliteToMysql(Activity a) {
            this.a = a;
            check = ERROR;
            historiales = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... strings) {
            historiales = extraerTodosPendientes(a);
            Connection conn;
            try {
                conn = Conexion.GetConnection(a);
                String consultaSql;
                for (int i = 0; i < historiales.size(); i++) {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                    PreparedStatement ps;
                    consultaSql = "INSERT INTO `historial_puntos_presion` " +
                            "(`latitud`, `longitud`, `presion`, `fecha`, `id_punto_presion`) " +
                            "VALUES " +
                            "('" + historiales.get(i).getLatitud() + "', " +
                            "'" + historiales.get(i).getLongitud() + "', " +
                            "'" + historiales.get(i).getPresion() + "', " +
                            "'" + historiales.get(i).getFecha() + "', " +
                            "'" + historiales.get(i).getPuntoPresion().getId() + "');";
                    ps = conn.prepareStatement(consultaSql);
                    ps.execute();
                    /*//////////////////////////////////////////////////////////////////////////////////
                                            UPDETEAMOS LA PRESION
                    //////////////////////////////////////////////////////////////////////////////////*/
                    consultaSql = "UPDATE `puntos_presion` " +
                            "SET `presion` = '" + historiales.get(i).getPresion() + "' " +
                            "WHERE `id` = '" + historiales.get(i).getPuntoPresion().getId() + "' ;";
                    ps = conn.prepareStatement(consultaSql);
                    ps.execute();
                    ps.close();
                    /*//////////////////////////////////////////////////////////////////////////////////
                                            BAJAMOS EL PENDIENTE DEL HISTORIAL
                    //////////////////////////////////////////////////////////////////////////////////*/
                    actualizarPendiente(historiales.get(i), a);
                    check++;
                }
                conn.close();
                if (check == historiales.size()) {
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
                mostrarMensaje(a, "Se enviaron los historiales de forma exitosa");
                PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
                puntoPresionControlador.sincronizarDeMysqlToSqlite(a);
            } else {
                mostrarMensaje(a, "Error en el checkHistorialToMysql");
            }
        }
    }

    public int sincronizarDeSqliteToMysql(Activity a) {
        try {
            syncSqliteToMysql = new SyncSqliteToMysql(a);
            syncSqliteToMysql.execute();
            return Util.EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Eror SyncSqliteToMysql HPC" + e.toString());
            return ERROR;
        }
    }

    private class SyncMysqlToSqlite extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Trayendo historial...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        public SyncMysqlToSqlite(Activity a) {
            this.a = a;
            check = ERROR;
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
                check++;
                if (check == EXITOSO) {
                    db.close();
                    rs.close();
                    ps.close();
                    conn.close();
                    return "EXITO";
                } else {
                    db.close();
                    rs.close();
                    ps.close();
                    conn.close();
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
                mostrarMensaje(a, "Se copio el historial de forma exitosa");
                abrirActivity(a, MapActivity.class);
            } else {
                mostrarMensaje(a, "Error en el checkHistorial");
            }
        }

    }

    public int sincronizarDeMysqlToSqlite(Activity a) {
        try {
            syncMysqlToSqlite = new SyncMysqlToSqlite(a);
            syncMysqlToSqlite.execute();
            return Util.EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Eror SyncMysqlToSqlite HPC" + e.toString());
            return ERROR;
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

    public ArrayList<HistorialPuntos> extraerTodosPendientes(Activity a) {
        historiales = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM historial_puntos_presion " +
                "WHERE pendiente = 1 " +
                "ORDER BY id ASC", null);
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
            mostrarMensaje(a, "Error insertar HPC " + e.toString());
            return ERROR;
        }
    }

    public int actualizarPendiente(HistorialPuntos historialPuntos, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE historial_puntos_presion " +
                    "SET presion = '" + historialPuntos.getPresion() + "', pendiente = 0 " +
                    "WHERE id=" + historialPuntos.getId();
            db.execSQL(sql);
            db.close();
            return Util.EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente HPC " + e.toString());
            return ERROR;
        }
    }
}
