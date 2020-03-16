package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.desarrollo.kuky.presionaguasriojanas.controlador.VolleySingleton;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Relevamiento;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.RelevamientoMedidor;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.INSERTAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_INSPECCION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class RelevamientoMedidorControlador {
    private ArrayList<RelevamientoMedidor> relevamientoMedidores;
    private ArrayList<RelevamientoMedidor> relevamientos = new ArrayList<>();
    private JSONArray relevamientosInserts;

    void sincronizarDeSqliteToMysql(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> method) {
        relevamientosInserts = new JSONArray();
        relevamientos = extraerTodosPendientes(a);
        displayProgressBar(a, progressBar, tvProgressBar, "Enviando Relevamiento Medidores...");
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
            lockProgressBar(a, progressBar, tvProgressBar);
            mostrarMensajeLog(a, response.toString());
            try {
                if (response.getJSONObject(0).getString("status").equals("OK")) {
                    Log.d("RESPUESTASERVER", "OK");
                    // SI SALE BIEN, BAJAMOS EL PENDIENTE AL PUNTO
                    for (int i = 0; i < relevamientos.size(); i++) {
                        actualizarPendiente(relevamientos.get(i), a);
                    }
                    // Y PASAMOS A LA SIGUIENTE REQUEST
                    try {
                        method.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("RESPUESTASERVER", "ERROR");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("RESPUESTASERVER", e.toString());
            }
        }, error -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            String problema = error.toString() + " en " + this.getClass().getSimpleName();
            setPreference(a, ERROR_PREFERENCE, problema);
            mostrarMensajeLog(a, problema);
            abrirActivity(a, ErrorActivity.class);
        });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    public void syncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> method) {
        displayProgressBar(a, progressBar, tvProgressBar, "Recibiendo Relevamiento Medidores...");
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_INSPECCION + "relevamiento_medidores_select.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
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
                mostrarMensajeLog(a, "Sin relevamientoMedidores.");
            }
            // POR MAS QUE DEVUELVA UN ARRAY VACIO, EJECUTA LA SIGUIENTE TAREA
            // (Porque puede que la tabla este vacia)
            try {
                method.call();
            } catch (Exception e) {
                mostrarMensajeLog(a, e.toString());
            }
        }, error -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            String problema = error.toString() + " en " + this.getClass().getSimpleName();
            setPreference(a, ERROR_PREFERENCE, problema);
            mostrarMensajeLog(a, problema);
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

    public ArrayList<RelevamientoMedidor> extraerTodosPendientes(Activity a, int idRelevamiento, String idUsuarioRelevamiento) {
        relevamientoMedidores = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM relevamiento_medidores " +
                " WHERE pendiente = 1 " +
                " AND id_relevamiento = " + idRelevamiento +
                " AND id_usuario_relevamiento = '" + idUsuarioRelevamiento + "'" +
                " ORDER BY id ASC", null);
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

    public int insertarArray(ArrayList<RelevamientoMedidor> relevamientoMedidores, int idRelevamiento, Activity a) {
        int retorno = EXITOSO;
        SQLiteDatabase dbDelete = BaseHelper.getInstance(a).getWritableDatabase();
        try {
            dbDelete.delete("relevamiento_medidores",
                    "id_relevamiento=" + idRelevamiento,
                    null);
        } catch (Exception eDelete) {
            mostrarMensaje(a, eDelete.toString());
        }
        dbDelete.close();
        for (int i = 0; i < relevamientoMedidores.size(); i++) {
            try {
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("id", relevamientoMedidores.get(i).getId());
                values.put("id_usuario", relevamientoMedidores.get(i).getIdUsuario());
                values.put("numero", relevamientoMedidores.get(i).getNumero());
                values.put("id_relevamiento", relevamientoMedidores.get(i).getRelevamiento().getId());
                values.put("id_usuario_relevamiento", relevamientoMedidores.get(i).getRelevamiento().getIdUsuario());
                values.put("pendiente", INSERTAR_PUNTO);
                db.insert("relevamiento_medidores", null, values);
                db.close();
            } catch (Exception e) {
                mostrarMensaje(a, "Error actualizar RMC " + e.toString());
                return ERROR;
            }
        }
        return retorno;
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
