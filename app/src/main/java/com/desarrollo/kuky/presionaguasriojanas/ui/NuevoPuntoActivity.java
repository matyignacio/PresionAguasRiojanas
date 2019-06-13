package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.validarCampos;

public class NuevoPuntoActivity extends AppCompatActivity {

    private static final String TAG = NuevoPuntoActivity.class.getSimpleName();
    @BindView(R.id.bEnviarNuevoPunto)
    Button bEnviarNuevoPunto;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;


    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    // boolean flag to toggle the ui
    public Boolean mRequestingLocationUpdates;

    private EditText etCircuito, etBarrio, etCalle1, etCalle2, etPresion;
    private Spinner sTipoPunto;
    private ArrayList<EditText> inputs = new ArrayList<>();
    private ArrayList<TipoPunto> tipoPuntos = new ArrayList<>();
    private TipoPuntoControlador tipoPuntoControlador = new TipoPuntoControlador();
    private TipoPunto tipoPunto = new TipoPunto();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_punto);
        ButterKnife.bind(this);
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
        configure_button();
    }

    @Override
    public void onBackPressed() {
        /* LO QUE HACE CUANDO VUELVA*/
        stopLocationUpdates();
        abrirActivity(this, MapActivity.class);
    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();

                //updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        startLocationUpdates();
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
        init();
        bEnviarNuevoPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogGuardar(NuevoPuntoActivity.this);
            }
        });
    }

    private void insertarPunto() {
        try {
            PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
            PuntoPresion puntoPresion = new PuntoPresion();
            TipoPresion tipoPresion = new TipoPresion();
            // INICIALIZAMOS LO Q VAMOS A NECESITAR
            puntoPresion.setCircuito(Integer.parseInt(etCircuito.getText().toString()));
            puntoPresion.setBarrio(etBarrio.getText().toString());
            puntoPresion.setCalle1(etCalle1.getText().toString());
            puntoPresion.setCalle2(etCalle2.getText().toString());
            puntoPresion.setLatitud(mCurrentLocation.getLatitude());
            puntoPresion.setLongitud(mCurrentLocation.getLongitude());
            puntoPresion.setPresion(Float.parseFloat(etPresion.getText().toString()));
            tipoPresion.setId(1);
            puntoPresion.setTipoPresion(tipoPresion);
            // AL TIPO PUNTO YA LO DEFINIMOS EN LA SELECCION DEL DROPDOWNLIST
            puntoPresion.setTipoPunto(tipoPunto);
            // INSERTAMOS EL NUEVO REGISTRO
            puntoPresionControlador.insertar(puntoPresion, this);
            mostrarMensaje(NuevoPuntoActivity.this, "Se ingreso con exito");
            // Y DETENEMOS EL USO DEL GPS
            stopLocationUpdates();
            abrirActivity(NuevoPuntoActivity.this, MapActivity.class);
        } catch (Exception e) {
            mostrarMensaje(NuevoPuntoActivity.this, "Ocurrio un error al intentar guardar");
        }
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "Empezo a obtener la ubicacion!");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(NuevoPuntoActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                mostrarMensaje(getApplicationContext(), errorMessage);
                        }
                    }
                });
    }

    private void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "Se detuvo la busqueda de ubicacion!");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    public void showDialogGuardar(final Activity a) {
        if (mCurrentLocation != null) {
            if (validarCampos(NuevoPuntoActivity.this, inputs) == EXITOSO) {
// get prompts.xml view
                LayoutInflater layoutInflater = LayoutInflater.from(a);
                View promptView = layoutInflater.inflate(R.layout.dialog_guardar, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
                alertDialogBuilder.setView(promptView);
                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Si, Guardar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                insertarPunto();
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
            } else {
                /**ESTE NO MUESTRA NINGUN MENSAJE, PORQUE LO HACE EL METODO GENERICO EN UTIL*/
            }
        } else {
            mostrarMensaje(NuevoPuntoActivity.this, "Debe activar el GPS");
            mostrarMensaje(NuevoPuntoActivity.this, "Una vez activo, abra nuevamente esta pantalla");
        }

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
