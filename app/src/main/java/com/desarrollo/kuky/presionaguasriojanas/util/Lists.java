package com.desarrollo.kuky.presionaguasriojanas.util;

import java.util.ArrayList;
import java.util.List;

public class Lists {
    public static List<String> labelsTipoInmueble = new ArrayList<>();
    public static List<String> labelsDestino = new ArrayList<>();
    public static List<String> labelsCoeficienteZonal = new ArrayList<>();
    public static List<String> labelsTipoServicio = new ArrayList<>();
    public static List<String> labelsEstado = new ArrayList<>();
    public static List<String> labelsMedida = new ArrayList<>();

    public static void formInmueble() {
        if (labelsCoeficienteZonal.size() == 0) {
            //////////////////////////////////////////////////////////////
            labelsTipoInmueble.add("Unifamiliar");
            labelsTipoInmueble.add("Multifamiliar");
            labelsTipoInmueble.add("Comercial");
            labelsTipoInmueble.add("Baldio");
            labelsTipoInmueble.add("Fiscal");
            //////////////////////////////////////////////////////////////
            labelsDestino.add("Supermercado");
            labelsDestino.add("Despensa");
            labelsDestino.add("Minimarket");
            labelsDestino.add("Kiosko");
            labelsDestino.add("Lavadero");
            labelsDestino.add("Ninguno de los anteriores");
            //////////////////////////////////////////////////////////////
            labelsCoeficienteZonal.add("1.0");
            labelsCoeficienteZonal.add("1.3");
            //////////////////////////////////////////////////////////////
            labelsTipoServicio.add("MR");
            labelsTipoServicio.add("AR");
            labelsTipoServicio.add("MG");
            labelsTipoServicio.add("MN");
            labelsTipoServicio.add("AN");
            labelsTipoServicio.add("AG");
            labelsTipoServicio.add("MD");
            labelsTipoServicio.add("AD");
            labelsTipoServicio.add("S100");
        }
    }

    public static void formDatosRelevados() {
        if (labelsEstado.size() == 0) {
            labelsEstado.add("Activa");
            labelsEstado.add("Baja");
            labelsMedida.add("SI");
            labelsMedida.add("NO");
        }
    }

}
