package com.desarrollo.kuky.presionaguasriojanas.ui.reclamo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.ResolucionReclamo;
import com.desarrollo.kuky.presionaguasriojanas.ui.adapters.lvaResolucionReclamo;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;

public class ResolucionesActivity extends AppCompatActivity {

    public static ArrayList<ResolucionReclamo> resolucionReclamos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resoluciones);
        ListView lvResoluciones = findViewById(R.id.lvResoluciones);
        lvaResolucionReclamo adaptador = new lvaResolucionReclamo(this, resolucionReclamos);
        lvResoluciones.setAdapter(adaptador);
    }

    @Override
    public void onBackPressed() {
        /* LO QUE HACE CUANDO VUELVA*/
        abrirActivity(this, ReclamoActivity.class);
    }
}
