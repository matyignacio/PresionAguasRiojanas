package com.desarrollo.kuky.presionaguasriojanas.controlador.presion;

import android.app.Activity;

import com.desarrollo.kuky.presionaguasriojanas.util.InternetDetector;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class MapActivityControlador {
    private InternetDetector internetDetector;
    int check = ERROR;

    public int sync(Activity a) {
        // Initializing Internet Checker
        internetDetector = new InternetDetector(a);
        if (!internetDetector.checkMobileInternetConn()) {
            mostrarMensaje(a, "No hay conexion de red disponible.");
        } else {
            /**
             * ORDEN:
             *  puntoPresionControlador.sincronizarDeSqliteToMysql(a) 1/6
             *  historialPuntosControlador.sincronizarDeSqliteToMysql(a) 2/6
             *  tipoPuntoControlador.sincronizarDeMysqlToSqlite(a) 3/6
             *  ordenControlador.sincronizarDeMysqlToSqlite(a) 4/6
             *  puntoPresionControlador.sincronizarDeMysqlToSqlite(a) 5/6
             *  historialPuntosControlador.sincronizarDeMysqlToSqlite(a) 6/6
             * */
            check = ERROR;
            PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
            if (puntoPresionControlador.sincronizarDeSqliteToMysql(a) == EXITOSO) {
                check = EXITOSO;
            }
        }
        return check;
    }
}
