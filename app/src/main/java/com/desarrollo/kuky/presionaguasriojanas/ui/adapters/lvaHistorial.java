package com.desarrollo.kuky.presionaguasriojanas.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.objeto.HistorialPuntos;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.convertirFecha;

public class lvaHistorial extends BaseAdapter {
    // Declare Variables
    Context context;
    ArrayList<HistorialPuntos> historialPuntos;
    LayoutInflater inflater;

    public lvaHistorial(Context context, ArrayList<HistorialPuntos> historialPuntos) {
        this.context = context;
        this.historialPuntos = historialPuntos;
    }

    @Override
    public int getCount() {
        return historialPuntos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {


        // Declare Variables
        TextView tvFecha, tvPresion;

        //http://developer.android.com/intl/es/reference/android/view/LayoutInflater.html
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.sli_historial, parent, false);

        // Locate the TextViews in listview_item.xml
        tvFecha = itemView.findViewById(R.id.etFecha);
        tvPresion = itemView.findViewById(R.id.etPresion);
        // Capture position and set to the TextViews

        tvFecha.setText(convertirFecha(historialPuntos.get(position).getFecha()));
        tvPresion.setText(historialPuntos.get(position).getPresion().toString() + " mca");


        return itemView;
    }
}
