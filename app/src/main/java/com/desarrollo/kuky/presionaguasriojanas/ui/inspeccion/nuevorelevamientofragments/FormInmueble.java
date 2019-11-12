package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.nuevorelevamientofragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion.RelevamientoActivityControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion.TipoInmuebleControlador;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.RelevamientoActivity;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.SPINNER_BARRIO_RELEVAMIENTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.cargarSpinner;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ocultarTeclado;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPreference;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class FormInmueble extends Fragment {
    ArrayList<String> labelsBarrios = new ArrayList<>();
    ArrayList<String> labelsTipoInmueble = new ArrayList<>();
    Spinner sTipoInmueble, sBarrios;
    Switch swConexionVisible;
    EditText etRubro, etMedidorLuz, etMedidorAgua, etObservaciones;
    TipoInmuebleControlador tipoInmuebleControlador = new TipoInmuebleControlador();

    public FormInmueble() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_relevamiento_form_inmueble, container, false);
        // Nuevos parametros para el view del fragmento
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        // Nueva Regla: EL fragmento estara debajo del boton add_fragment
        params.addRule(RelativeLayout.ABOVE, R.id.bSiguienteFragmento);
        // Margenes: top:15dp
        params.setMargins(0, 15, 0, 15);
        // Setear los parametros al view
        view.setLayoutParams(params);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        RelevamientoActivity.bSiguienteFragmento.setVisibility(View.INVISIBLE);
        if (labelsTipoInmueble.size() == 0) {
            // SOLO CARGAMOS LAS LISTAS ESTATICAS UNA VEZ (LA PRIMERA)
            for (int i = 0; i < tipoInmuebleControlador.extraerTodos(getActivity()).size(); i++) {
                labelsTipoInmueble.add(
                        tipoInmuebleControlador.extraerTodos(getActivity()).get(i).getNombre());
            }
            labelsBarrios.add("Ninguno");
            for (int i = 0; i < RelevamientoActivityControlador.barrios.size(); i++) {
                labelsBarrios.add(
                        RelevamientoActivityControlador.barrios.get(i).getDesCodigo());
            }
        }
        /** CAPTURAMOS LOS ELEMENTOS DE LA VISTA *********************/
        sBarrios = getActivity().findViewById(R.id.sBarrio);
        sTipoInmueble = getActivity().findViewById(R.id.sTipoInmueble);
        etRubro = getActivity().findViewById(R.id.etRubro);
        swConexionVisible = getActivity().findViewById(R.id.swConexionVisible);
        etMedidorLuz = getActivity().findViewById(R.id.etMedidorLuz);
        etMedidorAgua = getActivity().findViewById(R.id.etMedidorAgua);
        etObservaciones = getActivity().findViewById(R.id.etObservaciones);
        etMedidorLuz.addTextChangedListener(new addListenerOnTextChange(etMedidorLuz));
        /** SETEAMOS LOS TYPEFACES ***********************************/
        setPrimaryFontBold(getActivity(), etRubro);
        setPrimaryFontBold(getActivity(), etMedidorLuz);
        setPrimaryFontBold(getActivity(), etMedidorAgua);
        setPrimaryFontBold(getActivity(), etObservaciones);
        /** CARGAMOS LOS SPINNERS ************************************/
        cargarSpinner(sTipoInmueble,
                getActivity(),
                0,
                labelsTipoInmueble,
                () -> null,
                () -> null);
        ////////////////////////////////////////
        cargarSpinner(sBarrios,
                getActivity(),
                //el valor por defecto que toma es el ultimo barrio seleccionado, en caso de...
                //... no tener ninguno, carga el primero
                Util.getPreference(getActivity(), SPINNER_BARRIO_RELEVAMIENTO, 0),
                labelsBarrios,
                () -> {
                    // se guarda el barrio seleccionado, para luego mostrarlo cuando se vuelva a cargar
                    setPreference(getActivity(), SPINNER_BARRIO_RELEVAMIENTO, sBarrios.getSelectedItemPosition());
                    return null;
                },
                () -> null);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        RelevamientoActivity.relevamiento.setBarrio(
                labelsBarrios.get(sBarrios.getSelectedItemPosition())
        );
        RelevamientoActivity.relevamiento.setTipoInmueble(
                labelsTipoInmueble.get(sTipoInmueble.getSelectedItemPosition())
        );
        RelevamientoActivity.relevamiento.setRubro(
                etRubro.getText().toString());
        try {
            RelevamientoActivity.relevamiento.setMedidorLuz(
                    Integer.valueOf(etMedidorLuz.getText().toString()));
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(),
                    "No se pudo guardar " + etMedidorLuz.getHint());
        }
        try {
            RelevamientoActivity.relevamiento.setMedidorAgua(
                    Integer.valueOf(etMedidorAgua.getText().toString()));
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(),
                    "No se pudo guardar " + etMedidorAgua.getHint());
        }
        RelevamientoActivity.relevamiento.setConexionVisible(swConexionVisible.isChecked());
        RelevamientoActivity.relevamiento.setObservaciones(
                etObservaciones.getText().toString());
        ocultarTeclado(getActivity(), etObservaciones);
    }

    public class addListenerOnTextChange implements TextWatcher {
        EditText mEdittextview;

        addListenerOnTextChange(EditText edittextview) {
            super();
            this.mEdittextview = edittextview;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mEdittextview.getText().toString().length() > 4) {
                RelevamientoActivity.bSiguienteFragmento.setVisibility(View.VISIBLE);
            } else {
                RelevamientoActivity.bSiguienteFragmento.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //What you want to do
        }
    }
}
