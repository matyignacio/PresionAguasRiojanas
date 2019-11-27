package com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.controlador.UsuarioControlador;
import com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion.BarrioControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.Tramite;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.reclamo.NuevaResolucionActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.reclamo.ReclamoActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.reclamo.ResolucionesActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.reclamo.TramitesActivity;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.ui.reclamo.ResolucionesActivity.resolucionReclamos;
import static com.desarrollo.kuky.presionaguasriojanas.ui.reclamo.TramitesActivity.tramites;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_BAJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PRIMER_INICIO_MODULO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.SEGUNDO_INICIO_MODULO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class TramiteActivityControlador {
    private TipoTramiteControlador tipoTramiteControlador = new TipoTramiteControlador();
    private MotivoControlador motivoControlador = new MotivoControlador();
    private TipoResolucionControlador tipoResolucionControlador = new TipoResolucionControlador();
    private ResolucionMotivoControlador resolucionMotivoControlador = new ResolucionMotivoControlador();
    private ReclamoControlador reclamoControlador = new ReclamoControlador();
    private BarrioControlador barrioControlador = new BarrioControlador();
    private TramiteControlador tramiteControlador = new TramiteControlador();
    private ResolucionReclamoControlador resolucionReclamoControlador = new ResolucionReclamoControlador();

    public void abrirTramiteActivity(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        displayProgressBar(a, progressBar, tvProgressBar, "Cargando tramites...");
        tramites = new ArrayList<>();
        TramiteControlador tramiteControlador = new TramiteControlador();
        tramites = tramiteControlador.extraerTodos(a);
        lockProgressBar(a, progressBar, tvProgressBar);
//        if (tramites.size() > 0) {
        abrirActivity(a, TramitesActivity.class);
//        } else {
//            mostrarMensaje(a, "No hay tramites para mostrar");
//        }
    }

    public void abrirReclamoActivity(Activity a, Tramite tramite) {
        try {
            ReclamoControlador reclamoControlador = new ReclamoControlador();
            tramite.setReclamo(reclamoControlador.extraer(a,
                    tramite.getReclamo().getTipoTramite().getTipo(),
                    tramite.getReclamo().getNumeroTramite()));
            ReclamoActivity.tramite = tramite;
            abrirActivity(a, ReclamoActivity.class);
        } catch (Exception e) {
            mostrarMensaje(a, e.toString());
        }
    }

    public void abrirResolucionActivity(Activity a, String motivo) {
        TipoResolucionControlador tipoResolucionControlador = new TipoResolucionControlador();
        NuevaResolucionActivity.tipoResolucionSpinner = tipoResolucionControlador.extraerPorMotivo(a, motivo);
        if (NuevaResolucionActivity.tipoResolucionSpinner.getResoluciones().size() > 0) {
            abrirActivity(a, NuevaResolucionActivity.class);
        } else {
            mostrarMensaje(a, "No existen resoluciones para este motivo");
        }
    }

    public void abrirResolucionesActivity(Activity a, Tramite tramite) {
        resolucionReclamos = new ArrayList<>();
        ResolucionReclamoControlador resolucionReclamoControlador = new ResolucionReclamoControlador();
        resolucionReclamos = resolucionReclamoControlador.extraerTodosPorTramite(a, tramite);
        if (resolucionReclamos.size() > 0) {
            abrirActivity(a, ResolucionesActivity.class);
        } else {
            mostrarMensaje(a, "Este tramite aun no tiene resoluciones");
        }
    }

    public void sincronizar(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        tipoTramiteControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
            motivoControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                tipoResolucionControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                    resolucionMotivoControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                        reclamoControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                            barrioControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                                tramiteControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                                    resolucionReclamoControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar, () -> {
                                        UsuarioControlador usuarioControlador = new UsuarioControlador();
                                        if (LoginActivity.usuario.getBanderaModuloReclamo() == PRIMER_INICIO_MODULO) {
                                            usuarioControlador.editarBanderaModuloReclamo(a, SEGUNDO_INICIO_MODULO);
                                        }
                                        usuarioControlador.editarBanderaSyncModuloReclamo(a, BANDERA_BAJA);
                                        TramiteActivityControlador tramiteActivityControlador = new TramiteActivityControlador();
                                        tramiteActivityControlador.abrirTramiteActivity(a, progressBar, tvProgressBar);
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
                return null;
            });
            return null;
        });
    }

}
