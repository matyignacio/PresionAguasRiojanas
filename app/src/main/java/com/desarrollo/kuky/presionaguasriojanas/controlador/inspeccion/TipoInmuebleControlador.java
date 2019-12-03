package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.desarrollo.kuky.presionaguasriojanas.controlador.VolleySingleton;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.TipoInmueble;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_INSPECCION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class TipoInmuebleControlador {

    public void syncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> method) {
        displayProgressBar(a, progressBar, tvProgressBar, "Recibiendo tipos de inmuebles...");
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_INSPECCION + "tipo_inmueble_select.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM tipo_inmueble");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String sql = "INSERT INTO `tipo_inmueble`" +
                                " VALUES" +
                                " ('" + jsonArray.getJSONObject(i).getInt("id") + "','" +
                                jsonArray.getJSONObject(i).getString("nombre") + "');";
                        db.execSQL(sql);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Y AL FINAL ABRIMOS LA OTRA ACTIVITY
                try {
                    method.call();
                } catch (Exception e) {
                    mostrarMensajeLog(a, e.toString());
                }
            } else {
                mostrarMensaje(a, "Error en el checkTipoInmueble.");
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

    public ArrayList<TipoInmueble> extraerTodos(Activity a) {
        ArrayList<TipoInmueble> tipoInmuebles = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM tipo_inmueble", null);
        while (c.moveToNext()) {
            TipoInmueble tipoInmueble = new TipoInmueble();
            tipoInmueble.setId(c.getInt(0));
            tipoInmueble.setNombre(c.getString(1));
            tipoInmuebles.add(tipoInmueble);
        }
        c.close();
        db.close();
        return tipoInmuebles;
    }

}
