package com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.desarrollo.kuky.presionaguasriojanas.controlador.UsuarioControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.VolleySingleton;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.ResolucionReclamo;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.Tramite;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_ALTA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.DATE_TIME;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.HOUR_TIME;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.INSERTAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_RECLAMO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_DEFAULT_TIMEOUT;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class ResolucionReclamoControlador {
    private ArrayList<ResolucionReclamo> resoluciones;
    private TramiteControlador tramiteControlador = new TramiteControlador();

    @SuppressLint("SimpleDateFormat")
    public void insertToMySQL(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> method) {
        JSONArray resolucionesInserts = new JSONArray();
        SimpleDateFormat formatDate = new SimpleDateFormat(DATE_TIME);
        displayProgressBar(a, progressBar, tvProgressBar, "Enviando resoluciones...");
        resoluciones = extraerTodosPendientes(a);
        for (int i = 0; i < resoluciones.size(); i++) {
            try {
                JSONObject resolucion = new JSONObject();
                String fechaDesde = formatDate.format(resoluciones.get(i).getFechaDesde());
                String fechaHasta = formatDate.format(resoluciones.get(i).getFechaDesde());
                resolucion.put("tpo_tram", resoluciones.get(i).getTipoTramite());
                resolucion.put("num_tram", resoluciones.get(i).getNumeroTramite());
                resolucion.put("cod_res", resoluciones.get(i).getCodigoResolucion());
                resolucion.put("obs", resoluciones.get(i).getObservaciones());
                resolucion.put("usuario", resoluciones.get(i).getUsuario());
                resolucion.put("fecha_d", fechaDesde);
                resolucion.put("hora_d", resoluciones.get(i).getHoraDesde());
                resolucion.put("fecha_h", fechaHasta);
                resolucion.put("hora_h", resoluciones.get(i).getHoraHasta());
                resolucionesInserts.put(resolucion);
            } catch (JSONException e) {
                mostrarMensaje(a, e.toString());
            }
        }
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, VOLLEY_HOST + MODULO_RECLAMO + "resolucion_reclamo_insert.php", resolucionesInserts, response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            mostrarMensajeLog(a, response.toString());
            try {
                if (response.getJSONObject(0).getString("status").equals("OK")) {
                    Log.d("RESPUESTASERVER", "OK");
                    // SI SALE BIEN, BAJAMOS EL PENDIENTE AL PUNTO
                    for (int i = 0; i < resoluciones.size(); i++) {
                        actualizarPendiente(resoluciones.get(i), a);
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

    public void syncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> methodAcept) {
        JSONArray tramitesInserts = new JSONArray();
        displayProgressBar(a, progressBar, tvProgressBar, "Obteniendo resoluciones...");
        ArrayList<Tramite> tramitesInsertar = tramiteControlador.extraerTodos(a);
        for (int i = 0; i < tramitesInsertar.size(); i++) {
            try {
                JSONObject punto = new JSONObject();
                punto.put("tpo_tram", tramitesInsertar.get(i).getTipoTramite().getTipo());
                punto.put("num_tram", tramitesInsertar.get(i).getReclamo().getNumeroTramite());
                tramitesInserts.put(punto);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, VOLLEY_HOST + MODULO_RECLAMO + "resolucion_reclamo_select.php", tramitesInserts, response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            mostrarMensajeLog(a, response.toString());
            try {
                if (!response.getJSONObject(0).getString("tpo_tram").equals("ERROR")) {
                    SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                    /* LIMPIAMOS LA TABLA */
                    db.execSQL("DELETE FROM GTres_rec");
                    for (int i = 0; i < response.length(); i++) {
                        String sql = "INSERT INTO `GTres_rec` " +
                                " VALUES " +
                                "('" + response.getJSONObject(i).getString("tpo_tram") + "','" +
                                response.getJSONObject(i).getString("num_tram") + "','" +
                                response.getJSONObject(i).getString("cod_res") + "','" +
                                response.getJSONObject(i).getString("obs") + "','" +
                                response.getJSONObject(i).getString("usuario") + "','" +
                                response.getJSONObject(i).getString("fecha_d") + "','" +
                                response.getJSONObject(i).getString("hora_d") + "','" +
                                response.getJSONObject(i).getString("fecha_h") + "','" +
                                response.getJSONObject(i).getString("hora_h") + "'," +
                                "0);"; //EN CERO AL PENDIENTE
                        db.execSQL(sql);
                    }
                    db.close();
                } else {
                    mostrarMensajeLog(a, "No existen resoluciones de tramites");
                }
                // Y AL FINAL EJECUTAMOS LA SIGUIENTE REQUEST
                try {
                    methodAcept.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("RESPUESTASERVER", e.toString());
            }
        }, error ->
        {
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

    @SuppressLint("SimpleDateFormat")
    public int insertar(Activity a, ResolucionReclamo resolucionReclamo) {
        int retorno = ERROR;
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat formatDate = new SimpleDateFormat(DATE_TIME);
            SimpleDateFormat formatHour = new SimpleDateFormat(HOUR_TIME);
            String date = formatDate.format(currentTime);
            String hour = formatHour.format(currentTime);
            String sql = "INSERT INTO GTres_rec " +
                    " (tpo_tram,num_tram,cod_res,obs,usuario,fecha_d,hora_d," +
                    " fecha_h,hora_h) VALUES ('" +
                    resolucionReclamo.getTipoTramite() + "', " +
                    resolucionReclamo.getNumeroTramite() + "," +
                    resolucionReclamo.getCodigoResolucion() + ",'" +
                    resolucionReclamo.getObservaciones() + "','" +
                    resolucionReclamo.getUsuario() + "','" +
                    date + "','" +
                    hour + "','" +
                    date + "','" +
                    hour + "')";
            db.execSQL(sql);
            /** SUBIMOS LA BANDERA DE SYNC MODULO RECLAMO **/
            UsuarioControlador usuarioControlador = new UsuarioControlador();
            usuarioControlador.editarBanderaSyncModuloReclamo(a, BANDERA_ALTA);
            /** CERRAMOS LAS CONEXIONES **/
            db.close();
            mostrarMensaje(a, "La resolucion se guardo correctamente");
            retorno = EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Error insertar RRC " + e.toString());
        }
        return retorno;
    }

    @SuppressLint("SimpleDateFormat")
    public ArrayList<ResolucionReclamo> extraerTodosPorTramite(Activity a, Tramite tramite) {
        TipoResolucionControlador tipoResolucionControlador = new TipoResolucionControlador();
        ArrayList<ResolucionReclamo> resolucionReclamos = new ArrayList<>();
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_TIME);
            SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
            Cursor c = db.rawQuery("SELECT tpo_tram,num_tram,cod_res,obs,usuario,fecha_d,hora_d,fecha_h,hora_h " +
                    "                            FROM GTres_rec" +
                    "                            WHERE tpo_tram = '" + tramite.getTipoTramite().getTipo() + "'" +
                    "                            AND num_tram = " + tramite.getReclamo().getNumeroTramite(), null);
            while (c.moveToNext()) {
                ResolucionReclamo resolucionReclamo = new ResolucionReclamo();
                Date fechaDesde = format.parse(c.getString(5));
                Date fechaHasta = format.parse(c.getString(7));
                resolucionReclamo.setTipoTramite(c.getString(0));
                resolucionReclamo.setNumeroTramite(c.getInt(1));
                resolucionReclamo.setCodigoResolucion(c.getString(2));
                resolucionReclamo.setDescripcionResolucion(tipoResolucionControlador.extraer(a, c.getString(2)));
                resolucionReclamo.setObservaciones(c.getString(3));
                resolucionReclamo.setUsuario(c.getString(4));
                resolucionReclamo.setFechaDesde(fechaDesde);
                resolucionReclamo.setHoraDesde(c.getString(6));
                resolucionReclamo.setFechaHasta(fechaHasta);
                resolucionReclamo.setHoraHasta(c.getString(8));
                resolucionReclamos.add(resolucionReclamo);
            }
            c.close();
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, e.toString());
        }
        return resolucionReclamos;
    }

    private ArrayList<ResolucionReclamo> extraerTodosPendientes(Activity a) {
        ArrayList<ResolucionReclamo> resolucionReclamos = new ArrayList<>();
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_TIME);
            SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
            Cursor c = db.rawQuery("SELECT tpo_tram,num_tram,cod_res,obs,usuario,fecha_d,hora_d,fecha_h,hora_h" +
                    " FROM GTres_rec " +
                    " WHERE pendiente = " + INSERTAR_PUNTO, null);
            while (c.moveToNext()) {
                ResolucionReclamo resolucionReclamo = new ResolucionReclamo();
                Date fechaDesde = format.parse(c.getString(5));
                Date fechaHasta = format.parse(c.getString(7));
                resolucionReclamo.setTipoTramite(c.getString(0));
                resolucionReclamo.setNumeroTramite(c.getInt(1));
                resolucionReclamo.setCodigoResolucion(c.getString(2));
                resolucionReclamo.setObservaciones(c.getString(3));
                resolucionReclamo.setUsuario(c.getString(4));
                resolucionReclamo.setFechaDesde(fechaDesde);
                resolucionReclamo.setHoraDesde(c.getString(6));
                resolucionReclamo.setFechaHasta(fechaHasta);
                resolucionReclamo.setHoraHasta(c.getString(8));
                resolucionReclamos.add(resolucionReclamo);
            }
            c.close();
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, e.toString());
        }
        return resolucionReclamos;
    }

    private void actualizarPendiente(ResolucionReclamo resolucionReclamo, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE GTres_rec" +
                    " SET pendiente = 0" +
                    " WHERE tpo_tram='" + resolucionReclamo.getTipoTramite() + "'" +
                    " AND num_tram =" + resolucionReclamo.getNumeroTramite();
            db.execSQL(sql);
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente RRC " + e.toString());
        }
    }
}
