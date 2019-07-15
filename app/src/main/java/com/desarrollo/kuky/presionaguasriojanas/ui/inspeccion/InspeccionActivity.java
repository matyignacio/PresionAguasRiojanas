package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.ui.InicioActivity;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirFragmento;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.cerrarFragmento;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class InspeccionActivity extends AppCompatActivity {
    Button bSiguienteFragmento, bVolver, bGuardarInspeccion, bNuevaInspeccion;
    FormClienteInspeccionFragment formClienteInspeccionFragment = new FormClienteInspeccionFragment();
    FormInmuebleInspeccionFragment formInmuebleInspeccionFragment = new FormInmuebleInspeccionFragment();
    FormMapaInspeccionFragment formMapaInspeccionFragment = new FormMapaInspeccionFragment();
    FormDatosInspeccionFragment formDatosInspeccionFragment = new FormDatosInspeccionFragment();
    ArrayList<android.app.Fragment> fragmentos = new ArrayList<>();
    int form = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspeccion);
        fragmentos.add(formClienteInspeccionFragment);
        fragmentos.add(formInmuebleInspeccionFragment);
        fragmentos.add(formMapaInspeccionFragment);
        fragmentos.add(formDatosInspeccionFragment);
        bVolver = findViewById(R.id.bVolver);
        bNuevaInspeccion = findViewById(R.id.bNuevaInspeccion);
        bSiguienteFragmento = findViewById(R.id.bSiguienteFragmento);
        bGuardarInspeccion = findViewById(R.id.bGuardarInspeccion);
        /** SETEAMOS TYPEFACES  */
        setPrimaryFontBold(this, bVolver);
        setPrimaryFontBold(this, bSiguienteFragmento);
        setPrimaryFontBold(this, bGuardarInspeccion);
        setPrimaryFontBold(this, bNuevaInspeccion);
        bSiguienteFragmento.setText(form + 1 + "/" + fragmentos.size());
        /************************/
        bNuevaInspeccion.setOnClickListener(v -> {
            abrirFragmento(this, R.id.LLInspeccion, formClienteInspeccionFragment);
            setOnButtonsFragment();
        });
        bSiguienteFragmento.setOnClickListener(v -> {
            form = siguienteFragmento(this, fragmentos, R.id.LLInspeccion, form);
            mostrarMensaje(this, String.valueOf(form));
        });
        bVolver.setOnClickListener(v -> {
            form = volverFragmento(this, fragmentos, R.id.LLInspeccion, form);
            mostrarMensaje(this, String.valueOf(form));

        });
    }

    @Override
    public void onBackPressed() {
        abrirActivity(this, InicioActivity.class);

    }

    public int siguienteFragmento(Activity a, ArrayList<android.app.Fragment> fragmentos, int layout, int posicionFormulario) {
        if (posicionFormulario == 0) {//SIGNIFICA POSICION INICIAL
            // SIMPLEMENTE ABRIMOS EL SIGUIENTE FRAGMENTO
            cerrarFragmento(a, fragmentos.get(posicionFormulario));
            posicionFormulario++;
            bSiguienteFragmento.setText(posicionFormulario + 1 + "/" + fragmentos.size());
            abrirFragmento(a, layout, fragmentos.get(posicionFormulario));
            setOnButtonsFragment();
        } else if (posicionFormulario > 0 && posicionFormulario != fragmentos.size() - 1) {
            // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL SIGUIENTE
            cerrarFragmento(a, fragmentos.get(posicionFormulario));
            abrirFragmento(a, layout, fragmentos.get(posicionFormulario + 1));
            posicionFormulario++;
            bSiguienteFragmento.setText((posicionFormulario + 1) + "/" + fragmentos.size());
        } else if (posicionFormulario == fragmentos.size() - 1) {
            posicionFormulario++;
            bSiguienteFragmento.setVisibility(View.INVISIBLE);
            bGuardarInspeccion.setVisibility(View.VISIBLE);
        }
        return posicionFormulario;
    }

    public int volverFragmento(Activity a, ArrayList<android.app.Fragment> fragmentos, int layout, int posicionFormulario) {
        if (posicionFormulario == 0) {//SIGNIFICA POSICION INICIAL
            // SIMPLEMENTE CERRAMOS EL FRAGMENTO
            setOffButtonsFragment();
            cerrarFragmento(a, fragmentos.get(posicionFormulario));
        } else if (posicionFormulario > 0 && posicionFormulario != fragmentos.size()) {
            // EN ESTE CASO CERRAMOS EL FRAGMENTO Y ABRIMOS EL ANTERIOR
            cerrarFragmento(a, fragmentos.get(posicionFormulario));
            posicionFormulario--;
            bSiguienteFragmento.setText(posicionFormulario + 1 + "/" + fragmentos.size());
            abrirFragmento(a, layout, fragmentos.get(posicionFormulario));
        } else if (posicionFormulario == fragmentos.size()) {
            bGuardarInspeccion.setVisibility(View.INVISIBLE);
            bSiguienteFragmento.setVisibility(View.VISIBLE);
            posicionFormulario--;
        }
        return posicionFormulario;
    }

    public void setOnButtonsFragment() {
        bSiguienteFragmento.setVisibility(View.VISIBLE);
        bVolver.setVisibility(View.VISIBLE);
        bNuevaInspeccion.setVisibility(View.INVISIBLE);
    }

    public void setOffButtonsFragment() {
        bSiguienteFragmento.setVisibility(View.INVISIBLE);
        bVolver.setVisibility(View.INVISIBLE);
        bNuevaInspeccion.setVisibility(View.VISIBLE);
    }

}
