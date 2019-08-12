package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.desarrollo.kuky.presionaguasriojanas.controlador.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Barrio;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.RelevamientoActivity;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class RelevamientoActivityControlador {
    public static ArrayList<Barrio> barrios;
    private ProgressDialog pDialog;

    private class AbrirActivity extends AsyncTask<String, Float, ArrayList<Barrio>> {

        Activity a;
        String zonaBarrios;

        @Override
        protected void onPreExecute() {
            barrios = new ArrayList<>();
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setTitle("CARGANDO");
            pDialog.setMessage("Cargando barrios...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        AbrirActivity(Activity a, String zonaBarrios) {
            this.a = a;
            this.zonaBarrios = zonaBarrios;
        }

        @Override
        protected ArrayList<Barrio> doInBackground(String... strings) {
            SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
            Cursor c = db.rawQuery("SELECT codigo, des_codigo " +
                    " FROM barrios " +
                    " WHERE zona='" + zonaBarrios + "'" +
                    " ORDER BY des_codigo", null);
            while (c.moveToNext()) {
                Barrio barrio = new Barrio();
                barrio.setCodigo(c.getString(0));
                barrio.setDesCodigo(c.getString(1));
                barrios.add(barrio);
            }
            c.close();
            db.close();
            return barrios;
        }

        @Override
        protected void onPostExecute(ArrayList<Barrio> barrios) {
            pDialog.dismiss();
            if (barrios.size() > 0) {
                abrirActivity(a, RelevamientoActivity.class);
            } else {
                mostrarMensaje(a, "Error en el extraerTodosBarrios");
            }
        }
    }

    public ArrayList<Barrio> abrirActivityTask(Activity a, String zonaBarrios) {
        try {
            AbrirActivity abrirActivity = new AbrirActivity(a, zonaBarrios);
            abrirActivity.execute();
            return barrios;
        } catch (Exception e) {
            mostrarMensaje(a, "Error AbrirActivity RAC" + e.toString());
            return null;
        }
    }
}
