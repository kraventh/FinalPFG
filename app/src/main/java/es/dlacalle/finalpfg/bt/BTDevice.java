package es.dlacalle.finalpfg.bt;

import android.bluetooth.BluetoothClass;

import es.dlacalle.finalpfg.R;

/**
 * Created by Pedro on 09/08/2015.
 */
public class BTDevice {
    private String name;
    private String address;
    private int type;

    public BTDevice(String name, String address, int type) {
        this.name = name;
        this.address = address;
        this.type = check(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = check(type);
    }

    private int check(int type) {
        switch (type) {
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                return R.drawable.bt_generic_icon;
            case BluetoothClass.Device.Major.COMPUTER:
                return R.drawable.bt_generic_icon;
            case BluetoothClass.Device.Major.HEALTH:
                return R.drawable.bt_generic_icon;
            case BluetoothClass.Device.Major.IMAGING:
                return R.drawable.bt_generic_icon;
            case BluetoothClass.Device.Major.MISC:
                return R.drawable.bt_generic_icon;
            case BluetoothClass.Device.Major.NETWORKING:
                return R.drawable.bt_generic_icon;
            case BluetoothClass.Device.Major.PERIPHERAL:
                return R.drawable.bt_generic_icon;
            case BluetoothClass.Device.Major.PHONE:
                return R.drawable.bt_generic_icon;
            case BluetoothClass.Device.Major.TOY:
                return R.drawable.bt_generic_icon;
            case BluetoothClass.Device.Major.UNCATEGORIZED:
                return R.drawable.bt_generic_icon;
            case BluetoothClass.Device.Major.WEARABLE:
                return R.drawable.bt_generic_icon;
            default:
                return android.R.drawable.stat_sys_warning;
        }
    }
}
