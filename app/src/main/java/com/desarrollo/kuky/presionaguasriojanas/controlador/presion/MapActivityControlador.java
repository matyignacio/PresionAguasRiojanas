package com.desarrollo.kuky.presionaguasriojanas.controlador.presion;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.desarrollo.kuky.presionaguasriojanas.controlador.UsuarioControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.VolleySingleton;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.presion.MapActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_BAJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MODULO_PRESION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PRIMER_INICIO_MODULO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.SEGUNDO_INICIO_MODULO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.logOut;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class MapActivityControlador {
    PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
    HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();
    OrdenControlador ordenControlador = new OrdenControlador();
    TipoPuntoControlador tipoPuntoControlador = new TipoPuntoControlador();

    public void abrirMapActivity(Activity a, ProgressBar progressBar, TextView tvProgressBar, Callable<Void> method) {
        displayProgressBar(a, progressBar, tvProgressBar, "Revisando circuitos...");
        ArrayList<Integer> circuitos = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + MODULO_PRESION + "permisos_circuitos.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        circuitos.add(jsonArray.getJSONObject(i).getInt("circuito"));
                    }
                    LoginActivity.usuario.setCircuitos(circuitos);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Y AL FINAL ABRIMOS LA OTRA ACTIVITY
                try {
                    method.call();
                } catch (Exception e) {
                    mostrarMensaje(a, e.toString());
                }
            } else {
                mostrarMensaje(a, "No tiene ningun circuito habilitado.");
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
                params.put("user", LoginActivity.usuario.getId());
                return params;
            }
        };
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    public void sincronizar(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        puntoPresionControlador.insertToMySQL(a, progressBar, tvProgressBar, () -> {
            puntoPresionControlador.updateToMySQL(a, progressBar, tvProgressBar, () -> {
                historialPuntosControlador.insertToMySQL(a, progressBar, tvProgressBar, () -> {
                    tipoPuntoControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                        ordenControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                            puntoPresionControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                                historialPuntosControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                                    UsuarioControlador usuarioControlador = new UsuarioControlador();
                                    if (LoginActivity.usuario.getBanderaModuloPresion() == PRIMER_INICIO_MODULO) {
                                        usuarioControlador.editarBanderaModuloPresion(a, SEGUNDO_INICIO_MODULO);
                                    }
                                    usuarioControlador.editarBanderaSyncModuloPresion(a, BANDERA_BAJA);
                                    if (a.getClass().getSimpleName().equals("InicioActivity")) {
                                        logOut(a);
                                    } else if (a.getClass().getSimpleName().equals("MapActivity")) {
                                        mostrarMensaje(a, "Para seleccionar o modificar el CIRCUITO ingrese al menu izquierdo");
                                        abrirActivity(a, MapActivity.class);
                                    }
                                    return null;
                                });
                                return null;
                            });
                            return null;
                        });
                        return null;
                    });
                    return null;
                });
                return null;
            });
            return null;
        });
    }
}
