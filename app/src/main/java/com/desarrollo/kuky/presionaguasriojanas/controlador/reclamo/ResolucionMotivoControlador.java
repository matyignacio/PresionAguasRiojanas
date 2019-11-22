package com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.desarrollo.kuky.presionaguasriojanas.controlador.VolleySingleton;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;

import org.json.JSONArray;
import org.json.JSONException;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_RECLAMO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_DEFAULT_TIMEOUT;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class ResolucionMotivoControlador {

    public void syncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        displayProgressBar(a, progressBar, tvProgressBar, "Relacionando resolucion con motivos...");
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_RECLAMO + "resolucion_motivo_select.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            Log.d("response", response);
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM GTres_mot");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String sql = "INSERT INTO `GTres_mot` " +
                                " VALUES " +
                                "('" + jsonArray.getJSONObject(i).getString("resolucion") + "','" +
                                jsonArray.getJSONObject(i).getString("motivo") + "');";
                        db.execSQL(sql);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                db.close();
                // Y AL FINAL EJECUTAMOS LA SIGUIENTE REQUEST
                ReclamoControlador reclamoControlador = new ReclamoControlador();
                reclamoControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar);
            } else {
                Toast.makeText(a, "No existen resolucion de motivos", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            setPreference(a, ERROR_PREFERENCE, error.toString());
            mostrarMensajeLog(a, error.toString());
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
}
