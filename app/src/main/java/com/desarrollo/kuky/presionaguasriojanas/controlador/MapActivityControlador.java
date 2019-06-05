package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;

import com.desarrollo.kuky.presionaguasriojanas.ui.MapActivity;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;

public class MapActivityControlador {
    public int sync(Activity a) {
        int check = ERROR;
        PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
        if (puntoPresionControlador.sincronizarDeSqliteToMysql(a) == EXITOSO) {
            check = EXITOSO;
        }
        return check;
    }
}
