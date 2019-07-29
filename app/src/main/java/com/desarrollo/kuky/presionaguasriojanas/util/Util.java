package com.desarrollo.kuky.presionaguasriojanas.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.UsuarioControlador;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * EN ESTA CLASE UTIL VAMOS A IR CREANDO LAS CONSTANTES O FUNCIONES QUE NOS SIRVAN PARA
 * HACER CODIGO LIMPIO DURANTE EL DESARROLLO DE NUESTRA APP
 */
public class Util {
    public static final String DATA_BASE = "c1370466_aguas_out";
    public static final String HOST = "66.97.39.77";
    public static final String USER = "c1370466_consu";
    public static final String CLAVE = "Consulta963";
    //    public static final String DATA_BASE = "presion_aguas";
//    public static final String HOST = "192.168.1.46";
//    public static final String USER = "root";
//    public static final String CLAVE = "root";
    public static final String PUERTO = "3306";
    public static final int EXITOSO = 1;
    public static final int ERROR = 0;
    public static final int BANDERA_ALTA = 1;
    public static final int BANDERA_BAJA = 0;
    public static final int PRIMER_INICIO_MODULO_PRESION = 0;
    public static final int SEGUNDO_INICIO_MODULO_PRESION = 1;
    public static final int INSERTAR_PUNTO = 1;
    public static final int ACTUALIZAR_PUNTO = 2;
    public static final int ESTANDAR_MEDICION = 6;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String ID_PUNTO_PRESION_SHARED_PREFERENCE = "id_punto_presion";
    public static final String USUARIO_PUNTO_PRESION_SHARED_PREFERENCE = "id_usuario";
    public static final String ULTIMA_LATITUD = "latitud";
    public static final String ULTIMA_LONGITUD = "longitud";
    public static final String LATITUD_INSPECCION = "latitud_inspeccion";
    public static final String LONGITUD_INSPECCION = "longitud_inspeccion";
    public static final String CIRCUITO_USUARIO = "circuito_usuario";
    public static final String TIPO_MAPA = "id_tipo_punto";
    public static final String POSICION_SELECCIONADA = "posicion_seleccionada_spinner";
    public static final String SPINNER_TIPO_UNIDAD = "tipo_unidad";
    public static final String SPINNER_TIPO_UNIDAD2 = "tipo_unidad2";
    public static final int MAPA_RECORRIDO = 1;
    public static final int MAPA_CLIENTES = 2;
    public static final int MAXIMO_CIRCUITO = 2;
    private static final int MAXIMA_MEDICION = 20;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String font_primary_path = "font/font_primary.ttf";
    private static final String font_primary_bold_path = "font/font_primary_bold.ttf";
    /**
     * A LATITUD Y LONGITUD LAS DEFINO COMO STRINGS PARA PODER USARLAS COMO SHARED PREFERENCES
     * DESPUES LAS PARSEO A DOUBLE EN MAPACTIVITY
     **/
    public static final String LATITUD_LA_RIOJA = "-29.4126811";
    public static final String LONGITUD_LA_RIOJA = "-66.8576855";
    // location updates interval - 10sec
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    public static final int REQUEST_CHECK_SETTINGS = 100;

    public static void abrirActivity(Activity a, Class destino) {
        Intent intent = new Intent(a, destino);
        a.startActivity(intent);
        a.finish();
    }


    public static void abrirFragmento(Activity a, int layout, Fragment fragment) {
        //Paso 1: Obtener la instancia del administrador de fragmentos
        FragmentManager fragmentManager = a.getFragmentManager();

        //Paso 2: Crear una nueva transacción
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //Paso 3: Crear un nuevo fragmento y añadirlo
        transaction.add(layout, fragment);

        //Paso 4: Confirmar el cambio
        transaction.commit();
    }

    public static void siguienteFragmento(Activity a, int layout, Fragment fragmentActual, Fragment fragmentSiguiente) {
        a.getFragmentManager().beginTransaction().remove(fragmentActual).commit();
        FragmentManager fragmentManager = a.getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(layout, fragmentSiguiente);
        transaction.commit();
    }

    public static void cerrarFragmento(Activity a, Fragment fragment) {
        // SON LOS MISMOS PASOS QUE PARA AGREGAR UN FRAGMENT
        // SOLAMENTE CAMBIA EL ADD POR EL REMOVE
        // EN ESTE CODIGO ESTA SIMPLIFICADO EN UNA SOLA INSTRUCCION.
        a.getFragmentManager().beginTransaction().remove(fragment).commit();
    }

    public static void mostrarMensaje(Context c, String mensaje) {
        Toast.makeText(c, mensaje, Toast.LENGTH_SHORT).show();
        Log.e("MOSTRARMENSAJE:::", mensaje);
    }

    public static void mostrarMensajeLog(Context c, String mensaje) {
        Log.e("MOSTRARMENSAJE:::", mensaje);
    }

    public static String convertirFecha(Date date) {
        String fecha, hora;
        fecha = DateFormat.getDateInstance(DateFormat.SHORT).format(date);
        hora = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
        return fecha + "\na las " + hora;
    }

    public static int validarCampos(Activity a, ArrayList<EditText> inputs) {
        int i;
        for (i = 0; i < inputs.size(); i++) {
            if (inputs.get(i).getText().toString().equals("")) {
                mostrarMensaje(a, "Debe llenar el campo " + inputs.get(i).getHint().toString());
                inputs.get(i).requestFocus();
                return ERROR;
            }
        }
        return EXITOSO;
    }

    public static int validarPresion(Activity a, EditText etPresion) {
        if (Float.parseFloat(etPresion.getText().toString()) <= MAXIMA_MEDICION &&
                Float.parseFloat(etPresion.getText().toString()) > 0) {
            return EXITOSO;
        } else {
            mostrarMensaje(a, "La presion debe ser entre 0 mca y 20 mca");
            return ERROR;
        }
    }

    public static void logOut(Activity a) {
        UsuarioControlador usuarioControlador = new UsuarioControlador();
        if (usuarioControlador.eliminarUsuario(a) == EXITOSO) {
            abrirActivity(a, LoginActivity.class);
        }
    }

    public static void setPrimaryFont(Context a, TextView tv) {
        Typeface TF = Typeface.createFromAsset(a.getAssets(), font_primary_path);
        tv.setTypeface(TF);
    }

    public static void setPrimaryFont(Context a, EditText et) {
        Typeface TF = Typeface.createFromAsset(a.getAssets(), font_primary_path);
        et.setTypeface(TF);
    }

    public static void setPrimaryFont(Context a, Button bt) {
        Typeface TF = Typeface.createFromAsset(a.getAssets(), font_primary_path);
        bt.setTypeface(TF);
    }

    public static void setPrimaryFontBold(Context a, TextView tv) {
        Typeface TF = Typeface.createFromAsset(a.getAssets(), font_primary_bold_path);
        tv.setTypeface(TF);
    }

    public static void setPrimaryFontBold(Context a, EditText et) {
        Typeface TF = Typeface.createFromAsset(a.getAssets(), font_primary_bold_path);
        et.setTypeface(TF);
    }

    public static void setPrimaryFontBold(Context a, Button bt) {
        Typeface TF = Typeface.createFromAsset(a.getAssets(), font_primary_bold_path);
        bt.setTypeface(TF);
    }

    public static void setPrimaryFontBold(Context a, CheckBox bt) {
        Typeface TF = Typeface.createFromAsset(a.getAssets(), font_primary_bold_path);
        bt.setTypeface(TF);
    }


    public static void setPreference(Context c, String nombreDato, int dato) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(nombreDato, dato);
        editor.apply();
    }

    public static void setPreference(Context c, String nombreDato, String dato) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(nombreDato, dato);
        editor.apply();
    }

    public static int getPreference(Context c, String nombreDato, int defaultValue) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(nombreDato, defaultValue);
    }

    public static String getPreference(Context c, String nombreDato, String defaultValue) {
        SharedPreferences settings = c.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(nombreDato, defaultValue);
    }

    public static void showDialog(final Activity a, int dialog, String mensajeSI, Callable<Void> methodParam) {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(a);
        View promptView = layoutInflater.inflate(dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
        alertDialogBuilder.setView(promptView);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(mensajeSI, (dialog1, id) -> {
                    try {
                        methodParam.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancelar", (dialog12, id) -> dialog12.cancel());

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static void showDialog(final Activity a, int dialog, String mensajeSI, Callable<Void> methodAcept, Callable<Void> methodCancel) {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(a);
        View promptView = layoutInflater.inflate(dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(a);
        alertDialogBuilder.setView(promptView);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(mensajeSI, (dialog1, id) -> {
                    try {
                        methodAcept.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancelar", (dialog12, id) -> {

                    try {
                        methodCancel.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog12.cancel();
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static void showStandarDialog(final Activity a,
                                         String titulo,
                                         View view,
                                         String mensajeSI,
                                         Callable<Void> methodAccept) {
        AlertDialog dialog = new AlertDialog.Builder(a)
                .setTitle(titulo)
                //.setMessage("Seleccione el circuito")
                .setView(view)
                .setPositiveButton(mensajeSI, (dialog13, which) -> {
                    try {
                        methodAccept.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog.show();
    }

    public static void cargarSpinner(Spinner spinner,
                                     Activity a,
                                     int dato,
                                     List<String> labels,
                                     Callable<Void> methodAcept,
                                     Callable<Void> methodCancel) {
        spinner.setBackgroundResource(R.drawable.sp_redondo);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(a,
                android.R.layout.simple_spinner_item, labels);
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(dato - 1);
        spinner.setDropDownWidth(350);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setPreference(a, POSICION_SELECCIONADA, i + 1);
                try {
                    methodAcept.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                try {
                    methodCancel.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}