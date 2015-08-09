package es.dlacalle.finalpfg;

import android.bluetooth.BluetoothClass;

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
                return android.R.drawable.stat_sys_headset;
            case BluetoothClass.Device.Major.COMPUTER:
                return android.R.drawable.stat_sys_headset;
            case BluetoothClass.Device.Major.HEALTH:
                return android.R.drawable.stat_sys_headset;
            case BluetoothClass.Device.Major.IMAGING:
                return android.R.drawable.stat_sys_headset;
            case BluetoothClass.Device.Major.MISC:
                return android.R.drawable.stat_sys_headset;
            case BluetoothClass.Device.Major.NETWORKING:
                return android.R.drawable.stat_sys_headset;
            case BluetoothClass.Device.Major.PERIPHERAL:
                return android.R.drawable.stat_sys_headset;
            case BluetoothClass.Device.Major.PHONE:
                return android.R.drawable.stat_sys_speakerphone;
            case BluetoothClass.Device.Major.TOY:
                return android.R.drawable.stat_sys_headset;
            case BluetoothClass.Device.Major.UNCATEGORIZED:
                return android.R.drawable.stat_sys_headset;
            case BluetoothClass.Device.Major.WEARABLE:
                return android.R.drawable.star_off;
            default:
                return android.R.drawable.stat_sys_warning;
        }
    }
}
