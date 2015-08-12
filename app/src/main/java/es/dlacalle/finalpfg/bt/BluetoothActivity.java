package es.dlacalle.finalpfg.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import es.dlacalle.finalpfg.R;
import es.dlacalle.finalpfg.adapters.BTDeviceAdapter;

/**
 * Created by Pedro on 09/08/2015.
 */

public class BluetoothActivity extends AppCompatActivity {

    Button b3;
    int REQUEST_ENABLE_BT = 1;
    private Switch sw_btOnOff;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView lv;
    Boolean BT_AVAILABLE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        sw_btOnOff = (Switch) findViewById(R.id.sw_btOnOff);

        b3=(Button)findViewById(R.id.button3);

        BA = BluetoothAdapter.getDefaultAdapter();
        if(BA==null) BT_AVAILABLE = false;

        lv = (ListView)findViewById(R.id.listView);
        if(BT_AVAILABLE)
            if(BA.isEnabled())
                sw_btOnOff.setChecked(true);
    }

    public void toggleBt (View v) {
        if(BT_AVAILABLE)
            if (sw_btOnOff.isChecked()) {
                if (!BA.isEnabled()) {
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, REQUEST_ENABLE_BT);
                }
            } else {
                BA.disable();
            }
        else {
            sw_btOnOff.setChecked(false);
            Toast.makeText(this.getBaseContext(), "Bluetooth not available", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode != RESULT_OK) {
                sw_btOnOff.setChecked(false);
            }
        }
    }

    public void list(View v) {
        if (BT_AVAILABLE) {
            pairedDevices = BA.getBondedDevices();

            //Listado estándar
//            ArrayList<String> list = new ArrayList<>();
//
//            for (BluetoothDevice bt : pairedDevices)
//                list.add(bt.getName() + '\n' + bt.getAddress());
//            final ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

            //Listado personalizado
            ArrayList<BTDevice> listadoDispositivosBT = new ArrayList<BTDevice>();
            for (BluetoothDevice bt : pairedDevices)
            listadoDispositivosBT.add(new BTDevice(bt.getName(), bt.getAddress(), bt.getBluetoothClass().getMajorDeviceClass()));
            final BTDeviceAdapter adapter = new BTDeviceAdapter(this, listadoDispositivosBT);

            lv.setAdapter(adapter);
        } else Toast.makeText(this.getBaseContext(), "Bluetooth not available", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
