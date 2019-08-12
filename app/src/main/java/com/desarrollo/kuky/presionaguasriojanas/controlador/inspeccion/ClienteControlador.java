package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.desarrollo.kuky.presionaguasriojanas.controlador.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.controlador.Conexion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Cliente;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class ClienteControlador {
    private ProgressDialog pDialog;
    private ArrayList<Cliente> clientes;

    private class SyncSqliteToMysql extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;
        private ArrayList<Cliente> clientes;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setTitle("SINCRONIZANDO");
            pDialog.setMessage("1/" +
                    "12 - Enviando clientes...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        SyncSqliteToMysql(Activity a) {
            this.a = a;
            check = ERROR;
            clientes = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... strings) {
            /**
             IMPLEMENTO TRANSACCIONES CON COMMIT Y ROLLBACK EN LAS TAREAS ASYNCRONAS
             DESDE EL TELEFONO HACIA EL SERVER
             */
            clientes = extraerTodosPendientes(a);
            Connection conn;
            conn = Conexion.GetConnection();
            try {
                conn.setAutoCommit(false);
                String consultaSql;
                for (int i = 0; i < clientes.size(); i++) {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                    int estado = clientes.get(i).isEstado() ? 1 : 0;
                    PreparedStatement ps;
                    consultaSql = "INSERT INTO cliente" +
                            " (id, id_usuario, razon_social, direccion, barrio," +
                            " telefono, unidad, nis, med_agua, med_luz, tramite," +
                            " serv, estado, reclama)" +
                            " VALUES " +
                            "('" + clientes.get(i).getId() + "', " +
                            "'" + clientes.get(i).getIdUsuario() + "', " +
                            "'" + clientes.get(i).getRazonSocial() + "', " +
                            "'" + clientes.get(i).getDireccion() + "', " +
                            "'" + clientes.get(i).getBarrio() + "', " +
                            "'" + clientes.get(i).getTelefono() + "', " +
                            "'" + clientes.get(i).getUnidad() + "', " +
                            "'" + clientes.get(i).getNis() + "', " +
                            "'" + clientes.get(i).getMedidorAgua() + "', " +
                            "'" + clientes.get(i).getMedidorLuz() + "', " +
                            "'" + clientes.get(i).getTramite() + "', " +
                            "'" + clientes.get(i).getServ() + "', " +
                            "'" + estado + "', " +
                            "'" + clientes.get(i).getReclama() + "');";
                    ps = conn.prepareStatement(consultaSql);
                    ps.execute();
                    conn.commit();
                    ps.close();
                    /*//////////////////////////////////////////////////////////////////////////////////
                                            BAJAMOS EL PENDIENTE DEL CLIENTE
                    //////////////////////////////////////////////////////////////////////////////////*/
                    actualizarPendiente(clientes.get(i), a);
                    check++;
                }
                if (check == clientes.size()) {
                    return "EXITO";
                } else {
                    return "ERROR";
                }
            } catch (SQLException e) {
                try {
                    Log.e("MOSTRARMENSAJE:::", "Transaction is being rolled back");
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
                return e.toString();
            } finally {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s.equals("EXITO")) {
                //mostrarMensaje(a, "2/6 - Se enviaron los clientes con exito");
                InspeccionControlador inspeccionControlador = new InspeccionControlador();
                inspeccionControlador.sincronizarDeSqliteToMysql(a);
            } else {
                mostrarMensaje(a, "Error en el checkClientesToMysql");
            }
        }
    }

    public int sincronizarDeSqliteToMysql(Activity a) {
        try {
            SyncSqliteToMysql syncSqliteToMysql = new SyncSqliteToMysql(a);
            syncSqliteToMysql.execute();
            return EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Eror SyncSqliteToMysql CC" + e.toString());
            return ERROR;
        }
    }

    private class SyncMysqlToSqlite extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setTitle("SINCRONIZANDO");
            pDialog.setMessage("9/" +
                    "12 - Recibiendo clientes...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        SyncMysqlToSqlite(Activity a) {
            this.a = a;
            check = ERROR;
        }

        @Override
        protected String doInBackground(String... strings) {
            Connection conn;
            PreparedStatement ps;
            ResultSet rs;
            try {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                conn = Conexion.GetConnection();
                String consultaSql = "SELECT * FROM cliente ";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM cliente");
                while (rs.next()) {
                    String sql = "INSERT INTO cliente" +
                            " VALUES" +
                            " ('" + rs.getInt(1) + "','" + // id
                            rs.getString(2) + "','" + //id_usuario
                            rs.getString(3) + "','" + //razon_social
                            rs.getString(4) + "','" + //direccion
                            rs.getString(5) + "','" + //barrio
                            rs.getInt(6) + "','" + //telefono
                            rs.getInt(7) + "','" + //unidad
                            rs.getInt(8) + "','" + //nis
                            rs.getInt(9) + "','" + //med_agua
                            rs.getInt(10) + "','" + //med_luz
                            rs.getInt(11) + "','" + //tramite
                            rs.getString(12) + "','" + //serv
                            rs.getInt(13) + "','" + //estado
                            rs.getString(14) + "','" + //reclama
                            "0');"; // pendiente
                    db.execSQL(sql);
                }
                check++;
                if (check == EXITOSO) {
                    db.close();
                    rs.close();
                    ps.close();
                    conn.close();
                    return "EXITO";
                } else {
                    db.close();
                    rs.close();
                    ps.close();
                    conn.close();
                    return "ERROR";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s.equals("EXITO")) {
                InspeccionControlador inspeccionControlador = new InspeccionControlador();
                inspeccionControlador.sincronizarDeMysqlToSqlite(a);
            } else {
                mostrarMensaje(a, "Error en el checkCliente");
            }
        }

    }

    public void sincronizarDeMysqlToSqlite(Activity a) {
        try {
            SyncMysqlToSqlite syncMysqlToSqlite = new SyncMysqlToSqlite(a);
            syncMysqlToSqlite.execute();
        } catch (Exception e) {
            mostrarMensaje(a, "Error SyncMysqlToSqlite CC" + e.toString());
        }
    }

//    public void insertar(Cliente cliente, Activity a) {
//        try {
//            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
//            String sql = "INSERT INTO cliente" +
//                    " (id, id_usuario, razon_social, direccion, barrio," +
//                    " telefono, unidad, nis, med_agua, med_luz, tramite," +
//                    " serv, estado, reclama)" +
//                    " VALUES " +
//                    "('" + cliente.getLatitud() + "','" + // latitud
//                    cliente.getLongitud() + "','" + // longitud
//                    INSERTAR_PUNTO + "','" + // pendiente
//                    cliente.getPresion() + "','" + // presion
//                    cliente.getPuntoPresion().getId() + "','" + // id_punto_presion
//                    cliente.getPuntoPresion().getUsuario().getId() + "','" + // id_usuario
//                    cliente.getUsuario().getId() + "');";// id_usuario_historial
//            db.execSQL(sql);
//            PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
//            PuntoPresion puntoPresion = puntoPresionControlador.extraerPorIdYUsuario(a,
//                    cliente.getPuntoPresion().getId(),
//                    cliente.getPuntoPresion().getUsuario().getId());
//            /**
//             * EVALUAREMOS SI EL PUNTO ES UNO NUEVO SIN IMPACTAR EN LA BASE MYSQL
//             * O SI YA ES UN PUNTO CONOCIDO Y SIMPLEMENTE SE LE AGREGO UNA NUEVA MEDICION
//             */
//            SQLiteDatabase db2 = BaseHelper.getInstance(a).getWritableDatabase();
//            if (puntoPresion.getPendiente() == INSERTAR_PUNTO) {
//                sql = "UPDATE puntos_presion" +
//                        " SET presion = '" + cliente.getPresion() + "', pendiente = " + INSERTAR_PUNTO +
//                        " WHERE id=" + cliente.getPuntoPresion().getId() +
//                        " AND id_usuario LIKE '" + cliente.getPuntoPresion().getUsuario().getId() + "'";
//
//            } else {
//                sql = "UPDATE puntos_presion" +
//                        " SET presion = '" + cliente.getPresion() + "', pendiente = " + ACTUALIZAR_PUNTO +
//                        " WHERE id=" + cliente.getPuntoPresion().getId() +
//                        " AND id_usuario LIKE '" + cliente.getPuntoPresion().getUsuario().getId() + "'";
//            }
//            db2.execSQL(sql);
//            /** SUBIMOS LA BANDERA DE SYNC MODULO PRESION **/
//            UsuarioControlador usuarioControlador = new UsuarioControlador();
//            usuarioControlador.editarBanderaSyncModuloPresion(a, BANDERA_ALTA);
//            /** EVUALUAMOS SI HAY QUE CAMBIAR EL ORDEN **/
//            Orden orden = new Orden();
//            OrdenControlador ordenControlador = new OrdenControlador();
//            orden = ordenControlador.existePunto(a, cliente.getPuntoPresion());
//            if (orden.getId() > 0) {
//                /** SI EL PUNTO EXISTE EN LA TABLA DE ORDEN, LE BAJAMOS LA BANDERA **/
//                ordenControlador.editarActivo(a,
//                        orden.getPpActual().getId(),
//                        orden.getPpActual().getUsuario().getId(),
//                        BANDERA_BAJA);
//                /** Y LE SUBIMOS LA BANDERA AL SIGUIENTE PUNTO **/
//                ordenControlador.editarActivo(a,
//                        orden.getPpSiguiente().getId(),
//                        orden.getPpSiguiente().getUsuario().getId(),
//                        BANDERA_ALTA);
//            }
//            /** CERRAMOS LAS CONEXIONES **/
//            db.close();
//            db2.close();
//        } catch (Exception e) {
//            mostrarMensaje(a, "Error insertar HPC " + e.toString());
//        }
//    }

    public ArrayList<Cliente> extraerTodos(Activity a) {
        ArrayList<Cliente> clientes = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cliente", null);
        while (c.moveToNext()) {
            Cliente cliente = new Cliente();
            cliente.setId(c.getInt(0));
            cliente.setIdUsuario(c.getString(1));
            cliente.setRazonSocial(c.getString(2));
            cliente.setDireccion(c.getString(3));
            cliente.setBarrio(c.getString(4));
            cliente.setTelefono(c.getInt(5));
            cliente.setUnidad(c.getInt(6));
            cliente.setNis(c.getInt(7));
            cliente.setMedidorAgua(c.getInt(8));
            cliente.setMedidorLuz(c.getInt(9));
            cliente.setTramite(c.getInt(10));
            cliente.setServ(c.getString(11));
            if (c.getInt(12) == 0) {
                cliente.setEstado(false);
            } else {
                cliente.setEstado(true);
            }
            cliente.setReclama(c.getString(13));
            clientes.add(cliente);
        }
        c.close();
        db.close();
        return clientes;
    }

//    public Cliente extraerPorIdYUsuario(Activity a, int id, String usuario) {
//        Cliente puntoPresion = new Cliente();
//        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
//        Cursor c = db.rawQuery("SELECT * FROM cliente where id =" + id + " " +
//                "AND id_usuario like '" + usuario + "'", null);
//        while (c.moveToNext()) {
//            TipoPresion tipoPresion = new TipoPresion();
//            TipoPunto tipoPunto = new TipoPunto();
//            Usuario u = new Usuario();
//            puntoPresion.setId(c.getInt(0));
//            puntoPresion.setCircuito(c.getInt(1));
//            puntoPresion.setBarrio(c.getString(2));
//            puntoPresion.setCalle1(c.getString(3));
//            puntoPresion.setCalle2(c.getString(4));
//            puntoPresion.setLatitud(c.getDouble(5));
//            puntoPresion.setLongitud(c.getDouble(6));
//            puntoPresion.setPendiente(c.getInt(7));
//            puntoPresion.setPresion(c.getFloat(8));
//            tipoPresion.setId(c.getInt(9));
//            puntoPresion.setTipoPresion(tipoPresion);
//            tipoPunto.setId(c.getInt(10));
//            puntoPresion.setTipoPunto(tipoPunto);
//            u.setId(usuario);
//            puntoPresion.setUsuario(u);
//            puntoPresion.setUnidad(c.getInt(12));
//            puntoPresion.setTipoUnidad(c.getString(13));
//            puntoPresion.setUnidad2(c.getInt(14));
//            puntoPresion.setTipoUnidad2(c.getString(15));
//            puntoPresion.setCloro(c.getFloat(16));
//            puntoPresion.setMuestra(c.getString(17));
//        }
//        c.close();
//        db.close();
//        return puntoPresion;
//    }

    private ArrayList<Cliente> extraerTodosPendientes(Activity a) {
        clientes = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cliente " +
                "WHERE pendiente = 1 " +
                "ORDER BY id ASC", null);
        while (c.moveToNext()) {
            Cliente cliente = new Cliente();
            cliente.setId(c.getInt(0));
            cliente.setIdUsuario(c.getString(1));
            cliente.setRazonSocial(c.getString(2));
            cliente.setDireccion(c.getString(3));
            cliente.setBarrio(c.getString(4));
            cliente.setTelefono(c.getInt(5));
            cliente.setUnidad(c.getInt(6));
            cliente.setNis(c.getInt(7));
            cliente.setMedidorAgua(c.getInt(8));
            cliente.setMedidorLuz(c.getInt(9));
            cliente.setTramite(c.getInt(10));
            cliente.setServ(c.getString(11));
            if (c.getInt(12) == 0) {
                cliente.setEstado(false);
            } else {
                cliente.setEstado(true);
            }
            cliente.setReclama(c.getString(13));
            clientes.add(cliente);
        }
        c.close();
        db.close();
        return clientes;
    }

    private void actualizarPendiente(Cliente cliente, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE cliente" +
                    " SET pendiente = 0" +
                    " WHERE id=" + cliente.getId() +
                    " AND id_usuario LIKE '" + cliente.getIdUsuario() + "'";
            db.execSQL(sql);
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente CC " + e.toString());
        }
    }

    private int obtenerSiguienteId(Activity a) {
        int id = 1;
        try {
            SQLiteDatabase db3 = BaseHelper.getInstance(a).getReadableDatabase();
            String sql = "SELECT id FROM cliente ORDER BY id DESC LIMIT 1";
            Cursor c3 = db3.rawQuery(sql, null);
            while (c3.moveToNext()) {
                id = c3.getInt(0) + 1;
            }
            return id;
        } catch (Exception e) {
            mostrarMensaje(a, "Error obtenerSiguienteId CC " + e.toString());
            return Util.ERROR;
        }
    }

    public Cliente buscarPorTramite(Activity a, int tramite) {
        Cliente cliente = new Cliente();
        try {
            SQLiteDatabase db3 = BaseHelper.getInstance(a).getReadableDatabase();
            String sql = "select * from cliente where tramite = " + tramite;
            Cursor c3 = db3.rawQuery(sql, null);
            while (c3.moveToNext()) {
                cliente.setId(c3.getInt(0));
                cliente.setIdUsuario(c3.getString(1));
                cliente.setRazonSocial(c3.getString(2));
                cliente.setDireccion(c3.getString(3));
                cliente.setBarrio(c3.getString(4));
                cliente.setTelefono(c3.getInt(5));
                cliente.setTramite(tramite);

            }
            return cliente;
        } catch (Exception e) {
            mostrarMensaje(a, "Error obtenerSiguienteId CC " + e.toString());
            return null;
        }
    }
}
