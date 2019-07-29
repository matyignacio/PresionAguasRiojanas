package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.app.Activity;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;

public class InspeccionActivityControlador {

    public int sync(Activity a) {
        /**
         * ORDEN:
         *  clienteControlador.sincronizarDeSqliteToMysql(a) 1/9
         *  inspeccionControlador.sincronizarDeSqliteToMysql(a); 2/9
         *  datosRelevadosControlador.sincronizarDeSqliteToMysql(a); 3/9
         *  tipoInmuebleControlador.sincronizarDeMysqlToSqlite(a); 4/9
         *  tipoServicioControlador.sincronizarDeMysqlToSqlite(a); 5/9
         *  destinoInmuebleControlador.sincronizarDeMysqlToSqlite(a); 6/9
         *  clienteControlador.sincronizarDeMysqlToSqlite(a); 7/9
         *  inspeccionControlador.sincronizarDeMysqlToSqlite(a); 8/9
         *  datosRelevadosControlador.sincronizarDeMysqlToSqlite(a); 9/9
         * */
        int check = ERROR;
        ClienteControlador clienteControlador = new ClienteControlador();
        if (clienteControlador.sincronizarDeSqliteToMysql(a) == EXITOSO) {
            check = EXITOSO;
        }
        return check;
    }
}
