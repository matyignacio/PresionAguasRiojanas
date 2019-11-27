package com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_RECLAMO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_DEFAULT_TIMEOUT;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class TramiteControlador {
    private ArrayList<Tramite> tramites;

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
                        String sql = "INSERT INTO `GTtramite` " +
                                " VALUES " +
                                "('" + jsonArray.getJSONObject(i).getString("tpo_tram") + "','" +
                                jsonArray.getJSONObject(i).getString("num_tram") + "','" +
                                jsonArray.getJSONObject(i).getString("descripcion") + "','" +
                                jsonArray.getJSONObject(i).getString("motivo") + "'," +
                                "0);"; //EN CERO AL PENDIENTE
                        db.execSQL(sql);
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
                params.put("tpo_tram", "002");
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
}
