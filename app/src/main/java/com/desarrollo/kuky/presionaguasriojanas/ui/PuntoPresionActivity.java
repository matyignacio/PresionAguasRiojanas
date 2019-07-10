package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.PuntoPresionControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ID_PUNTO_PRESION_SHARED_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.USUARIO_PUNTO_PRESION_SHARED_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.getPreference;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class PuntoPresionActivity extends AppCompatActivity {

    PuntoPresion puntoPresion = new PuntoPresion();
    PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
    // UI References
    TextView /*etCircuito,*/ etTipoUnidad, etUnidad, etBarrio, etCalle1, etCalle2, etPresion;
    Button bHistorialPunto, bNuevaMedicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punto_presion);
        // CAPTURAMOS LOS ELEMENTOS
        //etCircuito = findViewById(R.id.etCircuito);
        etTipoUnidad = findViewById(R.id.etTipoUnidad);
        etUnidad = findViewById(R.id.etUnidad);
        etBarrio = findViewById(R.id.etBarrio);
        etCalle1 = findViewById(R.id.etCalle1);
        etCalle2 = findViewById(R.id.etCalle2);
        etPresion = findViewById(R.id.etPresion);
        bNuevaMedicion = findViewById(R.id.bNuevaMedicion);
        bHistorialPunto = findViewById(R.id.bHistorialPunto);
        // OBTENEMOS EL PUNTO DE PRESION
        int id = getPreference(this, ID_PUNTO_PRESION_SHARED_PREFERENCE, 0);
        String usuario = getPreference(this, USUARIO_PUNTO_PRESION_SHARED_PREFERENCE, "");
        //mostrarMensaje(this, "id: " + id + ", usuario: " + usuario);
        puntoPresion = puntoPresionControlador.extraerPorIdYUsuario(this, id, usuario);
        //etCircuito.setText(puntoPresion.getCircuito().toString());
        if (puntoPresion.getUnidad() > 0) {
            etTipoUnidad.setText(puntoPresion.getTipoUnidad());
            etUnidad.setText(String.valueOf(puntoPresion.getUnidad()));
        } else {
            etTipoUnidad.setText("");
            etUnidad.setText("Sin nº de unidad");
        }
        etBarrio.setText(puntoPresion.getBarrio());
        /** SETEAMOS LOS TYPEFACES*/
        //setPrimaryFontBold(this, etCircuito);
        setPrimaryFontBold(this, etTipoUnidad);
        setPrimaryFontBold(this, etUnidad);
        setPrimaryFontBold(this, etBarrio);
        setPrimaryFontBold(this, etCalle1);
        setPrimaryFontBold(this, etCalle2);
        setPrimaryFontBold(this, etPresion);
        setPrimaryFontBold(this, bHistorialPunto);
        setPrimaryFontBold(this, bNuevaMedicion);
        /**************************/
        etCalle1.setText(puntoPresion.getCalle1());
        etCalle2.setText(puntoPresion.getCalle2());
        etPresion.setText(puntoPresion.getPresion().toString() + " mca");
        bNuevaMedicion.setOnClickListener(view -> abrirActivity(PuntoPresionActivity.this, NuevaPresionActivity.class));
        bHistorialPunto.setOnClickListener(view -> abrirActivity(PuntoPresionActivity.this, HistorialActivity.class));

    }

    @Override
    public void onBackPressed() {
        /* LO QUE HACE CUANDO VUELVA*/
        abrirActivity(PuntoPresionActivity.this, MapActivity.class);
    }
}
