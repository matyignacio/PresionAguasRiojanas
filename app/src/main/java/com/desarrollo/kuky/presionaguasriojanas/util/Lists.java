package com.desarrollo.kuky.presionaguasriojanas.util;

import java.util.ArrayList;
import java.util.List;

public class Lists {

    public static List<String> labelsCoeficienteZonal = new ArrayList<>();
    public static List<String> labelsEstado = new ArrayList<>();
    public static List<String> labelsMedida = new ArrayList<>();

    public static void formInmueble() {
        if (labelsCoeficienteZonal.size() == 0) {
            labelsCoeficienteZonal.add("1.0");
            labelsCoeficienteZonal.add("1.3");
            labelsEstado.add("Activa");
            labelsEstado.add("Baja");
            labelsMedida.add("SI");
            labelsMedida.add("NO");
        }
    }

}
