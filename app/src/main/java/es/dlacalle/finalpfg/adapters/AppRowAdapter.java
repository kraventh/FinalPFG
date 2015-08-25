package es.dlacalle.finalpfg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import es.dlacalle.finalpfg.R;
import es.dlacalle.finalpfg.objects.AppRow;

public class AppRowAdapter extends BaseAdapter {

    private ArrayList<AppRow> listadoDispositivos;
    private LayoutInflater layoutInflater;

    public AppRowAdapter(Context context, ArrayList<AppRow> listadoDispositivos) {
        this.layoutInflater = LayoutInflater.from(context);
        this.listadoDispositivos = listadoDispositivos;
    }

    @Override
    public int getCount() {
        return listadoDispositivos.size();
    }

    @Override
    public Object getItem(int position) {
        return listadoDispositivos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppRowView btDeviceView;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.lista_approw, null);
            btDeviceView = new AppRowView();
            btDeviceView.appName = (TextView) convertView.findViewById(R.id.appRowName);
            btDeviceView.appPackage = (TextView) convertView.findViewById(R.id.appRowPackage);
            btDeviceView.appIcon = (ImageView) convertView.findViewById(R.id.appRowIcon);

            convertView.setTag(btDeviceView);
        } else btDeviceView = (AppRowView) convertView.getTag();

        AppRow device = (AppRow) getItem(position);
        btDeviceView.appName.setText(device.getName());
        btDeviceView.appPackage.setText(device.getApp_package());
        btDeviceView.appIcon.setImageDrawable(device.getIcon());
        return convertView;
    }

    class AppRowView {
        TextView appName;
        TextView appPackage;
        ImageView appIcon;
    }
}
