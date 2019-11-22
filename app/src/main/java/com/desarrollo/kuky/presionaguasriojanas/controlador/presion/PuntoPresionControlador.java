package com.desarrollo.kuky.presionaguasriojanas.controlador.presion;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.desarrollo.kuky.presionaguasriojanas.controlador.UsuarioControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.VolleySingleton;
import com.desarrollo.kuky.presionaguasriojanas.objeto.Usuario;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.TipoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.TipoPunto;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ACTUALIZAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_ALTA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.INSERTAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MAPA_CLIENTES;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_PRESION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class PuntoPresionControlador {
    private ArrayList<PuntoPresion> puntosPresion;
    private JSONArray puntosInserts;
    private JSONArray puntosUpdates;

    public void insertToMySQL(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        puntosInserts = new JSONArray();
        displayProgressBar(a, progressBar, tvProgressBar, "Enviando puntos...");
        ArrayList<PuntoPresion> puntosPresionInsertar = extraerTodosPendientesInsertar(a);
        for (int i = 0; i < puntosPresionInsertar.size(); i++) {
            try {
                JSONObject punto = new JSONObject();
                punto.put("id", puntosPresionInsertar.get(i).getId());
                punto.put("circuito", puntosPresionInsertar.get(i).getCircuito());
                punto.put("barrio", puntosPresionInsertar.get(i).getBarrio());
                punto.put("calle1", puntosPresionInsertar.get(i).getCalle1());
                punto.put("calle2", puntosPresionInsertar.get(i).getCalle2());
                punto.put("latitud", puntosPresionInsertar.get(i).getLatitud());
                punto.put("longitud", puntosPresionInsertar.get(i).getLongitud());
                punto.put("presion", puntosPresionInsertar.get(i).getPresion());
                punto.put("tipo_presion", puntosPresionInsertar.get(i).getTipoPresion().getId());
                punto.put("tipo_punto", puntosPresionInsertar.get(i).getTipoPunto().getId());
                punto.put("id_usuario", LoginActivity.usuario.getId());
                punto.put("unidad", puntosPresionInsertar.get(i).getUnidad());
                punto.put("tipo_unidad", puntosPresionInsertar.get(i).getTipoUnidad());
                punto.put("unidad2", puntosPresionInsertar.get(i).getUnidad2());
                punto.put("tipo_unidad2", puntosPresionInsertar.get(i).getTipoUnidad2());
                punto.put("cloro", puntosPresionInsertar.get(i).getCloro());
                punto.put("muestra", puntosPresionInsertar.get(i).getMuestra());
                puntosInserts.put(punto);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, VOLLEY_HOST + MODULO_PRESION + "puntos_presion_insert.php", puntosInserts, response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            mostrarMensajeLog(a, response.toString());
            try {
                if (response.getJSONObject(0).getString("status").equals("OK")) {
                    Log.d("RESPUESTASERVER", "OK");
                    // SI SALE BIEN, BAJAMOS EL PENDIENTE AL PUNTO
                    for (int i = 0; i < puntosPresionInsertar.size(); i++) {
                        actualizarPendiente(puntosPresionInsertar.get(i), a);
                    }
                    // Y PASAMOS A LA SIGUIENTE REQUEST
                    updateToMySQL(a, progressBar, tvProgressBar);
                } else {
                    Log.e("RESPUESTASERVER", "ERROR");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("RESPUESTASERVER", e.toString());
            }
        }, error -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            setPreference(a, ERROR_PREFERENCE, error.toString());
            mostrarMensajeLog(a, error.toString());
            abrirActivity(a, ErrorActivity.class);
        });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    public void updateToMySQL(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        puntosUpdates = new JSONArray();
        displayProgressBar(a, progressBar, tvProgressBar, "Actualizando puntos...");
        ArrayList<PuntoPresion> puntosPresionActualizar = extraerTodosPendientesActualizar(a);
        for (int i = 0; i < puntosPresionActualizar.size(); i++) {
            try {
                JSONObject punto = new JSONObject();
                punto.put("id", puntosPresionActualizar.get(i).getId());
                punto.put("presion", puntosPresionActualizar.get(i).getPresion());
                punto.put("id_usuario", LoginActivity.usuario.getId());
                puntosUpdates.put(punto);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, VOLLEY_HOST + MODULO_PRESION + "puntos_presion_update.php", puntosUpdates, response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            try {
                if (response.getJSONObject(0).getString("status").equals("OK")) {
                    Log.d("RESPUESTASERVER", "OK");
                    // SI SALE BIEN, BAJAMOS EL PENDIENTE AL PUNTO
                    for (int i = 0; i < puntosPresionActualizar.size(); i++) {
                        actualizarPendiente(puntosPresionActualizar.get(i), a);
                    }
                    // Y PASAMOS A LA SIGUIENTE REQUEST
                    HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();
                    historialPuntosControlador.insertToMySQL(a, progressBar, tvProgressBar);
                } else {
                    Log.e("RESPUESTASERVER", "ERROR");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("RESPUESTASERVER", e.toString());
            }
        }, error -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            setPreference(a, ERROR_PREFERENCE, error.toString());
            mostrarMensajeLog(a, error.toString());
            abrirActivity(a, ErrorActivity.class);
        });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    public void syncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        displayProgressBar(a, progressBar, tvProgressBar, "Obteniendo puntos... ");
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_PRESION + "puntos_presion_select.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            Log.d("response", response);
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM puntos_presion");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String sql = "INSERT INTO puntos_presion" +
                                "(id,circuito,barrio,calle1,calle2,latitud,longitud," +
                                "pendiente,presion,id_tipo_presion,id_tipo_punto,id_usuario," +
                                "unidad,tipo_unidad,unidad2,tipo_unidad2,cloro,muestra)" +
                                "VALUES" +
                                "(" + jsonArray.getJSONObject(i).getInt("id") + ",'" + // id
                                jsonArray.getJSONObject(i).getInt("circuito") + "','" + // circuito
                                jsonArray.getJSONObject(i).getString("barrio") + "','" + // barrio
                                jsonArray.getJSONObject(i).getString("calle1") + "','" + // calle1
                                jsonArray.getJSONObject(i).getString("calle2") + "','" + // calle2
                                jsonArray.getJSONObject(i).getDouble("latitud") + "','" + // latitud
                                jsonArray.getJSONObject(i).getDouble("longitud") + "','" + // longitud
                                "0','" + // pendiente
                                jsonArray.getJSONObject(i).getString("presion") + "','" + // presion
                                jsonArray.getJSONObject(i).getInt("id_tipo_presion") + "','" + // id_tipo_presion
                                jsonArray.getJSONObject(i).getInt("id_tipo_punto") + "','" + // id_tipo_punto
                                jsonArray.getJSONObject(i).getString("id_usuario") + "','" + // id_usuario
                                jsonArray.getJSONObject(i).getString("unidad") + "','" + // unidad
                                jsonArray.getJSONObject(i).getString("tipo_unidad") + "','" + // tipo_unidad
                                jsonArray.getJSONObject(i).getString("unidad2") + "','" + // unidad2
                                jsonArray.getJSONObject(i).getString("tipo_unidad2") + "','" + // tipo_unidad2
                                jsonArray.getJSONObject(i).getString("cloro") + "','" + // cloro
                                jsonArray.getJSONObject(i).getString("muestra") + "');"; // muestra
                        db.execSQL(sql);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                db.close();
                // Y AL FINAL EJECUTAMOS LA SIGUIENTE REQUEST
                HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();
                historialPuntosControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar);
            } else {
                Toast.makeText(a, "No existen puntos de presion", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            setPreference(a, ERROR_PREFERENCE, error.toString());
            mostrarMensajeLog(a, error.toString());
            abrirActivity(a, ErrorActivity.class);
        });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    public ArrayList<PuntoPresion> extraerTodos(Activity a, int idTipoPunto, int circuito) {
        /** Extrae todos los puntos que sean del tipo "idTipoPunto" y pertenezcan al circuito seleccinado*/
        puntosPresion = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c;
        if (idTipoPunto == MAPA_CLIENTES) {
            /** SI BUSCAMOS LOS CLIENTES, QUE NO SEAN MAS ANTIGUOS QUE UNA SEMANA */
            c = db.rawQuery("SELECT pp.id, pp.circuito, pp.barrio, pp.calle1, pp.calle2, " +
                    " pp.latitud, pp.longitud, pp.pendiente, pp.presion, " +
                    " pp.id_tipo_presion, pp.id_tipo_punto, pp.id_usuario, pp.unidad, pp.tipo_unidad, " +
                    " pp.unidad2, pp.tipo_unidad2, pp.cloro, pp.muestra" +
                    " FROM puntos_presion AS pp, historial_puntos_presion AS hp " +
                    " WHERE julianday('now') - julianday(hp.fecha) < 7.12510325247422" +
                    " AND pp.id = hp.id_punto_presion" +
                    " AND pp.id_usuario = hp.id_usuario" +
                    " AND id_tipo_punto = " + MAPA_CLIENTES + " " +
                    " AND circuito = " + circuito + " " +
                    " GROUP BY pp.id, pp.id_usuario" +
                    " ORDER BY hp.fecha DESC", null);
        } else {
            /** SI NO BUSCAMOS CLIENTES, QUE TRAIGA TODOS LOS PUNTOS HISTORICOS */
            c = db.rawQuery("SELECT * FROM puntos_presion" +
                    " WHERE id_tipo_punto =" + idTipoPunto +
                    " AND circuito =" + circuito, null);
        }
        while (c.moveToNext()) {
            Usuario u = new Usuario();
            PuntoPresion puntoPresion = new PuntoPresion();
            TipoPresion tipoPresion = new TipoPresion();
            TipoPunto tipoPunto = new TipoPunto();
            puntoPresion.setId(c.getInt(0));
            puntoPresion.setCircuito(c.getInt(1));
            puntoPresion.setBarrio(c.getString(2));
            puntoPresion.setCalle1(c.getString(3));
            puntoPresion.setCalle2(c.getString(4));
            puntoPresion.setLatitud(c.getDouble(5));
            puntoPresion.setLongitud(c.getDouble(6));
            puntoPresion.setPresion(c.getFloat(8));
            tipoPresion.setId(c.getInt(9));
            puntoPresion.setTipoPresion(tipoPresion);
            tipoPunto.setId(c.getInt(10));
            puntoPresion.setTipoPunto(tipoPunto);
            u.setId(c.getString(11));
            puntoPresion.setUsuario(u);
            puntoPresion.setUnidad(c.getInt(12));
            puntoPresion.setTipoUnidad(c.getString(13));
            puntoPresion.setUnidad(c.getInt(14));
            puntoPresion.setTipoUnidad(c.getString(15));
            puntoPresion.setCloro(c.getFloat(16));
            puntoPresion.setMuestra(c.getString(17));
            puntosPresion.add(puntoPresion);
        }
        c.close();
        db.close();
        return puntosPresion;
    }

    public PuntoPresion extraerPorIdYUsuario(Activity a, int id, String usuario) {
        PuntoPresion puntoPresion = new PuntoPresion();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM puntos_presion where id =" + id + " " +
                "AND id_usuario like '" + usuario + "'", null);
        while (c.moveToNext()) {
            TipoPresion tipoPresion = new TipoPresion();
            TipoPunto tipoPunto = new TipoPunto();
            Usuario u = new Usuario();
            puntoPresion.setId(c.getInt(0));
            puntoPresion.setCircuito(c.getInt(1));
            puntoPresion.setBarrio(c.getString(2));
            puntoPresion.setCalle1(c.getString(3));
            puntoPresion.setCalle2(c.getString(4));
            puntoPresion.setLatitud(c.getDouble(5));
            puntoPresion.setLongitud(c.getDouble(6));
            puntoPresion.setPendiente(c.getInt(7));
            puntoPresion.setPresion(c.getFloat(8));
            tipoPresion.setId(c.getInt(9));
            puntoPresion.setTipoPresion(tipoPresion);
            tipoPunto.setId(c.getInt(10));
            puntoPresion.setTipoPunto(tipoPunto);
            u.setId(usuario);
            puntoPresion.setUsuario(u);
            puntoPresion.setUnidad(c.getInt(12));
            puntoPresion.setTipoUnidad(c.getString(13));
            puntoPresion.setUnidad2(c.getInt(14));
            puntoPresion.setTipoUnidad2(c.getString(15));
            puntoPresion.setCloro(c.getFloat(16));
            puntoPresion.setMuestra(c.getString(17));
        }
        c.close();
        db.close();
        return puntoPresion;
    }

    public void insertar(PuntoPresion puntoPresion, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            int id = obtenerSiguienteId(a);
            String sql = "INSERT INTO `puntos_presion`" +
                    "(`id`," +
                    "`circuito`," +
                    "`barrio`," +
                    "`calle1`," +
                    "`calle2`," +
                    "`latitud`," +
                    "`longitud`," +
                    "`pendiente`," +
                    "`presion`," +
                    "`id_tipo_presion`," +
                    "`id_tipo_punto`," +
                    "`id_usuario`," +
                    "`unidad`," +
                    "`tipo_unidad`," +
                    "`unidad2`," +
                    "`tipo_unidad2`," +
                    "`cloro`," +
                    "`muestra`)" +
                    "VALUES" +
                    "('" + id + "','" + // id
                    puntoPresion.getCircuito() + "','" + // circuito
                    puntoPresion.getBarrio() + "','" + // barrio
                    puntoPresion.getCalle1() + "','" + // calle1
                    puntoPresion.getCalle2() + "','" + // calle2
                    puntoPresion.getLatitud() + "','" + // latitud
                    puntoPresion.getLongitud() + "','" + // longitud
                    INSERTAR_PUNTO + "','" + // pendiente
                    puntoPresion.getPresion() + "','" + // presion
                    puntoPresion.getTipoPresion().getId() + "','" + // tipo_presion
                    puntoPresion.getTipoPunto().getId() + "','" + // tipo_punto
                    LoginActivity.usuario.getId() + "','" + // id_usuario
                    puntoPresion.getUnidad() + "','" + // unidad
                    puntoPresion.getTipoUnidad() + "','" + // tipo_unidad
                    puntoPresion.getUnidad2() + "','" + // unidad2
                    puntoPresion.getTipoUnidad2() + "','" + // tipo_unidad2
                    puntoPresion.getCloro() + "','" + // cloro
                    puntoPresion.getMuestra() + "');"; // muestra
            db.execSQL(sql);
            /** SUBIMOS LA BANDERA DE SYNC MODULO PRESION **/
            UsuarioControlador usuarioControlador = new UsuarioControlador();
            usuarioControlador.editarBanderaSyncModuloPresion(a, BANDERA_ALTA);
            /** LE INSERTAMOS UN HISTORIAL AL PUNTO **/
            HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();
            historialPuntosControlador.insertar(puntoPresion, a, id);
            /** CERRAMOS LAS CONEXIONES **/
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error insertar PPC " + e.toString());
        }
    }

    private ArrayList<PuntoPresion> extraerTodosPendientesInsertar(Activity a) {
        puntosPresion = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM puntos_presion " +
                "WHERE pendiente = " + INSERTAR_PUNTO, null);
        while (c.moveToNext()) {
            PuntoPresion pp = new PuntoPresion();
            pp.setId(c.getInt(0));
            pp.setCircuito(c.getInt(1));
            pp.setBarrio(c.getString(2));
            pp.setCalle1(c.getString(3));
            pp.setCalle2(c.getString(4));
            pp.setLatitud(c.getDouble(5));
            pp.setLongitud(c.getDouble(6));
            pp.setPresion(c.getFloat(8));
            TipoPresion tipoPresion = new TipoPresion();
            tipoPresion.setId(c.getInt(9));
            pp.setTipoPresion(tipoPresion);
            TipoPunto tipoPunto = new TipoPunto();
            tipoPunto.setId(c.getInt(10));
            pp.setTipoPunto(tipoPunto);
            Usuario u = new Usuario();
            u.setId(c.getString(11));
            pp.setUsuario(u);
            pp.setUnidad(c.getInt(12));
            pp.setTipoUnidad(c.getString(13));
            pp.setUnidad2(c.getInt(14));
            pp.setTipoUnidad2(c.getString(15));
            pp.setCloro(c.getFloat(16));
            pp.setMuestra(c.getString(17));
            puntosPresion.add(pp);
        }
        c.close();
        db.close();
        return puntosPresion;
    }

    private ArrayList<PuntoPresion> extraerTodosPendientesActualizar(Activity a) {
        puntosPresion = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM puntos_presion " +
                "WHERE pendiente = " + ACTUALIZAR_PUNTO, null);
        while (c.moveToNext()) {
            PuntoPresion pp = new PuntoPresion();
            pp.setId(c.getInt(0));
            pp.setCircuito(c.getInt(1));
            pp.setBarrio(c.getString(2));
            pp.setCalle1(c.getString(3));
            pp.setCalle2(c.getString(4));
            pp.setLatitud(c.getDouble(5));
            pp.setLongitud(c.getDouble(6));
            pp.setPresion(c.getFloat(8));
            TipoPresion tipoPresion = new TipoPresion();
            tipoPresion.setId(c.getInt(9));
            pp.setTipoPresion(tipoPresion);
            TipoPunto tipoPunto = new TipoPunto();
            tipoPunto.setId(c.getInt(10));
            pp.setTipoPunto(tipoPunto);
            Usuario u = new Usuario();
            u.setId(c.getString(11));
            pp.setUsuario(u);
            pp.setUnidad(c.getInt(12));
            pp.setTipoUnidad(c.getString(13));
            pp.setUnidad2(c.getInt(14));
            pp.setTipoUnidad2(c.getString(15));
            pp.setCloro(c.getFloat(16));
            pp.setMuestra(c.getString(17));
            puntosPresion.add(pp);
        }
        c.close();
        db.close();
        return puntosPresion;
    }

    private void actualizarPendiente(PuntoPresion puntoPresion, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE puntos_presion" +
                    " SET pendiente = 0" +
                    " WHERE id=" + puntoPresion.getId() +
                    " AND id_usuario like '" + puntoPresion.getUsuario().getId() + "'";
            db.execSQL(sql);
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente PPC " + e.toString());
        }
    }

    private int obtenerSiguienteId(Activity a) {
        int id = 1;
        try {
            SQLiteDatabase db3 = BaseHelper.getInstance(a).getReadableDatabase();
            String sql = "SELECT id FROM puntos_presion ORDER BY id DESC LIMIT 1";
            Cursor c3 = db3.rawQuery(sql, null);
            while (c3.moveToNext()) {
                id = c3.getInt(0) + 1;
            }
            return id;
        } catch (Exception e) {
            mostrarMensaje(a, "Error obtenerSiguienteId PPC " + e.toString());
            return Util.ERROR;
        }
    }
}
