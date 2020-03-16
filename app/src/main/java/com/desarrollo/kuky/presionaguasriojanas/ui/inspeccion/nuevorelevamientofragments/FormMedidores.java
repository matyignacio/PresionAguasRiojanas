package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.nuevorelevamientofragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.RelevamientoMedidor;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.NuevoRelevamientoActivity;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.NuevoRelevamientoActivity.bNuevoMedidor;
import static com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.NuevoRelevamientoActivity.formMedidores;
import static com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.NuevoRelevamientoActivity.relevamientoMedidores;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MAX_LENGHT_MEDIDORES;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirFragmento;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.cerrarFragmento;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ocultarTeclado;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class FormMedidores extends Fragment {
    int marginTop = 15;
    int addMargin = 130;
    int idEditText = 0;
    int cantidadEditText = 1;
    /**
     * CREO ARRAYLIST PARA IR AGREGANDO LOS EDITTEXT EN TIEMPO DE EJECUCION
     * Y LUEGO EN "ON PAUSE" ASIGNARLOS AL ARRAYLIST RELEVAMIENTOMEDIDORES
     **/
    public static ArrayList<EditText> medidoresLuz;
    TextView tvMedidoresRelevados;

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
        tvMedidoresRelevados = getActivity().findViewById(R.id.tvMedidoresRelevados);
        tvMedidoresRelevados.setText("MEDIDORES");
        medidoresLuz = new ArrayList<>();
        idEditText = 0;
        marginTop = 15;
        if (relevamientoMedidores.size() > 0) {
            for (int i = 0; i < relevamientoMedidores.size(); i++) {
                nuevoRegistro(marginTop
                        , idEditText,
                        String.valueOf(relevamientoMedidores.get(i).getNumero()));
                marginTop += (addMargin * cantidadEditText);
                idEditText++;
            }
        }
        NuevoRelevamientoActivity.bGuardarRelevamiento.setVisibility(View.VISIBLE);
        NuevoRelevamientoActivity.bSiguienteFragmento.setVisibility(View.INVISIBLE);
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
        if (medidoresLuz.size() != relevamientoMedidores.size()) {
            relevamientoMedidores = new ArrayList<>();
            for (int i = 0; i < medidoresLuz.size(); i++) {
                try {
                    RelevamientoMedidor relevamientoMedidor = new RelevamientoMedidor();
                    relevamientoMedidor.setNumero(
                            Integer.valueOf(
                                    medidoresLuz.get(i).getText().toString()));
                    NuevoRelevamientoActivity.relevamientoMedidores.add(relevamientoMedidor);
                } catch (Exception e) {
                    mostrarMensajeLog(getActivity(), "No se pudo asignar " + e.toString());
                }
                ocultarTeclado(getActivity(), medidoresLuz.get(i));
            }
        } else {
            relevamientoMedidores = new ArrayList<>();
            for (int i = 0; i < medidoresLuz.size(); i++) {
                try {
                    RelevamientoMedidor relevamientoMedidor = new RelevamientoMedidor();
                    relevamientoMedidor.setNumero(
                            Integer.valueOf(
                                    medidoresLuz.get(i).getText().toString()));
                    NuevoRelevamientoActivity.relevamientoMedidores.add(relevamientoMedidor);
                } catch (Exception e) {
                    mostrarMensajeLog(getActivity(), "No se pudo asignar " + e.toString());
                }
                ocultarTeclado(getActivity(), medidoresLuz.get(i));
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        /* Si se llama al onStop es porque se volvio un fragmento entonces
         *  reseteamos el ArrayList **************************************/
        // EDIT 12-3-2020: NuevoRelevamientoActivity.relevamientoMedidores = new ArrayList<>(); lo mandamos al onCreate del activity NuevoRelevamientoActivity
        /* y mostramos de nuevo el boton SIGUIENTE ***********************/
        NuevoRelevamientoActivity.bGuardarRelevamiento.setVisibility(View.INVISIBLE);
        NuevoRelevamientoActivity.bSiguienteFragmento.setVisibility(View.VISIBLE);
    }

    public void nuevoRegistro(int marginTop, int idEditText) {
        LayoutPersonalizada lpMedidorLuz = new LayoutPersonalizada(marginTop);
        EditText etMedidorLuz;
        etMedidorLuz = new EditText(getActivity());
        etMedidorLuz.setId(idEditText);
        setPrimaryFontBold(getActivity().getApplicationContext(), etMedidorLuz);
        etMedidorLuz.requestFocus();
        etMedidorLuz.setTextColor(getActivity().getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
        etMedidorLuz.setHintTextColor(getActivity().getApplicationContext().getResources().getColor(R.color.colorAccent));
        etMedidorLuz.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGHT_MEDIDORES)});
        etMedidorLuz.setBackgroundResource(R.drawable.et_redondo);
        etMedidorLuz.setHint("Medidor de Luz " + (idEditText + 1));
        etMedidorLuz.setInputType(InputType.TYPE_CLASS_NUMBER);
        etMedidorLuz.setLayoutParams(lpMedidorLuz.getmRparams());
        lpMedidorLuz.getmRlayout().addView(etMedidorLuz);
        etMedidorLuz.setOnLongClickListener(v -> {
            cerrarFragmento(getActivity(), this);
            abrirFragmento(getActivity(), R.id.LLRelevamiento, formMedidores);
            return true;
        });
        medidoresLuz.add(etMedidorLuz);
    }

    public void nuevoRegistro(int marginTop, int idEditText, String texto) {
        LayoutPersonalizada lpMedidorLuz = new LayoutPersonalizada(marginTop);
        EditText etMedidorLuz;
        etMedidorLuz = new EditText(getActivity());
        etMedidorLuz.setId(idEditText);
        setPrimaryFontBold(getActivity().getApplicationContext(), etMedidorLuz);
        etMedidorLuz.requestFocus();
        etMedidorLuz.setTextColor(getActivity().getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
        etMedidorLuz.setHintTextColor(getActivity().getApplicationContext().getResources().getColor(R.color.colorAccent));
        etMedidorLuz.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGHT_MEDIDORES)});
        etMedidorLuz.setBackgroundResource(R.drawable.et_redondo);
        etMedidorLuz.setHint("Medidor de Luz " + (idEditText + 1));
        etMedidorLuz.setText(texto);
        etMedidorLuz.setInputType(InputType.TYPE_CLASS_NUMBER);
        etMedidorLuz.setLayoutParams(lpMedidorLuz.getmRparams());
        lpMedidorLuz.getmRlayout().addView(etMedidorLuz);
        etMedidorLuz.setOnLongClickListener(v -> {
            cerrarFragmento(getActivity(), this);
            abrirFragmento(getActivity(), R.id.LLRelevamiento, formMedidores);
            return true;
        });
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
