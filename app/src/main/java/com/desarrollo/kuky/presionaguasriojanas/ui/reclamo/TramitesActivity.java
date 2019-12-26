package com.desarrollo.kuky.presionaguasriojanas.ui.reclamo;

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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo.TramiteActivityControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.Tramite;
import com.desarrollo.kuky.presionaguasriojanas.ui.InicioActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.adapters.lvaTramite;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PRIMER_INICIO_MODULO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;

public class TramitesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ArrayList<Tramite> tramites;
    private ProgressBar progressBar;
    private TextView tvProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tramites);
        progressBar = findViewById(R.id.progressBar);
        tvProgressBar = findViewById(R.id.tvProgressBar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        /////////////////////////////////////////////////////////////
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        navigationView.setItemIconTintList(null);
        View headerView = navigationView.getHeaderView(0);
        TextView subTitle = headerView.findViewById(R.id.tvUsuarioNavBar);
        subTitle.setText(LoginActivity.usuario.getNombre());
        /////////////////////////////////////////////////////////////
        primerInicio();
        ListView lvTramite = findViewById(R.id.lvTramites);
        lvaTramite adaptador = new lvaTramite(this, tramites);
        lvTramite.setAdapter(adaptador);
        lvTramite.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvTramite.setOnItemClickListener((parent, view, position, id) -> {
            TramiteActivityControlador tramiteActivityControlador = new TramiteActivityControlador();
            tramiteActivityControlador.abrirReclamoActivity(this, tramites.get(position));
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

    private void primerInicio() {
        /**
         * A LA MODIFICACION DE LA BANDERA LA HAGO EN EL TramiteControlador
         * */
        if (LoginActivity.usuario.getBanderaModuloReclamo() == PRIMER_INICIO_MODULO) {
            new TramiteActivityControlador().sincronizar(this, progressBar, tvProgressBar);
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
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
                        new TramiteActivityControlador().sincronizar(this, progressBar, tvProgressBar);
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
}
