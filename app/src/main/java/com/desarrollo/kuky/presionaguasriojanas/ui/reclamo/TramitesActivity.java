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
import com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo.TipoTramiteControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo.TramiteActivityControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.Tramite;
import com.desarrollo.kuky.presionaguasriojanas.ui.InicioActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.adapters.lvaTramite;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.util.ArrayList;

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
        View headerView = navigationView.getHeaderView(0);
        TextView subTitle = headerView.findViewById(R.id.tvUsuarioNavBar);
        subTitle.setText(LoginActivity.usuario.getNombre());
        /////////////////////////////////////////////////////////////

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
        abrirActivity(this, InicioActivity.class);
    }

    private void sincronizar() {
        TipoTramiteControlador tipoTramiteControlador = new TipoTramiteControlador();
        tipoTramiteControlador.syncMysqlToSqlite(this, progressBar, tvProgressBar);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_sync) {
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
}
