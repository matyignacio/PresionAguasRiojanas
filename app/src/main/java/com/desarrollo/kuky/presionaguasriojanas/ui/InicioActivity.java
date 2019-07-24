package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.presion.MapActivityControlador;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.InspeccionActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.presion.MapActivity;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.logOut;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class InicioActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Button bModuloPresion;
    Button bModuloInspeccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        bModuloPresion = findViewById(R.id.bModuloPresion);
        bModuloInspeccion = findViewById(R.id.bModuloInspeccion);
        /** SETEAMOS LOS TYPEFACES*/
        setPrimaryFontBold(this, bModuloInspeccion);
        setPrimaryFontBold(this, bModuloPresion);
        /**************************/
        evaluarUsuario();
        bModuloPresion.setOnClickListener(view -> abrirActivity(this, MapActivity.class));
        bModuloInspeccion.setOnClickListener(view -> abrirActivity(this, InspeccionActivity.class));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView subTitle = headerView.findViewById(R.id.tvUsuarioNavBar);
        subTitle.setText(LoginActivity.usuario.getNombre());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_sign_out) {
            Util.showDialog(this,
                    R.layout.dialog_cerrar_sesion,
                    "Si, cerrar",
                    () -> {
                        if (LoginActivity.usuario.getBandera_sync_modulo_presion() == EXITOSO) {
                            Util.showDialog(this,
                                    R.layout.dialog_debe_sincronizar,
                                    "sincronizar ahora",
                                    () -> {
                                        MapActivityControlador mapActivityControlador = new MapActivityControlador();
                                        if (mapActivityControlador.sync(InicioActivity.this) == EXITOSO) {
                                        }
                                        return null;
                                    });
                            //mostrarMensaje(InicioActivity.this, "Debe sincronizar primero");
                        } else {
                            logOut(InicioActivity.this);
                        }
                        return null;
                    }
            );
        } else if (id == R.id.action_sync) {
            Util.showDialog(this,
                    R.layout.dialog_sincronizar,
                    "sincronizar",
                    () -> {
                        sincronizar();
                        return null;
                    }
            );
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sincronizar() {
        MapActivityControlador mapActivityControlador = new MapActivityControlador();
        if (mapActivityControlador.sync(this) == EXITOSO) {
        }
    }

    private void evaluarUsuario() {
        boolean prueba = true;
        if (prueba) {
            bModuloInspeccion.setEnabled(true);
            bModuloInspeccion.setBackgroundResource(R.drawable.boton_redondo);
        } else {
            bModuloInspeccion.setEnabled(false);
            bModuloInspeccion.setBackgroundResource(R.drawable.boton_redondo_disabled);
        }
    }
}
