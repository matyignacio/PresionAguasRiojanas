package com.desarrollo.kuky.presionaguasriojanas.controlador.presion;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.desarrollo.kuky.presionaguasriojanas.controlador.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.controlador.Conexion;
import com.desarrollo.kuky.presionaguasriojanas.controlador.VolleySingleton;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.Orden;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_ALTA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_PRESION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.checkConnection;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.progressBarVisibility;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class OrdenControlador {

    public void syncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        displayProgressBar(a, progressBar, tvProgressBar, "Obteniendo orden...");
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_PRESION + "orden_select.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            Log.d("response", response);
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM orden");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String sql = "INSERT INTO `orden` " +
                                " VALUES " +
                                "('" + jsonArray.getJSONObject(i).getInt("id") + "','" + // id
                                jsonArray.getJSONObject(i).getInt("id_pp_actual") + "','" + // id_pp_actual
                                jsonArray.getJSONObject(i).getString("id_usuario_pp_actual") + "','" + // id_usuario_pp_actual
                                jsonArray.getJSONObject(i).getInt("id_pp_siguiente") + "','" + // id_pp_siguiente
                                jsonArray.getJSONObject(i).getString("id_usuario_pp_siguiente") + "','" + // id_usuario_pp_siguiente
                                jsonArray.getJSONObject(i).getInt("activo") + "','" + // activo
                                jsonArray.getJSONObject(i).getInt("circuito") + "');"; // circuito
                        db.execSQL(sql);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                db.close();
                // Y AL FINAL EJECUTAMOS LA SIGUIENTE REQUEST
                PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
                puntoPresionControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar);
            } else {
                Toast.makeText(a, "No existe orden", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            setPreference(a, ERROR_PREFERENCE, error.toString());
            mostrarMensajeLog(a, error.toString());
            abrirActivity(a, ErrorActivity.class);
        });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    private class SyncMysqlToSqlite extends AsyncTask<String, Integer, String> {

        Activity a;
        private Integer check;
        private ProgressBar progressBar;
        private TextView tvProgressBar;

        @Override
        protected void onPreExecute() {
            progressBarVisibility(progressBar, tvProgressBar, true);
        }

        SyncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
            this.a = a;
            check = ERROR;
            this.progressBar = progressBar;
            this.tvProgressBar = tvProgressBar;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn;
                PreparedStatement ps;
                ResultSet rs;
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                conn = Conexion.GetConnection();
                String consultaSql = "SELECT * FROM orden ";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM orden");
                while (rs.next()) {
                    String sql = "INSERT INTO `orden` " +
                            " VALUES " +
                            "('" + rs.getInt(1) + "','" + // id
                            rs.getInt(2) + "','" + // id_pp_actual
                            rs.getString(3) + "','" + // id_usuario_pp_actual
                            rs.getInt(4) + "','" + // id_pp_siguiente
                            rs.getString(5) + "','" + // id_usuario_pp_siguiente
                            rs.getInt(6) + "','" + // activo
                            rs.getInt(7) + "');"; // circuito
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
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBarVisibility(progressBar, tvProgressBar, false);
            if (s.equals("EXITO")) {
                //mostrarMensaje(a, "4/6 - Se copio el orden con exito");
                PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
                puntoPresionControlador.insertToMySQL(a, progressBar, tvProgressBar);
            } else {
                mostrarMensaje(a, "Error en el checkOrden");
            }
        }

    }

    void sincronizarDeMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        checkConnection(a, () -> {
            try {
                SyncMysqlToSqlite syncMysqlToSqlite = new SyncMysqlToSqlite(a, progressBar, tvProgressBar);
                syncMysqlToSqlite.execute();
            } catch (Exception e) {
                mostrarMensaje(a, "Error SyncMysqlToSqlite OC" + e.toString());
            }
            return null;
        });
    }

    void editarActivo(Activity a, int id_punto, String id_usuario, int bandera) {
        try {
            SQLiteDatabase bh = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE orden SET activo = " + bandera +
                    " WHERE id_pp_actual =  " + id_punto +
                    " AND id_usuario_pp_actual like '" + id_usuario + "'";
            bh.execSQL(sql);
            bh.close();
        } catch (Exception e) {
            Util.mostrarMensaje(a, e.toString());
        }
    }

    public Orden extraerActivo(Activity a, int circuito) {
        Orden orden = new Orden();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM orden WHERE activo = 1 AND circuito = " + circuito, null);
        while (c.moveToNext()) {
            orden.setId(c.getInt(0));
            /**
             * DEFINIMOS LOS OBJETOS QUE VAMOS A USAR
             */
            PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
            PuntoPresion ppActual;
            PuntoPresion ppSiguiente;
            /**
             * LOS VAMOS CARGANDO CON LOS DATOS DE LA BASE
             */
            ppActual = puntoPresionControlador.extraerPorIdYUsuario(a,
                    c.getInt(1),
                    c.getString(2));
            ppSiguiente = puntoPresionControlador.extraerPorIdYUsuario(a,
                    c.getInt(3),
                    c.getString(4));
            /**
             * LOS SETEAMOS
             */
            orden.setPpActual(ppActual);
            orden.setPpSiguiente(ppSiguiente);
            if (c.getInt(5) == BANDERA_ALTA) {
                orden.setActivo(true);
            } else {
                orden.setActivo(false);
            }
        }
        c.close();
        db.close();
        return orden;
    }

    Orden existePunto(Activity a, PuntoPresion puntoPresion) {
        Orden orden = new Orden();
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM orden " +
                    " WHERE id_pp_actual =  " + puntoPresion.getId() +
                    " AND id_usuario_pp_actual like '" + puntoPresion.getUsuario().getId() + "'", null);
            while (c.moveToNext()) {
                orden.setId(c.getInt(0));
                /**
                 * DEFINIMOS LOS OBJETOS QUE VAMOS A USAR
                 */
                PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
                PuntoPresion ppActual;
                PuntoPresion ppSiguiente;
                /**
                 * LOS VAMOS CARGANDO CON LOS DATOS DE LA BASE
                 */
                ppActual = puntoPresionControlador.extraerPorIdYUsuario(a,
                        c.getInt(1),
                        c.getString(2));
                ppSiguiente = puntoPresionControlador.extraerPorIdYUsuario(a,
                        c.getInt(3),
                        c.getString(4));
                /**
                 * LOS SETEAMOS
                 */
                orden.setPpActual(ppActual);
                orden.setPpSiguiente(ppSiguiente);
                if (c.getInt(5) == BANDERA_ALTA) {
                    orden.setActivo(true);
                } else {
                    orden.setActivo(false);
                }
            }
            c.close();
            db.close();
            return orden;
        } catch (Exception e) {
            orden.setId(0);
            Util.mostrarMensaje(a, e.toString());
            return orden;
        }
    }
}
