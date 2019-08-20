package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.nuevainspeccionfragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Inspeccion;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.NuevaInspeccion;

import static com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.NuevaInspeccion.bGuardarInspeccion;
import static com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.NuevaInspeccion.bSiguienteFragmento;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class FormObservaciones extends Fragment {
    EditText etObservaciones;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inspeccion_form_observaciones, container, false);
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
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        etObservaciones = getActivity().findViewById(R.id.etObservaciones);
    }

    @Override
    public void onResume() {
        bSiguienteFragmento.setVisibility(View.INVISIBLE);
        bGuardarInspeccion.setVisibility(View.VISIBLE);
        bGuardarInspeccion.setOnClickListener(v -> {
            NuevaInspeccion.inspeccion.setObservaciones(etObservaciones.getText().toString());
            if (insertarInspeccion() == EXITOSO) {
                mostrarMensaje(getActivity(), "Se guardo la inspeccion con exito");
            }
        });
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        bSiguienteFragmento.setVisibility(View.VISIBLE);
        bGuardarInspeccion.setVisibility(View.INVISIBLE);
    }

    private int insertarInspeccion() {
        Inspeccion inspeccion = new Inspeccion();
        try {
            mostrarMensaje(getActivity(),
                    "Inmueble: " + NuevaInspeccion.inspeccion.getTipoInmueble().getId() +
                            "\nServicio: " + NuevaInspeccion.inspeccion.getTipoServicio().getId() +
                            "\nDestino: " + NuevaInspeccion.inspeccion.getDestinoInmueble().getId() +
                            "\nServicio Clocacal: " + NuevaInspeccion.inspeccion.isServicioCloacal() +
                            "\nCoeficiente zonal: " + NuevaInspeccion.inspeccion.getCoeficienteZonal() +
                            "\nObservaciones: " + NuevaInspeccion.inspeccion.getObservaciones() +
                            "\nLatitud: " + NuevaInspeccion.inspeccion.getLatitud() +
                            "\nLongitud: " + NuevaInspeccion.inspeccion.getLongitud() +
                            "\nLatitudUsuario: " + NuevaInspeccion.inspeccion.getLatitudUsuario() +
                            "\nLongitudUsuario: " + NuevaInspeccion.inspeccion.getLongitudUsuario() +
                            "\n--------------------" +
                            "\nCliente: " + NuevaInspeccion.cliente.getRazonSocial());
            return EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(getActivity(), e.toString());
            return ERROR;
        }
    }
}