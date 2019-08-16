package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.app.Activity;

import com.desarrollo.kuky.presionaguasriojanas.util.InternetDetector;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class InspeccionActivityControlador {
    private InternetDetector internetDetector;
    int check = ERROR;

    public int sync(Activity a) {
        // Initializing Internet Checker
        internetDetector = new InternetDetector(a);
        if (!internetDetector.checkMobileInternetConn()) {
            mostrarMensaje(a, "No hay conexion de red disponible.");
        } else {
            /**
             * ORDEN:
             *  clienteControlador.sincronizarDeSqliteToMysql(a) 1/11
             *  inspeccionControlador.sincronizarDeSqliteToMysql(a); 2/11
             *  relevamientoControlador.sincronizarDeSqliteToMysql(a); 3/11
             *  datosRelevadosControlador.sincronizarDeSqliteToMysql(a); 4/11
             *  tipoInmuebleControlador.sincronizarDeMysqlToSqlite(a); 5/11
             *  barrioControlador.sincronizarDeMysqlToSqlite(a); 6/11
             *  tipoServicioControlador.sincronizarDeMysqlToSqlite(a); 7/11
             *  clienteControlador.sincronizarDeMysqlToSqlite(a); 8/11
             *  inspeccionControlador.sincronizarDeMysqlToSqlite(a); 9/11
             *  datosRelevadosControlador.sincronizarDeMysqlToSqlite(a); 10/11
             *  relevamientoControlador.sincronizarDeMysqlToSqlite(a); 11/11
             * */
            check = ERROR;
            ClienteControlador clienteControlador = new ClienteControlador();
            if (clienteControlador.sincronizarDeSqliteToMysql(a) == EXITOSO) {
                check = EXITOSO;
            }
        }
        return check;
    }
}
