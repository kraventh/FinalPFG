package es.dlacalle.finalpfg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
        AppRowView appRowView;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.lista_approw, null);
            appRowView = new AppRowView();
            appRowView.appName = (TextView) convertView.findViewById(R.id.appRowName);
            appRowView.appPackage = (TextView) convertView.findViewById(R.id.appRowPackage);
            appRowView.appIcon = (ImageView) convertView.findViewById(R.id.appRowIcon);
            appRowView.appChecked = (CheckBox) convertView.findViewById(R.id.appRowCheck);

            convertView.setTag(appRowView);
        } else appRowView = (AppRowView) convertView.getTag();

        AppRow app = (AppRow) getItem(position);
        appRowView.appName.setText(app.getName());
        appRowView.appPackage.setText(app.getApp_package());
        appRowView.appIcon.setImageDrawable(app.getIcon());
        appRowView.appChecked.setChecked(app.isChecked());
        return convertView;
    }

    class AppRowView {
        TextView appName;
        TextView appPackage;
        ImageView appIcon;
        CheckBox appChecked;
    }
}
