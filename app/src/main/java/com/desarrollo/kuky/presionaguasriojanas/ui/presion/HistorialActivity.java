package com.desarrollo.kuky.presionaguasriojanas.ui.presion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.presion.HistorialPuntosControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.HistorialPuntos;
import com.desarrollo.kuky.presionaguasriojanas.ui.adapters.lvaHistorial;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ID_PUNTO_PRESION_SHARED_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.USUARIO_PUNTO_PRESION_SHARED_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.getPreference;

public class HistorialActivity extends AppCompatActivity {

    private HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        // OBTENEMOS EL PUNTO DE PRESION
        int id = getPreference(this, ID_PUNTO_PRESION_SHARED_PREFERENCE, 0);
        String usuario = getPreference(this, USUARIO_PUNTO_PRESION_SHARED_PREFERENCE, "");
        ArrayList<HistorialPuntos> historiales = historialPuntosControlador.extraerTodosPorPunto(this, id, usuario);
        ListView lvHistorial = findViewById(R.id.lvHistorial);
        lvaHistorial adaptador = new lvaHistorial(this, historiales);
        lvHistorial.setAdapter(adaptador);
    }

    @Override
    public void onBackPressed() {
        /* LO QUE HACE CUANDO VUELVA*/
        abrirActivity(this, PuntoPresionActivity.class);
    }
}
