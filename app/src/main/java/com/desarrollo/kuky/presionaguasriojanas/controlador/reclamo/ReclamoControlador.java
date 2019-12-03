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
import java.util.concurrent.Callable;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
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

public class ReclamoControlador {

    public void syncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> method) {
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
                        ContentValues values = new ContentValues();
                        values.put("tpo_tram", jsonArray.getJSONObject(i).getString("tpo_tram"));
                        values.put("num_tram", jsonArray.getJSONObject(i).getString("num_tram"));
                        values.put("unidad_sol", jsonArray.getJSONObject(i).getString("unidad_sol"));
                        values.put("razon_sol", jsonArray.getJSONObject(i).getString("razon_sol"));
                        values.put("calle", jsonArray.getJSONObject(i).getString("calle"));
                        values.put("numero", jsonArray.getJSONObject(i).getString("num_casa"));
                        values.put("dat_complem", jsonArray.getJSONObject(i).getString("dat_complem"));
                        values.put("cod_barrio", jsonArray.getJSONObject(i).getString("cod_barrio"));
                        values.put("descripcion", jsonArray.getJSONObject(i).getString("descripcion"));
                        values.put("ubicacion", jsonArray.getJSONObject(i).getString("ubicacion"));
                        db.insertOrThrow("GTreclamo", null, values);
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
                mostrarMensaje(a, "No existen reclamos");
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

    public Reclamo extraer(Activity a, String tipoTramite, int numTramite) {
        Reclamo reclamo = new Reclamo();
        BarrioControlador barrioControlador = new BarrioControlador();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT unidad_sol,razon_sol,calle,numero,dat_complem,cod_barrio,descripcion,ubicacion " +
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
            reclamo.setUbicacion(c.getString(7));
        }
        c.close();
        db.close();
        return reclamo;
    }

    public int actualizarUbicacion(Reclamo reclamo, Activity a) {
        // A la nueva ubicacion la sincronizo con MySQL en el metodo updateToMySQL de TramiteControlador
        int retorno = ERROR;
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE GTreclamo" +
                    " SET ubicacion = '" + reclamo.getUbicacion() + "'" +
                    " WHERE tpo_tram='" + reclamo.getTipoTramite().getTipo() + "'" +
                    " AND num_tram =" + reclamo.getNumeroTramite();
            db.execSQL(sql);
            db.close();
            mostrarMensaje(a, "Se actualizo la ubicacion del reclamo");
            retorno = EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarUbicacion RC " + e.toString());
        }
        return retorno;
    }
}
