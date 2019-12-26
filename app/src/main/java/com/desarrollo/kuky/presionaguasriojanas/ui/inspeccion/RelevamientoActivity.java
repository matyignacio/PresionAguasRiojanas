package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion;

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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion.RelevamientoControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion.RelevamientoMedidorControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Relevamiento;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.RelevamientoMedidor;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.nuevorelevamientofragments.FormFoto;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.nuevorelevamientofragments.FormInmueble;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.nuevorelevamientofragments.FormMapa;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.nuevorelevamientofragments.FormMedidores;
import com.desarrollo.kuky.presionaguasriojanas.ui.presion.NuevaPresionActivity;
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

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.LATITUD_INSPECCION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.LONGITUD_INSPECCION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.REQUEST_CHECK_SETTINGS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirFragmento;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.cerrarFragmento;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class RelevamientoActivity extends AppCompatActivity {
    public static Button bSiguienteFragmento, bVolver, bGuardarRelevamiento;
    public static ImageButton bNuevoMedidor;
    public static FormInmueble formInmueble;
    public static FormMapa formMapa;
    public static FormFoto formFoto;
    public static FormMedidores formMedidores;
    public static int posicionFormulario;
    public static Relevamiento relevamiento;
    public static ArrayList<RelevamientoMedidor> relevamientoMedidores;
    /**
     * LO REFERENTE A OBTENER LA UBICACION
     */
    private static final String TAG = NuevaPresionActivity.class.getSimpleName();

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    // boolean flag to toggle the ui
    public Boolean mRequestingLocationUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relevamiento);
        relevamiento = new Relevamiento();
        formInmueble = new FormInmueble();
        formMapa = new FormMapa();
        formFoto = new FormFoto();
        formMedidores = new FormMedidores();
        posicionFormulario = 0;
        /************************************************/
        bVolver = findViewById(R.id.bVolver);
        bSiguienteFragmento = findViewById(R.id.bSiguienteFragmento);
        bGuardarRelevamiento = findViewById(R.id.bGuardarRelevamiento);
        /** SETEAMOS TYPEFACES  */
        setPrimaryFontBold(this, bVolver);
        setPrimaryFontBold(this, bSiguienteFragmento);
        setPrimaryFontBold(this, bGuardarRelevamiento);
        /************************/
        request_permissions();
        bSiguienteFragmento.setOnClickListener(v -> posicionFormulario = siguienteFragmento(this, R.id.LLRelevamiento, posicionFormulario));
        bVolver.setOnClickListener(v -> posicionFormulario = volverFragmento(this, R.id.LLRelevamiento, posicionFormulario));
        bGuardarRelevamiento.setOnClickListener(v -> {
            Util.createCustomDialog(this, "¿Confirma que desea guardar?",
                    "",
                    "Si, Guardar",
                    "Cancelar",
                    // ACEPTAR
                    () -> {
                        insertar();
                        return null;
                    },
                    // CANCELAR
                    () -> {
                        return null;
                    }).show();
        });
    }

    @Override
    public void onBackPressed() {
        if (formMapa.isVisible() ||
                formFoto.isVisible() ||
                formMedidores.isVisible()) {
            mostrarMensaje(this, "Debe cerrar el formulario para poder volver");
        } else {
            stopLocationUpdates();
            abrirActivity(this, InspeccionActivity.class);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startLocationUpdates();
    }

    private void init() {
        posicionFormulario = siguienteFragmento(this, R.id.LLRelevamiento, posicionFormulario);
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
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
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
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
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

    public int siguienteFragmento(Activity a, int layout, int posicionFormulario) {
        switch (posicionFormulario) {
            case 0: // SIGNIFICA POSICION INICIAL
                // SIMPLEMENTE ABRIMOS EL SIGUIENTE FRAGMENTO
                posicionFormulario++;
                abrirFragmento(a, layout, formInmueble);
                setOnButtonsFragment();
                break;
            case 1:
                // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL SIGUIENTE
                if (mCurrentLocation != null) {
                    // LE ASIGNAMOS AL RELEVAMIENTO LA UBICACION DEL USUARIO
                    relevamiento.setLatitudUsuario(mCurrentLocation.getLatitude());
                    relevamiento.setLongitudUsuario(mCurrentLocation.getLongitude());
                    // Y TAMBIEN ACTUALIZAMOS EL CENTRO DEL MAPA QUE SE ABRIRA EN EL FRAGMENTO
                    setPreference(this, LATITUD_INSPECCION, String.valueOf(mCurrentLocation.getLatitude()));
                    setPreference(this, LONGITUD_INSPECCION, String.valueOf(mCurrentLocation.getLongitude()));
                }
                posicionFormulario++;
                Util.siguienteFragmento(a, layout,
                        formInmueble,
                        formMapa);
                break;
            case 2:
                // EN ESTE CASO...
                posicionFormulario++;
                // ... CERRAMOS EL FRAGMENTO Y ...
                cerrarFragmento(a, formMapa);
                // ... ABRIMOS EL SIGUIENTE
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.LLRelevamiento, formFoto)
                        .commit();
                break;
            case 3:
                // EN ESTE CASO...
                posicionFormulario++;
                // ... CERRAMOS EL FRAGMENT formFoto...
                getSupportFragmentManager().beginTransaction().
                        remove(formFoto).
                        commit();
                // ... Y LUEGO  ABRIMOS UN NUEVO FRAGMENTO formMedidores
                abrirFragmento(a, layout, formMedidores);
                break;
            default:
                posicionFormulario++;
                bSiguienteFragmento.setVisibility(View.INVISIBLE);
                Util.createCustomDialog(this, "¿Confirma que desea guardar?",
                        "",
                        "Si, Guardar",
                        "Cancelar",
                        // ACEPTAR
                        () -> {
                            insertar();
                            return null;
                        },
                        // CANCELAR
                        () -> {
                            return null;
                        }).show();
                break;
        }
        return posicionFormulario;
    }

    public int volverFragmento(Activity a, int layout, int posicionFormulario) {
        switch (posicionFormulario) {
            case 1: // SIGNIFICA POSICION INICIAL
                // CERRAMOS EL FRAGMENTO Y BACKEAMOS EL ACTIVITY
                setOffButtonsFragment();
                posicionFormulario--;
                cerrarFragmento(a, formInmueble);
                stopLocationUpdates();
                abrirActivity(this, InspeccionActivity.class);
                break;
            case 2:
                // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL ANTERIOR
                posicionFormulario--;
                Util.siguienteFragmento(a, layout,
                        formMapa,
                        formInmueble);
                break;
            case 3:
                // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL ANTERIOR
                posicionFormulario--;
                getSupportFragmentManager().beginTransaction().
                        remove(formFoto).
                        commit();
                abrirFragmento(a, layout, formMapa);
                break;
            case 4:
                // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL ANTERIOR
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.LLRelevamiento, formFoto)
                        .commit();
                posicionFormulario--;
                cerrarFragmento(a, formMedidores);
                break;
            default:
                bGuardarRelevamiento.setVisibility(View.INVISIBLE);
                bSiguienteFragmento.setVisibility(View.VISIBLE);
                posicionFormulario--;
                break;
        }
        return posicionFormulario;
    }

    public void setOnButtonsFragment() {
        bSiguienteFragmento.setVisibility(View.VISIBLE);
        bVolver.setVisibility(View.VISIBLE);
    }

    public void setOffButtonsFragment() {
        bSiguienteFragmento.setVisibility(View.INVISIBLE);
        bVolver.setVisibility(View.INVISIBLE);
    }

    private void insertar() {
        RelevamientoMedidorControlador relevamientoMedidorControlador = new RelevamientoMedidorControlador();
        try {
            RelevamientoControlador relevamientoControlador = new RelevamientoControlador();
            relevamiento.setId(relevamientoControlador.obtenerSiguienteId(this));
            relevamiento.setIdUsuario(LoginActivity.usuario.getId());
            if (relevamientoControlador.insertar(relevamiento, this) == EXITOSO) {
                for (int i = 0; i < FormMedidores.medidoresLuz.size(); i++) {
                    RelevamientoMedidor relevamientoMedidor = new RelevamientoMedidor();
                    relevamientoMedidor.setId(relevamientoMedidorControlador.obtenerSiguienteId(this));
                    relevamientoMedidor.setIdUsuario(LoginActivity.usuario.getId());
                    try {
                        relevamientoMedidor.setNumero(Integer.parseInt(FormMedidores.medidoresLuz.get(i).getText().toString()));
                        relevamientoMedidor.setRelevamiento(relevamiento);
                        relevamientoMedidorControlador.insertar(relevamientoMedidor, this);
                    } catch (Exception e2) {
                        mostrarMensajeLog(this, "Ocurrio un error al insertar el relevamiento" + e2.toString());
                    }
                }
                mostrarMensaje(this, "Se guardo el relevamiento con exito!");
                stopLocationUpdates();
                abrirActivity(this, RelevamientoActivity.class);
            } else {
                mostrarMensaje(this, "Ocurrio un error al insertar el relevamiento");
                stopLocationUpdates();
            }
        } catch (Exception e) {
            mostrarMensaje(this, "Ocurrio un error al insertar el relevamiento" + e.toString());
        }
    }
}
