package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.desarrollo.kuky.presionaguasriojanas.R;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class FormObservacionesInspeccionFragment extends Fragment {
    EditText etObservaciones;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_form_observaciones_inspeccion, container, false);
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
    public void onResume() {
        etObservaciones = getActivity().findViewById(R.id.etObservaciones);
        InspeccionActivity.bSiguienteFragmento.setVisibility(View.INVISIBLE);
        InspeccionActivity.bGuardarInspeccion.setVisibility(View.VISIBLE);
        InspeccionActivity.bGuardarInspeccion.setOnClickListener(v -> {
            mostrarMensaje(getActivity(), etObservaciones.getText().toString());
        });
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        InspeccionActivity.bSiguienteFragmento.setVisibility(View.VISIBLE);
        InspeccionActivity.bGuardarInspeccion.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}