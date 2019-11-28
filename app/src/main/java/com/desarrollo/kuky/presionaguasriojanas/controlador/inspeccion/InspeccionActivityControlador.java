package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.controlador.UsuarioControlador;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.InspeccionActivity;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_BAJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PRIMER_INICIO_MODULO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.SEGUNDO_INICIO_MODULO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class InspeccionActivityControlador {
    RelevamientoControlador relevamientoControlador = new RelevamientoControlador();
    RelevamientoMedidorControlador relevamientoMedidorControlador = new RelevamientoMedidorControlador();
    TipoInmuebleControlador tipoInmuebleControlador = new TipoInmuebleControlador();
    BarrioControlador barrioControlador = new BarrioControlador();

    public void sincronizar(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        relevamientoControlador.sincronizarDeSqliteToMysql(a, progressBar, tvProgressBar, () -> {
            relevamientoMedidorControlador.sincronizarDeSqliteToMysql(a, progressBar, tvProgressBar, () -> {
                tipoInmuebleControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                    barrioControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                        relevamientoMedidorControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                            relevamientoControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                                mostrarMensaje(a, "Se sincronizo con exito!");
                                UsuarioControlador usuarioControlador = new UsuarioControlador();
                                if (LoginActivity.usuario.getBanderaModuloInspeccion() == PRIMER_INICIO_MODULO) {
                                    usuarioControlador.editarBanderaModuloInspeccion(a, SEGUNDO_INICIO_MODULO);
                                }
                                usuarioControlador.editarBanderaSyncModuloInspeccion(a, BANDERA_BAJA);
                                abrirActivity(a, InspeccionActivity.class);
                                return null;
                            });
                            return null;
                        });
                        return null;
                    });
                    return null;
                });
                return null;
            });
            return null;
        });
    }
}


