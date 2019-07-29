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
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion.InspeccionActivityControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Cliente;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.DatosRelevados;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Inspeccion;
import com.desarrollo.kuky.presionaguasriojanas.ui.InicioActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
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
import java.util.List;

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
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class InspeccionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * LAS DEFINICIONES ESTATICAS QUE NECESITO PARA LOS FRAGMENTOS
     */
    public static List<String> labelsTipoInmueble = new ArrayList<>();
    public static List<String> labelsTipoServicio = new ArrayList<>();
    public static List<String> labelsDestino = new ArrayList<>();
    public static Cliente cliente;
    public static Inspeccion inspeccion;
    public static ArrayList<DatosRelevados> datosRelevados;
    public static FormClienteInspeccionFragment formClienteInspeccionFragment = new FormClienteInspeccionFragment();
    public static FormInmuebleInspeccionFragment formInmuebleInspeccionFragment = new FormInmuebleInspeccionFragment();
    public static FormObservacionesInspeccionFragment formObservacionesInspeccionFragment = new FormObservacionesInspeccionFragment();
    public static FormMapaInspeccionFragment formMapaInspeccionFragment = new FormMapaInspeccionFragment();
    public static FormDatosInspeccionFragment formDatosInspeccionFragment = new FormDatosInspeccionFragment();
    public static int posicionFormulario = 0;
    public static Button bSiguienteFragmento, bVolver, bGuardarInspeccion, bNuevaInspeccion;
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

    /**
     * DEMAS INICIALIZACIONES
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspeccion);
        /************************************************/
        bVolver = findViewById(R.id.bVolver);
        bNuevaInspeccion = findViewById(R.id.bNuevaInspeccion);
        bSiguienteFragmento = findViewById(R.id.bSiguienteFragmento);
        bGuardarInspeccion = findViewById(R.id.bGuardarInspeccion);
        /** SETEAMOS TYPEFACES  */
        setPrimaryFontBold(this, bVolver);
        setPrimaryFontBold(this, bSiguienteFragmento);
        setPrimaryFontBold(this, bGuardarInspeccion);
        setPrimaryFontBold(this, bNuevaInspeccion);
        /************************/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView subTitle = headerView.findViewById(R.id.tvUsuarioNavBar);
        subTitle.setText(LoginActivity.usuario.getNombre());
        /************************/
        bNuevaInspeccion.setOnClickListener(v -> {

            /************************/
            request_permissions();
        });
        bSiguienteFragmento.setOnClickListener(v -> posicionFormulario = siguienteFragmento(this, R.id.LLInspeccion, posicionFormulario));
        bVolver.setOnClickListener(v -> posicionFormulario = volverFragmento(this, R.id.LLInspeccion, posicionFormulario));
    }

    @Override
    public void onBackPressed() {
        if (formMapaInspeccionFragment.isVisible() ||
                formClienteInspeccionFragment.isVisible() ||
                formDatosInspeccionFragment.isVisible() ||
                formInmuebleInspeccionFragment.isVisible() ||
                formObservacionesInspeccionFragment.isVisible()) {
            mostrarMensaje(this, "Debe cerrar el formulario para poder volver");
        } else {
            abrirActivity(this, InicioActivity.class);
        }
    }

    private void init() {
        posicionFormulario = siguienteFragmento(this, R.id.LLInspeccion, posicionFormulario);
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

//        bEnviarMedicion.setOnClickListener(view -> {
//            if (mCurrentLocation != null) {
//                if (validarCampos(this, inputs) == EXITOSO) {
//                    Util.showDialog(this,
//                            R.layout.dialog_guardar,
//                            "Si, Guardar",
//                            () -> {
//                                insertarMedicion();
//                                return null;
//                            }
//                    );
//                } /**else {
//                 ESTE ELSE NO MUESTRA NINGUN MENSAJE, PORQUE LO HACE EL METODO GENERICO EN UTIL
//                 }*/
//            } else {
//                mostrarMensaje(this, "Debe activar el GPS. Una vez activo, abra nuevamente esta pantalla");
//            }
//        });
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
            case 0: //SIGNIFICA POSICION INICIAL
                // SIMPLEMENTE ABRIMOS EL SIGUIENTE FRAGMENTO
                posicionFormulario++;
                abrirFragmento(a, layout, formClienteInspeccionFragment);
                setOnButtonsFragment();
                break;
            case 1:
                // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL SIGUIENTE
                posicionFormulario++;
                Util.siguienteFragmento(a, layout,
                        formClienteInspeccionFragment,
                        formInmuebleInspeccionFragment);
                break;
            case 2:
                if (mCurrentLocation != null) {
                    Util.setPreference(this, LATITUD_INSPECCION, String.valueOf(mCurrentLocation.getLatitude()));
                    Util.setPreference(this, LONGITUD_INSPECCION, String.valueOf(mCurrentLocation.getLongitude()));
                }
                // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL SIGUIENTE
                posicionFormulario++;
                Util.siguienteFragmento(a, layout,
                        formInmuebleInspeccionFragment,
                        formMapaInspeccionFragment);
                break;
            case 3:
                // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL SIGUIENTE
                posicionFormulario++;
                Util.siguienteFragmento(a, layout,
                        formMapaInspeccionFragment,
                        formDatosInspeccionFragment);
                break;
            case 4:
                // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL SIGUIENTE
                posicionFormulario++;
                Util.siguienteFragmento(a, layout,
                        formDatosInspeccionFragment,
                        formObservacionesInspeccionFragment);
                break;
            default:
                posicionFormulario++;
                bSiguienteFragmento.setVisibility(View.INVISIBLE);
                Util.showDialog(this,
                        R.layout.dialog_guardar,
                        "Si, Guardar",
                        () -> {

                            return null;
                        },
                        () -> {
                            InspeccionActivity.posicionFormulario--;
                            bSiguienteFragmento.setVisibility(View.VISIBLE);
                            return null;
                        }
                );
                break;
        }
        return posicionFormulario;
    }

    public int volverFragmento(Activity a, int layout, int posicionFormulario) {
        switch (posicionFormulario) {
            case 1: //SIGNIFICA POSICION INICIAL
                // SIMPLEMENTE CERRAMOS EL FRAGMENTO
                setOffButtonsFragment();
                posicionFormulario--;
                cerrarFragmento(a, formClienteInspeccionFragment);
                stopLocationUpdates();
                break;
            case 2:
                // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL ANTERIOR
                posicionFormulario--;
                Util.siguienteFragmento(a, layout,
                        formInmuebleInspeccionFragment,
                        formClienteInspeccionFragment);
                break;
            case 3:
                // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL ANTERIOR
                posicionFormulario--;
                Util.siguienteFragmento(a, layout,
                        formMapaInspeccionFragment,
                        formInmuebleInspeccionFragment);
                break;
            case 4:
                // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL ANTERIOR
                posicionFormulario--;
                Util.siguienteFragmento(a, layout,
                        formDatosInspeccionFragment,
                        formMapaInspeccionFragment);
                break;
            case 5:
                // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL ANTERIOR
                posicionFormulario--;
                Util.siguienteFragmento(a, layout,
                        formObservacionesInspeccionFragment,
                        formDatosInspeccionFragment);
                break;
            default:
                bGuardarInspeccion.setVisibility(View.INVISIBLE);
                bSiguienteFragmento.setVisibility(View.VISIBLE);
                posicionFormulario--;
                break;
        }
        return posicionFormulario;
    }

    public void setOnButtonsFragment() {
        bSiguienteFragmento.setVisibility(View.VISIBLE);
        bVolver.setVisibility(View.VISIBLE);
        bNuevaInspeccion.setVisibility(View.INVISIBLE);
    }

    public void setOffButtonsFragment() {
        bSiguienteFragmento.setVisibility(View.INVISIBLE);
        bVolver.setVisibility(View.INVISIBLE);
        bNuevaInspeccion.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.action_sync) {
            Util.showDialog(this,
                    R.layout.dialog_sincronizar,
                    "sincronizar",
                    () -> {
                        sincronizar();
                        return null;
                    }
            );
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sincronizar() {
        InspeccionActivityControlador inspeccionActivityControlador = new InspeccionActivityControlador();
        if (inspeccionActivityControlador.sync(this) == EXITOSO) {
        }
    }
}
