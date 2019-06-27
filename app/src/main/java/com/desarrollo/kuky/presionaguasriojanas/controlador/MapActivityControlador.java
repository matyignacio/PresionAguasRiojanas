package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;

public class MapActivityControlador {

    public int sync(Activity a) {
        /**
         * ORDEN:
         *  puntoPresionControlador.sincronizarDeSqliteToMysql(a)
         *  historialPuntosControlador.sincronizarDeSqliteToMysql(a)
         *  tipoPuntoControlador.sincronizarDeMysqlToSqlite(a)
         *  ordenControlador.sincronizarDeMysqlToSqlite(a)
         *  puntoPresionControlador.sincronizarDeMysqlToSqlite(a)
         *  historialPuntosControlador.sincronizarDeMysqlToSqlite(a)
         * */
        int check = ERROR;
        PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
        if (puntoPresionControlador.sincronizarDeSqliteToMysql(a) == EXITOSO) {
            check = EXITOSO;
        }
        return check;
    }
}
