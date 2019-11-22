package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.desarrollo.kuky.presionaguasriojanas.controlador.VolleySingleton;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.TipoServicio;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ASYNCTASK_INSPECCION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_INSPECCION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class TipoServicioControlador {
    private ProgressDialog pDialog;

    public void sincronizarDeMysqlToSqlite(Activity a) {
        pDialog = new ProgressDialog(a);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setTitle("SINCRONIZANDO");
        pDialog.setMessage("8/" +
                +ASYNCTASK_INSPECCION + " - Recibiendo tipos de servicios...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_INSPECCION + "tipo_servicio_select.php", response -> {
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM tipo_servicio");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String sql = "INSERT INTO tipo_servicio" +
                                " VALUES" +
                                " ('" + jsonArray.getJSONObject(i).getString("id") + "','" +
                                jsonArray.getJSONObject(i).getString("nombre") + "');";
                        db.execSQL(sql);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Y AL FINAL ABRIMOS LA OTRA ACTIVITY
                InspeccionControlador inspeccionControlador = new InspeccionControlador();
                inspeccionControlador.sincronizarDeMysqlToSqlite(a);
            } else {
                mostrarMensaje(a, "Error en el checkTipoServicio.");
            }
            pDialog.dismiss();
        }, error -> {
            pDialog.dismiss();
            setPreference(a, ERROR_PREFERENCE, error.toString());
            mostrarMensajeLog(a, error.toString());
            abrirActivity(a, ErrorActivity.class);
        });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    public ArrayList<TipoServicio> extraerTodos(Activity a) {
        ArrayList<TipoServicio> tipoServicios = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM tipo_servicio", null);
        while (c.moveToNext()) {
            TipoServicio tipoServicio = new TipoServicio();
            tipoServicio.setId(c.getInt(0));
            tipoServicio.setNombre(c.getString(1));
            tipoServicios.add(tipoServicio);
        }
        c.close();
        db.close();
        return tipoServicios;
    }


}
