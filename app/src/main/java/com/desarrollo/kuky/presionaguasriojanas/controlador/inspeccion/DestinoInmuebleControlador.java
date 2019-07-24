package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.desarrollo.kuky.presionaguasriojanas.controlador.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.controlador.Conexion;
import com.desarrollo.kuky.presionaguasriojanas.controlador.presion.TipoPuntoControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.DestinoInmueble;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.TOTAL_ASYNCTASKS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class DestinoInmuebleControlador {
    private ProgressDialog pDialog;

    private class SyncMysqlToSqlite extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setTitle("SINCRONIZANDO");
            pDialog.setMessage("5/" + TOTAL_ASYNCTASKS +
                    " - Recibiendo destino de inmuebles...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        SyncMysqlToSqlite(Activity a) {
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
                conn = Conexion.GetConnection();
                String consultaSql = "SELECT * FROM destino_inmueble ";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM destino_inmueble");
                while (rs.next()) {
                    String sql = "INSERT INTO `destino_inmueble`" +
                            " VALUES" +
                            " ('" + rs.getInt(1) + "','" + // id
                            rs.getString(2) + "');"; // nombre
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
                TipoPuntoControlador tipoPuntoControlador = new TipoPuntoControlador();
                tipoPuntoControlador.sincronizarDeMysqlToSqlite(a);
            } else {
                mostrarMensaje(a, "Error en el checkDestinoInmueble");
            }
        }

    }

    public void sincronizarDeMysqlToSqlite(Activity a) {
        try {
            SyncMysqlToSqlite syncMysqlToSqlite = new SyncMysqlToSqlite(a);
            syncMysqlToSqlite.execute();
        } catch (Exception e) {
            mostrarMensaje(a, "Eror SyncMysqlToSqlite DIC" + e.toString());
        }
    }

    public ArrayList<DestinoInmueble> extraerTodos(Activity a) {
        ArrayList<DestinoInmueble> destinosInmuebles = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM destino_inmueble", null);
        while (c.moveToNext()) {
            DestinoInmueble destinoInmueble = new DestinoInmueble();
            destinoInmueble.setId(c.getInt(0));
            destinoInmueble.setNombre(c.getString(1));
            destinosInmuebles.add(destinoInmueble);
        }
        c.close();
        db.close();
        return destinosInmuebles;
    }

}
