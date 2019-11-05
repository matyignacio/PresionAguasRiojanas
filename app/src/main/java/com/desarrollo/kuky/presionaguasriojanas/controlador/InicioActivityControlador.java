package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.desarrollo.kuky.presionaguasriojanas.ui.ErrorActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.presion.MapActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.VOLLEY_HOST;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class InicioActivityControlador {

    public void abrirMapActivity(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        displayProgressBar(a, progressBar, tvProgressBar);
        tvProgressBar.setText("Revisando circuitos...");
        ArrayList<Integer> circuitos = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.POST, VOLLEY_HOST + "permisos_circuitos.php", response -> {
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
                abrirActivity(a, MapActivity.class);
            } else {
                Toast.makeText(a, "No tiene ningun circuito habilitado", Toast.LENGTH_SHORT).show();
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
                params.put("user", LoginActivity.usuario.getId());
                return params;
            }
        };
        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(a).addToRequestQueue(request);
    }
}
