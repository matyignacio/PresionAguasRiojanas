package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Sync {
    private ProgressDialog pDialog;
    private SyncPuntos syncPuntos;
    private Integer check = 0;

    private class SyncPuntos extends AsyncTask<String, Float, String> {

        Activity a;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Estableciendo conexion...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        public SyncPuntos(Activity a) {
            this.a = a;
        }

        @Override
        protected String doInBackground(String... strings) {
            Connection conn;
            PreparedStatement ps;
            ResultSet rs, rs2;
            try {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                conn = Conexion.GetConnection(a);
                String consultaSql = "SELECT * FROM usuarios " +
                        "WHERE pendiente = 1 " +
                        "AND id_tipo_pendiente = 1";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                rs2 = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                while (rs.next()) {
                    String sql = "INSERT INTO usuarios" +
                            "(id," +
                            "nombre," +
                            "mail," +
                            "clave)" +
                            "VALUES" +
                            "(" + rs.getInt(1) + ",'" +
                            rs.getString(2) + "','" +
                            rs.getString(3) + "','" +
                            rs.getString(4) + "');";
                    db.execSQL(sql);
                }
                /* Reseteamos los pendientes*/
                while (rs2.next()) {
                    consultaSql = "UPDATE usuarios " +
                            " SET pendiente = 0 " +
                            " WHERE id = " + rs2.getInt(1) + ";";
                    ps = conn.prepareStatement(consultaSql);
                    ps.executeUpdate();
                }
                check++;
                /*//////////////////////////////////////////////////////////////////////////////////
                                            UPDETEAMOS
                //////////////////////////////////////////////////////////////////////////////////*/

                /*//////////////////////////////////////////////////////////////////////////////////
                                            DELETEAMOS
                //////////////////////////////////////////////////////////////////////////////////*/

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
                Toast.makeText(a, "Se sincronizo con exito", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(a, check.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sincronizarUsuarios(Activity a) {
        syncPuntos = new SyncPuntos(a);
        syncPuntos.execute();
    }
}
