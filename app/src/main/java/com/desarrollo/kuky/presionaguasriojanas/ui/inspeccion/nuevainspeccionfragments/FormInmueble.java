package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.nuevainspeccionfragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion.DestinoInmuebleControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion.TipoInmuebleControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion.TipoServicioControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.DestinoInmueble;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Inspeccion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.TipoInmueble;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.TipoServicio;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.NuevaInspeccion;
import com.desarrollo.kuky.presionaguasriojanas.util.Lists;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.NuevaInspeccion.inspeccion;
import static com.desarrollo.kuky.presionaguasriojanas.util.Lists.labelsCoeficienteZonal;

public class FormInmueble extends Fragment {
    public static List<String> labelsTipoInmueble = new ArrayList<>();
    public static List<String> labelsTipoServicio = new ArrayList<>();
    public static List<String> labelsDestino = new ArrayList<>();
    Spinner sTipoInmueble, sDestino, sCoeficienteZonal, sTipoServicio;
    Switch swServicioCloacal;
    TipoInmuebleControlador tipoInmuebleControlador = new TipoInmuebleControlador();
    TipoServicioControlador tipoServicioControlador = new TipoServicioControlador();
    DestinoInmuebleControlador destinoInmuebleControlador = new DestinoInmuebleControlador();


    public FormInmueble() {
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
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        inspeccion = new Inspeccion();
        /** SETEO LISTAS QUE VOY A USAR EN EL FORM*/
        Lists.formInmueble();
        /************************************************/
        if (labelsTipoInmueble.size() == 0) {
            // SOLO CARGAMOS LAS LISTAS ESTATICAS UNA VEZ (LA PRIMERA)
            for (int i = 0; i < tipoInmuebleControlador.extraerTodos(getActivity()).size(); i++) {
                labelsTipoInmueble.add(
                        tipoInmuebleControlador.extraerTodos(getActivity()).get(i).getNombre());
            }
            for (int i = 0; i < tipoServicioControlador.extraerTodos(getActivity()).size(); i++) {
                labelsTipoServicio.add(
                        tipoServicioControlador.extraerTodos(getActivity()).get(i).getNombre());
            }
            labelsDestino.add("Ninguno");
            for (int i = 0; i < destinoInmuebleControlador.extraerTodos(getActivity()).size(); i++) {
                labelsDestino.add(
                        destinoInmuebleControlador.extraerTodos(getActivity()).get(i).getNombre());
            }
        }
        /************************************************/
    }

    @Override
    public void onResume() {
        super.onResume();
        /** CAPTURAMOS LOS ELEMENTOS DE LA VISTA *********************/
        sTipoInmueble = getActivity().findViewById(R.id.sTipoInmueble);
        sDestino = getActivity().findViewById(R.id.sDestino);
        swServicioCloacal = getActivity().findViewById(R.id.swConexionVisible);
        sCoeficienteZonal = getActivity().findViewById(R.id.sCoeficienteZonal);
        sTipoServicio = getActivity().findViewById(R.id.sBarrio);
        /** CARGAMOS LOS SPINNERS ************************************/
        Util.cargarSpinner(sTipoInmueble,
                getActivity(),
                1,
                labelsTipoInmueble,
                () -> {
                    NuevaInspeccion.inspeccion.setTipoInmueble(
                            new TipoInmueble(sTipoInmueble.getSelectedItemPosition() + 1));
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
                    NuevaInspeccion.inspeccion.setDestinoInmueble(
                            new DestinoInmueble(sDestino.getSelectedItemPosition() + 1));
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
                    NuevaInspeccion.inspeccion.setCoeficienteZonal(
                            Float.parseFloat(sCoeficienteZonal.getSelectedItem().toString())
                    );
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
                    NuevaInspeccion.inspeccion.setTipoServicio(
                            new TipoServicio(sTipoServicio.getSelectedItemPosition() + 1));
                    return null;
                },
                () -> {
                    return null;
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        NuevaInspeccion.inspeccion.setServicioCloacal(swServicioCloacal.isChecked());
    }
}
