package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.Editable;
import android.widget.Toast;

import com.desarrollo.kuky.presionaguasriojanas.LoginActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioControlador {
    private ProgressDialog pDialog;
    private UsuarioPorMailYClave usuarioPorMailYClave;

    private class UsuarioPorMailYClave extends AsyncTask<String, Float, String> {
        Activity a;
        Editable mail;
        Editable clave;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Estableciendo conexion...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        public UsuarioPorMailYClave(Activity a, Editable mail, Editable clave) {
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
                conn = (Connection) Conexion.GetConnection(a);
                String consultaSql = "SELECT * FROM usuarios where mail like " + mail + " AND clave like " + clave ;
                ps = (PreparedStatement) conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                if (rs.next()) {
                    LoginActivity.usuario.setId(rs.getInt(1));
                    LoginActivity.usuario.setNombre(rs.getString(2));
                    LoginActivity.usuario.setMail(rs.getString(3));
                    LoginActivity.usuario.setClave(rs.getString(4));
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
            if (s.equals("")) {
                //Toast.makeText(a, s, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(a, s.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void extraerPorMailYClave(Activity a, Editable mail, Editable clave) {
        usuarioPorMailYClave = new UsuarioPorMailYClave(a, mail, clave);
        usuarioPorMailYClave.execute();
    }

}
