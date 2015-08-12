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
import es.dlacalle.finalpfg.bt.BTDevice;

/**
 * Created by Pedro on 09/08/2015.
 */
public class BTDeviceAdapter extends BaseAdapter {

    private ArrayList<BTDevice> listadoDispositivos;
    private LayoutInflater layoutInflater;

    public BTDeviceAdapter(Context context, ArrayList<BTDevice> listadoDispositivos) {
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
        BTDeviceView btDeviceView;

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.lista_btdevices, null);
            btDeviceView = new BTDeviceView();
            btDeviceView.deviceName = (TextView) convertView.findViewById(R.id.btDeviceName);
            btDeviceView.deviceAddress = (TextView) convertView.findViewById(R.id.btDeviceAddress);
            btDeviceView.deviceType = (ImageView) convertView.findViewById(R.id.btDeviceType);

            convertView.setTag(btDeviceView);
        } else btDeviceView = (BTDeviceView) convertView.getTag();

        BTDevice device = (BTDevice)getItem(position);
        btDeviceView.deviceName.setText(device.getName());
        btDeviceView.deviceAddress.setText(device.getAddress());
        btDeviceView.deviceType.setImageResource(device.getType());
        return convertView;
    }

    class BTDeviceView{
        TextView deviceName;
        TextView deviceAddress;
        ImageView deviceType;
    }
}
