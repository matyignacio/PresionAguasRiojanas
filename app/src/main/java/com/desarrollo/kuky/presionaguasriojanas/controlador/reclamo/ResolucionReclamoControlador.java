package com.desarrollo.kuky.presionaguasriojanas.controlador.reclamo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import com.desarrollo.kuky.presionaguasriojanas.controlador.UsuarioControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.ResolucionReclamo;
import com.desarrollo.kuky.presionaguasriojanas.sqlite.BaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_ALTA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.DATE_TIME;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.HOUR_TIME;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class ResolucionReclamoControlador {
    @SuppressLint("SimpleDateFormat")
    public int insertar(Activity a, ResolucionReclamo resolucionReclamo) {
        int retorno = ERROR;
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat formatDate = new SimpleDateFormat(DATE_TIME);
            SimpleDateFormat formatHour = new SimpleDateFormat(HOUR_TIME);
            String date = formatDate.format(currentTime);
            String hour = formatHour.format(currentTime);
            String sql = "INSERT INTO GTres_rec" +
                    "(tpo_tram," +
                    "num_tram," +
                    "cod_res," +
                    "obs," +
                    "usuario," +
                    "fecha_d," +
                    "hora_d," +
                    "fecha_h," +
                    "hora_h)" +
                    "VALUES" +
                    "('" + resolucionReclamo.getTipoTramite() + "', " +
                    resolucionReclamo.getNumeroTramite() + "," +
                    resolucionReclamo.getCodigoResolucion() + ",'" +
                    resolucionReclamo.getObservaciones() + "','" +
                    resolucionReclamo.getUsuario() + "','" +
                    date + "','" +
                    hour + "','" +
                    date + "','" +
                    hour + "')";
            db.execSQL(sql);
            /** SUBIMOS LA BANDERA DE SYNC MODULO RECLAMO **/
            UsuarioControlador usuarioControlador = new UsuarioControlador();
            usuarioControlador.editarBanderaSyncModuloReclamo(a, BANDERA_ALTA);
            /** CERRAMOS LAS CONEXIONES **/
            db.close();
            mostrarMensaje(a, "La resolucion se guardo correctamente");
            retorno = EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Error insertar RRC " + e.toString());
        }
        return retorno;
    }
}
