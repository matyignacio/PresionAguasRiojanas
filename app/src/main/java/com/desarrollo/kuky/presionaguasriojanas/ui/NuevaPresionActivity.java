package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.HistorialPuntosControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.HistorialPuntos;
import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PREFS_NAME;

public class NuevaPresionActivity extends AppCompatActivity {
    EditText etPresion;
    Button bEnviarMedicion;

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
                        Toast.makeText(NuevaPresionActivity.this, "Se ingreso con exito", Toast.LENGTH_SHORT).show();
                        Util.abrirActivity(NuevaPresionActivity.this, PuntoPresionActivity.class);
                        NuevaPresionActivity.this.finish();
                    } else {
                        Toast.makeText(NuevaPresionActivity.this, "Ocurrio un error durante el ingreso", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NuevaPresionActivity.this, "Debe ingresar una presion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /* LO QUE HACE CUANDO VUELVA*/
        Util.abrirActivity(this, PuntoPresionActivity.class);
        this.finish();
    }

    private int insertarMedicion(Float presion) {
        try {
            // INICIALIZAMOS LO Q VAMOS A NECESITAR
            HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();
            HistorialPuntos historialPuntos = new HistorialPuntos();
            PuntoPresion puntoPresion = new PuntoPresion();
            // CAPTURAMOS EL ID DEL PUNTO, DESDE LAS SHARED PREFERENCES
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            int id = settings.getInt(Util.ID_PUNTO_PRESION_SHARED_PREFERENCE, 0);
            puntoPresion.setId(id);
            // CARGAMOS EL OBJETO historialPuntos
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Location location = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(criteria, false));
                historialPuntos.setLatitud(location.getLatitude());
                historialPuntos.setLongitud(location.getLongitude());
            }
            Toast.makeText(NuevaPresionActivity.this, historialPuntos.getLongitud().toString(), Toast.LENGTH_SHORT).show();
            historialPuntos.setPresion(presion);
            historialPuntos.setPuntoPresion(puntoPresion);
            // INSERTAMOS EL NUEVO REGISTRO
            historialPuntosControlador.insertar(historialPuntos, this);
            return Util.EXITOSO;
        } catch (Exception e) {
            return Util.ERROR;
        }
    }
}
