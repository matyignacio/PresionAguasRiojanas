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
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.TipoResolucion;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
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

public class TipoResolucionControlador {

    public void syncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> method) {
        displayProgressBar(a, progressBar, tvProgressBar, "Obteniendo tipos de resolucion...");
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_RECLAMO + "tipo_resolucion_select.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            Log.d("response", response);
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM GTresolucion");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String sql = "INSERT INTO `GTresolucion` " +
                                " VALUES " +
                                "('" + jsonArray.getJSONObject(i).getString("resolucion") + "','" +
                                jsonArray.getJSONObject(i).getString("descripcion") + "');";
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
                mostrarMensaje(a, "No existen resoluciones de tramite");
            }
        }, error -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            String problema = error.toString() + " en " + this.getClass().getSimpleName();
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

    public TipoResolucion.TipoResolucionSpinner extraerPorMotivo(Activity a, String motivo) {
        TipoResolucion.TipoResolucionSpinner tipoResolucionSpinner = new TipoResolucion().new TipoResolucionSpinner();
        ArrayList<TipoResolucion> resoluciones = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT r.resolucion, r.descripcion FROM GTresolucion r, GTres_mot rm" +
                "        WHERE r.resolucion = rm.resolucion" +
                "        AND rm.motivo ='" + motivo + "'", null);
        while (c.moveToNext()) {
            TipoResolucion tipoResolucion = new TipoResolucion();
            tipoResolucion.setResolucion(c.getString(0));
            tipoResolucion.setDescripcion(c.getString(1));
            resoluciones.add(tipoResolucion);
        }
        c.close();
        db.close();
        List<String> labelsResoluciones = new ArrayList<>();
        for (int i = 0; i < resoluciones.size(); i++) {
            labelsResoluciones.add(resoluciones.get(i).getDescripcion());
        }
        tipoResolucionSpinner.setResoluciones(resoluciones);
        tipoResolucionSpinner.setLabelsResoluciones(labelsResoluciones);
        return tipoResolucionSpinner;
    }
}
