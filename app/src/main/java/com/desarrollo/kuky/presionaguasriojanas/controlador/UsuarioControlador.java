package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.desarrollo.kuky.presionaguasriojanas.objeto.Usuario;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;

public class UsuarioControlador {
    private ProgressDialog pDialog;

    private class UsuarioPorMailYClave extends AsyncTask<String, Float, String> {
        Activity a;
        String eMail;
        String clave;

        @Override
        protected void onPreExecute() {
            LoginActivity.usuario = new Usuario();
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Iniciando sesion...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        UsuarioPorMailYClave(Activity a, String eMail, String clave) {
            this.a = a;
            this.eMail = eMail;
            this.clave = clave;
        }

        @Override
        protected String doInBackground(String... strings) {
            Connection conn;
            PreparedStatement ps;
            ResultSet rs;
            try {
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
                    guardarUsuario(a, LoginActivity.usuario);
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
            pDialog.dismiss();
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

    public void extraerPorMailYClave(Activity a, String mail, String clave) {
        try {
            UsuarioPorMailYClave usuarioPorMailYClave = new UsuarioPorMailYClave(a, mail, clave);
            usuarioPorMailYClave.execute();
        } catch (Exception e) {
            Util.mostrarMensaje(a, e.toString());
        }
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
            Util.mostrarMensaje(a, e.toString());
        }
    }

    public int eliminarUsuario(Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "DELETE FROM susuario";
            db.execSQL(sql);
            db.close();
            return EXITOSO;
        } catch (Exception e) {
            Util.mostrarMensaje(a, e.toString());
            return ERROR;
        }
    }

    public int existeUsuario(Activity a) {
        try {
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
                return EXITOSO;
            }
            c.close();
            db.close();
            return ERROR;
        } catch (Exception e) {
            Util.mostrarMensaje(a, e.toString());
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
            Util.mostrarMensaje(a, e.toString());
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
            Util.mostrarMensaje(a, e.toString());
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
            Util.mostrarMensaje(a, e.toString());
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
            Util.mostrarMensaje(a, e.toString());
        }
    }

    public void actualizarTablas(Activity a) {
        SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
        /** DROPEAMOS PARA QUE SE CREEN */
        db.execSQL(BaseHelper.getInstance(a).dropTable("tipo_inmueble"));
        db.execSQL(BaseHelper.getInstance(a).dropTable("destino_inmueble"));
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
        /** Y AHORA LAS VOLVEMOS A CREAR CON FORMATO DEFINITIVO */
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaTipoInmueble());
        db.execSQL(BaseHelper.getInstance(a).getSqlTablaDestinoInmueble());
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
        db.close();
    }

}
