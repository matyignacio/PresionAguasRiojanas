package com.desarrollo.kuky.presionaguasriojanas.ui.reclamo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo.TramiteActivityControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.Tramite;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class ReclamoActivity extends AppCompatActivity {

    public static Tramite tramite;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamo);
        TextView tvTramite = findViewById(R.id.tvDatosCliente);
        TextView etCliente = findViewById(R.id.etCliente);
        TextView etUnidad = findViewById(R.id.etUnidad);
        TextView etBarrio = findViewById(R.id.etBarrio);
        TextView etCalle = findViewById(R.id.etCalle);
        TextView etNumero = findViewById(R.id.etNumeroCasa);
        TextView etDatosComplementarios = findViewById(R.id.etDatosComplementarios);
        TextView etDescripcion = findViewById(R.id.etDescripcion);
        Button bVerEnElMapa = findViewById(R.id.bVerEnElMapa);
        Button bResolver = findViewById(R.id.bResolver);
        /** SETEAMOS LOS TYPEFACES*/
        setPrimaryFontBold(this, tvTramite);
        setPrimaryFontBold(this, etCliente);
        setPrimaryFontBold(this, etUnidad);
        setPrimaryFontBold(this, etBarrio);
        setPrimaryFontBold(this, etCalle);
        setPrimaryFontBold(this, etNumero);
        setPrimaryFontBold(this, etDatosComplementarios);
        setPrimaryFontBold(this, etDescripcion);
        setPrimaryFontBold(this, bVerEnElMapa);
        setPrimaryFontBold(this, bResolver);
        /**************************/
        tvTramite.setText("Tramite: " + tramite.getTipoTramite().getTipo() + " - " + tramite.getReclamo().getNumeroTramite());
        etCliente.setText(tramite.getReclamo().getRazonSocial());
        etUnidad.setText(String.valueOf(tramite.getReclamo().getUnidad()));
        etBarrio.setText(tramite.getReclamo().getBarrio().getDesCodigo());
        etCalle.setText(tramite.getReclamo().getCalle());
        etNumero.setText(String.valueOf(tramite.getReclamo().getNumeroCasa()));
        etDatosComplementarios.setText(tramite.getReclamo().getDatoComplementario());
        etDescripcion.setText(tramite.getReclamo().getDescripcion());
        bVerEnElMapa.setOnClickListener(v -> {
            if (!tramite.getReclamo().getUbicacion().equals("null")) {
                abrirActivity(this, UbicacionReclamoActivity.class);
            } else {
                mostrarMensaje(this, "Este tramite aun no tiene asignada una ubicacion");
            }
        });
        bResolver.setOnClickListener(v -> Util.showDialog(this,
                R.layout.dialog_resoluciones,
                "Ver resoluciones",
                "Nueva resolucion",
                () -> {
                    TramiteActivityControlador tramiteActivityControlador = new TramiteActivityControlador();
                    tramiteActivityControlador.abrirResolucionesActivity(this, tramite);
                    return null;
                },
                () -> {
                    TramiteActivityControlador tramiteActivityControlador = new TramiteActivityControlador();
                    tramiteActivityControlador.abrirResolucionActivity(this, tramite.getMotivo().getMotivo());
                    return null;
                }
        ));
    }

    @Override
    public void onBackPressed() {
        /* LO QUE HACE CUANDO VUELVA*/
        abrirActivity(this, TramitesActivity.class);
    }
}
