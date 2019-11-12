package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.app.Activity;

import com.desarrollo.kuky.presionaguasriojanas.util.InternetDetector;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class InspeccionActivityControlador {
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
             *  relevamientoControlador.sincronizarDeSqliteToMysql(a); 1
             *  relevamientoMedidorControlador.sincronizarDeSqliteToMysql(a); 2
             *  tipoInmuebleControlador.sincronizarDeMysqlToSqlite(a); 3
             *  barrioControlador.sincronizarDeMysqlToSqlite(a); 4
             *  relevamientoMedidorControlador.sincronizarDeMysqlToSqlite(a); 5
             *  relevamientoControlador.sincronizarDeMysqlToSqlite(a); 6
             * */
            check = ERROR;
            RelevamientoControlador relevamientoControlador = new RelevamientoControlador();
            relevamientoControlador.sincronizarDeSqliteToMysql(a);
            check = EXITOSO;
        }
        return check;
    }
}
