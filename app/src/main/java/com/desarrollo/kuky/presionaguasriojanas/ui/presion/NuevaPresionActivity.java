package com.desarrollo.kuky.presionaguasriojanas.ui.presion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.presion.HistorialPuntosControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.Usuario;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.HistorialPuntos;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.presion.fragments.CalidadNuevaMedicion;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;
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
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ID_PUNTO_PRESION_SHARED_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.REQUEST_CHECK_SETTINGS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.USUARIO_PUNTO_PRESION_SHARED_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirFragmento;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.getPreference;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.validarCampos;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.validarPresion;

public class NuevaPresionActivity extends AppCompatActivity {
    private static final String TAG = NuevaPresionActivity.class.getSimpleName();
    private CalidadNuevaMedicion calidadNuevaMedicion = new CalidadNuevaMedicion();
    public static CheckBox cbCalidad;
    public static Button bEnviarMedicion;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    // boolean flag to toggle the ui
    public Boolean mRequestingLocationUpdates;
    private EditText etPresion;
    private ArrayList<EditText> inputs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_presion);
        cbCalidad = findViewById(R.id.cbCalidad);
        bEnviarMedicion = findViewById(R.id.bEnviarMediicion);
        etPresion = findViewById(R.id.etPresion);
        inputs.add(etPresion);
        /** SETEAMOS LOS TYPEFACES*/
        setPrimaryFontBold(this, etPresion);
        setPrimaryFontBold(this, bEnviarMedicion);
        /**************************/
        cbCalidad.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                abrirFragmento(this, R.id.rlNuevaMedicion, calidadNuevaMedicion);
                setEnabledInputs(false);
            } else {
                setEnabledInputs(true);
            }
        });
        request_permissions();
    }

    @Override
    public void onBackPressed() {
        /* LO QUE HACE CUANDO VUELVA*/
        if (calidadNuevaMedicion.isVisible()) {
            cbCalidad.setChecked(false);
            Util.cerrarFragmento(this, calidadNuevaMedicion);
        } else {
            stopLocationUpdates();
            abrirActivity(this, PuntoPresionActivity.class);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startLocationUpdates();
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
                request_permissions();
                break;
            }
        }
    }

    private void request_permissions() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        init();

        bEnviarMedicion.setOnClickListener(view -> {
            if (mCurrentLocation != null) {
                if (validarCampos(NuevaPresionActivity.this, inputs) == EXITOSO) {
                    Util.showDialog(NuevaPresionActivity.this,
                            R.layout.dialog_guardar,
                            "Si, Guardar",
                            () -> {
                                insertarMedicion();
                                return null;
                            }
                    );
                } /**else {
                 ESTE ELSE NO MUESTRA NINGUN MENSAJE, PORQUE LO HACE EL METODO GENERICO EN UTIL
                 }*/
            } else {
                mostrarMensaje(NuevaPresionActivity.this, "Debe activar el GPS. Una vez activo, abra nuevamente esta pantalla");
            }
        });
    }

    private void insertarMedicion() {
        if (validarPresion(this, etPresion) == EXITOSO) {
            try {
                // INICIALIZAMOS LO Q VAMOS A NECESITAR
                HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();
                HistorialPuntos historialPuntos = new HistorialPuntos();
                PuntoPresion puntoPresion = new PuntoPresion();
                Usuario uPunto = new Usuario();
                // CAPTURAMOS EL ID DEL PUNTO, DESDE LAS SHARED PREFERENCES
                int id = getPreference(NuevaPresionActivity.this,
                        ID_PUNTO_PRESION_SHARED_PREFERENCE,
                        0);
                puntoPresion.setId(id);
                String usuario = getPreference(NuevaPresionActivity.this,
                        USUARIO_PUNTO_PRESION_SHARED_PREFERENCE,
                        "");
                uPunto.setId(usuario);
                puntoPresion.setUsuario(uPunto);
                // CARGAMOS EL OBJETO historialPuntos
                historialPuntos.setLatitud(mCurrentLocation.getLatitude());
                historialPuntos.setLongitud(mCurrentLocation.getLongitude());
                historialPuntos.setPresion(Float.parseFloat(etPresion.getText().toString()));
                historialPuntos.setPuntoPresion(puntoPresion);
                historialPuntos.setCloro(calidadNuevaMedicion.calidad.getCloro());
                historialPuntos.setMuestra(calidadNuevaMedicion.calidad.getMuestra());
                historialPuntos.setUsuario(LoginActivity.usuario);
                // INSERTAMOS EL NUEVO REGISTRO
                historialPuntosControlador.insertar(historialPuntos, this);
                mostrarMensaje(NuevaPresionActivity.this, "Se ingreso con exito");
                // Y DETENEMOS EL USO DEL GPS
                stopLocationUpdates();
                abrirActivity(NuevaPresionActivity.this, PuntoPresionActivity.class);
            } catch (Exception e) {
                mostrarMensaje(NuevaPresionActivity.this, "Ocurrio un error al intentar guardar");
            }
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
                .addOnFailureListener(this, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings ");
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(NuevaPresionActivity.this, REQUEST_CHECK_SETTINGS);
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
                });
    }

    private void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, task -> Log.e(TAG, "Se detuvo la busqueda de ubicacion!"));
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

    private void setEnabledInputs(boolean enabled) {
        cbCalidad.setEnabled(enabled);
        etPresion.setEnabled(enabled);
        bEnviarMedicion.setEnabled(enabled);
    }
}
