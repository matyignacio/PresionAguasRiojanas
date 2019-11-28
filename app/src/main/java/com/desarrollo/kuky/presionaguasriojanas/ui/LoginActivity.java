package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.UsuarioControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.Usuario;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.validarCampos;

public class LoginActivity extends AppCompatActivity {

    // UI references.
    public static Usuario usuario = new Usuario();
    private ArrayList<EditText> inputs = new ArrayList<>();
    private EditText etMail;
    private EditText etClave;
    private UsuarioControlador uControlador = new UsuarioControlador();
    private ProgressBar progressBar;
    private TextView tvProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // -----------------------------------------------------------------------------------------
        // CAPTURAMOS LOS ELEMENTOS
        progressBar = findViewById(R.id.progressBar);
        tvProgressBar = findViewById(R.id.tvProgressBar);
        etMail = findViewById(R.id.etMail);
        etClave = findViewById(R.id.etClave);
        inputs.add(etMail);
        inputs.add(etClave);
        Button bLogin = findViewById(R.id.bLogin);
        /** SETEAMOS LOS TYPEFACES*/
        setPrimaryFontBold(this, etMail);
        setPrimaryFontBold(this, etClave);
        setPrimaryFontBold(this, bLogin);
        /**************************/
        bLogin.setOnClickListener(view -> attemptLogin());
        if (uControlador.existeUsuario(this) == Util.EXITOSO) {
            abrirActivity(LoginActivity.this, InicioActivity.class);
        } else {
            /** ACTUALIZAMOS LOS FORMATOS DE TABLAS */
            uControlador.actualizarTablas(this);
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private void attemptLogin() {
        if (validarCampos(this, inputs) == EXITOSO) {
            uControlador.loguearUsuario(this,
                    etMail.getText().toString(),
                    etClave.getText().toString(),
                    progressBar,
                    tvProgressBar,
                    () -> {
                        uControlador.validarUsuario(this, LoginActivity.usuario.getId(), progressBar, tvProgressBar, () -> {
                            abrirActivity(this, InicioActivity.class);
                            return null;
                        });
                        return null;
                    });
        }
    }

}
