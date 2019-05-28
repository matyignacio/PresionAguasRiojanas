package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.HistorialPuntosControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.HistorialPuntos;
import com.desarrollo.kuky.presionaguasriojanas.ui.adapters.lvaHistorial;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PREFS_NAME;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;

public class HistorialActivity extends AppCompatActivity {

    private ListView lvHistorial;
    private HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();
    private ArrayList<HistorialPuntos> historiales = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        // OBTENEMOS EL PUNTO DE PRESION
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int id = settings.getInt(Util.ID_PUNTO_PRESION_SHARED_PREFERENCE, 0);
        historiales = historialPuntosControlador.extraerTodosPorPunto(this, id);
        lvHistorial = findViewById(R.id.lvHistorial);
        lvaHistorial adaptador = new lvaHistorial(this, historiales);
        lvHistorial.setAdapter(adaptador);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /* LO QUE HACE CUANDO VUELVA*/
        abrirActivity(this, PuntoPresionActivity.class);
    }
}
