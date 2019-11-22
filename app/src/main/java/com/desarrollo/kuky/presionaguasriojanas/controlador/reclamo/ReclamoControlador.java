package com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.desarrollo.kuky.presionaguasriojanas.controlador.VolleySingleton;
import com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion.BarrioControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.Reclamo;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.TipoTramite;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_RECLAMO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_DEFAULT_TIMEOUT;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class ReclamoControlador {

    public void syncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        displayProgressBar(a, progressBar, tvProgressBar, "Obteniendo reclamos...");
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_RECLAMO + "reclamo_tramite_select.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            Log.d("response", response);
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM GTreclamo");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String sql = "INSERT INTO `GTreclamo` " +
                                " VALUES " +
                                "('" + jsonArray.getJSONObject(i).getString("tpo_tram") + "','" +
                                jsonArray.getJSONObject(i).getString("num_tram") + "','" +
                                jsonArray.getJSONObject(i).getString("unidad_sol") + "','" +
                                jsonArray.getJSONObject(i).getString("razon_sol") + "','" +
                                jsonArray.getJSONObject(i).getString("cod_barrio") + "','" +
                                jsonArray.getJSONObject(i).getString("calle") + "','" +
                                jsonArray.getJSONObject(i).getString("num_casa") + "','" +
                                jsonArray.getJSONObject(i).getString("dat_complem") + "','" +
                                jsonArray.getJSONObject(i).getString("descripcion") + "');";
                        db.execSQL(sql);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                db.close();
                // Y AL FINAL EJECUTAMOS LA SIGUIENTE REQUEST
                TramiteControlador tramiteControlador = new TramiteControlador();
                tramiteControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar);
            } else {
                Toast.makeText(a, "No existen reclamos", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            setPreference(a, ERROR_PREFERENCE, error.toString());
            mostrarMensajeLog(a, error.toString());
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

    public Reclamo extraer(Activity a, String tipoTramite, int numTramite) {
        Reclamo reclamo = new Reclamo();
        BarrioControlador barrioControlador = new BarrioControlador();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT unidad_sol,razon_sol,calle,numero,dat_complem,cod_barrio,descripcion " +
                "FROM GTreclamo " +
                "WHERE tpo_tram like '" + tipoTramite + "' " +
                "AND num_tram = " + numTramite, null);
        while (c.moveToNext()) {
            reclamo.setNumeroTramite(numTramite);
            reclamo.setTipoTramite(new TipoTramite(tipoTramite));
            reclamo.setUnidad(c.getInt(0));
            reclamo.setRazonSocial(c.getString(1));
            reclamo.setCalle(c.getString(2));
            reclamo.setNumeroCasa(c.getInt(3));
            reclamo.setDatoComplementario(c.getString(4));
            reclamo.setBarrio(barrioControlador.extraer(a, c.getString(5)));
            reclamo.setDescripcion(c.getString(6));
        }
        c.close();
        db.close();
        return reclamo;
    }
}
