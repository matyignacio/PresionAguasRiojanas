package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.nuevainspeccionfragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.DatosRelevados;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.NuevaInspeccion;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Lists.labelsEstado;
import static com.desarrollo.kuky.presionaguasriojanas.util.Lists.labelsMedida;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;

public class FormDatos extends Fragment {
    ImageButton bNuevoDato;
    int marginTop = 15;
    int addMargin = 130;
    int idEditText = 0;
    int cantidadEditText = 6;
    Spinner sEstado, sMedida;
    EditText etUnidad, etMedidorAgua, etMedidorLuz, etNis;
    /**
     * CREO ARRAYLIST PARA IR AGREGANDO LOS EDITTEXT EN TIEMPO DE EJECUCION
     * Y LUEGO EN "ON PAUSE" ASIGNARLOS AL ARRAYLIST DE DATOSRELEVADOS
     **/
    ArrayList<EditText> unidades = new ArrayList<>();
    ArrayList<EditText> medidoresAgua = new ArrayList<>();
    ArrayList<EditText> medidoresLuz = new ArrayList<>();
    ArrayList<EditText> nises = new ArrayList<>();
    ArrayList<Spinner> estados = new ArrayList<>();
    ArrayList<Spinner> medidas = new ArrayList<>();

    public FormDatos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inspeccion_form_datos, container, false);

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

        idEditText = 0;
        marginTop = 15;
    }

    @Override
    public void onResume() {
        super.onResume();
        bNuevoDato = getActivity().findViewById(R.id.bNuevoDato);
        bNuevoDato.setOnClickListener(v -> {
            nuevoRegistro(marginTop
                    , idEditText);
            marginTop += (addMargin * cantidadEditText);
            idEditText++;
        });
        /** EVALUAMOS SI TIENE DATOS RELEVADOS AÑADIDOS Y VOLVIO A ESTE FRAGMENT **/
        try {
            if (NuevaInspeccion.datosRelevados.size() > 0) {
                nuevoRegistro(marginTop
                        , idEditText
                        , NuevaInspeccion.datosRelevados);
                mostrarMensaje(getActivity(), "Si tiene registros");
            }
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(), e.toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (int i = 0; i < unidades.size(); i++) {
            NuevaInspeccion.datosRelevados = new ArrayList<>();
            DatosRelevados datoRelevado = new DatosRelevados();
            datoRelevado.setIdUsuario(LoginActivity.usuario.getId());
            try {
                datoRelevado.setUnidad(Integer.parseInt(unidades.get(i).getText().toString()));
            } catch (Exception e) {
                mostrarMensajeLog(getActivity(), e.toString());
            }
            try {
                datoRelevado.setMedidorAgua(Integer.parseInt(medidoresAgua.get(i).getText().toString()));
            } catch (Exception e) {
                mostrarMensajeLog(getActivity(), e.toString());
            }
            try {
                datoRelevado.setMedidorLuz(Integer.parseInt(medidoresLuz.get(i).getText().toString()));
            } catch (Exception e) {
                mostrarMensajeLog(getActivity(), e.toString());
            }
            try {
                datoRelevado.setNis(Integer.parseInt(nises.get(i).getText().toString()));
            } catch (Exception e) {
                mostrarMensajeLog(getActivity(), e.toString());
            }
            if (estados.get(i).getSelectedItemPosition() == 0) { //ACTIVO = 0; BAJA = 1
                datoRelevado.setEstado(true);
            } else {
                datoRelevado.setEstado(false);
            }
            if (medidas.get(i).getSelectedItemPosition() == 0) { //SI = 0; NO = 1
                datoRelevado.setMedida(true);
            } else {
                datoRelevado.setMedida(false);
            }
            NuevaInspeccion.datosRelevados.add(datoRelevado);
        }
    }

    public void nuevoRegistro(int marginTop, int idEditText, ArrayList<DatosRelevados> datos) {
        for (int i = 0; i < datos.size(); i++) {
            LayoutPersonalizada lpUnidad = new LayoutPersonalizada(marginTop);
            etUnidad = new EditText(getActivity());
            etUnidad.setId(idEditText);
            etUnidad.setBackgroundResource(R.drawable.et_redondo);
            etUnidad.setText(String.valueOf(datos.get(i).getUnidad()));
            etUnidad.setInputType(InputType.TYPE_CLASS_NUMBER);
            etUnidad.setLayoutParams(lpUnidad.getmRparams());
            lpUnidad.getmRlayout().addView(etUnidad);
            unidades.add(etUnidad);
            ///////////////////////////////////////////////////////////////////
            marginTop += addMargin; //movemos el margen de arriba
            LayoutPersonalizada lpEstado = new LayoutPersonalizada(marginTop);
            sEstado = new Spinner(getActivity());
            sEstado.setId(idEditText);
            sEstado.setBackgroundResource(R.drawable.sp_redondo);
            sEstado.setLayoutParams(lpEstado.getmRparams());
            lpEstado.getmRlayout().addView(sEstado);
            /** CARGAMOS LOS SPINNERS ************************************/
            Util.cargarSpinner(sEstado,
                    getActivity(),
                    1,
                    labelsEstado,
                    () -> null,
                    () -> null);
            estados.add(sEstado);
            ///////////////////////////////////////////////////////////////////
            marginTop += addMargin; //movemos el margen de arriba
            LayoutPersonalizada lpMedida = new LayoutPersonalizada(marginTop);
            sMedida = new Spinner(getActivity());
            sMedida.setId(idEditText);
            sMedida.setBackgroundResource(R.drawable.sp_redondo);
            sMedida.setLayoutParams(lpMedida.getmRparams());
            lpMedida.getmRlayout().addView(sMedida);
            /** CARGAMOS LOS SPINNERS ************************************/
            Util.cargarSpinner(sMedida,
                    getActivity(),
                    1,
                    labelsMedida,
                    () -> null,
                    () -> null);
            medidas.add(sMedida);
            ///////////////////////////////////////////////////////////////////
            marginTop += addMargin; //movemos el margen de arriba
            LayoutPersonalizada lpMedidorAgua = new LayoutPersonalizada(marginTop);
            etMedidorAgua = new EditText(getActivity());
            etMedidorAgua.setId(idEditText);
            etMedidorAgua.setBackgroundResource(R.drawable.et_redondo);
            etMedidorAgua.setText(String.valueOf(datos.get(i).getMedidorAgua()));
            etMedidorAgua.setInputType(InputType.TYPE_CLASS_NUMBER);
            etMedidorAgua.setLayoutParams(lpMedidorAgua.getmRparams());
            lpMedidorAgua.getmRlayout().addView(etMedidorAgua);
            medidoresAgua.add(etMedidorAgua);
            ///////////////////////////////////////////////////////////////////
            marginTop += addMargin; //movemos el margen de arriba
            LayoutPersonalizada lpMedidorLuz = new LayoutPersonalizada(marginTop);
            etMedidorLuz = new EditText(getActivity());
            etMedidorLuz.setId(idEditText);
            etMedidorLuz.setBackgroundResource(R.drawable.et_redondo);
            etMedidorLuz.setText(String.valueOf(datos.get(i).getMedidorLuz()));
            etMedidorLuz.setInputType(InputType.TYPE_CLASS_NUMBER);
            etMedidorLuz.setLayoutParams(lpMedidorLuz.getmRparams());
            lpMedidorLuz.getmRlayout().addView(etMedidorLuz);
            medidoresLuz.add(etMedidorLuz);
            ///////////////////////////////////////////////////////////////////
            marginTop += addMargin; //movemos el margen de arriba
            LayoutPersonalizada lpNis = new LayoutPersonalizada(marginTop);
            etNis = new EditText(getActivity());
            etNis.setId(idEditText);
            etNis.setBackgroundResource(R.drawable.et_redondo);
            etNis.setText(String.valueOf(datos.get(i).getNis()));
            etNis.setInputType(InputType.TYPE_CLASS_NUMBER);
            etNis.setLayoutParams(lpNis.getmRparams());
            lpNis.getmRlayout().addView(etNis);
            nises.add(etNis);
            /** MUEVO EL MARGINTOP Y SUMO EL IDEDITTEXT PARA SIMULAR QUE
             *  SE APRETO EL BOTON DE NUEVO REGISTRO ******************/
            this.marginTop += (addMargin * cantidadEditText);
            this.idEditText++;
        }
    }

    public void nuevoRegistro(int marginTop, int idEditText) {
        LayoutPersonalizada lpUnidad = new LayoutPersonalizada(marginTop);
        etUnidad = new EditText(getActivity());
        etUnidad.setId(idEditText);
        etUnidad.setBackgroundResource(R.drawable.et_redondo);
        etUnidad.setHint("Unidad " + (idEditText + 1));
        etUnidad.setInputType(InputType.TYPE_CLASS_NUMBER);
        etUnidad.setLayoutParams(lpUnidad.getmRparams());
        lpUnidad.getmRlayout().addView(etUnidad);
        unidades.add(etUnidad);
        ///////////////////////////////////////////////////////////////////
        marginTop += addMargin; //movemos el margen de arriba
        LayoutPersonalizada lpEstado = new LayoutPersonalizada(marginTop);
        sEstado = new Spinner(getActivity());
        sEstado.setId(idEditText);
        sEstado.setBackgroundResource(R.drawable.sp_redondo);
        sEstado.setLayoutParams(lpEstado.getmRparams());
        lpEstado.getmRlayout().addView(sEstado);
        /** CARGAMOS LOS SPINNERS ************************************/
        Util.cargarSpinner(sEstado,
                getActivity(),
                1,
                labelsEstado,
                () -> null,
                () -> null);
        estados.add(sEstado);
        ///////////////////////////////////////////////////////////////////
        marginTop += addMargin; //movemos el margen de arriba
        LayoutPersonalizada lpMedida = new LayoutPersonalizada(marginTop);
        sMedida = new Spinner(getActivity());
        sMedida.setId(idEditText);
        sMedida.setBackgroundResource(R.drawable.sp_redondo);
        sMedida.setLayoutParams(lpMedida.getmRparams());
        lpMedida.getmRlayout().addView(sMedida);
        /** CARGAMOS LOS SPINNERS ************************************/
        Util.cargarSpinner(sMedida,
                getActivity(),
                1,
                labelsMedida,
                () -> null,
                () -> null);
        medidas.add(sMedida);
        ///////////////////////////////////////////////////////////////////
        marginTop += addMargin; //movemos el margen de arriba
        LayoutPersonalizada lpMedidorAgua = new LayoutPersonalizada(marginTop);
        etMedidorAgua = new EditText(getActivity());
        etMedidorAgua.setId(idEditText);
        etMedidorAgua.setBackgroundResource(R.drawable.et_redondo);
        etMedidorAgua.setHint("Medidor de Agua " + (idEditText + 1));
        etMedidorAgua.setInputType(InputType.TYPE_CLASS_NUMBER);
        etMedidorAgua.setLayoutParams(lpMedidorAgua.getmRparams());
        lpMedidorAgua.getmRlayout().addView(etMedidorAgua);
        medidoresAgua.add(etMedidorAgua);
        ///////////////////////////////////////////////////////////////////
        marginTop += addMargin; //movemos el margen de arriba
        LayoutPersonalizada lpMedidorLuz = new LayoutPersonalizada(marginTop);
        etMedidorLuz = new EditText(getActivity());
        etMedidorLuz.setId(idEditText);
        etMedidorLuz.setBackgroundResource(R.drawable.et_redondo);
        etMedidorLuz.setHint("Medidor de Luz " + (idEditText + 1));
        etMedidorLuz.setInputType(InputType.TYPE_CLASS_NUMBER);
        etMedidorLuz.setLayoutParams(lpMedidorLuz.getmRparams());
        lpMedidorLuz.getmRlayout().addView(etMedidorLuz);
        medidoresLuz.add(etMedidorLuz);
        ///////////////////////////////////////////////////////////////////
        marginTop += addMargin; //movemos el margen de arriba
        LayoutPersonalizada lpNis = new LayoutPersonalizada(marginTop);
        etNis = new EditText(getActivity());
        etNis.setId(idEditText);
        etNis.setBackgroundResource(R.drawable.et_redondo);
        etNis.setHint("Nis " + (idEditText + 1));
        etNis.setInputType(InputType.TYPE_CLASS_NUMBER);
        etNis.setLayoutParams(lpNis.getmRparams());
        lpNis.getmRlayout().addView(etNis);
        nises.add(etNis);
    }

    private class LayoutPersonalizada {
        private RelativeLayout mRlayout;
        private RelativeLayout.LayoutParams mRparams;

        LayoutPersonalizada(int marginTop) {
            mRlayout = getActivity().findViewById(R.id.fDatos);
            mRparams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            mRparams.addRule(RelativeLayout.ALIGN_START, R.id.tvDatosRelevados);
            mRparams.addRule(RelativeLayout.ALIGN_END, R.id.tvDatosRelevados);
            mRparams.addRule(RelativeLayout.BELOW, R.id.tvDatosRelevados);
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
