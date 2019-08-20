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
             *  clienteControlador.sincronizarDeSqliteToMysql(a) 1/13
             *  inspeccionControlador.sincronizarDeSqliteToMysql(a); 2/13
             *  relevamientoControlador.sincronizarDeSqliteToMysql(a); 3/13
             *  relevamientoMedidorControlador.sincronizarDeSqliteToMysql(a); 4/13
             *  datosRelevadosControlador.sincronizarDeSqliteToMysql(a); 5/13
             *  tipoInmuebleControlador.sincronizarDeMysqlToSqlite(a); 6/13
             *  barrioControlador.sincronizarDeMysqlToSqlite(a); 7/13
             *  tipoServicioControlador.sincronizarDeMysqlToSqlite(a); 8/13
             *  clienteControlador.sincronizarDeMysqlToSqlite(a); 9/13
             *  inspeccionControlador.sincronizarDeMysqlToSqlite(a); 10/13
             *  relevamientoMedidorControlador.sincronizarDeMysqlToSqlite(a); 11/13
             *  datosRelevadosControlador.sincronizarDeMysqlToSqlite(a); 12/13
             *  relevamientoControlador.sincronizarDeMysqlToSqlite(a); 13/13
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
