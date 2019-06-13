package com.desarrollo.kuky.presionaguasriojanas.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

/**
 * EN ESTA CLASE UTIL VAMOS A IR CREANDO LAS CONSTANTES O FUNCIONES QUE NOS SIRVAN PARA
 * HACER CODIGO LIMPIO DURANTE EL DESARROLLO DE NUESTRA APP
 */
public class Util {
//    public static final String DATA_BASE = "presion_aguas";
//    public static final String HOST = "192.168.1.46";
//    public static final String USER = "root";
//    public static final String CLAVE = "root";
        public static final String DATA_BASE = "u101901458_presi";
    public static final String HOST = "sql200.main-hosting.eu";
    public static final String USER = "u101901458_matia";
    public static final String CLAVE = "Miseignacio11";
    public static final String PUERTO = "3306";
    public static final int EXITOSO = 1;
    public static final int ERROR = 0;
    public static final int PRIMER_INICIO_MODULO_PRESION = 0;
    public static final int SEGUNDO_INICIO_MODULO_PRESION = 1;
    public static final int INSERTAR_PUNTO = 1;
    public static final int ACTUALIZAR_PUNTO = 2;
    public static final int ESTANDAR_MEDICION = 6;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String ID_PUNTO_PRESION_SHARED_PREFERENCE = "id_punto_presion";
    public static final LatLng LA_RIOJA = new LatLng(-29.4126811, -66.8576855);

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
        fecha = date.getDate() + "/" + (date.getMonth() + 1) + "/" + (date.getYear() + 1900) +
                "\na las: " + date.getHours() + ":" + minutos;
        return fecha;
    }

    public static int validarCampos(Activity a, ArrayList<EditText> inputs) {
        int i, check = 1;
        for (i = 0; i < inputs.size(); i++) {
            if (inputs.get(i).getText().toString().equals("")) {
                check = 0;
            }
        }
        if (check == EXITOSO) {
            return EXITOSO;
        } else {
            mostrarMensaje(a, "Debe llenar los campos");
            return ERROR;
        }
    }

    public static void toggleButtons(ArrayList<Button> botones, Boolean bandera) {
        if (bandera) {
            for (int i = 0; i < botones.size(); i++) {
                botones.get(i).setEnabled(false);
            }
        } else {
            for (int i = 0; i < botones.size(); i++) {
                botones.get(i).setEnabled(true);
            }
        }
    }
}