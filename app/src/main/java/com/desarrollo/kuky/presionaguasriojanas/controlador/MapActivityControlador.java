package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;

public class MapActivityControlador {
    public int sync(Activity a) {
        int check = ERROR;
        PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
        HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();
        if (puntoPresionControlador.sincronizarDeSqliteToMysql(a) == EXITOSO) {
            if (historialPuntosControlador.sincronizarDeSqliteToMysql(a) == EXITOSO) {
                if (historialPuntosControlador.sincronizarDeMysqlToSqlite(a) == EXITOSO) {
                    if (puntoPresionControlador.sincronizarDeMysqlToSqlite(a) == EXITOSO) {
                        check = EXITOSO;
                    }
                }
            }
        }
        return check;
    }
}
