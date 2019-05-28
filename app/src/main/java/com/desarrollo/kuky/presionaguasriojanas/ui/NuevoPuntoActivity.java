package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.PuntoPresionControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.TipoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.TipoPunto;
import com.desarrollo.kuky.presionaguasriojanas.util.GPSTracker;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class NuevoPuntoActivity extends AppCompatActivity {

    EditText etCircuito, etBarrio, etCalle1, etCalle2, etPresion;
    Button bEnviarNuevoPunto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_punto);
        etCircuito = findViewById(R.id.etCircuito);
        etBarrio = findViewById(R.id.etBarrio);
        etCalle1 = findViewById(R.id.etCalle1);
        etCalle2 = findViewById(R.id.etCalle2);
        etPresion = findViewById(R.id.etPresion);
        bEnviarNuevoPunto = findViewById(R.id.bEnviarNuevoPunto);
        bEnviarNuevoPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCircuito.getText().toString().equals("") ||
                        etCalle1.getText().toString().equals("") ||
                        etPresion.getText().toString().equals("") ||
                        etBarrio.getText().toString().equals("")) {
                    mostrarMensaje(NuevoPuntoActivity.this, "Debe llenar los campos");
                } else {
                    if (insertarPunto() == EXITOSO) {
                        mostrarMensaje(NuevoPuntoActivity.this, "Se agrego el nuevo punto");
                        abrirActivity(NuevoPuntoActivity.this, MapActivity.class);
                    } else {
                        mostrarMensaje(NuevoPuntoActivity.this, "Ocurrio un error al agregar el punto");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /* LO QUE HACE CUANDO VUELVA*/
        abrirActivity(this, MapActivity.class);
    }

    private int insertarPunto() {
        try {
            // INICIALIZAMOS LO Q VAMOS A NECESITAR
            PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
            PuntoPresion puntoPresion = new PuntoPresion();
            TipoPunto tipoPunto = new TipoPunto();
            TipoPresion tipoPresion = new TipoPresion();
            GPSTracker gpsTracker = new GPSTracker(this);
            // OBTENEMOS LA UBICACION
            gpsTracker.getLocation();
            gpsTracker.updateGPSCoordinates();
            // CARGAMOS EL OBJETO historialPuntos
            puntoPresion.setCircuito(Integer.parseInt(etCircuito.getText().toString()));
            puntoPresion.setBarrio(etBarrio.getText().toString());
            puntoPresion.setCalle1(etCalle1.getText().toString());
            puntoPresion.setCalle2(etCalle2.getText().toString());
            puntoPresion.setLatitud(gpsTracker.getLatitude());
            puntoPresion.setLongitud(gpsTracker.getLongitude());
            puntoPresion.setPresion(Float.parseFloat(etPresion.getText().toString()));
            tipoPresion.setId(1);
            puntoPresion.setTipoPresion(tipoPresion);
            tipoPunto.setId(1);
            puntoPresion.setTipoPunto(tipoPunto);
            // INSERTAMOS EL NUEVO REGISTRO
            puntoPresionControlador.insertar(puntoPresion, this);
            // Y DETENEMOS EL USO DEL GPS
            gpsTracker.stopUsingGPS();
            return EXITOSO;
        } catch (Exception e) {
            return ERROR;
        }
    }
}
