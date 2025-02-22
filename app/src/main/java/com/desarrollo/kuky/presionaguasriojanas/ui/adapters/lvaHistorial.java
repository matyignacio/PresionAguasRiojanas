package com.desarrollo.kuky.presionaguasriojanas.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.HistorialPuntos;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ESTANDAR_MEDICION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.convertirFecha;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class lvaHistorial extends BaseAdapter {
    // Declare Variables
    private Context context;
    private ArrayList<HistorialPuntos> historialPuntos;

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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.sli_historial, parent, false);

        // Locate the TextViews in listview_item.xml
        tvFecha = itemView.findViewById(R.id.etFecha);
        tvPresion = itemView.findViewById(R.id.etPresion);
        // Capture position and set to the TextViews
        /** SETEAMOS LOS TYPEFACES*/
        setPrimaryFontBold(context, tvFecha);
        setPrimaryFontBold(context, tvPresion);
        /**************************/
        tvFecha.setText(convertirFecha(historialPuntos.get(position).getFecha()));
        tvPresion.setText(historialPuntos.get(position).getPresion().toString() + " mca");
        if (historialPuntos.get(position).getPresion() > ESTANDAR_MEDICION) {
            tvFecha.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            tvPresion.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        } else {
            tvFecha.setTextColor(context.getResources().getColor(R.color.marcador_rojo));
            tvPresion.setTextColor(context.getResources().getColor(R.color.marcador_rojo));
        }

        return itemView;
    }
}
