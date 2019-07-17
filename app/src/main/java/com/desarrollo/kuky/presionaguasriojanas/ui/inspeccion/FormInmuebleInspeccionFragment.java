package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.util.Lists;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import static com.desarrollo.kuky.presionaguasriojanas.util.Lists.labelsCoeficienteZonal;
import static com.desarrollo.kuky.presionaguasriojanas.util.Lists.labelsDestino;
import static com.desarrollo.kuky.presionaguasriojanas.util.Lists.labelsTipoInmueble;
import static com.desarrollo.kuky.presionaguasriojanas.util.Lists.labelsTipoServicio;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class FormInmuebleInspeccionFragment extends Fragment {
    Spinner sTipoInmueble, sDestino, sCoeficienteZonal, sTipoServicio;
    Switch swServicioCloacal;


    public FormInmuebleInspeccionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_inmueble_inspeccion, container, false);
        //Nuevos parametros para el view del fragmento
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        //Nueva Regla: EL fragmento estara debajo del boton add_fragment
        params.addRule(RelativeLayout.ABOVE, R.id.bSiguienteFragmento);
        //Margenes: top:15dp
        params.setMargins(0, 15, 0, 15);
        //Setear los parametros al view
        view.setLayoutParams(params);
        /** SETEO LISTAS QUE VOY A USAR EN EL FORM*/
        Lists.formInmueble();
        /************************************************/
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        /** CAPTURAMOS LOS ELEMENTOS DE LA VISTA *********************/
        sTipoInmueble = getActivity().findViewById(R.id.sTipoInmueble);
        sDestino = getActivity().findViewById(R.id.sDestino);
        swServicioCloacal = getActivity().findViewById(R.id.swServicioCloacal);
        sCoeficienteZonal = getActivity().findViewById(R.id.sCoeficienteZonal);
        sTipoServicio = getActivity().findViewById(R.id.sTipoServicio);
        /** CARGAMOS LOS SPINNERS ************************************/
        Util.cargarSpinner(sTipoInmueble,
                getActivity(),
                1,
                labelsTipoInmueble,
                () -> {
                    return null;
                },
                () -> {

                    return null;
                });
        ////////////////////////////////////////
        Util.cargarSpinner(sDestino,
                getActivity(),
                1,
                labelsDestino,
                () -> {
                    return null;
                },
                () -> {

                    return null;
                });
        ////////////////////////////////////////
        Util.cargarSpinner(sCoeficienteZonal,
                getActivity(),
                1,
                labelsCoeficienteZonal,
                () -> {
                    return null;
                },
                () -> {

                    return null;
                });
////////////////////////////////////////
        Util.cargarSpinner(sTipoServicio,
                getActivity(),
                1,
                labelsTipoServicio,
                () -> {
                    return null;
                },
                () -> {

                    return null;
                });
        swServicioCloacal.setOnClickListener(v -> {
            mostrarMensaje(getActivity(), " " + swServicioCloacal.isChecked());
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        //mostrarMensaje(getActivity(), " " + swServicioCloacal.isChecked());
    }
}
