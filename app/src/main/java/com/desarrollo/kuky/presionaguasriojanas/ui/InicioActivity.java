package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.UsuarioControlador;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.NOMBRE_USUARIO_SHARED_PREFERENCE;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PREFS_NAME;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;

public class InicioActivity extends AppCompatActivity {

    private Button bMapas;
    private TextView tvUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        tvUsuario = findViewById(R.id.tvUsuario);
        bMapas = findViewById(R.id.bMapas);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        tvUsuario.setText(" Bienvenido " +
                settings.getString(NOMBRE_USUARIO_SHARED_PREFERENCE, ""));
        bMapas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirActivity(InicioActivity.this, MapActivity.class);
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_sign_out) {
            /* LO QUE TENGAMOS QUE HACER PARA CERRAR SESION*/
            showDialogCerrarSesion(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void showDialogCerrarSesion(final Activity a) {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(a);
        View promptView = layoutInflater.inflate(R.layout.dialog_cerrar_sesion, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
        alertDialogBuilder.setView(promptView);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Si, cerrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UsuarioControlador usuarioControlador = new UsuarioControlador();
                        if (usuarioControlador.eliminarUsuario(a) == EXITOSO) {
                            abrirActivity(InicioActivity.this, LoginActivity.class);
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
