package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.desarrollo.kuky.presionaguasriojanas.controlador.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.controlador.VolleySingleton;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Barrio;
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

public class BarrioControlador {
    private ProgressDialog pDialog;

    public void sincronizarDeMysqlToSqlite(Activity a) {
        pDialog = new ProgressDialog(a);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setTitle("SINCRONIZANDO");
        pDialog.setMessage("4/" +
                +ASYNCTASK_INSPECCION + " - Recibiendo barrios...");
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_INSPECCION + "barrios_select.php", response -> {
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
                // Y AL FINAL ABRIMOS LA OTRA ACTIVITY
                RelevamientoMedidorControlador relevamientoMedidorControlador = new RelevamientoMedidorControlador();
                relevamientoMedidorControlador.sincronizarDeMysqlToSqlite(a);
            } else {
                mostrarMensaje(a, "Error en el checkBarrios.");
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
}
