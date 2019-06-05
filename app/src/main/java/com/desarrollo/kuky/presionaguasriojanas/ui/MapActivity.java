package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.MapActivityControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.PuntoPresionControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.UsuarioControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ESTANDAR_MEDICION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ID_PUNTO_PRESION_SHARED_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PREFS_NAME;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class MapActivity extends AppCompatActivity /* FragmentActivity para que no tenga AppBar */
        implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker puntoMarcador;
    private static final LatLng laRioja = new LatLng(-29.4126811, -66.8576855);
    private LatLng marcador;
    private ArrayList<PuntoPresion> puntosPresion = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_sync) {
            try {
                MapActivityControlador mapActivityControlador = new MapActivityControlador();
                if (mapActivityControlador.sync(this) == EXITOSO) {
//                    abrirActivity(this, MapActivity.class);
                }
            } catch (Exception e) {
                mostrarMensaje(this, e.toString());
            }
            return true;
        }

        if (id == R.id.action_sign_out) {
            /* LO QUE TENGAMOS QUE HACER PARA CERRAR SESION*/
            showDialogCerrarSesion(this);
            return true;
        }

        if (id == R.id.action_add) {
            abrirActivity(MapActivity.this, NuevoPuntoActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Move camera to La Rioja
        mMap.moveCamera(CameraUpdateFactory.newLatLng(laRioja));
        // Add a marker in La Rioja
//        mMap.addMarker(new MarkerOptions().position(laRioja).title("La Rioja"));
//        // Traemos los puntos de presion
        PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
        puntosPresion = puntoPresionControlador.extraerTodos(this);
        // Recorremos el arrayList para ir creando los marcadores
        for (Integer i = 0; i < puntosPresion.size(); i++) {
            if (puntosPresion.get(i).getPresion() > ESTANDAR_MEDICION) {
                marcador = new LatLng(puntosPresion.get(i).getLatitud(),
                        puntosPresion.get(i).getLongitud());
                puntoMarcador = mMap.addMarker(new MarkerOptions()
                        .position(marcador)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .title(puntosPresion.get(i).getCalle1() + ", Bº: " +
                                puntosPresion.get(i).getBarrio()));
                puntoMarcador.setTag(puntosPresion.get(i));
            } else {
                marcador = new LatLng(puntosPresion.get(i).getLatitud(),
                        puntosPresion.get(i).getLongitud());
                puntoMarcador = mMap.addMarker(new MarkerOptions()
                        .position(marcador)
                        .title(puntosPresion.get(i).getCalle1() + ", Bº: " +
                                puntosPresion.get(i).getBarrio()));
                puntoMarcador.setTag(puntosPresion.get(i));
            }
        }

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        PuntoPresion puntoPresion = new PuntoPresion();
        puntoPresion = (PuntoPresion) marker.getTag();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(ID_PUNTO_PRESION_SHARED_PREFERENCE, puntoPresion.getId());
        // Commit the edits!
        editor.commit();

        abrirActivity(this, PuntoPresionActivity.class);

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return true;
    }


    public void showDialogCerrarSesion(final Activity a) {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(a);
        View promptView = layoutInflater.inflate(R.layout.dialog_cerrar_sesion, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
        alertDialogBuilder.setView(promptView);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Si, cerrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UsuarioControlador usuarioControlador = new UsuarioControlador();
                        if (usuarioControlador.eliminarUsuario(a) == EXITOSO) {
                            abrirActivity(MapActivity.this, LoginActivity.class);
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
}
