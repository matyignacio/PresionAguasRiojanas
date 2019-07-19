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
import android.widget.Spinner;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.presion.PuntoPresionControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.presion.TipoPuntoControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.TipoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.TipoPunto;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
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
import java.util.List;

import butterknife.ButterKnife;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.CIRCUITO_USUARIO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MAPA_CLIENTES;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MAPA_RECORRIDO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.POSICION_SELECCIONADA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.REQUEST_CHECK_SETTINGS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.SPINNER_TIPO_UNIDAD;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.SPINNER_TIPO_UNIDAD2;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.TIPO_MAPA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirFragmento;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.getPreference;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.validarCampos;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.validarPresion;

public class NuevoPuntoActivity extends AppCompatActivity {

    private static final String TAG = NuevoPuntoActivity.class.getSimpleName();
    public static Button bEnviarNuevoPunto;
    public static CheckBox cbCalidad;
    private ControlCalidadFragment controlCalidadFragment = new ControlCalidadFragment();

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    // boolean flag to toggle the ui
    public Boolean mRequestingLocationUpdates;
    private EditText etUnidad, etUnidad2, etBarrio, etCalle1, etCalle2, etPresion;
    private Spinner sTipoPunto, sTipoUnidad, sTipoUnidad2;
    private ArrayList<EditText> inputs = new ArrayList<>();
    private ArrayList<TipoPunto> tipoPuntos = new ArrayList<>();
    private TipoPuntoControlador tipoPuntoControlador = new TipoPuntoControlador();
    private TipoPunto tipoPunto = new TipoPunto();
    private List<String> labelsTipoUnidad = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_punto);
        ButterKnife.bind(this);
        cbCalidad = findViewById(R.id.cbCalidad);
        tipoPuntos = tipoPuntoControlador.extraerTodos(this);
        //tvUnidad = findViewById(R.id.tvUnidad);
        bEnviarNuevoPunto = findViewById(R.id.bEnviarNuevoPunto);
        etUnidad = findViewById(R.id.etUnidad);
        etUnidad2 = findViewById(R.id.etUnidad2);
        etBarrio = findViewById(R.id.etBarrio);
        etCalle1 = findViewById(R.id.etCalle1);
        etCalle2 = findViewById(R.id.etCalle2);
        etPresion = findViewById(R.id.etPresion);
        sTipoPunto = findViewById(R.id.sTipoPunto);
        sTipoUnidad = findViewById(R.id.sTipoUnidad);
        sTipoUnidad2 = findViewById(R.id.sTipoUnidad2);
        labelsTipoUnidad.add("Unidad");
        labelsTipoUnidad.add("Nis");
        labelsTipoUnidad.add("Medidor Aguas");
        labelsTipoUnidad.add("Medidor Edelar");
        cargarSpinnerTipoPunto();
        cargarSpinnerTipoUnidad();
        /** SETEAMOS LOS TYPEFACES*/
        //setPrimaryFont(this, tvUnidad);
        setPrimaryFontBold(this, etUnidad);
        setPrimaryFontBold(this, etUnidad2);
        setPrimaryFontBold(this, etBarrio);
        setPrimaryFontBold(this, etCalle1);
        setPrimaryFontBold(this, etCalle2);
        setPrimaryFontBold(this, etPresion);
        setPrimaryFontBold(this, bEnviarNuevoPunto);
        setPrimaryFontBold(this, cbCalidad);
        /**************************/
        inputs.add(etBarrio);
        inputs.add(etCalle1);
        // inputs.add(etCalle2); A ESTE LO COMENTO PORQUE NO ES OBLIGATORIO EL CAMPO
        inputs.add(etPresion);
        cbCalidad.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                abrirFragmento(NuevoPuntoActivity.this, R.id.rlNuevoPunto, controlCalidadFragment);
                setEnabledInputs(false);
            } else {
                setEnabledInputs(true);
            }
        });
        request_permissions();
    }

    @Override
    public void onBackPressed() {
        if (controlCalidadFragment.isVisible()) {
            cbCalidad.setChecked(false);
            Util.cerrarFragmento(this, controlCalidadFragment);
        } else {
            stopLocationUpdates();
            abrirActivity(this, MapActivity.class);
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
        bEnviarNuevoPunto.setOnClickListener(view -> {
            if (mCurrentLocation != null) {
                if (validarCampos(NuevoPuntoActivity.this, inputs) == EXITOSO) {
                    Util.showDialog(NuevoPuntoActivity.this,
                            R.layout.dialog_guardar,
                            "Si, Guardar",
                            () -> {
                                insertarPunto();
                                return null;
                            }
                    );
                } /**else {
                 ESTE NO MUESTRA NINGUN MENSAJE, PORQUE LO HACE EL METODO GENERICO EN UTIL
                 }*/
            } else {
                mostrarMensaje(NuevoPuntoActivity.this, "Debe activar el GPS. Una vez activo, abra nuevamente esta pantalla");
            }
        });
    }

    private void insertarPunto() {
        if (validarPresion(this, etPresion) == EXITOSO) {
            try {
                PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
                PuntoPresion puntoPresion = new PuntoPresion();
                TipoPresion tipoPresion = new TipoPresion();
                // INICIALIZAMOS LO Q VAMOS A NECESITAR
                puntoPresion.setCircuito(getPreference(this, CIRCUITO_USUARIO, 1));
                puntoPresion.setBarrio(etBarrio.getText().toString());
                puntoPresion.setCalle1(etCalle1.getText().toString());
                puntoPresion.setCalle2(etCalle2.getText().toString());
                puntoPresion.setLatitud(mCurrentLocation.getLatitude());
                puntoPresion.setLongitud(mCurrentLocation.getLongitude());
                puntoPresion.setPresion(Float.parseFloat(etPresion.getText().toString()));
                tipoPresion.setId(1);
                puntoPresion.setTipoPresion(tipoPresion);
                puntoPresion.setUsuario(LoginActivity.usuario);
                if (inputs.size() == 4) {
                    puntoPresion.setTipoUnidad(labelsTipoUnidad.get(sTipoUnidad.getSelectedItemPosition()));
                    puntoPresion.setUnidad(Integer.parseInt(etUnidad.getText().toString()));
                    if (!etUnidad2.getText().toString().equals("")) { //VALIDAMOS QUE unidad2 TENGA ALGUN DATO PARA GUARDAR
                        puntoPresion.setTipoUnidad2(labelsTipoUnidad.get(sTipoUnidad2.getSelectedItemPosition()));
                        puntoPresion.setUnidad2(Integer.parseInt(etUnidad2.getText().toString()));
                    } else {
                        puntoPresion.setTipoUnidad2("-");
                        puntoPresion.setUnidad2(0);
                    }
                } else {
                    puntoPresion.setUnidad(0);
                    puntoPresion.setUnidad2(0);
                }
                puntoPresion.setCloro(controlCalidadFragment.calidad.getCloro());
                puntoPresion.setMuestra(controlCalidadFragment.calidad.getMuestra());
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

    private void cargarSpinnerTipoPunto() {
        int idTipoPunto = getPreference(NuevoPuntoActivity.this, TIPO_MAPA, MAPA_RECORRIDO);
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < tipoPuntos.size(); i++) {
            labels.add(tipoPuntos.get(i).getNombre());
        }
        Util.cargarSpinner(sTipoPunto,
                this,
                idTipoPunto,
                labels,
                () -> {
                    tipoPunto.setId(getPreference(this, POSICION_SELECCIONADA, 1));
                    if (getPreference(this, POSICION_SELECCIONADA, 1) == MAPA_CLIENTES) {
                        etUnidad.setEnabled(true);
                        sTipoUnidad.setEnabled(true);
                        cargarSpinnerTipoUnidad();
                        if (inputs.size() == 3) { /**EVALUAMOS SI UNIDAD YA PERTENECE A LOS CAMPOS A VALIDAR*/
                            inputs.add(etUnidad);
                        }
                    } else {
                        if (inputs.size() == 4) { /**EVALUAMOS SI UNIDAD YA PERTENECE A LOS CAMPOS A VALIDAR*/
                            inputs.remove(3);
                        }
                        cargarSpinnerTipoUnidad();
                        sTipoUnidad.setEnabled(false);
                        etUnidad.setEnabled(false);
                        etUnidad.setText("");
                    }
                    //mostrarMensaje(NuevoPuntoActivity.this, String.valueOf(tipoPunto.getId()));
                    return null;
                },
                () -> {
                    tipoPunto.setId(MAPA_RECORRIDO);
                    return null;
                });
    }

    private void cargarSpinnerTipoUnidad() {
        Util.cargarSpinner(sTipoUnidad,
                this,
                1,
                labelsTipoUnidad,
                () -> {//ON ITEM SELECTED
                    etUnidad.setHint("Numero de " + labelsTipoUnidad.get(sTipoUnidad.getSelectedItemPosition()));
                    setPreference(NuevoPuntoActivity.this, SPINNER_TIPO_UNIDAD, sTipoUnidad.getSelectedItemPosition() + 1);
                    return null;
                },
                () -> null);
        Util.cargarSpinner(sTipoUnidad2,
                this,
                1,
                labelsTipoUnidad,
                () -> {//ON ITEM SELECTED
                    etUnidad2.setHint("Numero 2 de " + labelsTipoUnidad.get(sTipoUnidad2.getSelectedItemPosition()));
                    setPreference(NuevoPuntoActivity.this, SPINNER_TIPO_UNIDAD2, sTipoUnidad2.getSelectedItemPosition() + 1);
                    return null;
                },
                () -> null);
        if (getPreference(this, POSICION_SELECCIONADA, 1) == MAPA_CLIENTES) {
            //etUnidad.setHint("Numero de " + labelsTipoUnidad.get(sTipoUnidad.getSelectedItemPosition()));
            etUnidad.setBackgroundResource(R.drawable.et_redondo);
            sTipoUnidad.setBackgroundResource(R.drawable.sp_redondo);
            /** REPLICO PARA EL OTRO SPINNER **/
            etUnidad2.setBackgroundResource(R.drawable.et_redondo);
            sTipoUnidad2.setBackgroundResource(R.drawable.sp_redondo);
        } else {
            etUnidad.setBackgroundResource(R.drawable.et_redondo_disabled);
            sTipoUnidad.setBackgroundResource(R.drawable.sp_redondo_disabled);
            etUnidad.setHint("Sin n° de unidad");
            /** REPLICO PARA EL OTRO SPINNER **/
            etUnidad2.setBackgroundResource(R.drawable.et_redondo_disabled);
            sTipoUnidad2.setBackgroundResource(R.drawable.sp_redondo_disabled);
            etUnidad2.setHint("Sin n° de unidad");
        }
    }

    private void setEnabledInputs(boolean enabled) {
        cbCalidad.setEnabled(enabled);
        bEnviarNuevoPunto.setEnabled(enabled);
        etUnidad.setEnabled(enabled);
        etUnidad2.setEnabled(enabled);
        etBarrio.setEnabled(enabled);
        etCalle1.setEnabled(enabled);
        etCalle2.setEnabled(enabled);
        etPresion.setEnabled(enabled);
        sTipoPunto.setEnabled(enabled);
        sTipoUnidad.setEnabled(enabled);
        sTipoUnidad2.setEnabled(enabled);
    }

}
