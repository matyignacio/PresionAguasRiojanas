package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.presion.MapActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_CARGAR_CIRCUITOS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.checkConnection;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class InicioActivityControlador {

    private ProgressDialog pDialog;

    @SuppressLint("StaticFieldLeak")
    private class AbrirMapActivityTask extends AsyncTask<String, Float, String> {
        String RETURN = "ERROR";
        Activity a;
        ArrayList<Integer> circuitos = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Cargando circuitos...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        AbrirMapActivityTask(Activity a) {
            this.a = a;
        }

        @Override
        protected String doInBackground(String... strings) {
            Connection conn;
            PreparedStatement ps;
            ResultSet rs;
            try {
                conn = Conexion.GetConnection();
                String consultaSql = "SELECT circuito " +
                        " FROM permisos_circuitos" +
                        " WHERE id_usuario = '" + LoginActivity.usuario.getId() + "'";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                while (rs.next()) {
                    circuitos.add(rs.getInt(1));
                    RETURN = "";
                }
                rs.close();
                ps.close();
                conn.close();
                return RETURN;
            } catch (SQLException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s.equals("")) {
                LoginActivity.usuario.setCircuitos(circuitos);
                abrirActivity(a, MapActivity.class);
            } else {
                setPreference(a, ERROR_PREFERENCE, ERROR_CARGAR_CIRCUITOS);
                mostrarMensajeLog(a, ERROR_CARGAR_CIRCUITOS);
                abrirActivity(a, ErrorActivity.class);
            }
        }
    }

    public void abrirMapActivity(Activity a) {
        checkConnection(a, () -> {
            try {
                AbrirMapActivityTask abrirMapActivityTask = new AbrirMapActivityTask(a);
                abrirMapActivityTask.execute();
            } catch (Exception e) {
                mostrarMensaje(a, e.toString());
            }
            return null;
        });
    }
}
