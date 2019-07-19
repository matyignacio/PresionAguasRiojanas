package com.desarrollo.kuky.presionaguasriojanas.ui.presion;

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
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class ControlCalidadFragment extends Fragment {
    private EditText etCloro, etMuestra;
    private CheckBox cbMuestra;
    private Button bCancelar, bAceptar;
    public Calidad calidad = new Calidad();


    public ControlCalidadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control_calidad, container, false);
        //Nuevos parametros para el view del fragmento
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.setMargins(30, 15, 30, 15);
        view.setLayoutParams(params);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        etCloro = getActivity().findViewById(R.id.etCloro);
        bAceptar = getActivity().findViewById(R.id.bAceptar);
        bCancelar = getActivity().findViewById(R.id.bCancelar);
        etMuestra = getActivity().findViewById(R.id.etMuestra);
        cbMuestra = getActivity().findViewById(R.id.cbMuestra);
        /** SETEAMOS LOS TYPEFACES*/
        setPrimaryFontBold(getActivity(), etCloro);
        setPrimaryFontBold(getActivity(), etMuestra);
        setPrimaryFontBold(getActivity(), cbMuestra);

        bAceptar.setOnClickListener(v -> {
            NuevoPuntoActivity.cbCalidad.setEnabled(true);
            NuevoPuntoActivity.bEnviarNuevoPunto.setEnabled(true);
            Util.cerrarFragmento(getActivity(), this);
        });
        bCancelar.setOnClickListener(v -> {
            NuevoPuntoActivity.cbCalidad.setChecked(false);
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
        etCloro.setText(String.valueOf(calidad.getCloro()));
        etMuestra.setText(calidad.getMuestra());
    }

    @Override
    public void onPause() {
        super.onPause();
        calidad.setCloro(Float.parseFloat(etCloro.getText().toString()));
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
