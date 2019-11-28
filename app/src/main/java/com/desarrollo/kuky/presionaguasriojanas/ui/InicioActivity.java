package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.presion.MapActivityControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo.TramiteActivityControlador;
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
    Button bModuloReclamo;
    private ProgressBar progressBar;
    private TextView tvProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        progressBar = findViewById(R.id.progressBar);
        tvProgressBar = findViewById(R.id.tvProgressBar);
        bModuloPresion = findViewById(R.id.bModuloPresion);
        bModuloInspeccion = findViewById(R.id.bModuloInspeccion);
        bModuloReclamo = findViewById(R.id.bModuloReclamo);
        /** SETEAMOS LOS TYPEFACES*/
        setPrimaryFontBold(this, bModuloInspeccion);
        setPrimaryFontBold(this, bModuloPresion);
        setPrimaryFontBold(this, bModuloReclamo);
        /**************************/
        evaluarUsuario();
        bModuloPresion.setOnClickListener(view -> {
            MapActivityControlador mapActivityControlador = new MapActivityControlador();
            mapActivityControlador.abrirMapActivity(this, progressBar, tvProgressBar, () -> {
                abrirActivity(this, MapActivity.class);
                return null;
            });
        });
        bModuloInspeccion.setOnClickListener(view -> abrirActivity(this, InspeccionActivity.class));
        bModuloReclamo.setOnClickListener(view -> {
            TramiteActivityControlador tramiteActivityControlador = new TramiteActivityControlador();
            tramiteActivityControlador.abrirTramiteActivity(this, progressBar, tvProgressBar);
        });
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
        if (progressBar.getVisibility() != View.VISIBLE) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                this.finish();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_sign_out) {
            Util.showDialog(this,
                    R.layout.dialog_cerrar_sesion,
                    "Si, cerrar",
                    () -> {
                        if (LoginActivity.usuario.getBanderaSyncModuloPresion() == EXITOSO) {
                            Util.showDialog(this,
                                    R.layout.dialog_debe_sincronizar,
                                    "sincronizar ahora",
                                    () -> {
                                        MapActivityControlador mapActivityControlador = new MapActivityControlador();
                                        mapActivityControlador.sincronizar(InicioActivity.this, progressBar, tvProgressBar);
                                        return null;
                                    });
                            //mostrarMensaje(InicioActivity.this, "Debe sincronizar primero");
                        } else {
                            logOut(InicioActivity.this);
                        }
                        return null;
                    }
            );
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void evaluarUsuario() {
        for (int i = 0; i < LoginActivity.usuario.getModulos().size(); i++) {
            switch (LoginActivity.usuario.getModulos().get(i).getNombre()) {
                case "presion":
                    bModuloPresion.setVisibility(View.VISIBLE);
                    break;
                case "inspeccion":
                    bModuloInspeccion.setVisibility(View.VISIBLE);
                    break;
                case "reclamo":
                    bModuloReclamo.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
