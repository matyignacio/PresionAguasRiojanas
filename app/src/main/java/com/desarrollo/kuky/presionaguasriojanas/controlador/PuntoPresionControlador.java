package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.TipoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.TipoPunto;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PuntoPresionControlador {
    private ProgressDialog pDialog;
    private SyncMysqlToSqlite syncMysqlToSqlite;
    private Integer check;
    private ArrayList<PuntoPresion> puntosPresion;

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
            PreparedStatement ps, ps2;
            ResultSet rs, rs2;
            try {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                conn = Conexion.GetConnection(a);
                String consultaSql = "SELECT * FROM puntos_presion " +
                        "WHERE pendiente = 1 ";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                while (rs.next()) {
                    String sql = "INSERT INTO puntos_presion" +
                            "(id," +
                            "circuito," +
                            "barrio," +
                            "calle1," +
                            "calle2," +
                            "latitud," +
                            "longitud," +
                            "presion," +
                            "id_tipo_presion," +
                            "id_tipo_punto)" +
                            "VALUES" +
                            "(" + rs.getInt(1) + ",'" + // id
                            rs.getInt(2) + "','" + // circuito
                            rs.getString(3) + "','" + // barrio
                            rs.getString(4) + "','" + // calle1
                            rs.getString(5) + "','" + // calle2
                            rs.getDouble(6) + "','" + // latitud
                            rs.getDouble(7) + "','" + // longitud
                            rs.getInt(9) + "','" + // presion
                            rs.getInt(10) + "','" + // id_tipo_presion
                            rs.getInt(11) + "');"; // id_tipo_punto
                    db.execSQL(sql);
                }
                db.close();
                /* Reseteamos los pendientes*/
                ps2 = conn.prepareStatement(consultaSql);
                ps2.execute();
                rs2 = ps2.getResultSet();
                while (rs2.next()) {
                    consultaSql = "UPDATE puntos_presion " +
                            " SET pendiente = 0 " +
                            " WHERE id = " + rs2.getInt(1) + ";";
                    ps = conn.prepareStatement(consultaSql);
                    ps.executeUpdate();
                }
                check++;
                rs.close();
                rs2.close();
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
                Toast.makeText(a, "Se copiaron puntos de forma exitosa", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(a, "Error en el check", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int sincronizarDeMysqlToSqlite(Activity a) {
        try {
            syncMysqlToSqlite = new SyncMysqlToSqlite(a);
            syncMysqlToSqlite.execute();
            return Util.EXITOSO;
        } catch (Exception e) {
            Toast.makeText(a, e.toString(), Toast.LENGTH_SHORT).show();
            return Util.ERROR;
        }
    }

    public ArrayList<PuntoPresion> extraerTodos(Activity a) {
        puntosPresion = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM puntos_presion", null);
        while (c.moveToNext()) {
            PuntoPresion puntoPresion = new PuntoPresion();
            TipoPresion tipoPresion = new TipoPresion();
            TipoPunto tipoPunto = new TipoPunto();
            puntoPresion.setCircuito(c.getInt(0));
            puntoPresion.setCircuito(c.getInt(1));
            puntoPresion.setBarrio(c.getString(2));
            puntoPresion.setCalle1(c.getString(3));
            puntoPresion.setCalle2(c.getString(4));
            puntoPresion.setLatitud(c.getDouble(5));
            puntoPresion.setLongitud(c.getDouble(6));
            puntoPresion.setPresion(c.getFloat(8));
            tipoPresion.setId(c.getInt(9));
            puntoPresion.setTipoPresion(tipoPresion);
            tipoPunto.setId(c.getInt(10));
            puntoPresion.setTipoPunto(tipoPunto);
            puntosPresion.add(puntoPresion);


        }
        c.close();
        db.close();
        return puntosPresion;
    }
}
