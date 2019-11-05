package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;

import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.ERROR_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Errores.MENSAJE_ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.getPreference;

public class ErrorActivity extends AppCompatActivity {
    TextView tvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        tvError = findViewById(R.id.tvError);
        tvError.setText(getPreference(this, ERROR_PREFERENCE, MENSAJE_ERROR));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        abrirActivity(this, LoginActivity.class);
    }
}
