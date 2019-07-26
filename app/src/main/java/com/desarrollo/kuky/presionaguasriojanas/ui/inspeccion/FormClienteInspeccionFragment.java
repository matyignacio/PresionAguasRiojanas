package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Cliente;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;

public class FormClienteInspeccionFragment extends Fragment {
    EditText etRazonSocial, etTelefono, etDireccion,
            etBarrio, etUnidad, etTramite, etServ,
            etNis, etMedidorAgua, etEstado, etMedidorLuz,
            etReclama;

    public FormClienteInspeccionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_cliente_inspeccion, container, false);
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
        InspeccionActivity.cliente = new Cliente();
        etRazonSocial = getActivity().findViewById(R.id.etRazonSocial);
        etBarrio = getActivity().findViewById(R.id.etBarrio);
        etDireccion = getActivity().findViewById(R.id.etDireccion);
        etEstado = getActivity().findViewById(R.id.etEstado);
        etMedidorAgua = getActivity().findViewById(R.id.etMedidorAgua);
        etNis = getActivity().findViewById(R.id.etNis);
        etTelefono = getActivity().findViewById(R.id.etTelefono);
        etServ = getActivity().findViewById(R.id.etServ);
        etReclama = getActivity().findViewById(R.id.etReclama);
        etMedidorLuz = getActivity().findViewById(R.id.etMedidorLuz);
        etTramite = getActivity().findViewById(R.id.etTramite);
        etUnidad = getActivity().findViewById(R.id.etUnidad);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        rellenarCliente();
    }

    private void rellenarCliente() {
        try {
            InspeccionActivity.cliente.setRazonSocial(etRazonSocial.getText().toString());
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(), e.toString());
        }
        try {
            InspeccionActivity.cliente.setBarrio(etBarrio.getText().toString());
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(), e.toString());
        }
        try {
            InspeccionActivity.cliente.setDireccion(etDireccion.getText().toString());
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(), e.toString());
        }
        try {
            InspeccionActivity.cliente.setEstado(Boolean.valueOf(etEstado.getText().toString()));
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(), e.toString());
        }
        try {
            InspeccionActivity.cliente.setMedidorAgua(Integer.parseInt(etMedidorAgua.getText().toString()));
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(), e.toString());
        }
        try {
            InspeccionActivity.cliente.setNis(Integer.parseInt(etNis.getText().toString()));
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(), e.toString());
        }
        try {
            InspeccionActivity.cliente.setTelefono(Long.parseLong(etTelefono.getText().toString()));
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(), e.toString());
        }
        try {
            InspeccionActivity.cliente.setServ(etServ.getText().toString());
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(), e.toString());
        }
        try {
            InspeccionActivity.cliente.setReclama(etReclama.getText().toString());
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(), e.toString());
        }
        try {
            InspeccionActivity.cliente.setMedidorLuz(Integer.parseInt(etMedidorLuz.getText().toString()));
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(), e.toString());
        }
        try {
            InspeccionActivity.cliente.setTramite(Integer.parseInt(etTramite.getText().toString()));
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(), e.toString());
        }
        try {
            InspeccionActivity.cliente.setUnidad(Integer.parseInt(etUnidad.getText().toString()));
        } catch (Exception e) {
            mostrarMensajeLog(getActivity(), e.toString());
        }
    }
}
