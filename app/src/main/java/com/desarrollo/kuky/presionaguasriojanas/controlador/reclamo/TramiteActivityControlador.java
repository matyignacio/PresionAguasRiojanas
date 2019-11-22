package com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.Motivo;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.Reclamo;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.TipoTramite;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.Tramite;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.ui.reclamo.ReclamoActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.reclamo.ResolucionActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.reclamo.TramitesActivity;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.ui.reclamo.TramitesActivity.tramites;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.displayProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.lockProgressBar;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class TramiteActivityControlador {

    public void extraerTodos(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        displayProgressBar(a, progressBar, tvProgressBar, "Cargando tramites...");
        tramites = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT t.tpo_tram, t.num_tram, r.descripcion, t.motivo, r.razon_sol, m.descripcion" +
                "                            FROM GTtramite t, GTreclamo r, GTmot_req m" +
                "                            WHERE t.tpo_tram = r.tpo_tram" +
                "                            AND t.num_tram = r.num_tram" +
                "                            AND t.motivo = m.motivo ", null);
        while (c.moveToNext()) {
            Tramite tramite = new Tramite();
            Motivo motivo = new Motivo(c.getString(3));
            motivo.setDescripcion(c.getString(5));
            Reclamo reclamo = new Reclamo(new TipoTramite(c.getString(0)), c.getInt(1));
            reclamo.setRazonSocial(c.getString(4));
            tramite.setTipoTramite(new TipoTramite(c.getString(0)));
            tramite.setReclamo(reclamo);
            tramite.setDescripcion(c.getString(2));
            tramite.setMotivo(motivo);
            tramites.add(tramite);
        }
        c.close();
        db.close();
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
        ResolucionActivity.tipoResolucionSpinner = tipoResolucionControlador.extraerPorMotivo(a, motivo);
        if (ResolucionActivity.tipoResolucionSpinner.getResoluciones().size() > 0) {
            abrirActivity(a, ResolucionActivity.class);
        } else {
            mostrarMensaje(a, "No existen resoluciones para este motivo");
        }
    }

}
