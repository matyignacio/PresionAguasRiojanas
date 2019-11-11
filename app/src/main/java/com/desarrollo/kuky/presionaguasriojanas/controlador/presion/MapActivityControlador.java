package com.desarrollo.kuky.presionaguasriojanas.controlador.presion;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.checkConnection;

public class MapActivityControlador {
    int check = ERROR;

    public int sync(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        checkConnection(a, () -> {
            /**
             * ORDEN:
             *  puntoPresionControlador.insertToMySQL(a, progressBar, tvProgressBar)
             *  puntoPresionControlador.updateToMySQL(a, progressBar, tvProgressBar)
             *  historialPuntosControlador.insertToMySQL(a, progressBar, tvProgressBar)
             *  tipoPuntoControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar)
             *  ordenControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar)
             *  puntoPresionControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar)
             *  historialPuntosControlador.syncMysqlToSqlite(a, progressBar, tvProgressBar)
             * */
            PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
            puntoPresionControlador.insertToMySQL(a, progressBar, tvProgressBar);
            check = EXITOSO;
            return null;
        });
        return check;
    }
}
