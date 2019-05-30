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
    public static int EXITOSO = 1;
    public static int ERROR = 0;
    public static int ESTANDAR_MEDICION = 3;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
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
        String fecha, minutos;
        if (date.getMinutes() < 10) {
            minutos = "0" + date.getMinutes();
        } else {
            minutos = String.valueOf(date.getMinutes());
        }
        fecha = date.getDay() + "/" + date.getMonth() + "/" + (date.getYear() + 1900) +
                " a las: " + date.getHours() + ":" + minutos;
        return fecha;
    }
//
//    public static void showDialog(Activity a, int layout) {
//
//
//    }
}