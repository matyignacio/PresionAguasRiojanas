package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.nuevorelevamientofragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.RelevamientoMedidor;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.RelevamientoActivity;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.RelevamientoActivity.bNuevoMedidor;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;

public class FormMedidores extends Fragment {
    int marginTop = 15;
    int addMargin = 130;
    int idEditText = 0;
    int cantidadEditText = 1;
    EditText etMedidorLuz;
    /**
     * CREO ARRAYLIST PARA IR AGREGANDO LOS EDITTEXT EN TIEMPO DE EJECUCION
     * Y LUEGO EN "ON PAUSE" ASIGNARLOS AL ARRAYLIST RELEVAMIENTOMEDIDORES
     **/
    public static ArrayList<EditText> medidoresLuz;

    public FormMedidores() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_relevamiento_form_medidores, container, false);

        //Nuevos parametros para el view del fragmento
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        //Nueva Regla: EL fragmento estara debajo del boton bSiguienteFragmento
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
        medidoresLuz = new ArrayList<>();
        idEditText = 0;
        marginTop = 15;
        RelevamientoActivity.bGuardarRelevamiento.setVisibility(View.VISIBLE);
        RelevamientoActivity.bSiguienteFragmento.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        bNuevoMedidor = getActivity().findViewById(R.id.bNuevoMedidor);
        bNuevoMedidor.setVisibility(View.VISIBLE);
        bNuevoMedidor.setOnClickListener(v -> {
            nuevoRegistro(marginTop
                    , idEditText);
            marginTop += (addMargin * cantidadEditText);
            idEditText++;
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        bNuevoMedidor.setVisibility(View.INVISIBLE);
        for (int i = 0; i < medidoresLuz.size(); i++) {
            try {
                RelevamientoMedidor relevamientoMedidor = new RelevamientoMedidor();
                relevamientoMedidor.setNumero(
                        Integer.valueOf(
                                medidoresLuz.get(i).getText().toString()));
                RelevamientoActivity.relevamientoMedidores.add(relevamientoMedidor);
            } catch (Exception e) {
                mostrarMensajeLog(getActivity(), "No se pudo asignar " + e.toString());
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        /* Si se llama al onStop es porque se volvio un fragmento entonces
         *  reseteamos el ArrayList **************************************/
        RelevamientoActivity.relevamientoMedidores = new ArrayList<>();
        /* y mostramos de nuevo el boton SIGUIENTE ***********************/
        RelevamientoActivity.bGuardarRelevamiento.setVisibility(View.INVISIBLE);
        RelevamientoActivity.bSiguienteFragmento.setVisibility(View.VISIBLE);
    }

    public void nuevoRegistro(int marginTop, int idEditText) {
        LayoutPersonalizada lpMedidorLuz = new LayoutPersonalizada(marginTop);
        etMedidorLuz = new EditText(getActivity());
        etMedidorLuz.setId(idEditText);
        etMedidorLuz.requestFocus();
        etMedidorLuz.setBackgroundResource(R.drawable.et_redondo);
        etMedidorLuz.setHint("Medidor de Luz " + (idEditText + 1));
        etMedidorLuz.setInputType(InputType.TYPE_CLASS_NUMBER);
        etMedidorLuz.setLayoutParams(lpMedidorLuz.getmRparams());
        lpMedidorLuz.getmRlayout().addView(etMedidorLuz);
        medidoresLuz.add(etMedidorLuz);
    }

    private class LayoutPersonalizada {
        private RelativeLayout mRlayout;
        private RelativeLayout.LayoutParams mRparams;

        LayoutPersonalizada(int marginTop) {
            mRlayout = getActivity().findViewById(R.id.fMedidores);
            mRparams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            mRparams.addRule(RelativeLayout.ALIGN_START, R.id.tvMedidoresRelevados);
            mRparams.addRule(RelativeLayout.ALIGN_END, R.id.tvMedidoresRelevados);
            mRparams.addRule(RelativeLayout.BELOW, R.id.tvMedidoresRelevados);
            mRparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mRparams.setMargins(0, marginTop, 0, 0);
        }

        RelativeLayout getmRlayout() {
            return mRlayout;
        }

        RelativeLayout.LayoutParams getmRparams() {
            return mRparams;
        }
    }

}
