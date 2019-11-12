package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.app.Activity;

import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Barrio;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.RelevamientoActivity;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class RelevamientoActivityControlador {
    public static ArrayList<Barrio> barrios;

    public void abrirActivityTask(Activity a, String zonaBarrios) {
        BarrioControlador barrioControlador = new BarrioControlador();
        barrios = new ArrayList<>();
        barrios = barrioControlador.extraerTodosPorLocalidad(a, zonaBarrios);
        if (barrios.size() > 0) {
            abrirActivity(a, RelevamientoActivity.class);
        } else {
            mostrarMensaje(a, "No se obtuvieron barrios para mostrar.");
        }
    }
}
