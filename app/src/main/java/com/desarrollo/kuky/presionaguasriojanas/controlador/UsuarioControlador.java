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
    private UsuarioPorMailYClave usuarioPorMailYClave;

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

        public UsuarioPorMailYClave(Activity a, String eMail, String clave) {
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
                conn = Conexion.GetConnection(a);
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
                    LoginActivity.usuario.setBandera_modulo_presion(0);
                    LoginActivity.usuario.setBandera_sync_modulo_presion(0);
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
            /*////////////////////////////////////////////////////////////////////////////////////*/
            // ACA NO MUESTRO NADA, LO USE PARA DEPURAR NOMAS. A LOS MENSAJES DE RESPUESTA...
            // ... LOS MUESTRO EN LA ASYNCTASK DE LOGINACTIVITY
            /*if (s.equals("")) {
                Toast.makeText(a, s, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(a, s, Toast.LENGTH_SHORT).show();
            }*/
            /*////////////////////////////////////////////////////////////////////////////////////*/
        }
    }

    public int extraerPorMailYClave(Activity a, String mail, String clave) {
        try {
            usuarioPorMailYClave = new UsuarioPorMailYClave(a, mail, clave);
            usuarioPorMailYClave.execute();
            return EXITOSO;
        } catch (Exception e) {
            Util.mostrarMensaje(a, e.toString());
            return ERROR;
        }
    }

    public int guardarUsuario(Activity a, Usuario u) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "INSERT INTO susuario VALUES( '" +
                    u.getId() + "', '"
                    + u.getNombre() + "', '"
                    + u.getEmail() + "', '"
                    + u.getClave() + "', '"
                    + u.getTipo() + "', '"
                    + u.getActivo() + "', '"
                    + u.getBandera_modulo_presion() + "', '"
                    + u.getBandera_sync_modulo_presion() + "')";
            db.execSQL(sql);
            db.close();
            return EXITOSO;
        } catch (Exception e) {
            Util.mostrarMensaje(a, e.toString());
            return ERROR;
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
            SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM susuario", null);
            if (c.moveToFirst()) {
                LoginActivity.usuario.setId(c.getString(0));
                LoginActivity.usuario.setNombre(c.getString(1));
                LoginActivity.usuario.setTipo(c.getString(4));
                LoginActivity.usuario.setActivo(c.getString(5));
                LoginActivity.usuario.setBandera_modulo_presion(c.getInt(6));
                LoginActivity.usuario.setBandera_sync_modulo_presion(c.getInt(7));
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

    public int editarBanderaModuloPresion(Activity a, int bandera) {
        try {
            SQLiteDatabase bh = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE susuario SET  'bandera_modulo_presion' =" +
                    bandera;
            bh.execSQL(sql);
            bh.close();
            LoginActivity.usuario.setBandera_modulo_presion(bandera);
            return EXITOSO;
        } catch (Exception e) {
            Util.mostrarMensaje(a, e.toString());
            return ERROR;
        }
    }

    public int editarBanderaSyncModuloPresion(Activity a, int bandera) {
        try {
            SQLiteDatabase bh = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE susuario SET 'bandera_sync_modulo_presion' = " +
                    bandera;
            bh.execSQL(sql);
            bh.close();
            LoginActivity.usuario.setBandera_sync_modulo_presion(bandera);
            return EXITOSO;
        } catch (Exception e) {
            Util.mostrarMensaje(a, e.toString());
            return ERROR;
        }
    }


}
