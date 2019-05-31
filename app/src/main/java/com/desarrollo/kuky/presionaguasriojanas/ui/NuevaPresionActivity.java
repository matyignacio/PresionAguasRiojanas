package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PREFS_NAME;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.validarCampos;

public class NuevaPresionActivity extends AppCompatActivity {
    private EditText etPresion;
    private Button bEnviarMedicion;
    private ArrayList<EditText> inputs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_presion);
        etPresion = findViewById(R.id.etPresion);
        inputs.add(etPresion);
        bEnviarMedicion = findViewById(R.id.bEnviarMediicion);
        bEnviarMedicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos(NuevaPresionActivity.this, inputs) == EXITOSO) {
                    if (insertarMedicion(Float.parseFloat(etPresion.getText().toString())) == EXITOSO) {
                        mostrarMensaje(NuevaPresionActivity.this, "Se ingreso con exito");
                        abrirActivity(NuevaPresionActivity.this, PuntoPresionActivity.class);
                    } else {
                        mostrarMensaje(NuevaPresionActivity.this, "Ocurrio un error al intentar guardar");
                    }
                }
            }
        });
        if (validaPermisos()) {
            bEnviarMedicion.setEnabled(true);
        } else {
            bEnviarMedicion.setEnabled(false);
        }
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


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bEnviarMedicion.setEnabled(true);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    solicitarPermisosManual();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private boolean validaPermisos() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if ((checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) /*&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)*/) {
            return true;
        }

        if ((shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))/* ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))*/) {
            cargarDialogoRecomendacion();
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        return false;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });
        dialogo.show();
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones = {"Si", "No"};
        AlertDialog.Builder alertOpciones = new AlertDialog.Builder(NuevaPresionActivity.this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Si")) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } else {
                    mostrarMensaje(NuevaPresionActivity.this, "Los permisos no fueron aceptados");
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }
}
