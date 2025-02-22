package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.nuevainspeccionfragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.NuevaInspeccion;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.LATITUD_LA_RIOJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.LONGITUD_LA_RIOJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ULTIMA_LATITUD_RELEVAMIENTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ULTIMA_LONGITUD_RELEVAMIENTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.getPreference;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;

public class FormMapa extends Fragment {

    MapView mMapView;
    Marker marcador;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inspeccion_form_mapa, container, false);
        //Nuevos parametros para el view del fragmento
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        //Nueva Regla: EL fragmento estara debajo del boton add_fragment
        params.addRule(RelativeLayout.ABOVE, R.id.bSiguienteFragmento);
        //Margenes: top:15dp
        params.setMargins(0, 15, 0, 15);
        //Setear los parametros al view
        rootView.setLayoutParams(params);

        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;
            // DEFINIMOS EL TIPO DE MAPA
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            Double latitud = Double.valueOf(getPreference(getActivity(), ULTIMA_LATITUD_RELEVAMIENTO,
                    LATITUD_LA_RIOJA));
            Double longitud = Double.valueOf(getPreference(getActivity(), ULTIMA_LONGITUD_RELEVAMIENTO,
                    LONGITUD_LA_RIOJA));
            LatLng centroMapa = new LatLng(latitud, longitud);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(centroMapa));
            googleMap.setOnMapClickListener(point -> {
                try {
                    marcador.remove();
                } catch (Exception e) {
                    mostrarMensajeLog(getActivity(), e.toString());
                }
                NuevaInspeccion.bSiguienteFragmento.setVisibility(View.VISIBLE);
                marcador = googleMap.addMarker(new MarkerOptions()
                        .position(point));
                NuevaInspeccion.inspeccion.setLatitud(point.latitude);
                NuevaInspeccion.inspeccion.setLongitud(point.longitude);
            });
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        NuevaInspeccion.bSiguienteFragmento.setVisibility(View.VISIBLE);
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        NuevaInspeccion.bSiguienteFragmento.setVisibility(View.INVISIBLE);
    }
}