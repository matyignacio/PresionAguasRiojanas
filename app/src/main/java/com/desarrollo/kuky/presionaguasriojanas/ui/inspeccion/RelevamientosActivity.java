package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion.RelevamientoControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Relevamiento;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.LATITUD_LA_RIOJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.LONGITUD_LA_RIOJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MEDIDOR_LUZ_RELEVAMIENTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ULTIMA_LATITUD_RELEVAMIENTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ULTIMA_LONGITUD_RELEVAMIENTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivityWithBundle;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.createCustomDialog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.getPreference;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;

public class RelevamientosActivity extends FragmentActivity implements
        GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relevamientos);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        abrirActivity(this, InspeccionActivity.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        /** BUSCAMOS EN SHARED PREFERENCES LA ULTIMA LATITUD Y LONGITUD SELECCIONADAS.
         *  EN CASO DE NO HABER, SETEAMOS EL MAPA EN LA RIOJA                       **/
        double latitud = Double.parseDouble(getPreference(this, ULTIMA_LATITUD_RELEVAMIENTO,
                LATITUD_LA_RIOJA));
        double longitud = Double.parseDouble(getPreference(this, ULTIMA_LONGITUD_RELEVAMIENTO,
                LONGITUD_LA_RIOJA));
        LatLng laRioja = new LatLng(latitud, longitud);
        map.moveCamera(CameraUpdateFactory.newLatLng(laRioja));
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setOnMapClickListener(latLng -> {
            mostrarMensaje(this, "Para cargar un nuevo relevamiento, mantenga presionado.");
        });
        map.setOnMapLongClickListener(latLng -> {
            setPreference(this, ULTIMA_LATITUD_RELEVAMIENTO, String.valueOf(latLng.latitude));
            setPreference(this, ULTIMA_LONGITUD_RELEVAMIENTO, String.valueOf(latLng.longitude));
            abrirActivity(this, NuevoRelevamientoActivity.class);
        });
        // TRAEMOS LOS RELEVAMIENTOS PENDIENTES
        RelevamientoControlador relevamientoControlador = new RelevamientoControlador();
        ArrayList<Relevamiento> relevamientos = relevamientoControlador.extraerTodosPendientes(this);
        // Recorremos el arrayList para ir creando los marcadores
        for (int i = 0; i < relevamientos.size(); i++) {
            Marker relevamientoMarcador;
            LatLng marcador;
            /**
             *  EN EL SIGUIENTE IF ELSE EVALUAMOS LA PRESION DEL PUNTO
             */
            marcador = new LatLng(relevamientos.get(i).getLatitud(),
                    relevamientos.get(i).getLongitud());
            relevamientoMarcador = googleMap.addMarker(new MarkerOptions()
                    .position(marcador)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red))
                    .title(String.valueOf(relevamientos.get(i).getMedidorLuz())));
            relevamientoMarcador.setTag(relevamientos.get(i));
            int finalI = i;
            map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_yellow));
                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_red));
                    relevamientos.get(finalI).setLatitud(marker.getPosition().latitude);
                    relevamientos.get(finalI).setLongitud(marker.getPosition().longitude);
                    if (relevamientoControlador.actualizar(relevamientos.get(finalI),
                            RelevamientosActivity.this) == EXITOSO) {
                        mostrarMensaje(RelevamientosActivity.this,
                                "Se actualizó la ubicación del relevamiento " + relevamientos.get(finalI).getMedidorLuz() +
                                        " con éxito.");
                    }
                }
            });
        }

        // Set a listener for marker click.
        googleMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Relevamiento relevamiento;
        relevamiento = (Relevamiento) marker.getTag();
        createCustomDialog(RelevamientosActivity.this, "Editar relevamiento " + relevamiento.getMedidorLuz(),
                "Quiere editar este relevamiento?",
                "EDITAR",
                "CANCELAR",
                () -> {
                    /** GUARDAMOS EN SHARED PREFERENCES EL ID Y USUARIO DEL PUNTO
                     *  TAMBIEN GUARDAMOS SU UBICACION PARA REABRIR EL MAPA EN EL PUNTO SELECCIONADO **/
                    setPreference(this, MEDIDOR_LUZ_RELEVAMIENTO, relevamiento.getMedidorLuz());
                    setPreference(this, ULTIMA_LATITUD_RELEVAMIENTO, relevamiento.getLatitud().toString());
                    setPreference(this, ULTIMA_LONGITUD_RELEVAMIENTO, relevamiento.getLongitud().toString());
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("relevamiento", relevamiento);
                    abrirActivityWithBundle(this, NuevoRelevamientoActivity.class, mBundle);
                    return null;
                }, () -> {
                    return null;
                }).show();
        return true;
    }
}
