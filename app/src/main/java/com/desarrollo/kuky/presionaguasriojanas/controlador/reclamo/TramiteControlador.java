package com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.desarrollo.kuky.presionaguasriojanas.controlador.VolleySingleton;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.Motivo;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.Reclamo;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.TipoTramite;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.Tramite;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.INSERTAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_RECLAMO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_DEFAULT_TIMEOUT;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.TIPO_TRAMITE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class TramiteControlador {
    private static final String TAG = TramiteControlador.class.getSimpleName();
    private ArrayList<Tramite> tramites;
    private JSONArray tramitesUpdate;

    public void updateToMySQL(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> method) {
        tramitesUpdate = new JSONArray();
        displayProgressBar(a, progressBar, tvProgressBar, "Actualizando tramites...");
        tramites = extraerTodosPendientes(a);
        for (int i = 0; i < tramites.size(); i++) {
            try {
                JSONObject tramite = new JSONObject();
                tramite.put("tpo_tram", tramites.get(i).getTipoTramite().getTipo());
                tramite.put("num_tram", tramites.get(i).getReclamo().getNumeroTramite());
                tramite.put("ubicacion", tramites.get(i).getReclamo().getUbicacion());
                tramite.put("unidad", tramites.get(i).getReclamo().getUnidad());
                tramitesUpdate.put(tramite);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, VOLLEY_HOST + MODULO_RECLAMO + "tramite_update.php", tramitesUpdate, response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            mostrarMensajeLog(a, response.toString());
            try {
                if (response.getJSONObject(0).getString("status").equals("OK")) {
                    Log.d("RESPUESTASERVER", "OK " + TAG);
                    // SI SALE BIEN, BAJAMOS EL PENDIENTE AL PUNTO
                    for (int i = 0; i < tramites.size(); i++) {
                        actualizarPendiente(tramites.get(i), a);
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
            String problema = error.toString() + " en " + TAG;
            setPreference(a, ERROR_PREFERENCE, problema);
            mostrarMensajeLog(a, problema);
            abrirActivity(a, ErrorActivity.class);
        });
        // Establecer una política de reintentos en mi petición Volley mediante el método setRetryPolicy
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    public void syncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> method) {
        displayProgressBar(a, progressBar, tvProgressBar, "Obteniendo tramites...");
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_RECLAMO + "tramite_select.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            Log.d("response", response);
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM GTtramite");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ContentValues values = new ContentValues();
                        values.put("tpo_tram", jsonArray.getJSONObject(i).getString("tpo_tram"));
                        values.put("num_tram", jsonArray.getJSONObject(i).getString("num_tram"));
                        values.put("descripcion", jsonArray.getJSONObject(i).getString("descripcion"));
                        values.put("motivo", jsonArray.getJSONObject(i).getString("motivo"));
                        values.put("pendiente", 0);
                        db.insertOrThrow("GTtramite", null, values);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                db.close();
                // Y AL FINAL EJECUTAMOS LA SIGUIENTE REQUEST
                try {
                    method.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mostrarMensaje(a, "No existen tramites");
            }
        }, error -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            String problema = error.toString() + " en " + this.getClass().getSimpleName();
            setPreference(a, ERROR_PREFERENCE, problema);
            mostrarMensajeLog(a, problema);
            abrirActivity(a, ErrorActivity.class);
        }) {
            //Pass Your Parameters here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tpo_tram", TIPO_TRAMITE);
                return params;
            }
        };
        // Establecer una política de reintentos en mi petición Volley mediante el método setRetryPolicy
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    public ArrayList<Tramite> extraerTodos(Activity a) {
        ArrayList<Tramite> tramites = new ArrayList<>();
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
            Cursor c = db.rawQuery("SELECT t.tpo_tram, t.num_tram, r.descripcion, t.motivo, r.razon_sol, m.descripcion" +
                    "                            FROM GTtramite t, GTreclamo r, GTmot_req m" +
                    "                            WHERE t.tpo_tram = r.tpo_tram" +
                    "                            AND t.num_tram = r.num_tram" +
                    "                            AND t.motivo = m.motivo ", null);
            while (c.moveToNext()) {
                Tramite tramite = new Tramite();
                Motivo motivo = new Motivo(c.getString(3));
                motivo.setDescripcion(c.getString(5));
                Reclamo reclamo = new Reclamo(new TipoTramite(c.getString(0)), c.getInt(1));
                reclamo.setRazonSocial(c.getString(4));
                tramite.setTipoTramite(new TipoTramite(c.getString(0)));
                tramite.setReclamo(reclamo);
                tramite.setDescripcion(c.getString(2));
                tramite.setMotivo(motivo);
                tramites.add(tramite);
            }
            c.close();
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, e.toString());
        }
        return tramites;
    }

    public ArrayList<Tramite> extraerTodosPendientes(Activity a) {
        ArrayList<Tramite> tramites = new ArrayList<>();
        ReclamoControlador reclamoControlador = new ReclamoControlador();
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
            Cursor c = db.rawQuery("SELECT t.tpo_tram, t.num_tram, r.descripcion, t.motivo, r.razon_sol, m.descripcion" +
                    "                            FROM GTtramite t, GTreclamo r, GTmot_req m" +
                    "                            WHERE t.tpo_tram = r.tpo_tram" +
                    "                            AND t.num_tram = r.num_tram" +
                    "                            AND t.motivo = m.motivo " +
                    "                            AND t.pendiente = " + INSERTAR_PUNTO, null);
            while (c.moveToNext()) {
                Tramite tramite = new Tramite();
                Motivo motivo = new Motivo(c.getString(3));
                motivo.setDescripcion(c.getString(5));
                Reclamo reclamo = reclamoControlador.extraer(a, c.getString(0), c.getInt(1));
//                reclamo.setRazonSocial(c.getString(4));
                tramite.setTipoTramite(new TipoTramite(c.getString(0)));
                tramite.setReclamo(reclamo);
                tramite.setDescripcion(c.getString(2));
                tramite.setMotivo(motivo);
                tramites.add(tramite);
            }
            c.close();
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, e.toString());
        }
        return tramites;
    }

    public int actualizarEstado(Tramite tramite, Activity a) {
        int retorno = ERROR;
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE GTtramite" +
                    " SET pendiente = " + INSERTAR_PUNTO +
                    " WHERE tpo_tram='" + tramite.getTipoTramite().getTipo() + "'" +
                    " AND num_tram =" + tramite.getReclamo().getNumeroTramite();
            db.execSQL(sql);
            db.close();
            mostrarMensaje(a, "Se actualizo el estado del tramite a primer cierre");
            retorno = EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarEstado TC " + e.toString());
        }
        return retorno;
    }

    private void actualizarPendiente(Tramite tramite, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE GTtramite" +
                    " SET pendiente = 0" +
                    " WHERE tpo_tram='" + tramite.getTipoTramite().getTipo() + "'" +
                    " AND num_tram =" + tramite.getReclamo().getNumeroTramite();
            db.execSQL(sql);
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente TC " + e.toString());
        }
    }
}
