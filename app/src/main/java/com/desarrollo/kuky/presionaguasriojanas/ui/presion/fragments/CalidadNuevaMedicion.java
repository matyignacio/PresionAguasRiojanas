package com.desarrollo.kuky.presionaguasriojanas.ui.presion.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.ui.presion.NuevaPresionActivity;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class CalidadNuevaMedicion extends Fragment {
    private EditText etCloro, etMuestra;
    private CheckBox cbMuestra;
    public Calidad calidad = new Calidad();


    public CalidadNuevaMedicion() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calidad_nueva_medicion, container, false);
        //Nuevos parametros para el view del fragmento
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.setMargins(30, 15, 30, 15);
        view.setLayoutParams(params);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        etCloro = getActivity().findViewById(R.id.etCloro);
        Button bAceptar = getActivity().findViewById(R.id.bAceptar);
        Button bCancelar = getActivity().findViewById(R.id.bCancelar);
        etMuestra = getActivity().findViewById(R.id.etMuestra);
        cbMuestra = getActivity().findViewById(R.id.cbMuestra);
        /** SETEAMOS LOS TYPEFACES*/
        setPrimaryFontBold(getActivity(), etCloro);
        setPrimaryFontBold(getActivity(), etMuestra);
        setPrimaryFontBold(getActivity(), cbMuestra);

        bAceptar.setOnClickListener(v -> {
            NuevaPresionActivity.cbCalidad.setEnabled(true);
            NuevaPresionActivity.bEnviarMedicion.setEnabled(true);
            Util.cerrarFragmento(getActivity(), this);
        });
        bCancelar.setOnClickListener(v -> {
            NuevaPresionActivity.cbCalidad.setChecked(false);
            Util.cerrarFragmento(getActivity(), this);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        cbMuestra.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                etMuestra.setVisibility(View.VISIBLE);
            } else {
                etMuestra.setVisibility(View.INVISIBLE);
                etMuestra.setText("");
            }
        });
        if (calidad.getCloro() != 0) {
            etCloro.setText(String.valueOf(calidad.getCloro()));
        }
        etMuestra.setText(calidad.getMuestra());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!etCloro.getText().toString().equals("")) {
            calidad.setCloro(Float.parseFloat(etCloro.getText().toString()));
        }
        calidad.setMuestra(etMuestra.getText().toString());
    }

    public class Calidad {
        float cloro;
        String muestra;

        public float getCloro() {
            return cloro;
        }

        public void setCloro(float cloro) {
            this.cloro = cloro;
        }

        public String getMuestra() {
            return muestra;
        }

        public void setMuestra(String muestra) {
            this.muestra = muestra;
        }
    }
}
