package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion;

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
import com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion.InspeccionActivityControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion.RelevamientoActivityControlador;
import com.desarrollo.kuky.presionaguasriojanas.ui.InicioActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PRIMER_INICIO_MODULO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class InspeccionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressBar progressBar;
    private TextView tvProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspeccion);
        progressBar = findViewById(R.id.progressBar);
        tvProgressBar = findViewById(R.id.tvProgressBar);
        /************************************************/
        Button bNuevaInspeccion = findViewById(R.id.bNuevaInspeccion);
        Button bRelevamiento = findViewById(R.id.bRelevamiento);
        /** SETEAMOS TYPEFACES  */
        setPrimaryFontBold(this, bNuevaInspeccion);
        setPrimaryFontBold(this, bRelevamiento);
        /************************/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        primerInicio();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        navigationView.setItemIconTintList(null);
        View headerView = navigationView.getHeaderView(0);
        TextView subTitle = headerView.findViewById(R.id.tvUsuarioNavBar);
        subTitle.setText(LoginActivity.usuario.getNombre());
        /************************/
        bNuevaInspeccion.setOnClickListener(v -> abrirActivity(this, NuevaInspeccion.class));
        bRelevamiento.setOnClickListener(v -> {
            RelevamientoActivityControlador relevamientoActivityControlador =
                    new RelevamientoActivityControlador();
            relevamientoActivityControlador.abrirActivityTask(this, "04");
        });

    }

    @Override
    public void onBackPressed() {
        /* LO QUE HACE CUANDO VUELVA*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (progressBar.getVisibility() != View.VISIBLE) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                abrirActivity(this, InicioActivity.class);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.action_sync) {
            Util.createCustomDialog(this, "¿Esta seguro de sincronizar?",
                    "",
                    "Sincronizar",
                    "Cancelar",
                    // ACEPTAR
                    () -> {
                        sincronizar();
                        return null;
                    },
                    // CANCELAR
                    () -> {
                        return null;
                    }).show();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sincronizar() {
        InspeccionActivityControlador inspeccionActivityControlador = new InspeccionActivityControlador();
        inspeccionActivityControlador.sincronizar(this, progressBar, tvProgressBar);
    }

    private void primerInicio() {
        /**
         * A LA MODIFICACION DE LA BANDERA LA HAGO EN EL onPostExecute del DatosRelevadosControlador.syncMysqlToSqlite
         * */
        if (LoginActivity.usuario.getBanderaModuloInspeccion() == PRIMER_INICIO_MODULO) {
            sincronizar();
        }
    }
}
