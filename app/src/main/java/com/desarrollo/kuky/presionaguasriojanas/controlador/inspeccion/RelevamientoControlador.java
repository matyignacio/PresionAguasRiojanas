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
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
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

public class RelevamientoControlador {
    private ArrayList<Relevamiento> relevamientos = new ArrayList<>();
    private JSONArray relevamientosInserts;

    public void sincronizarDeSqliteToMysql(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> method) {
        relevamientosInserts = new JSONArray();
        relevamientos = extraerTodosPendientes(a);
        displayProgressBar(a, progressBar, tvProgressBar, "Enviando Relevamientos...");
        for (int i = 0; i < relevamientos.size(); i++) {
            int conexionVisible = relevamientos.get(i).isConexionVisible() ? 1 : 0;
            try {
                JSONObject relevamiento = new JSONObject();
                relevamiento.put("id", relevamientos.get(i).getId());
                relevamiento.put("id_usuario", relevamientos.get(i).getIdUsuario());
                relevamiento.put("barrio", relevamientos.get(i).getBarrio());
                relevamiento.put("tipo_inmueble", relevamientos.get(i).getTipoInmueble());
                relevamiento.put("rubro", relevamientos.get(i).getRubro());
                relevamiento.put("conexion_visible", conexionVisible);
                relevamiento.put("medidor_luz", relevamientos.get(i).getMedidorLuz());
                relevamiento.put("medidor_agua", relevamientos.get(i).getMedidorAgua());
                relevamiento.put("latitud", relevamientos.get(i).getLatitud());
                relevamiento.put("longitud", relevamientos.get(i).getLongitud());
                relevamiento.put("latitud_usuario", relevamientos.get(i).getLatitudUsuario());
                relevamiento.put("longitud_usuario", relevamientos.get(i).getLatitudUsuario());
                relevamiento.put("observaciones", relevamientos.get(i).getObservaciones());
                relevamiento.put("foto", relevamientos.get(i).getFoto());
                relevamiento.put("fecha", relevamientos.get(i).getFecha());
                relevamientosInserts.put(relevamiento);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, VOLLEY_HOST + MODULO_INSPECCION + "relevamiento_insert.php", relevamientosInserts, response -> {
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
                        mostrarMensajeLog(a, e.toString());
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
        displayProgressBar(a, progressBar, tvProgressBar, "Recibiendo relevamientos...");
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_INSPECCION + "relevamiento_select.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM relevamiento");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ContentValues values = new ContentValues();
                        values.put("id", jsonArray.getJSONObject(i).getInt("id"));
                        values.put("id_usuario", jsonArray.getJSONObject(i).getString("id_usuario"));
                        values.put("barrio", jsonArray.getJSONObject(i).getString("barrio"));
                        values.put("tipo_inmueble", jsonArray.getJSONObject(i).getString("tipo_inmueble"));
                        values.put("rubro", jsonArray.getJSONObject(i).getString("rubro"));
                        // atendeme ese IF ELSE :)
                        values.put("conexion_visible", jsonArray.getJSONObject(i).getInt("conexion_visible") == 1 ? 1 : 0);
                        values.put("medidor_luz", jsonArray.getJSONObject(i).getInt("medidor_luz"));
                        values.put("medidor_agua", jsonArray.getJSONObject(i).getInt("medidor_agua"));
                        values.put("latitud", jsonArray.getJSONObject(i).getDouble("latitud"));
                        values.put("longitud", jsonArray.getJSONObject(i).getDouble("longitud"));
                        values.put("latitud_usuario", jsonArray.getJSONObject(i).getDouble("latitud_usuario"));
                        values.put("longitud_usuario", jsonArray.getJSONObject(i).getDouble("longitud_usuario"));
                        values.put("observaciones", jsonArray.getJSONObject(i).getString("observaciones"));
                        /* Al 14 no lo traigo por que es la foto, no quiero poblar mucho
                         *  la bd del telefono. Pero si traigo los registros para que el
                         *  obtenerSiguienteId() ande bien xD  **************************/
                        values.put("fecha", String.valueOf(jsonArray.getJSONObject(i).getString("fecha")));
                        values.put("pendiente", 0);
                        db.insert("relevamiento", "foto", values);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mostrarMensaje(a, "Sin relevamientos.");
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

    private void actualizarPendiente(Relevamiento relevamiento, Activity a) {
        String[] whereArgs = {String.valueOf(relevamiento.getId()), relevamiento.getIdUsuario()};
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("pendiente", 0);
            db.update("relevamiento", values,
                    "id = ? AND id_usuario = ?",
                    whereArgs);
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente RC " + e.toString());
        }
    }

    private ArrayList<Relevamiento> extraerTodosPendientes(Activity a) {
        relevamientos = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM relevamiento " +
                "WHERE pendiente = 1 " +
                "ORDER BY id ASC", null);
        while (c.moveToNext()) {
            Relevamiento relevamiento = new Relevamiento();
            relevamiento.setId(c.getInt(0));
            relevamiento.setIdUsuario(c.getString(1));
            relevamiento.setBarrio(c.getString(2));
            relevamiento.setTipoInmueble(c.getString(3));
            relevamiento.setRubro(c.getString(4));
            if (c.getInt(5) == 0) {
                relevamiento.setConexionVisible(false);
            } else {
                relevamiento.setConexionVisible(true);
            }
            relevamiento.setMedidorLuz(c.getInt(6));
            relevamiento.setMedidorAgua(c.getInt(7));
            relevamiento.setLatitud(c.getDouble(8));
            relevamiento.setLongitud(c.getDouble(9));
            relevamiento.setLatitudUsuario(c.getDouble(10));
            relevamiento.setLongitudUsuario(c.getDouble(11));
            relevamiento.setObservaciones(c.getString(12));
            relevamiento.setFoto(c.getString(13));
            relevamiento.setFecha(Timestamp.valueOf(c.getString(14)));
            relevamientos.add(relevamiento);
        }
        c.close();
        db.close();
        return relevamientos;
    }

    public int insertar(Relevamiento relevamiento, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", relevamiento.getId());
            values.put("id_usuario", relevamiento.getIdUsuario());
            values.put("barrio", relevamiento.getBarrio());
            values.put("tipo_inmueble", relevamiento.getTipoInmueble());
            values.put("rubro", relevamiento.getRubro());
            // atendeme ese IF ELSE :)
            values.put("conexion_visible", relevamiento.isConexionVisible() ? 1 : 0);
            values.put("medidor_luz", relevamiento.getMedidorLuz());
            values.put("medidor_agua", relevamiento.getMedidorAgua());
            values.put("latitud", relevamiento.getLatitud());
            values.put("longitud", relevamiento.getLongitud());
            values.put("latitud_usuario", relevamiento.getLatitudUsuario());
            values.put("longitud_usuario", relevamiento.getLongitudUsuario());
            values.put("observaciones", relevamiento.getObservaciones());
            values.put("foto", relevamiento.getFoto());
            values.put("pendiente", INSERTAR_PUNTO);
            if (db.insert("relevamiento", null, values) > 0) {
                db.close();
                return EXITOSO;
            }
            db.close();
            return ERROR;
        } catch (Exception e) {
            mostrarMensaje(a, "Error insertar RC " + e.toString());
            return ERROR;
        }
    }

    public int obtenerSiguienteId(Activity a) {
        int id = 1;
        try {
            SQLiteDatabase db3 = BaseHelper.getInstance(a).getReadableDatabase();
            String sql = "SELECT id FROM relevamiento ORDER BY id DESC LIMIT 1";
            Cursor c3 = db3.rawQuery(sql, null);
            while (c3.moveToNext()) {
                id = c3.getInt(0) + 1;
            }
            return id;
        } catch (Exception e) {
            mostrarMensaje(a, "Error obtenerSiguienteId RC " + e.toString());
            return ERROR;
        }
    }
}
