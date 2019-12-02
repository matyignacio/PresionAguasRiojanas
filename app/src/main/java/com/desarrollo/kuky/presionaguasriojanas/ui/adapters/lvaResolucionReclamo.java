package com.desarrollo.kuky.presionaguasriojanas.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.ResolucionReclamo;

import java.text.DateFormat;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class lvaResolucionReclamo extends BaseAdapter {
    // Declare Variables
    private Context context;
    private ArrayList<ResolucionReclamo> resolucionReclamos;

    public lvaResolucionReclamo(Context context, ArrayList<ResolucionReclamo> resolucionReclamos) {
        this.context = context;
        this.resolucionReclamos = resolucionReclamos;
    }

    @Override
    public int getCount() {
        return resolucionReclamos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {


        // Declare Variables
        TextView tvResolucion, tvFecha, tvObservacion;

        //http://developer.android.com/intl/es/reference/android/view/LayoutInflater.html
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.sli_resoluciones, parent, false);

        // Locate the TextViews in listview_item.xml
        tvResolucion = itemView.findViewById(R.id.tvResolucion);
        tvFecha = itemView.findViewById(R.id.tvFecha);
        tvObservacion = itemView.findViewById(R.id.tvObservacion);
        // Capture position and set to the TextViews
        /** SETEAMOS LOS TYPEFACES*/
        setPrimaryFontBold(context, tvResolucion);
        setPrimaryFontBold(context, tvFecha);
        setPrimaryFontBold(context, tvObservacion);
        /**************************/
        tvResolucion.setText("Resolucion: " + resolucionReclamos.get(position).getDescripcionResolucion());
        // A esta conversion de fecha la copie de Util.convertirFecha()
        tvFecha.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(resolucionReclamos.get(position).getFechaDesde()));
        tvObservacion.setText(resolucionReclamos.get(position).getObservaciones());

        return itemView;
    }
}
