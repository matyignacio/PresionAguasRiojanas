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

public class UsuarioControlador {
    private ProgressDialog pDialog;
    private UsuarioPorMailYClave usuarioPorMailYClave;

    private class UsuarioPorMailYClave extends AsyncTask<String, Float, String> {
        Activity a;
        String mail;
        String clave;

        @Override
        protected void onPreExecute() {
            LoginActivity.usuario = new Usuario();
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Estableciendo conexion...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        public UsuarioPorMailYClave(Activity a, String mail, String clave) {
            this.a = a;
            this.mail = mail;
            this.clave = clave;
        }

        @Override
        protected String doInBackground(String... strings) {
            Connection conn;
            PreparedStatement ps;
            ResultSet rs;
            try {
                conn = Conexion.GetConnection(a);
                String consultaSql = "SELECT * FROM usuarios WHERE mail LIKE '" + mail + "' AND clave LIKE '" + clave + "'";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                if (rs.next()) {
                    LoginActivity.usuario.setId(rs.getInt(1));
                    LoginActivity.usuario.setNombre(rs.getString(2));
                    LoginActivity.usuario.setMail(rs.getString(3));
                    LoginActivity.usuario.setClave(rs.getString(4));
                    guardarUsuario(a, LoginActivity.usuario);
                } else {
                    LoginActivity.usuario.setNombre(null);
                }
                rs.close();
                ps.close();
                conn.close();
                if (LoginActivity.usuario.getNombre() == null) {
                    return "El nombre de usuario es inexistente";
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
            return Util.EXITOSO;
        } catch (Exception e) {
            Util.mostrarMensaje(a, e.toString());
            return Util.ERROR;
        }
    }

    public int guardarUsuario(Activity a, Usuario u) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "INSERT INTO usuarios VALUES( '" +
                    u.getId() + "', '"
                    + u.getNombre() + "', '"
                    + u.getMail() + "', '"
                    + u.getClave() + "')";
            db.execSQL(sql);
            db.close();
            return Util.EXITOSO;
        } catch (Exception e) {
            Util.mostrarMensaje(a, e.toString());
            return Util.ERROR;
        }
    }

    public int eliminarUsuario(Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "DROP TABLE usuarios";
            db.execSQL(sql);
            db.execSQL(BaseHelper.getInstance(a).getSqlTablaUsuarios());
            db.close();
            return Util.EXITOSO;
        } catch (Exception e) {
            Util.mostrarMensaje(a, e.toString());
            return Util.ERROR;
        }
    }

    public int existeUsuario(Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM usuarios", null);
            if (c.moveToFirst()) {
                return Util.EXITOSO;
            }
            c.close();
            db.close();
            return Util.ERROR;
        } catch (Exception e) {
            Util.mostrarMensaje(a, e.toString());
            return Util.ERROR;
        }
    }

}
