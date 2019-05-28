package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.HistorialPuntosControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.HistorialPuntos;
import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.util.GPSTracker;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PREFS_NAME;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class NuevaPresionActivity extends AppCompatActivity {
    private EditText etPresion;
    private Button bEnviarMedicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_presion);
        etPresion = findViewById(R.id.etPresion);
        bEnviarMedicion = findViewById(R.id.bEnviarMediicion);
        bEnviarMedicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etPresion.getText().toString().equals("")) {
                    if (insertarMedicion(Float.parseFloat(etPresion.getText().toString())) == EXITOSO) {
                        mostrarMensaje(NuevaPresionActivity.this, "Se ingreso con exito");
                        abrirActivity(NuevaPresionActivity.this, PuntoPresionActivity.class);
                    } else {
                        mostrarMensaje(NuevaPresionActivity.this, "Ocurrio un error durante el ingreso");
                    }
                } else {
                    mostrarMensaje(NuevaPresionActivity.this, "Debe ingresar una presion");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /* LO QUE HACE CUANDO VUELVA*/
        abrirActivity(this, PuntoPresionActivity.class);
    }

    private int insertarMedicion(Float presion) {
        try {
            // INICIALIZAMOS LO Q VAMOS A NECESITAR
            HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();
            HistorialPuntos historialPuntos = new HistorialPuntos();
            PuntoPresion puntoPresion = new PuntoPresion();
            GPSTracker gpsTracker = new GPSTracker(this);
            // CAPTURAMOS EL ID DEL PUNTO, DESDE LAS SHARED PREFERENCES
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            int id = settings.getInt(Util.ID_PUNTO_PRESION_SHARED_PREFERENCE, 0);
            puntoPresion.setId(id);
            // OBTENEMOS LA UBICACION
            gpsTracker.getLocation();
            gpsTracker.updateGPSCoordinates();
            // CARGAMOS EL OBJETO historialPuntos
            historialPuntos.setLatitud(gpsTracker.getLatitude());
            historialPuntos.setLongitud(gpsTracker.getLongitude());
            historialPuntos.setPresion(presion);
            historialPuntos.setPuntoPresion(puntoPresion);
            // INSERTAMOS EL NUEVO REGISTRO
            historialPuntosControlador.insertar(historialPuntos, this);
            // Y DETENEMOS EL USO DEL GPS
            gpsTracker.stopUsingGPS();
            return EXITOSO;
        } catch (Exception e) {
            return ERROR;
        }
    }
}
