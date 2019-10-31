package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.objeto.Modulo;
import com.desarrollo.kuky.presionaguasriojanas.objeto.Usuario;
import com.desarrollo.kuky.presionaguasriojanas.ui.InicioActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.CIRCUITO_USUARIO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.POSICION_SELECCIONADA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.checkConnection;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ocultarTeclado;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.progressBarVisibility;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setEnabledActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class UsuarioControlador {

    private class UsuarioPorMailYClave extends AsyncTask<String, Float, String> {
        Activity a;
        String eMail;
        String clave;
        private ProgressBar progressBar;
        private TextView tvProgressBar;

        @Override
        protected void onPreExecute() {
            setEnabledActivity(a, false);
            ocultarTeclado(a, progressBar);
            tvProgressBar.setText("Iniciando sesion...");
            progressBarVisibility(progressBar, tvProgressBar, true);
            LoginActivity.usuario = new Usuario();
        }

        UsuarioPorMailYClave(Activity a, String eMail, String clave, ProgressBar progressBar, TextView tvProgressBar) {
            this.a = a;
            this.eMail = eMail;
            this.clave = clave;
            this.progressBar = progressBar;
            this.tvProgressBar = tvProgressBar;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn;
                PreparedStatement ps;
                ResultSet rs;
                conn = Conexion.GetConnection();
                String consultaSql = "SELECT * FROM susuario WHERE email LIKE '" + eMail + "' AND clave LIKE '" + clave + "' AND activo LIKE 's'";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                if (rs.next()) {
                    LoginActivity.usuario.setId(rs.getString(1));
                    LoginActivity.usuario.setNombre(rs.getString(2));
                    LoginActivity.usuario.setEmail(rs.getString(3));
                    LoginActivity.usuario.setClave(rs.getString(4));
                    LoginActivity.usuario.setTipo(rs.getString(5));
                    LoginActivity.usuario.setActivo(rs.getString(6));
                    LoginActivity.usuario.setBanderaModuloPresion(0);
                    LoginActivity.usuario.setBanderaSyncModuloPresion(0);
                    LoginActivity.usuario.setBanderaModuloInspeccion(0);
                    LoginActivity.usuario.setBanderaSyncModuloInspeccion(0);
                } else {
                    LoginActivity.usuario.setNombre(null);
                }
                rs.close();
                ps.close();
                conn.close();
                if (LoginActivity.usuario.getNombre() == null) {
                    return "El nombre de usuario es inexistente o esta dado de baja.";
                } else {
                    return "";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            setEnabledActivity(a, true);
            progressBarVisibility(progressBar, tvProgressBar, false);
            if (s.equals("")) {
                extraerPermisos(a, LoginActivity.usuario.getId(), progressBar, tvProgressBar);
            } else {
                mostrarMensaje(a, s);
            }

            /***
             * ACA NO MUESTRO NADA, LO USE PARA DEPURAR NOMAS. A LOS MENSAJES DE RESPUESTA
             * LOS MUESTRO EN LA ASYNCTASK DE LOGINACTIVITY
             if (s.equals("")) {
             Toast.makeText(a, s, Toast.LENGTH_SHORT).show();
             } else {
             Toast.makeText(a, s, Toast.LENGTH_SHORT).show();
             }
             */
        }
    }

    public void extraerPorMailYClave(Activity a, String mail, String clave, ProgressBar progressBar, TextView tvProgressBar) {
        checkConnection(a, () -> {
            try {
                UsuarioPorMailYClave usuarioPorMailYClave = new UsuarioPorMailYClave(a, mail, clave, progressBar, tvProgressBar);
                usuarioPorMailYClave.execute();
            } catch (Exception e) {
                mostrarMensaje(a, e.toString());
            }
            return null;
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class ExtraerPermisos extends AsyncTask<String, Float, String> {
        Activity a;
        String usuario;
        private ProgressBar progressBar;
        private TextView tvProgressBar;

        @Override
        protected void onPreExecute() {
            /** VACIAMOS LA TABLA DE MODULOS */
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "DELETE FROM modulos";
            db.execSQL(sql);
            db.close();
            /*********************************/
            setEnabledActivity(a, false);
            tvProgressBar.setText("Revisando permisos...");
            progressBarVisibility(progressBar, tvProgressBar, true);
        }

        ExtraerPermisos(Activity a, String usuario, ProgressBar progressBar, TextView tvProgressBar) {
            this.a = a;
            this.usuario = usuario;
            this.progressBar = progressBar;
            this.tvProgressBar = tvProgressBar;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                guardarUsuario(a, LoginActivity.usuario);
                Connection conn;
                PreparedStatement ps;
                ResultSet rs;
                ArrayList<Modulo> modulos = new ArrayList<>();
                conn = Conexion.GetConnection();
                String consultaSql = "SELECT m.id, m.nombre FROM susuario u, permisos p, modulos m" +
                        " WHERE u.usuario = p.id_usuario" +
                        " AND p.id_modulo = m.id" +
                        " AND u.usuario='" + usuario + "'";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                while (rs.next()) {
                    // CARGAMOS LOS MODULOS EN EL USUARIO
                    Modulo modulo = new Modulo();
                    modulo.setId(rs.getInt(1));
                    modulo.setNombre(rs.getString(2));
                    modulos.add(modulo);
                    // Y TAMBIEN LOS GUARDAMOS
                    guardarModulo(a, modulo);
                }
                LoginActivity.usuario.setModulos(modulos);
                rs.close();
                ps.close();
                conn.close();
                if (modulos.size() == 0) {
                    return "El usuario no tiene permisos";
                } else {
                    return "";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            setEnabledActivity(a, true);
            progressBarVisibility(progressBar, tvProgressBar, false);
            if (s.equals("")) {
                abrirActivity(a, InicioActivity.class);
            } else {
                mostrarMensaje(a, s);
            }
        }
    }

    public void extraerPermisos(Activity a, String usuario, ProgressBar progressBar, TextView tvProgressBar) {
        checkConnection(a, () -> {
            try {
                ExtraerPermisos extraerPermisos = new ExtraerPermisos(a, usuario, progressBar, tvProgressBar);
                extraerPermisos.execute();
            } catch (Exception e) {
                mostrarMensaje(a, e.toString());
            }
            return null;
        });
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
        db.execSQL(BaseHelper.getInstance(a).dropTable("tipo_inmueble"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("tipo_servicio"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("cliente"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("inspeccion"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("datos_relevados"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("orden"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("historial_puntos_presion"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("puntos_presion"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("tipo_punto"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("susuario"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("barrios"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("relevamiento"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("relevamiento_medidores"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("permisos"));
        /** Y AHORA LAS VOLVEMOS A CREAR CON FORMATO DEFINITIVO */
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaTipoInmueble());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaTipoServicio());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaCliente());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaInspeccion());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaDatosRelevados());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaOrden());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaHistorialPuntosPresion());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaPuntosPresion());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaTipoPunto());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaUsuarios());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaBarrios());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaRelevamiento());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaRelevamientoMedidores());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaModulos());
        db.close();
    }

}
