package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.UsuarioControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.Usuario;

public class LoginActivity extends AppCompatActivity {

    // UI references.
    public static Usuario usuario;
    private EditText etMail;
    private EditText etClave;
    private Button bLogin;
    UsuarioControlador uControlador = new UsuarioControlador();
    private AttemptLogin attemptLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // -----------------------------------------------------------------------------------------
        // CAPTURAMOS LOS ELEMENTOS
        etMail = findViewById(R.id.etMail);
        etClave = findViewById(R.id.etClave);
        Button bLogin = findViewById(R.id.bLogin);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_sync) {
            // lo ideal aquí sería hacer un intent para abrir una nueva clase como lo siguiente
            Toast.makeText(this, "Sync", Toast.LENGTH_SHORT).show();
//            Intent about = new Intent(getApplicationContext(), About.class);
//            startActivity(about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void attemptLogin() {
        attemptLogin = new AttemptLogin(this);
        attemptLogin.execute();

    }

    private class AttemptLogin extends AsyncTask<String, String, String> {

        Activity a;
        String nombre, pass, regreso;

        public AttemptLogin(Activity a) {
            this.a = a;
        }

        @Override
        protected void onPreExecute() {
            nombre = String.valueOf(etMail.getText());
            pass = String.valueOf(etClave.getText());
            uControlador.extraerPorMailYClave(LoginActivity.this, String.valueOf(etMail.getText()), String.valueOf(etClave.getText()));
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if (nombre.equalsIgnoreCase(usuario.getMail())) {
                    if (pass.equals(usuario.getClave())) {
                        regreso = "correcto";
                    } else {
                        regreso = "La clave no coincide con el usuario";
                    }
                } else {
                    regreso = "La clave no coincide con el usuario";
                }
            } catch (Exception e) {
                regreso = "Error en la conexion";
            }

            return regreso;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("correcto")) {
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(intent);
                a.finish();
            } else {
                Toast.makeText(a, s, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
