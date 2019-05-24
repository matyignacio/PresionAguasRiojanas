package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.desarrollo.kuky.presionaguasriojanas.R;

public class NuevoPuntoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_punto);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /* LO QUE HACE CUANDO VUELVA*/
        Intent intent = new Intent(NuevoPuntoActivity.this, MapActivity.class);
        startActivity(intent);
        this.finish();
    }
}
