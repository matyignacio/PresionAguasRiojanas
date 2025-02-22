package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

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
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Barrio;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_INSPECCION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_DEFAULT_TIMEOUT;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class BarrioControlador {

    public void syncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> method) {
        displayProgressBar(a, progressBar, tvProgressBar, "Obteniendo barrios...");
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_INSPECCION + "barrios_select.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            Log.d("response", response);
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM barrios");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String sql = "INSERT INTO barrios" +
                                " VALUES" +
                                " ('" + jsonArray.getJSONObject(i).getString("codigo") + "','" + // codigo
                                jsonArray.getJSONObject(i).getString("des_codigo") + "','" + // des_codigo
                                jsonArray.getJSONObject(i).getString("zona") + "');"; // zonaBarrios
                        db.execSQL(sql);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Y AL FINAL EJECUTAMOS LA SIGUIENTE REQUEST
                try {
                    method.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mostrarMensaje(a, "Error en el checkBarrios.");
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

    public ArrayList<Barrio> extraerTodosPorLocalidad(Activity a, String zona) {
        ArrayList<Barrio> barrios = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT codigo, des_codigo " +
                " FROM barrios " +
                " WHERE zona='" + zona + "'" +
                " ORDER BY des_codigo", null);
        while (c.moveToNext()) {
            Barrio barrio = new Barrio();
            barrio.setCodigo(c.getString(0));
            barrio.setDesCodigo(c.getString(1));
            barrios.add(barrio);
        }
        c.close();
        db.close();
        return barrios;
    }

    public Barrio extraer(Activity a, String codigo) {
        Barrio barrio = new Barrio();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT des_codigo " +
                " FROM barrios " +
                " WHERE codigo='" + codigo + "'", null);
        while (c.moveToNext()) {
            barrio.setCodigo(codigo);
            barrio.setDesCodigo(c.getString(0));
        }
        c.close();
        db.close();
        return barrio;
    }
}
