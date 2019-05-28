package com.desarrollo.kuky.presionaguasriojanas.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class Util {
    public static String DATA_BASE = "presion_aguas";
    public static String DIRECCION_IP = "192.168.1.46";
    public static String PUERTO = "3306";
    public static String USER = "root";
    public static String CLAVE = "root";
    public static Integer EXITOSO = 1;
    public static Integer ERROR = 0;
    public static Integer ESTANDAR_MEDICION = 3;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static String ID_PUNTO_PRESION_SHARED_PREFERENCE = "id_punto_presion";

    public static void abrirActivity(Activity a, Class destino) {
        Intent intent = new Intent(a, destino);
        a.startActivity(intent);
        a.finish();
    }

    public static void mostrarMensaje(Context c, String mensaje) {
        Toast.makeText(c, mensaje, Toast.LENGTH_SHORT).show();
        Log.e("MOSTRARMENSAJE:::", mensaje);
    }

    public static String convertirFecha(Date date) {
        String fecha;
        fecha = date.getDay() + "/" + date.getMonth() + "/" + (date.getYear() + 1900) +
                " a las: " + date.getHours() + ":" + date.getMinutes();
        return fecha;
    }
}
