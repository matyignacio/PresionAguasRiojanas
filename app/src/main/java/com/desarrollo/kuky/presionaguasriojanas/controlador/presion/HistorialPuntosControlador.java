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
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.HistorialPuntos;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.Orden;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ACTUALIZAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_ALTA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_BAJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.INSERTAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_PRESION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class HistorialPuntosControlador {

    private ArrayList<HistorialPuntos> historiales;
    private JSONArray puntosInserts;
    private JSONArray puntosUpdates;

    public void insertToMySQL(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> method) {
        puntosInserts = new JSONArray();
        displayProgressBar(a, progressBar, tvProgressBar, "Enviando historial...");
        ArrayList<HistorialPuntos> historialesInsertar = extraerTodosPendientes(a);
        for (int i = 0; i < historialesInsertar.size(); i++) {
            try {
                JSONObject punto = new JSONObject();
                punto.put("latitud", historialesInsertar.get(i).getLatitud());
                punto.put("longitud", historialesInsertar.get(i).getLongitud());
                punto.put("presion", historialesInsertar.get(i).getPresion());
                punto.put("fecha", historialesInsertar.get(i).getFecha());
                punto.put("id_punto_presion", historialesInsertar.get(i).getPuntoPresion().getId());
                punto.put("id_usuario", historialesInsertar.get(i).getPuntoPresion().getUsuario().getId());
                punto.put("id_usuario_historial", LoginActivity.usuario.getId());
                punto.put("cloro", historialesInsertar.get(i).getCloro());
                punto.put("muestra", historialesInsertar.get(i).getMuestra());
                puntosInserts.put(punto);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, VOLLEY_HOST + MODULO_PRESION + "historial_puntos_presion_insert.php", puntosInserts, response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            mostrarMensajeLog(a, response.toString());
            try {
                if (response.getJSONObject(0).getString("status").equals("OK")) {
                    Log.d("RESPUESTASERVER", "OK");
                    // SI SALE BIEN, BAJAMOS EL PENDIENTE AL PUNTO
                    for (int i = 0; i < historialesInsertar.size(); i++) {
                        actualizarPendiente(historialesInsertar.get(i), a);
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
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    public void syncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> method) {
        displayProgressBar(a, progressBar, tvProgressBar, "Obteniendo historial...");
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_PRESION + "historial_puntos_presion_select.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            Log.d("response", response);
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM historial_puntos_presion");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String sql = "INSERT INTO historial_puntos_presion" +
                                "(id,latitud,longitud,pendiente,presion,fecha,id_punto_presion," +
                                "id_usuario,id_usuario_historial,cloro,muestra) VALUES" +
                                "('" + jsonArray.getJSONObject(i).getInt("id") + "','" + // id
                                jsonArray.getJSONObject(i).getDouble("latitud") + "','" + // latitud
                                jsonArray.getJSONObject(i).getDouble("longitud") + "','" + // longitud
                                "0','" + // pendiente
                                jsonArray.getJSONObject(i).getString("presion") + "','" + // presion
                                jsonArray.getJSONObject(i).getString("fecha") + "','" + // fecha
                                jsonArray.getJSONObject(i).getInt("id_punto_presion") + "','" + // id_tipo_presion
                                jsonArray.getJSONObject(i).getString("id_usuario") + "','" + // id_usuario
                                jsonArray.getJSONObject(i).getString("id_usuario_historial") + "','" + // id_usuario_historial
                                jsonArray.getJSONObject(i).getString("cloro") + "','" + // cloro
                                jsonArray.getJSONObject(i).getString("muestra") + "');"; // muestra
                        db.execSQL(sql);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                db.close();
                // Y AL FINAL EJECUTAMOS
                try {
                    method.call();
                } catch (Exception e) {
                    mostrarMensajeLog(a, e.toString());
                }
            } else {
                Toast.makeText(a, "No existe historial de presion", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            String problema = error.toString() + " en " + this.getClass().getName();
            setPreference(a, ERROR_PREFERENCE, problema);
            mostrarMensajeLog(a, problema);
            abrirActivity(a, ErrorActivity.class);
        });
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    public ArrayList<HistorialPuntos> extraerTodosPorPunto(Activity a, int id, String usuario) {
        historiales = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT hp.id," +
                "    hp.latitud," +
                "    hp.longitud," +
                "    hp.presion," +
                "    hp.fecha," +
                "    hp.id_punto_presion," +
                "    hp.id_usuario," +
                "    hp.id_usuario_historial," +
                "    hp.cloro," +
                "    hp.muestra" +
                " FROM historial_puntos_presion AS hp, puntos_presion AS pp " +
                " WHERE pp.id=hp.id_punto_presion " +
                " AND pp.id_usuario = hp.id_usuario" +
                " AND pp.id = " + id +
                " AND pp.id_usuario like '" + usuario + "'" +
                " ORDER BY fecha DESC", null);
        while (c.moveToNext()) {
            HistorialPuntos historialPuntos = new HistorialPuntos();
            PuntoPresion puntoPresion = new PuntoPresion();
            Usuario uPunto = new Usuario();
            Usuario uHistorial = new Usuario();
            historialPuntos.setId(c.getInt(0));
            historialPuntos.setLatitud(c.getDouble(1));
            historialPuntos.setLongitud(c.getDouble(2));
            historialPuntos.setPresion(c.getFloat(3));
            historialPuntos.setFecha(Timestamp.valueOf(c.getString(4)));
            puntoPresion.setId(c.getInt(5));
            uPunto.setId(c.getString(6));
            puntoPresion.setUsuario(uPunto);
            uHistorial.setId(c.getString(7));
            historialPuntos.setPuntoPresion(puntoPresion);
            historialPuntos.setUsuario(uHistorial);
            historialPuntos.setCloro(c.getFloat(8));
            historialPuntos.setMuestra(c.getString(9));
            historiales.add(historialPuntos);
        }
        c.close();
        db.close();
        return historiales;
    }

    private ArrayList<HistorialPuntos> extraerTodosPendientes(Activity a) {
        historiales = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM historial_puntos_presion " +
                "WHERE pendiente = 1 " +
                "ORDER BY id ASC", null);
        while (c.moveToNext()) {
            HistorialPuntos historialPuntos = new HistorialPuntos();
            PuntoPresion puntoPresion = new PuntoPresion();
            Usuario uPunto = new Usuario();
            Usuario uHistorial = new Usuario();
            historialPuntos.setId(c.getInt(0));
            historialPuntos.setLatitud(c.getDouble(1));
            historialPuntos.setLongitud(c.getDouble(2));
            historialPuntos.setPresion(c.getFloat(4));
            historialPuntos.setFecha(Timestamp.valueOf(c.getString(5)));
            puntoPresion.setId(c.getInt(6));
            uPunto.setId(c.getString(7));
            puntoPresion.setUsuario(uPunto);
            historialPuntos.setPuntoPresion(puntoPresion);
            uHistorial.setId(c.getString(8));
            historialPuntos.setUsuario(uHistorial);
            historialPuntos.setCloro(c.getFloat(9));
            historialPuntos.setMuestra(c.getString(10));
            historiales.add(historialPuntos);
        }
        c.close();
        db.close();
        return historiales;
    }

    public void insertar(HistorialPuntos historialPuntos, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "INSERT INTO historial_puntos_presion" +
                    "(latitud," +
                    "longitud," +
                    "pendiente," +
                    "presion," +
                    "id_punto_presion," +
                    "id_usuario," +
                    "id_usuario_historial," +
                    "cloro," +
                    "muestra)" +
                    "VALUES" +
                    "('" + historialPuntos.getLatitud() + "','" + // latitud
                    historialPuntos.getLongitud() + "','" + // longitud
                    INSERTAR_PUNTO + "','" + // pendiente
                    historialPuntos.getPresion() + "','" + // presion
                    historialPuntos.getPuntoPresion().getId() + "','" + // id_punto_presion
                    historialPuntos.getPuntoPresion().getUsuario().getId() + "','" + // id_usuario
                    historialPuntos.getUsuario().getId() + "','" + // id_usuario_historial
                    historialPuntos.getCloro() + "','" + // cloro
                    historialPuntos.getMuestra() + "');";// muestra
            db.execSQL(sql);
            PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
            PuntoPresion puntoPresion = puntoPresionControlador.extraerPorIdYUsuario(a,
                    historialPuntos.getPuntoPresion().getId(),
                    historialPuntos.getPuntoPresion().getUsuario().getId());
            /**
             * EVALUAREMOS SI EL PUNTO ES UNO NUEVO SIN IMPACTAR EN LA BASE MYSQL
             * O SI YA ES UN PUNTO CONOCIDO Y SIMPLEMENTE SE LE AGREGO UNA NUEVA MEDICION
             */
            SQLiteDatabase db2 = BaseHelper.getInstance(a).getWritableDatabase();
            if (puntoPresion.getPendiente() == INSERTAR_PUNTO) {
                sql = "UPDATE puntos_presion" +
                        " SET presion = '" + historialPuntos.getPresion() + "', pendiente = " + INSERTAR_PUNTO +
                        " WHERE id=" + historialPuntos.getPuntoPresion().getId() +
                        " AND id_usuario LIKE '" + historialPuntos.getPuntoPresion().getUsuario().getId() + "'";

            } else {
                sql = "UPDATE puntos_presion" +
                        " SET presion = '" + historialPuntos.getPresion() + "', pendiente = " + ACTUALIZAR_PUNTO +
                        " WHERE id=" + historialPuntos.getPuntoPresion().getId() +
                        " AND id_usuario LIKE '" + historialPuntos.getPuntoPresion().getUsuario().getId() + "'";
            }
            db2.execSQL(sql);
            /** SUBIMOS LA BANDERA DE SYNC MODULO PRESION **/
            UsuarioControlador usuarioControlador = new UsuarioControlador();
            usuarioControlador.editarBanderaSyncModuloPresion(a, BANDERA_ALTA);
            /** EVUALUAMOS SI HAY QUE CAMBIAR EL ORDEN **/
            Orden orden = new Orden();
            OrdenControlador ordenControlador = new OrdenControlador();
            orden = ordenControlador.existePunto(a, historialPuntos.getPuntoPresion());
            if (orden.getId() > 0) {
                /** SI EL PUNTO EXISTE EN LA TABLA DE ORDEN, LE BAJAMOS LA BANDERA **/
                ordenControlador.editarActivo(a,
                        orden.getPpActual().getId(),
                        orden.getPpActual().getUsuario().getId(),
                        BANDERA_BAJA);
                /** Y LE SUBIMOS LA BANDERA AL SIGUIENTE PUNTO **/
                ordenControlador.editarActivo(a,
                        orden.getPpSiguiente().getId(),
                        orden.getPpSiguiente().getUsuario().getId(),
                        BANDERA_ALTA);
            }
            /** CERRAMOS LAS CONEXIONES **/
            db.close();
            db2.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error insertar HPC " + e.toString());
        }
    }

    void insertar(PuntoPresion pp, Activity a, int id) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "INSERT INTO historial_puntos_presion" +
                    "(latitud," +
                    "longitud," +
                    "pendiente," +
                    "presion," +
                    "id_punto_presion," +
                    "id_usuario," +
                    "id_usuario_historial," +
                    "cloro," +
                    "muestra)" +
                    "VALUES" +
                    "('" + pp.getLatitud() + "','" + // latitud
                    pp.getLongitud() + "','" + // longitud
                    INSERTAR_PUNTO + "','" + // pendiente
                    pp.getPresion() + "','" + // presion
                    id + "','" + // id_punto_presion
                    pp.getUsuario().getId() + "','" + // id_usuario
                    pp.getUsuario().getId() + "','" + // id_usuario_historial
                    pp.getCloro() + "','" + // cloro
                    pp.getMuestra() + "');";// muestra
            db.execSQL(sql);
            /**
             * EVALUAREMOS SI EL PUNTO ES UNO NUEVO SIN IMPACTAR EN LA BASE MYSQL
             * O SI YA ES UN PUNTO CONOCIDO Y SIMPLEMENTE SE LE AGREGO UNA NUEVA MEDICION
             */
            SQLiteDatabase db2 = BaseHelper.getInstance(a).getWritableDatabase();
            sql = "UPDATE puntos_presion" +
                    " SET presion = '" + pp.getPresion() + "', pendiente = " + INSERTAR_PUNTO +
                    " WHERE id=" + pp.getId() +
                    " AND id_usuario LIKE '" + pp.getUsuario().getId() + "'";

            db2.execSQL(sql);
            db.close();
            db2.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error insertar HPC + PPC " + e.toString());
        }
    }

    private void actualizarPendiente(HistorialPuntos historialPuntos, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE historial_puntos_presion " +
                    "SET presion = '" + historialPuntos.getPresion() + "', pendiente = 0 " +
                    " WHERE id=" + historialPuntos.getPuntoPresion().getId() +
                    " AND id_usuario LIKE '" + historialPuntos.getPuntoPresion().getUsuario().getId() + "'";
            db.execSQL(sql);
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente HPC " + e.toString());
        }
    }
}
