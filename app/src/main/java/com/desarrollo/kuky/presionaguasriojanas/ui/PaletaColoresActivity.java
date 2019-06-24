package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.desarrollo.kuky.presionaguasriojanas.R;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;

public class PaletaColoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paleta_colores);
    }

    @Override
    public void onBackPressed() {
        abrirActivity(this, MapActivity.class);
    }
}
