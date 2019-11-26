package com.desarrollo.kuky.presionaguasriojanas.ui.reclamo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo.ResolucionReclamoControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.ResolucionReclamo;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.TipoResolucion;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.ui.reclamo.ReclamoActivity.tramite;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.cargarSpinner;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.validarCampos;

public class NuevaResolucionActivity extends AppCompatActivity {

    public static TipoResolucion.TipoResolucionSpinner tipoResolucionSpinner = new TipoResolucion().new TipoResolucionSpinner();
    Spinner sTipoResolucion;
    EditText etObservaciones;
    private ArrayList<EditText> inputs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_resolucion);
        sTipoResolucion = findViewById(R.id.sTipoResolucion);
        cargarSpinner(sTipoResolucion,
                this,
                0,
                tipoResolucionSpinner.getLabelsResoluciones(),
                () -> null,
                () -> null);
        etObservaciones = findViewById(R.id.etObservaciones);
        Button bEnviarResolucion = findViewById(R.id.bEnviarResolucion);
        /** SETEAMOS LOS TYPEFACES*/
        setPrimaryFontBold(this, etObservaciones);
        setPrimaryFontBold(this, bEnviarResolucion);
        /**************************/
        inputs.add(etObservaciones);
        bEnviarResolucion.setOnClickListener(v -> {
            if (validarCampos(this, inputs) == EXITOSO) {
                Util.showDialog(this,
                        R.layout.dialog_guardar,
                        "Si, Guardar",
                        () -> {
                            insertarResolucion();
                            return null;
                        }
                );
            }
        });
    }

    @Override
    public void onBackPressed() {
        /* LO QUE HACE CUANDO VUELVA*/
        abrirActivity(this, ReclamoActivity.class);
    }

    private void insertarResolucion() {
        ResolucionReclamo resolucionReclamo = new ResolucionReclamo();
        ResolucionReclamoControlador resolucionReclamoControlador = new ResolucionReclamoControlador();
        resolucionReclamo.setTipoTramite(tramite.getTipoTramite().getTipo());
        resolucionReclamo.setNumeroTramite(tramite.getReclamo().getNumeroTramite());
        resolucionReclamo.setCodigoResolucion(tipoResolucionSpinner.getResoluciones().get(sTipoResolucion.getSelectedItemPosition()).getResolucion());
        resolucionReclamo.setObservaciones(etObservaciones.getText().toString());
        resolucionReclamo.setUsuario(LoginActivity.usuario.getId());
        if (resolucionReclamoControlador.insertar(this, resolucionReclamo) == EXITOSO) {
            abrirActivity(this, TramitesActivity.class);
        }
    }
}
