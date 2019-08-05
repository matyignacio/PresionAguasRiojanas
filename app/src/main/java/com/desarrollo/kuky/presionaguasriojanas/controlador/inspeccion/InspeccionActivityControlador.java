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
             *  clienteControlador.sincronizarDeSqliteToMysql(a) 1/10
             *  inspeccionControlador.sincronizarDeSqliteToMysql(a); 2/10
             *  datosRelevadosControlador.sincronizarDeSqliteToMysql(a); 3/10
             *  barrioControlador.sincronizarDeMysqlToSqlite(a); 4/10
             *  tipoInmuebleControlador.sincronizarDeMysqlToSqlite(a); 5/10
             *  tipoServicioControlador.sincronizarDeMysqlToSqlite(a); 6/10
             *  destinoInmuebleControlador.sincronizarDeMysqlToSqlite(a); 7/10
             *  clienteControlador.sincronizarDeMysqlToSqlite(a); 8/10
             *  inspeccionControlador.sincronizarDeMysqlToSqlite(a); 9/10
             *  datosRelevadosControlador.sincronizarDeMysqlToSqlite(a); 10/10
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
