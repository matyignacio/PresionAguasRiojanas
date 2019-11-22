package com.desarrollo.kuky.presionaguasriojanas.ui.reclamo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.TipoResolucion;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.cargarSpinner;

public class ResolucionActivity extends AppCompatActivity {

    public static TipoResolucion.TipoResolucionSpinner tipoResolucionSpinner = new TipoResolucion().new TipoResolucionSpinner();
    Spinner sTipoResolucion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolucion);
        sTipoResolucion = findViewById(R.id.sTipoResolucion);
        cargarSpinner(sTipoResolucion,
                this,
                0,
                tipoResolucionSpinner.getLabelsResoluciones(),
                () -> null,
                () -> null);
    }

    @Override
    public void onBackPressed() {
        /* LO QUE HACE CUANDO VUELVA*/
        abrirActivity(this, ReclamoActivity.class);
    }
}
