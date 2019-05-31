package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.PuntoPresionControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.TipoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.TipoPunto;
import com.desarrollo.kuky.presionaguasriojanas.util.GPSTracker;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.validarCampos;

public class NuevoPuntoActivity extends AppCompatActivity {

    private EditText etCircuito, etBarrio, etCalle1, etCalle2, etPresion;
    private Button bEnviarNuevoPunto;
    private ArrayList<EditText> inputs = new ArrayList<>();
    private PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
    private PuntoPresion puntoPresion = new PuntoPresion();
    private TipoPunto tipoPunto = new TipoPunto();
    private TipoPresion tipoPresion = new TipoPresion();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_punto);
        etCircuito = findViewById(R.id.etCircuito);
        etBarrio = findViewById(R.id.etBarrio);
        etCalle1 = findViewById(R.id.etCalle1);
        etCalle2 = findViewById(R.id.etCalle2);
        etPresion = findViewById(R.id.etPresion);
        Spinner spinner = findViewById(R.id.sTipoPunto);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_punto_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tipoPunto.setId(i+1);
                mostrarMensaje(NuevoPuntoActivity.this, String.valueOf(tipoPunto.getId()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                tipoPunto.setId(0);
                mostrarMensaje(NuevoPuntoActivity.this, String.valueOf(tipoPunto.getId()));
            }
        });
        inputs.add(etCircuito);
        inputs.add(etBarrio);
        inputs.add(etCalle1);
        // inputs.add(etCalle2); A ESTE LO COMENTO PORQUE NO ES OBLIGATORIO EL CAMPO
        inputs.add(etPresion);
        bEnviarNuevoPunto = findViewById(R.id.bEnviarNuevoPunto);
        bEnviarNuevoPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos(NuevoPuntoActivity.this, inputs) == EXITOSO) {
                    if (insertarPunto() == EXITOSO) {
                        mostrarMensaje(NuevoPuntoActivity.this, "Se agrego el nuevo punto");
                        abrirActivity(NuevoPuntoActivity.this, MapActivity.class);
                    } else {
                        mostrarMensaje(NuevoPuntoActivity.this, "Ocurrio un error al intentar guardar");
                    }
                }
            }
        });
        if (validaPermisos()) {
            bEnviarNuevoPunto.setEnabled(true);
        } else {
            bEnviarNuevoPunto.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /* LO QUE HACE CUANDO VUELVA*/
        abrirActivity(this, MapActivity.class);
    }

    private int insertarPunto() {
        try {
            // INICIALIZAMOS LO Q VAMOS A NECESITAR
            GPSTracker gpsTracker = new GPSTracker(this);
            // OBTENEMOS LA UBICACION
            gpsTracker.getLocation();
            gpsTracker.updateGPSCoordinates();
            // CARGAMOS EL OBJETO historialPuntos
            puntoPresion.setCircuito(Integer.parseInt(etCircuito.getText().toString()));
            puntoPresion.setBarrio(etBarrio.getText().toString());
            puntoPresion.setCalle1(etCalle1.getText().toString());
            puntoPresion.setCalle2(etCalle2.getText().toString());
            puntoPresion.setLatitud(gpsTracker.getLatitude());
            puntoPresion.setLongitud(gpsTracker.getLongitude());
            puntoPresion.setPresion(Float.parseFloat(etPresion.getText().toString()));
            tipoPresion.setId(1);
            puntoPresion.setTipoPresion(tipoPresion);
            tipoPunto.setId(1);
            puntoPresion.setTipoPunto(tipoPunto);
            // INSERTAMOS EL NUEVO REGISTRO
            puntoPresionControlador.insertar(puntoPresion, this);
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
                    bEnviarNuevoPunto.setEnabled(true);
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
        AlertDialog.Builder alertOpciones = new AlertDialog.Builder(NuevoPuntoActivity.this);
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
                    mostrarMensaje(NuevoPuntoActivity.this, "Los permisos no fueron aceptados");
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }
}
