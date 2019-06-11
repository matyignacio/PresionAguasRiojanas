package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.PuntoPresionControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.TipoPuntoControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.TipoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.TipoPunto;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.validarCampos;

public class NuevoPuntoActivity extends AppCompatActivity {

    private EditText etCircuito, etBarrio, etCalle1, etCalle2, etPresion;
    private Spinner sTipoPunto;
    private Button bEnviarNuevoPunto;
    private ArrayList<EditText> inputs = new ArrayList<>();
    private ArrayList<TipoPunto> tipoPuntos = new ArrayList<>();
    private TipoPuntoControlador tipoPuntoControlador = new TipoPuntoControlador();
    private PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
    private PuntoPresion puntoPresion = new PuntoPresion();
    private TipoPunto tipoPunto = new TipoPunto();
    private TipoPresion tipoPresion = new TipoPresion();
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private String provider_info;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private LatLng posicion;

    private LocationManager locationManager;
    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_punto);
        tipoPuntos = tipoPuntoControlador.extraerTodos(this);
        etCircuito = findViewById(R.id.etCircuito);
        etBarrio = findViewById(R.id.etBarrio);
        etCalle1 = findViewById(R.id.etCalle1);
        etCalle2 = findViewById(R.id.etCalle2);
        etPresion = findViewById(R.id.etPresion);
        sTipoPunto = findViewById(R.id.sTipoPunto);
        cargarSpinnerTipoPunto();
        inputs.add(etCircuito);
        inputs.add(etBarrio);
        inputs.add(etCalle1);
        // inputs.add(etCalle2); A ESTE LO COMENTO PORQUE NO ES OBLIGATORIO EL CAMPO
        inputs.add(etPresion);
        bEnviarNuevoPunto = findViewById(R.id.bEnviarNuevoPunto);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                posicion = new LatLng(location.getLatitude(), location.getLongitude());
                etCalle1.setText(String.valueOf(posicion.latitude));
                etCalle2.setText(String.valueOf(posicion.longitude));
                locationManager.removeUpdates(listener);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
//        bEnviarNuevoPunto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDialogGuardar(NuevoPuntoActivity.this);
//            }
//        });
//        if (validaPermisos()) {
//            locationManager.requestLocationUpdates("network", 5000, 0, listener);
//            bEnviarNuevoPunto.setEnabled(true);
//            //gpsTracker.getLocation();
////            if (gpsTracker.getIsGPSTrackingEnabled()) {
////                bEnviarNuevoPunto.setEnabled(true);
////            } else {
////                gpsTracker.showSettingsAlert();
////            }
//        } else {
//            bEnviarNuevoPunto.setEnabled(false);
//        }
        configure_button();
    }

    @Override
    public void onBackPressed() {
        /* LO QUE HACE CUANDO VUELVA*/
        abrirActivity(this, MapActivity.class);
    }

    private int insertarPunto() {
        try {
            // INICIALIZAMOS LO Q VAMOS A NECESITAR

            // OBTENEMOS LA UBICACION

            // CARGAMOS EL OBJETO historialPuntos
            puntoPresion.setCircuito(Integer.parseInt(etCircuito.getText().toString()));
            puntoPresion.setBarrio(etBarrio.getText().toString());
            puntoPresion.setCalle1(etCalle1.getText().toString());
            puntoPresion.setCalle2(etCalle2.getText().toString());
//            puntoPresion.setLatitud(location.getLatitude());
//            puntoPresion.setLongitud(location.getLongitude());
            puntoPresion.setPresion(Float.parseFloat(etPresion.getText().toString()));
            tipoPresion.setId(1);
            puntoPresion.setTipoPresion(tipoPresion);
            // AL TIPO PUNTO YA LO DEFINIMOS EN LA SELECCION DEL DROPDOWNLIST
            puntoPresion.setTipoPunto(tipoPunto);
            // INSERTAMOS EL NUEVO REGISTRO
            puntoPresionControlador.insertar(puntoPresion, this);
            // Y DETENEMOS EL USO DEL GPS

            return EXITOSO;
        } catch (Exception e) {
            return ERROR;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                configure_button();
                break;
            }
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        bEnviarNuevoPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                provider_info = LocationManager.GPS_PROVIDER;

                if (!provider_info.isEmpty()) {
                    locationManager.requestLocationUpdates(
                            provider_info,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            listener
                    );
                }

            }
        });
    }

    public void showDialogGuardar(final Activity a) {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(a);
        View promptView = layoutInflater.inflate(R.layout.dialog_guardar, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
        alertDialogBuilder.setView(promptView);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Si, Guardar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (validarCampos(NuevoPuntoActivity.this, inputs) == EXITOSO) {
                            if (insertarPunto() == EXITOSO) {
                                mostrarMensaje(NuevoPuntoActivity.this, "Se agrego el nuevo punto");
                                abrirActivity(NuevoPuntoActivity.this, MapActivity.class);
                            } else {
                                mostrarMensaje(NuevoPuntoActivity.this, "Ocurrio un error al intentar guardar");
                            }
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void cargarSpinnerTipoPunto() {
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < tipoPuntos.size(); i++) {
            labels.add(tipoPuntos.get(i).getNombre());
        }
        /******************************************************************************************/
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, labels);
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sTipoPunto.setAdapter(spinnerAdapter);
        sTipoPunto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tipoPunto.setId(i + 1);
//                mostrarMensaje(NuevoPuntoActivity.this, String.valueOf(tipoPunto.getId()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                tipoPunto.setId(0);
//                mostrarMensaje(NuevoPuntoActivity.this, String.valueOf(tipoPunto.getId()));
            }
        });
    }
}
