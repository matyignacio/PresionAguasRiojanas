package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.desarrollo.kuky.presionaguasriojanas.objeto.Modulo;
import com.desarrollo.kuky.presionaguasriojanas.objeto.Usuario;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.InicioActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.CIRCUITO_USUARIO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.POSICION_SELECCIONADA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class UsuarioControlador {

    public void loguearUsuario(Activity a, String eMail, String clave, ProgressBar progressBar, TextView tvProgressBar) {
        displayProgressBar(a, progressBar, tvProgressBar, "Iniciando sesion...");
        LoginActivity.usuario = new Usuario();
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + "login.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    LoginActivity.usuario.setId(jsonObject.getString("usuario"));
                    LoginActivity.usuario.setNombre(jsonObject.getString("nombre"));
                    LoginActivity.usuario.setEmail(jsonObject.getString("email"));
                    LoginActivity.usuario.setClave(jsonObject.getString("clave"));
                    LoginActivity.usuario.setTipo(jsonObject.getString("tipo"));
                    LoginActivity.usuario.setActivo(jsonObject.getString("activo"));
                    LoginActivity.usuario.setBanderaModuloPresion(0);
                    LoginActivity.usuario.setBanderaSyncModuloPresion(0);
                    LoginActivity.usuario.setBanderaModuloInspeccion(0);
                    LoginActivity.usuario.setBanderaSyncModuloInspeccion(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Y AL FINAL EJECUTAMOS LA SIGUIENTE REQUEST
                validarUsuario(a, LoginActivity.usuario.getId(), progressBar, tvProgressBar);
            } else {
                Toast.makeText(a, "No existe ningun usuario con esa clave", Toast.LENGTH_SHORT).show();
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
                params.put("user", eMail);
                params.put("clave", clave);
                return params;
            }
        };
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    public void validarUsuario(Activity a, String usuario, ProgressBar progressBar, TextView tvProgressBar) {
        /** VACIAMOS LA TABLA DE MODULOS */
        SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
        String sql = "DELETE FROM modulos";
        db.execSQL(sql);
        db.close();
        /*********************************/
        ArrayList<Modulo> modulos = new ArrayList<>();
        displayProgressBar(a, progressBar, tvProgressBar, "Revisando permisos...");
        guardarUsuario(a, LoginActivity.usuario);
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + "permisos.php", response -> {
            lockProgressBar(a, progressBar, tvProgressBar);
            if (!response.equals("ERROR_ARRAY_VACIO")) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        // CARGAMOS LOS MODULOS EN EL USUARIO
                        Modulo modulo = new Modulo();
                        modulo.setId(jsonArray.getJSONObject(i).getInt("id"));
                        modulo.setNombre(jsonArray.getJSONObject(i).getString("nombre"));
                        modulos.add(modulo);
                        // Y TAMBIEN LOS GUARDAMOS
                        guardarModulo(a, modulo);
                    }
                    LoginActivity.usuario.setModulos(modulos);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Y AL FINAL ABRIMOS LA OTRA ACTIVITY
                abrirActivity(a, InicioActivity.class);
            } else {
                mostrarMensaje(a, "El usuario no tiene permisos.");
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
                params.put("user", usuario);
                return params;
            }
        };
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }

    private void guardarUsuario(Activity a, Usuario u) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            db.execSQL(BaseHelper.getInstance(a).getSqlTablaUsuarios());
            String sql = "INSERT INTO susuario VALUES( '" +
                    u.getId() + "', '"
                    + u.getNombre() + "', '"
                    + u.getEmail() + "', '"
                    + u.getClave() + "', '"
                    + u.getTipo() + "', '"
                    + u.getActivo() + "', '"
                    + u.getBanderaModuloPresion() + "', '"
                    + u.getBanderaSyncModuloPresion() + "', '"
                    + u.getBanderaModuloInspeccion() + "', '"
                    + u.getBanderaSyncModuloInspeccion() + "')";
            db.execSQL(sql);
            db.close();
        } catch (Exception e) {
            mostrarMensajeLog(a, e.toString());
        }
    }

    private void guardarModulo(Activity a, Modulo m) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            db.execSQL(BaseHelper.getInstance(a).getSqlTablaUsuarios());
            String sql = "INSERT INTO modulos VALUES( '" +
                    m.getId() + "', '"
                    + m.getNombre() + "')";
            db.execSQL(sql);
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, e.toString());
        }
    }

    public int eliminarUsuario(Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "DELETE FROM susuario";
            db.execSQL(sql);
            db.close();
            // RESETEAMOS LA POSICION DEL SPINNER DE circuito A 0 PARA QUE NO TIRE ERROR.
            setPreference(a, POSICION_SELECCIONADA, 0);
            // RESETEAMOS EL CIRCUITO A 0 PARA QUE NO MUESTRE EL CIRCUITO DEL USUARIO ANTERIOR.
            setPreference(a, CIRCUITO_USUARIO, 0);
            return EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, e.toString());
            return ERROR;
        }
    }

    public int existeUsuario(Activity a) {
        try {
            ArrayList<Modulo> modulos = new ArrayList<>();
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            db.execSQL(BaseHelper.getInstance(a).getSqlTablaUsuarios());
            Cursor c = db.rawQuery("SELECT * FROM susuario", null);
            if (c.moveToFirst()) {
                LoginActivity.usuario.setId(c.getString(0));
                LoginActivity.usuario.setNombre(c.getString(1));
                LoginActivity.usuario.setTipo(c.getString(4));
                LoginActivity.usuario.setActivo(c.getString(5));
                LoginActivity.usuario.setBanderaModuloPresion(c.getInt(6));
                LoginActivity.usuario.setBanderaSyncModuloPresion(c.getInt(7));
                LoginActivity.usuario.setBanderaModuloInspeccion(c.getInt(8));
                LoginActivity.usuario.setBanderaSyncModuloInspeccion(c.getInt(9));
                Cursor c2 = db.rawQuery("SELECT * FROM modulos", null);
                while (c2.moveToNext()) {
                    Modulo modulo = new Modulo();
                    modulo.setId(c2.getInt(0));
                    modulo.setNombre(c2.getString(1));
                    modulos.add(modulo);
                }
                LoginActivity.usuario.setModulos(modulos);
                c2.close();
                return EXITOSO;
            }
            c.close();
            db.close();
            return ERROR;
        } catch (Exception e) {
            mostrarMensaje(a, e.toString());
            return ERROR;
        }
    }

    public void editarBanderaModuloPresion(Activity a, int bandera) {
        try {
            SQLiteDatabase bh = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE susuario SET  'bandera_modulo_presion' =" +
                    bandera;
            bh.execSQL(sql);
            bh.close();
            LoginActivity.usuario.setBanderaModuloPresion(bandera);
        } catch (Exception e) {
            mostrarMensaje(a, e.toString());
        }
    }

    public void editarBanderaSyncModuloInspeccion(Activity a, int bandera) {
        try {
            SQLiteDatabase bh = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE susuario SET 'bandera_sync_modulo_inspeccion' = " +
                    bandera;
            bh.execSQL(sql);
            bh.close();
            LoginActivity.usuario.setBanderaSyncModuloInspeccion(bandera);
        } catch (Exception e) {
            mostrarMensaje(a, e.toString());
        }
    }

    public void editarBanderaModuloInspeccion(Activity a, int bandera) {
        try {
            SQLiteDatabase bh = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE susuario SET  'bandera_modulo_inspeccion' =" +
                    bandera;
            bh.execSQL(sql);
            bh.close();
            LoginActivity.usuario.setBanderaModuloInspeccion(bandera);
        } catch (Exception e) {
            mostrarMensaje(a, e.toString());
        }
    }

    public void editarBanderaSyncModuloPresion(Activity a, int bandera) {
        try {
            SQLiteDatabase bh = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE susuario SET 'bandera_sync_modulo_presion' = " +
                    bandera;
            bh.execSQL(sql);
            bh.close();
            LoginActivity.usuario.setBanderaSyncModuloPresion(bandera);
        } catch (Exception e) {
            mostrarMensaje(a, e.toString());
        }
    }

    public void actualizarTablas(Activity a) {
        SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
        /** DROPEAMOS PARA QUE SE CREEN */
        /* MODULO RECLAMO */
        db.execSQL(BaseHelper.getInstance(a).dropTable("GTtramite"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("GTreclamo"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("GTresolucion"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("GTres_mot"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("GTmot_req"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("GTtpo_tram"));
        /* MODULO INSPECCION */
        db.execSQL(BaseHelper.getInstance(a).dropTable("tipo_inmueble"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("barrios"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("relevamiento"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("relevamiento_medidores"));
        /* MODULO PRESION */
        db.execSQL(BaseHelper.getInstance(a).dropTable("orden"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("historial_puntos_presion"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("puntos_presion"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("tipo_punto"));
        /* GENERAL */
        db.execSQL(BaseHelper.getInstance(a).dropTable("susuario"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("modulos"));
        /** Y AHORA LAS VOLVEMOS A CREAR CON FORMATO DEFINITIVO */
        /* MODULO RECLAMO */
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaTipoTramite());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaMotivoTramite());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaTipoResolucion());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaResolucionMotivos());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaReclamoTramite());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaTramite());
        /* MODULO INSPECCION */
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaTipoInmueble());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaBarrios());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaRelevamiento());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaRelevamientoMedidores());
        /* MODULO PRESION */
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaOrden());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaHistorialPuntosPresion());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaPuntosPresion());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaTipoPunto());
        /* GENERAL */
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaUsuarios());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaModulos());
        db.close();
    }

}
