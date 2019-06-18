package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.MapActivityControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.PuntoPresionControlador;
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
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.LA_RIOJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MAPA_CLIENTES;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MAPA_RECORRIDO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PREFS_NAME;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PRIMER_INICIO_MODULO_PRESION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.TIPO_MAPA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.USUARIO_PUNTO_PRESION_SHARED_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker puntoMarcador;
    private LatLng marcador;
    private ArrayList<PuntoPresion> puntosPresion = new ArrayList<>();
    private int tipoPunto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        primerInicio();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        tipoPunto = settings.getInt(TIPO_MAPA, MAPA_RECORRIDO);
        if (tipoPunto == MAPA_RECORRIDO) {
            this.setTitle("Mapa recorrido");
        } else if (tipoPunto == MAPA_CLIENTES) {
            this.setTitle("Mapa clientes");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            abrirActivity(this, InicioActivity.class);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.map_recorrido) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(TIPO_MAPA, MAPA_RECORRIDO);
            editor.commit();
            abrirActivity(MapActivity.this, MapActivity.class);
        } else if (id == R.id.map_clientes) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(TIPO_MAPA, MAPA_CLIENTES);
            editor.commit();
            abrirActivity(MapActivity.this, MapActivity.class);
        } else if (id == R.id.action_sync) {
            showDialogSync(MapActivity.this);
        } else if (id == R.id.action_add) {
            abrirActivity(MapActivity.this, NuevoPuntoActivity.class);
        }
//         else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LA_RIOJA));
        // Add a marker in La Rioja
//        mMap.addMarker(new MarkerOptions().position(laRioja).title("La Rioja"));
//        // Traemos los puntos de presion
        PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
        puntosPresion = puntoPresionControlador.extraerTodos(this, tipoPunto);
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
        PuntoPresion puntoPresion;
        puntoPresion = (PuntoPresion) marker.getTag();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(ID_PUNTO_PRESION_SHARED_PREFERENCE, puntoPresion.getId());
        editor.putString(USUARIO_PUNTO_PRESION_SHARED_PREFERENCE, puntoPresion.getUsuario().getId());
        // Commit the edits!
        editor.commit();

        abrirActivity(this, PuntoPresionActivity.class);

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return true;
    }

    public void showDialogSync(final Activity a) {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(a);
        View promptView = layoutInflater.inflate(R.layout.dialog_sincronizar, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
        alertDialogBuilder.setView(promptView);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Si, sincrozar bases", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sincronizar();
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

    private void primerInicio() {
        /**
         * A LA MODIFICACION DE LA BANDERA LA HAGO EN EL onPostExecute del historialPuntosControlador.sincronizarDeMysqlToSqlite
         * */
        if (LoginActivity.usuario.getBandera_modulo_presion() == PRIMER_INICIO_MODULO_PRESION) {
            sincronizar();
        }

    }

    private void sincronizar() {
        MapActivityControlador mapActivityControlador = new MapActivityControlador();
        if (mapActivityControlador.sync(MapActivity.this) == EXITOSO) {
        }
    }
}
