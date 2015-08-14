package es.dlacalle.finalpfg.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import es.dlacalle.finalpfg.R;
import es.dlacalle.finalpfg.adapters.BTDeviceAdapter;

public class BluetoothActivity extends AppCompatActivity {

    private String NAME = "FinalPFG_BT_Service";
    private UUID MY_UUID = UUID.fromString("39deed76-872d-4d68-b2fd-b256a4126e18");
    private Switch sw_btOnOff;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView lv_paired;
    private ListView lv_new;

    private ArrayList<BTDevice> listadoEmparejados;
    private ArrayList<BTDevice> listadoNuevos;

    private BTDeviceAdapter adapterNuevos;
    private BTDeviceAdapter adapterEmparejados;

    Boolean BT_AVAILABLE = true;
    int REQUEST_ENABLE_BT = 1;

    private TextView textViewEmparejados;
    private TextView textViewNuevos;

    private ProgressBar progressBarBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        //Botones
        sw_btOnOff = (Switch) findViewById(R.id.sw_btOnOff);

        //ListView
        lv_paired = (ListView) findViewById(R.id.bt_paired_list);
        lv_new = (ListView) findViewById(R.id.bt_newdevices_list);

        //ArrayList
        listadoEmparejados = new ArrayList<>();
        listadoNuevos = new ArrayList<>();

        //ArrayAdapters
        adapterEmparejados = new BTDeviceAdapter(this, listadoEmparejados);
        lv_paired.setAdapter(adapterEmparejados);

        adapterNuevos = new BTDeviceAdapter(this, listadoNuevos);
        lv_new.setAdapter(adapterNuevos);

        //TextView
        textViewEmparejados = (TextView)findViewById(R.id.tv_paired);
        textViewNuevos = (TextView)findViewById(R.id.tv_new_devices);
        textViewEmparejados.setVisibility(View.INVISIBLE);
        textViewNuevos.setVisibility(View.INVISIBLE);

        progressBarBT = (ProgressBar)findViewById(R.id.progressBarBT);
        progressBarBT.setVisibility(View.INVISIBLE);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBTAdapter == null) BT_AVAILABLE = false;

        if (BT_AVAILABLE)
            if (mBTAdapter.isEnabled())
                sw_btOnOff.setChecked(true);

        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bt_buscar) {
            buscarNuevos(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toggleBt(View v) {
        if (BT_AVAILABLE)
            if (sw_btOnOff.isChecked()) {
                if (!mBTAdapter.isEnabled()) {
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, REQUEST_ENABLE_BT);
                }
            } else {
                mBTAdapter.disable();
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

    public void listarEmparejados(View v) {

            pairedDevices = mBTAdapter.getBondedDevices();

            //Listado estándar
//            ArrayList<String> list = new ArrayList<>();
//
//            for (BluetoothDevice bt : pairedDevices)
//                list.add(bt.getName() + '\n' + bt.getAddress());
//            final ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

            //Listado personalizado
            for (BluetoothDevice bt : pairedDevices)
                listadoEmparejados.add(new BTDevice(bt.getName(), bt.getAddress(), bt.getBluetoothClass().getMajorDeviceClass()));
            adapterEmparejados.notifyDataSetChanged();
        int c = adapterEmparejados.getCount();
        if(c == 0) textViewEmparejados.setVisibility(View.INVISIBLE);
        else if (c == 1) textViewEmparejados.setText(c + " dispositivo emparejado:");
        else textViewEmparejados.setText(c + " dispositivos emparejados:");
    }

    public void buscarNuevos(View v) {

        if (BT_AVAILABLE) {

            //Emparejados
            listadoEmparejados.clear();
            adapterEmparejados.notifyDataSetChanged();
            textViewEmparejados.setVisibility(View.VISIBLE);
            listarEmparejados(v);

            //Nuevos
            listadoNuevos.clear();
            adapterNuevos.notifyDataSetChanged();
            textViewNuevos.setText("Buscando dispositivos...");
            textViewNuevos.setVisibility(View.VISIBLE);
            progressBarBT.setVisibility(View.VISIBLE);

            if (mBTAdapter.isDiscovering()) mBTAdapter.cancelDiscovery();
            mBTAdapter.startDiscovery();
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice bt = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(getBaseContext(), bt.getName(), Toast.LENGTH_SHORT).show();

                // Add the name and address to an array adapter to show in a ListView
                listadoNuevos.add(new BTDevice(bt.getName(), bt.getAddress(), bt.getBluetoothClass().getMajorDeviceClass()));
                adapterNuevos.notifyDataSetChanged();
            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Toast.makeText(getBaseContext(), "Busqueda finalizada", Toast.LENGTH_SHORT).show();
                if(adapterNuevos.getCount()==0) textViewNuevos.setText("No se encontraron dispositivos");
                else {
                    int c = adapterNuevos.getCount();
                    if (c>1) textViewNuevos.setText(adapterNuevos.getCount() + " dispositivos encontrados:");
                    else textViewNuevos.setText(adapterNuevos.getCount() + " dispositivo encontrado:");
                }
                progressBarBT.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

}
