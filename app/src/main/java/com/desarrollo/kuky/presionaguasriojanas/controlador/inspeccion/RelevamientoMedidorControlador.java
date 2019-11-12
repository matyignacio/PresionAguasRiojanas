package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.desarrollo.kuky.presionaguasriojanas.controlador.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.controlador.Conexion;
import com.desarrollo.kuky.presionaguasriojanas.controlador.VolleySingleton;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Relevamiento;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.RelevamientoMedidor;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ASYNCTASK_INSPECCION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.INSERTAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_INSPECCION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class RelevamientoMedidorControlador {
    private ProgressDialog pDialog;
    private ArrayList<RelevamientoMedidor> relevamientoMedidores;
    private ArrayList<RelevamientoMedidor> relevamientos = new ArrayList<>();
    private JSONArray relevamientosInserts;

    void sincronizarDeSqliteToMysql(Activity a) {
        relevamientosInserts = new JSONArray();
        relevamientos = extraerTodosPendientes(a);
        pDialog = new ProgressDialog(a);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setTitle("SINCRONIZANDO");
        pDialog.setMessage("2/" +
                +ASYNCTASK_INSPECCION + " - Enviando Relevamiento Medidores...");
        pDialog.setCancelable(false);
        pDialog.show();
        for (int i = 0; i < relevamientoMedidores.size(); i++) {
            try {
                JSONObject relevamiento = new JSONObject();
                relevamiento.put("id", relevamientos.get(i).getId());
                relevamiento.put("id_usuario", relevamientos.get(i).getIdUsuario());
                relevamiento.put("numero", relevamientos.get(i).getNumero());
                relevamiento.put("id_relevamiento", relevamientos.get(i).getRelevamiento().getId());
                relevamiento.put("id_usuario_relevamiento", relevamientos.get(i).getRelevamiento().getIdUsuario());
                relevamientosInserts.put(relevamiento);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, VOLLEY_HOST + MODULO_INSPECCION + "relevamiento_medidores_insert.php", relevamientosInserts, response -> {
            pDialog.dismiss();
            mostrarMensajeLog(a, response.toString());
            try {
                if (response.getJSONObject(0).getString("status").equals("OK")) {
                    Log.d("RESPUESTASERVER", "OK");
                    // SI SALE BIEN, BAJAMOS EL PENDIENTE AL PUNTO
                    for (int i = 0; i < relevamientos.size(); i++) {
                        actualizarPendiente(relevamientos.get(i), a);
                    }
                    // Y PASAMOS A LA SIGUIENTE REQUEST
                    TipoInmuebleControlador tipoInmuebleControlador = new TipoInmuebleControlador();
                    tipoInmuebleControlador.sincronizarDeMysqlToSqlite(a);
                } else {
                    Log.e("RESPUESTASERVER", "ERROR");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("RESPUESTASERVER", e.toString());
            }
        }, error -> {
            pDialog.dismiss();
            setPreference(a, ERROR_PREFERENCE, error.toString());
            mostrarMensajeLog(a, error.toString());
            abrirActivity(a, ErrorActivity.class);
        });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    @SuppressLint("StaticFieldLeak")
    private class SyncSqliteToMysql extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;
        private ArrayList<RelevamientoMedidor> relevamientoMedidores;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setTitle("SINCRONIZANDO");
            pDialog.setMessage("2/" +
                    +ASYNCTASK_INSPECCION + " - Enviando Relevamiento Medidores...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        SyncSqliteToMysql(Activity a) {
            this.a = a;
            check = ERROR;
            relevamientoMedidores = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... strings) {
            /**
             IMPLEMENTO TRANSACCIONES CON COMMIT Y ROLLBACK EN LAS TAREAS ASYNCRONAS
             DESDE EL TELEFONO HACIA EL SERVER
             */
            relevamientoMedidores = extraerTodosPendientes(a);
            Connection conn;
            conn = Conexion.GetConnection();
            try {
                conn.setAutoCommit(false);
                String consultaSql;
                for (int i = 0; i < relevamientoMedidores.size(); i++) {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                    PreparedStatement ps;
                    consultaSql = "INSERT INTO relevamiento_medidores" +
                            "(id," +
                            "id_usuario," +
                            "numero," +
                            "id_relevamiento," +
                            "id_usuario_relevamiento)" +
                            "VALUES " +
                            "('" + relevamientoMedidores.get(i).getId() + "', " +
                            "'" + relevamientoMedidores.get(i).getIdUsuario() + "', " +
                            "'" + relevamientoMedidores.get(i).getNumero() + "', " +
                            "'" + relevamientoMedidores.get(i).getRelevamiento().getId() + "', " +
                            "'" + relevamientoMedidores.get(i).getRelevamiento().getIdUsuario() + "')";
                    ps = conn.prepareStatement(consultaSql);
                    ps.execute();
                    conn.commit();
                    ps.close();
                    /*//////////////////////////////////////////////////////////////////////////////////
                                            BAJAMOS EL PENDIENTE DEL inspeccion
                    //////////////////////////////////////////////////////////////////////////////////*/
                    actualizarPendiente(relevamientoMedidores.get(i), a);
                    check++;
                }
                if (check == relevamientoMedidores.size()) {
                    return "EXITO";
                } else {
                    return "ERROR";
                }
            } catch (SQLException e) {
                try {
                    mostrarMensajeLog(a, e.toString());
                    Log.e("MOSTRARMENSAJE:::", "Transaction is being rolled back");
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
                return e.toString();
            } finally {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s.equals("EXITO")) {
                TipoInmuebleControlador tipoInmuebleControlador = new TipoInmuebleControlador();
                tipoInmuebleControlador.sincronizarDeMysqlToSqlite(a);
                // VACIAMOS LA TABLA ?????
                // SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                // db.delete("relevamiento", null, null);
            } else {
                mostrarMensaje(a, "Error en el checkRelevamientoMedidoresToMysql");
            }
        }
    }

//    void sincronizarDeSqliteToMysql(Activity a) {
//        try {
//            SyncSqliteToMysql syncSqliteToMysql = new SyncSqliteToMysql(a);
//            syncSqliteToMysql.execute();
//        } catch (Exception e) {
//            mostrarMensaje(a, "Error SyncSqliteToMysql RMC" + e.toString());
//        }
//    }

    public void sincronizarDeMysqlToSqlite(Activity a) {
        pDialog = new ProgressDialog(a);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setTitle("SINCRONIZANDO");
        pDialog.setMessage("5/" +
                +ASYNCTASK_INSPECCION + " - Recibiendo Relevamiento Medidores...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_INSPECCION + "relevamiento_medidores_select.php", response -> {
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM relevamiento_medidores");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ContentValues values = new ContentValues();
                        values.put("id", jsonArray.getJSONObject(i).getInt("id"));
                        values.put("id_usuario", jsonArray.getJSONObject(i).getString("id_usuario"));
                        values.put("numero", jsonArray.getJSONObject(i).getInt("numero"));
                        values.put("id_relevamiento", jsonArray.getJSONObject(i).getInt("id_relevamiento"));
                        values.put("id_usuario_relevamiento", jsonArray.getJSONObject(i).getString("id_usuario_relevamiento"));
                        values.put("pendiente", 0);
                        db.insert("relevamiento_medidores", null, values);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mostrarMensaje(a, "Sin relevamientoMedidores.");
            }
            // POR MAS QUE DEVUELVA UN ARRAY VACIO, EJECUTA LA SIGUIENTE TAREA
            // (Porque puede que la tabla este vacia)
            RelevamientoControlador relevamientoControlador = new RelevamientoControlador();
            relevamientoControlador.sincronizarDeMysqlToSqlite(a);
            pDialog.dismiss();
        }, error -> {
            pDialog.dismiss();
            setPreference(a, ERROR_PREFERENCE, error.toString());
            mostrarMensajeLog(a, error.toString());
            abrirActivity(a, ErrorActivity.class);
        });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    private void actualizarPendiente(RelevamientoMedidor relevamientoMedidor, Activity a) {
        String[] whereArgs = {String.valueOf(relevamientoMedidor.getId()), relevamientoMedidor.getIdUsuario()};
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("pendiente", 0);
            db.update("relevamiento_medidores", values,
                    "id = ? AND id_usuario = ?",
                    whereArgs);
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente RMC " + e.toString());
        }
    }

    private ArrayList<RelevamientoMedidor> extraerTodosPendientes(Activity a) {
        relevamientoMedidores = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM relevamiento_medidores " +
                "WHERE pendiente = 1 " +
                "ORDER BY id ASC", null);
        while (c.moveToNext()) {
            RelevamientoMedidor relevamientoMedidor = new RelevamientoMedidor();
            Relevamiento relevamiento = new Relevamiento();
            relevamientoMedidor.setId(c.getInt(0));
            relevamientoMedidor.setIdUsuario(c.getString(1));
            relevamientoMedidor.setNumero(c.getInt(2));
            relevamiento.setId(c.getInt(3));
            relevamiento.setIdUsuario(c.getString(4));
            relevamientoMedidor.setRelevamiento(relevamiento);
            relevamientoMedidores.add(relevamientoMedidor);
        }
        c.close();
        db.close();
        return relevamientoMedidores;
    }

    public int insertar(RelevamientoMedidor relevamiento, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", relevamiento.getId());
            values.put("id_usuario", relevamiento.getIdUsuario());
            values.put("numero", relevamiento.getNumero());
            values.put("id_relevamiento", relevamiento.getRelevamiento().getId());
            values.put("id_usuario_relevamiento", relevamiento.getRelevamiento().getIdUsuario());
            values.put("pendiente", INSERTAR_PUNTO);
            if (db.insert("relevamiento_medidores", null, values) > 0) {
                db.close();
                return EXITOSO;
            }
            db.close();
            return ERROR;
        } catch (Exception e) {
            mostrarMensaje(a, "Error insertar RMC " + e.toString());
            return ERROR;
        }
    }

    public int obtenerSiguienteId(Activity a) {
        int id = 1;
        try {
            SQLiteDatabase db3 = BaseHelper.getInstance(a).getReadableDatabase();
            String sql = "SELECT id FROM relevamiento_medidores ORDER BY id DESC LIMIT 1";
            Cursor c3 = db3.rawQuery(sql, null);
            while (c3.moveToNext()) {
                id = c3.getInt(0) + 1;
            }
            return id;
        } catch (Exception e) {
            mostrarMensaje(a, "Error obtenerSiguienteId RMC " + e.toString());
            return ERROR;
        }
    }
}
