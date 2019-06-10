package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.PuntoPresionControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PREFS_NAME;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;

public class PuntoPresionActivity extends AppCompatActivity {

    PuntoPresion puntoPresion = new PuntoPresion();
    PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
    // UI References
    TextView etCircuito, etBarrio, etCalle1, etCalle2, etPresion;
    Button bHistorialPunto, bNuevaMedicion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punto_presion);
        // CAPTURAMOS LOS ELEMENTOS
        etCircuito = findViewById(R.id.etCircuito);
        etBarrio = findViewById(R.id.etBarrio);
        etCalle1 = findViewById(R.id.etCalle1);
        etCalle2 = findViewById(R.id.etCalle2);
        etPresion = findViewById(R.id.etPresion);
        bNuevaMedicion = findViewById(R.id.bNuevaMedicion);
        bHistorialPunto = findViewById(R.id.bHistorialPunto);
        // OBTENEMOS EL PUNTO DE PRESION
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int id = settings.getInt(Util.ID_PUNTO_PRESION_SHARED_PREFERENCE, 0);
        puntoPresion = puntoPresionControlador.extraerPorId(this, id);
        etCircuito.setText(puntoPresion.getCircuito().toString());
        etBarrio.setText(puntoPresion.getBarrio());
        etCalle1.setText(puntoPresion.getCalle1());
        etCalle2.setText(puntoPresion.getCalle2());
        etPresion.setText(puntoPresion.getPresion().toString());
        bNuevaMedicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirActivity(PuntoPresionActivity.this, NuevaPresionActivity.class);
            }
        });
        bHistorialPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirActivity(PuntoPresionActivity.this, HistorialActivity.class);
            }
        });

    }

    @Override
    public void onBackPressed() {
        /* LO QUE HACE CUANDO VUELVA*/
        abrirActivity(PuntoPresionActivity.this, MapActivity.class);
    }
}
