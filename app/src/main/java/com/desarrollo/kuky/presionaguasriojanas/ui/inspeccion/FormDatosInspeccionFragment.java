package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.util.Lists;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import static com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.InspeccionActivity.bSiguienteFragmento;
import static com.desarrollo.kuky.presionaguasriojanas.util.Lists.labelsEstado;
import static com.desarrollo.kuky.presionaguasriojanas.util.Lists.labelsMedida;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class FormDatosInspeccionFragment extends Fragment {
    ImageButton bNuevoDato;

    int marginTop = 15;
    int addMargin = 130;
    int idEditText = 0;
    int cantidadEditText = 6;
    Spinner sEstado, sMedida;
    EditText etUnidad, etMedidorAgua, etMedidorLuz, etNis;


    public FormDatosInspeccionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_datos_inspeccion, container, false);

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
        /** SETEO LISTAS QUE VOY A USAR EN EL FORM*/
        Lists.formDatosRelevados();
        /************************************************/
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        marginTop = 15;
        idEditText = 0;
        bSiguienteFragmento.setOnSystemUiVisibilityChangeListener(v -> {
            mostrarMensaje(getActivity(), "hola");
        });
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
    }

    public void nuevoRegistro(int marginTop, int idEditText) {
        LayoutPersonalizada lpUnidad = new LayoutPersonalizada(marginTop);
        etUnidad = new EditText(getActivity());
        etUnidad.setId(idEditText);
        etUnidad.setBackgroundResource(R.drawable.et_redondo);
        etUnidad.setHint("Unidad " + (idEditText + 1));
        etUnidad.setLayoutParams(lpUnidad.getmRparams());
        lpUnidad.getmRlayout().addView(etUnidad);
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
                () -> {
                    return null;
                },
                () -> {
                    return null;
                });
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
                () -> {
                    return null;
                },
                () -> {
                    return null;
                });
        ///////////////////////////////////////////////////////////////////
        marginTop += addMargin; //movemos el margen de arriba
        LayoutPersonalizada lpMedidorAgua = new LayoutPersonalizada(marginTop);
        etMedidorAgua = new EditText(getActivity());
        etMedidorAgua.setId(idEditText);
        etMedidorAgua.setBackgroundResource(R.drawable.et_redondo);
        etMedidorAgua.setHint("Medidor de Agua " + (idEditText + 1));
        etMedidorAgua.setLayoutParams(lpMedidorAgua.getmRparams());
        lpMedidorAgua.getmRlayout().addView(etMedidorAgua);
        ///////////////////////////////////////////////////////////////////
        marginTop += addMargin; //movemos el margen de arriba
        LayoutPersonalizada lpMedidorLuz = new LayoutPersonalizada(marginTop);
        etMedidorLuz = new EditText(getActivity());
        etMedidorLuz.setId(idEditText);
        etMedidorLuz.setBackgroundResource(R.drawable.et_redondo);
        etMedidorLuz.setHint("Medidor de Luz " + (idEditText + 1));
        etMedidorLuz.setLayoutParams(lpMedidorLuz.getmRparams());
        lpMedidorLuz.getmRlayout().addView(etMedidorLuz);
        ///////////////////////////////////////////////////////////////////
        /** INSTANCIAMOS LOS EDITTEXTS ************************************/
        marginTop += addMargin; //movemos el margen de arriba
        LayoutPersonalizada lpNis = new LayoutPersonalizada(marginTop);
        etNis = new EditText(getActivity());
        etNis.setId(idEditText);
        etNis.setBackgroundResource(R.drawable.et_redondo);
        etNis.setHint("Nis " + (idEditText + 1));
        etNis.setLayoutParams(lpNis.getmRparams());
        lpNis.getmRlayout().addView(etNis);
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
