package com.desarrollo.kuky.presionaguasriojanas.util;

import android.content.Context;
import android.content.Intent;

public class Util {
    public static String DATA_BASE = "presion_aguas";
    public static String DIRECCION_IP = "192.168.1.46";
    public static String PUERTO = "3306";
    public static String USER = "root";
    public static String CLAVE = "root";
    public static Integer EXITOSO = 1;
    public static Integer ERROR = 0;
    public static Integer ESTANDAR_MEDICION= 3;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static String ID_PUNTO_PRESION_SHARED_PREFERENCE = "id_punto_presion";

    public static void abrirActivity(Context a, Class destino) {
        Intent intent = new Intent(a, destino);
        a.startActivity(intent);

    }
}
