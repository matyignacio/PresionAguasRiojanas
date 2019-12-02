package com.desarrollo.kuky.presionaguasriojanas.ui.reclamo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.desarrollo.kuky.presionaguasriojanas.ui.reclamo.ReclamoActivity.tramite;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;

public class UbicacionReclamoActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion_reclamo);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        /* LO QUE HACE CUANDO VUELVA*/
        abrirActivity(this, ReclamoActivity.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // DEFINIMOS EL TIPO DE MAPA
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        String[] ubicaciones = tramite.getReclamo().getUbicacion().trim().split(",");
        Double latitud = Double.valueOf(ubicaciones[0]);
        Double longitud = Double.valueOf(ubicaciones[1]);
        LatLng ubicacion = new LatLng(latitud, longitud);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
        Marker puntoMarcador;
        puntoMarcador = googleMap.addMarker(new MarkerOptions()
                .position(ubicacion)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green))
                .title("Tramite: " + tramite.getTipoTramite().getTipo() + " - " + tramite.getReclamo().getNumeroTramite()));
    }
}
