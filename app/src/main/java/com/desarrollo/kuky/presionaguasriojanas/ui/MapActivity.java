package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.MapActivityControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.OrdenControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.PuntoPresionControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.Orden;
import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.CIRCUITO_USUARIO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ESTANDAR_MEDICION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ID_PUNTO_PRESION_SHARED_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.LATITUD_LA_RIOJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.LONGITUD_LA_RIOJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MAPA_CLIENTES;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MAPA_RECORRIDO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MAPA_RED;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MAXIMO_CIRCUITO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PREFS_NAME;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PRIMER_INICIO_MODULO_PRESION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.TIPO_MAPA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ULTIMA_LATITUD;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ULTIMA_LONGITUD;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.USUARIO_PUNTO_PRESION_SHARED_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private int tipoPunto;
    TextView subTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        primerInicio();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton fab = findViewById(R.id.fNuevoPunto);
        fab.setOnClickListener(view -> abrirActivity(MapActivity.this, NuevoPuntoActivity.class));

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        subTitle = headerView.findViewById(R.id.tvUsuarioNavBar);
        setNombreUsuario();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        tipoPunto = settings.getInt(TIPO_MAPA, MAPA_RECORRIDO);
        if (tipoPunto == MAPA_RECORRIDO) {
            this.setTitle("Mapa Recorrido");
        } else if (tipoPunto == MAPA_CLIENTES) {
            this.setTitle("Mapa Clientes");
        } else if (tipoPunto == MAPA_RED) {
            this.setTitle("Mapa Red");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        } else if (id == R.id.map_red) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(TIPO_MAPA, MAPA_RED);
            editor.commit();
            abrirActivity(MapActivity.this, MapActivity.class);
        } else if (id == R.id.ayuda_colores) {
            abrirActivity(MapActivity.this, PaletaColoresActivity.class);
        } else if (id == R.id.action_sync) {
            Util.showDialog(MapActivity.this,
                    R.layout.dialog_sincronizar,
                    "Si, sincronizar bases",
                    () -> {
                        sincronizar();
                        return null;
                    }
            );
        } else if (id == R.id.action_add) {
            abrirActivity(MapActivity.this, NuevoPuntoActivity.class);
        } else if (id == R.id.set_circuito) {
            showDialogSetCircuito(this);
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        GoogleMap mMap = googleMap;
        // Move camera to La Rioja
        /** BUSCAMOS EN SHARED PREFERENCES LA ULTIMA LATITUD Y LONGITUD SELECCIONADAS.
         *  EN CASO DE NO HABER, SETEAMOS EL MAPA EN LA RIOJA                       **/
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Double latitud = Double.valueOf(
                settings.getString(ULTIMA_LATITUD,
                        LATITUD_LA_RIOJA));
        Double longitud = Double.valueOf(
                settings.getString(ULTIMA_LONGITUD,
                        LONGITUD_LA_RIOJA));
        LatLng laRioja = new LatLng(latitud, longitud);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(laRioja));
//        // Traemos los puntos de presion
        PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
        ArrayList<PuntoPresion> puntosPresion = puntoPresionControlador.extraerTodos(this, tipoPunto);
        // Recorremos el arrayList para ir creando los marcadores
        for (Integer i = 0; i < puntosPresion.size(); i++) {
            Marker puntoMarcador;
            LatLng marcador;
            /**
             *  EN EL SIGUIENTE IF ELSE EVALUAMOS LA PRESION DEL PUNTO
             */
            marcador = new LatLng(puntosPresion.get(i).getLatitud(),
                    puntosPresion.get(i).getLongitud());
            if (puntosPresion.get(i).getPresion() > ESTANDAR_MEDICION) {
                puntoMarcador = mMap.addMarker(new MarkerOptions()
                        .position(marcador)
                        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green))
                        .title(puntosPresion.get(i).getCalle1() + ", Bº: " +
                                puntosPresion.get(i).getBarrio()));
                puntoMarcador.setTag(puntosPresion.get(i));
            } else {
                puntoMarcador = mMap.addMarker(new MarkerOptions()
                        .position(marcador)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red))
                        .title(puntosPresion.get(i).getCalle1() + ", Bº: " +
                                puntosPresion.get(i).getBarrio()));
                puntoMarcador.setTag(puntosPresion.get(i));
            }
            /**
             * Y EN ESTE IF ELSE EVALUAMOS SI ES EL SIGUIENTE PUNTO
             */
            OrdenControlador ordenControlador = new OrdenControlador();
            Orden orden;
            orden = ordenControlador.extraerActivo(this);
            if (puntosPresion.get(i).getId() == orden.getPpActual().getId() &&
                    puntosPresion.get(i).getUsuario().getId().equals(orden.getPpActual().getUsuario().getId())) {
                puntoMarcador = mMap.addMarker(new MarkerOptions()
                        .position(marcador)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_yellow))
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

        /** GUARDAMOS EN SHARED PREFERENCES EL ID Y USUARIO DEL PUNTO
         *  TAMBIEN GUARDAMOS SU UBICACION PARA REABRIR EL MAPA EN EL PUNTO SELECCIONADO **/
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(ID_PUNTO_PRESION_SHARED_PREFERENCE, puntoPresion.getId());
        editor.putString(USUARIO_PUNTO_PRESION_SHARED_PREFERENCE, puntoPresion.getUsuario().getId());
        editor.putString(ULTIMA_LATITUD, puntoPresion.getLatitud().toString());
        editor.putString(ULTIMA_LONGITUD, puntoPresion.getLongitud().toString());
        // Commit the edits!
        editor.commit();

        abrirActivity(this, PuntoPresionActivity.class);

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return true;
    }

    private void primerInicio() {
        /**
         * A LA MODIFICACION DE LA BANDERA LA HAGO EN EL onPostExecute del historialPuntosControlador.sincronizarDeMysqlToSqlite
         * */
        if (LoginActivity.usuario.getBandera_modulo_presion() == PRIMER_INICIO_MODULO_PRESION) {
            sincronizar();
        }

    }

    private void setNombreUsuario() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String usuario = LoginActivity.usuario.getNombre() + "\n" +
                "Circuito " + settings.getInt(CIRCUITO_USUARIO, 1);
        subTitle.setText(usuario);
    }

    private void sincronizar() {
        MapActivityControlador mapActivityControlador = new MapActivityControlador();
        if (mapActivityControlador.sync(MapActivity.this) == EXITOSO) {
        }
    }

    public void showDialogSetCircuito(final Activity a) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        final Spinner taskSpinner = new Spinner(a);
        taskSpinner.setBackgroundResource(R.drawable.sp_redondo);
        List<String> labels = new ArrayList<>();
        for (int i = 1; i <= MAXIMO_CIRCUITO; i++) {
            labels.add("Circuito " + String.valueOf(i));
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, labels);
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskSpinner.setAdapter(spinnerAdapter);
        taskSpinner.setDropDownWidth(250);
        taskSpinner.setSelection(settings.getInt(CIRCUITO_USUARIO, 1) - 1);
        taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(CIRCUITO_USUARIO, i + 1);
                editor.commit();
                //mostrarMensaje(MapActivity.this, "Se actualizo el circuito a: " +
                //        String.valueOf(i + 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                /** NO MODIFICA NADA*/
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(a)
                .setTitle(" ")
                //.setMessage("Seleccione el circuito")
                .setView(taskSpinner)
                .setPositiveButton("Listo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setNombreUsuario();
                        mostrarMensaje(MapActivity.this, "Se actualizo el circuito");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
    }
}
