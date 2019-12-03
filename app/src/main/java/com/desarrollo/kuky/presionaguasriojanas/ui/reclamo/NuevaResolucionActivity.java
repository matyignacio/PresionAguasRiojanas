package com.desarrollo.kuky.presionaguasriojanas.ui.reclamo;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo.ReclamoControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo.ResolucionReclamoControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo.TramiteControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.ResolucionReclamo;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.TipoResolucion;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.presion.NuevoPuntoActivity;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.ui.reclamo.ReclamoActivity.tramite;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.REQUEST_CHECK_SETTINGS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.cargarSpinner;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFont;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.validarCampos;

public class NuevaResolucionActivity extends AppCompatActivity {
    private static final String TAG = NuevoPuntoActivity.class.getSimpleName();
    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    // boolean flag to toggle the ui
    public Boolean mRequestingLocationUpdates;

    public static TipoResolucion.TipoResolucionSpinner tipoResolucionSpinner = new TipoResolucion().new TipoResolucionSpinner();
    Spinner sTipoResolucion;
    Switch swActualizarUbicacion;
    EditText etObservaciones;
    private ArrayList<EditText> inputs = new ArrayList<>();
    Button bEnviarResolucion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_resolucion);
        sTipoResolucion = findViewById(R.id.sTipoResolucion);
        cargarSpinner(sTipoResolucion,
                this,
                0,
                tipoResolucionSpinner.getLabelsResoluciones(),
                () -> null,
                () -> null);
        etObservaciones = findViewById(R.id.etObservaciones);
        swActualizarUbicacion = findViewById(R.id.swActualizarUbicacion);
        bEnviarResolucion = findViewById(R.id.bEnviarResolucion);
        /** SETEAMOS LOS TYPEFACES*/
        setPrimaryFont(this, etObservaciones);
        setPrimaryFont(this, swActualizarUbicacion);
        setPrimaryFontBold(this, bEnviarResolucion);
        /**************************/
        inputs.add(etObservaciones);
        if (tramite.getReclamo().getUbicacion().equals("null")) {
            // SI EL PUNTO NO TIENE UBICACION PREVIA, OBLIGA AL USUARIO A ACTUALIZAR LA UBICACION
            swActualizarUbicacion.setChecked(true);
            swActualizarUbicacion.setEnabled(false);
        }
        request_permissions();
    }

    @Override
    public void onBackPressed() {
        /* LO QUE HACE CUANDO VUELVA*/
        stopLocationUpdates();
        abrirActivity(this, ReclamoActivity.class);
    }

    private void insertarResolucion() {
        ReclamoControlador reclamoControlador = new ReclamoControlador();
        TramiteControlador tramiteControlador = new TramiteControlador();
        ResolucionReclamo resolucionReclamo = new ResolucionReclamo();
        ResolucionReclamoControlador resolucionReclamoControlador = new ResolucionReclamoControlador();
        resolucionReclamo.setTipoTramite(tramite.getTipoTramite().getTipo());
        resolucionReclamo.setNumeroTramite(tramite.getReclamo().getNumeroTramite());
        resolucionReclamo.setCodigoResolucion(tipoResolucionSpinner.getResoluciones().get(sTipoResolucion.getSelectedItemPosition()).getResolucion());
        resolucionReclamo.setObservaciones(etObservaciones.getText().toString());
        resolucionReclamo.setUsuario(LoginActivity.usuario.getId());
        if (resolucionReclamoControlador.insertar(this, resolucionReclamo) == EXITOSO) {
            if (tramiteControlador.actualizarEstado(tramite, this) == EXITOSO) {
                if (swActualizarUbicacion.isChecked()) {
                    // SI EL SWITCH ESTA EN ON, ACTUALIZA LA UBICACION.
                    tramite.getReclamo().setUbicacion(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                    if (reclamoControlador.actualizarUbicacion(tramite.getReclamo(), this) == EXITOSO) {
                        abrirActivity(this, TramitesActivity.class);
                    }
                } else {
                    abrirActivity(this, TramitesActivity.class);
                }
            }
        }
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

    void request_permissions() {
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
        bEnviarResolucion.setOnClickListener(v -> {
            if (mCurrentLocation != null) {
                if (validarCampos(this, inputs) == EXITOSO) {
                    Util.showDialog(this,
                            R.layout.dialog_guardar,
                            "Si, Guardar",
                            () -> {
                                insertarResolucion();
                                return null;
                            }
                    );
                }
            }/**else {
             ESTE NO MUESTRA NINGUN MENSAJE, PORQUE LO HACE EL METODO GENERICO EN UTIL
             }*/
            else {
                mostrarMensaje(this, "Debe activar el GPS. Una vez activo, abra nuevamente esta pantalla");
            }

        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    Log.i(TAG, "Empezo a obtener la ubicacion!");

                    //noinspection MissingPermission
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback, Looper.myLooper());
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
                                rae.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
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
}
