package es.dlacalle.finalpfg.connectivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import es.dlacalle.finalpfg.R;
import es.dlacalle.finalpfg.adapters.BTDeviceAdapter;
import es.dlacalle.finalpfg.objects.BTDevice;

public class BluetoothActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_DISCOVERABLE = 4;
    Boolean BT_AVAILABLE = true;
    private String NAME = "FinalPFG_BT_Service";
    private Switch sw_btOnOff;
    private Switch sw_btVisibility;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView lv_paired;
    private ListView lv_new;
    private ArrayList<BTDevice> listadoEmparejados;
    private ArrayList<BTDevice> listadoNuevos;
    private BTDeviceAdapter adapterNuevos;
    private BTDeviceAdapter adapterEmparejados;
    private TextView textViewEmparejados;
    private TextView textViewNuevos;

    private ProgressBar progressBarBT;
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
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(getBaseContext(), "Busqueda finalizada", Toast.LENGTH_SHORT).show();
                if (adapterNuevos.getCount() == 0)
                    textViewNuevos.setText("No se encontraron dispositivos");
                else {
                    int c = adapterNuevos.getCount();
                    if (c > 1)
                        textViewNuevos.setText(adapterNuevos.getCount() + " dispositivos encontrados:");
                    else
                        textViewNuevos.setText(adapterNuevos.getCount() + " dispositivo encontrado:");
                }
                progressBarBT.setVisibility(View.INVISIBLE);
            }
        }
    };
    private CountDownTimer cd_visibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        //Botones
        sw_btOnOff = (Switch) findViewById(R.id.sw_btOnOff);
        sw_btVisibility = (Switch) findViewById(R.id.sw_visibility);

        //ListView
        lv_paired = (ListView) findViewById(R.id.bt_paired_list);
        lv_new = (ListView) findViewById(R.id.bt_newdevices_list);

        //ArrayList
        listadoEmparejados = new ArrayList<>();
        listadoNuevos = new ArrayList<>();

        //ArrayAdapters
        adapterEmparejados = new BTDeviceAdapter(this, listadoEmparejados);
        lv_paired.setAdapter(adapterEmparejados);
        lv_paired.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BTDevice clicked = (BTDevice) parent.getItemAtPosition(position);
                /*Toast.makeText(getBaseContext(),
                        "Seleccionado: " + clicked.getName() + '\n' + clicked.getAddress(),
                        Toast.LENGTH_SHORT).show();
                */
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", clicked.getAddress());
                setResult(RESULT_OK, returnIntent);
                finish();
            }

        });

        adapterNuevos = new BTDeviceAdapter(this, listadoNuevos);
        lv_new.setAdapter(adapterNuevos);
        lv_new.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BTDevice clicked = (BTDevice) parent.getItemAtPosition(position);
                /* Toast.makeText(getBaseContext(),
                        "Seleccionado: " + clicked.getName() + '\n' + clicked.getAddress(),
                        Toast.LENGTH_SHORT).show();
                        */
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", clicked.getAddress());
                setResult(RESULT_OK, returnIntent);
                finish();
            }

        });

        //TextView
        textViewEmparejados = (TextView) findViewById(R.id.tv_paired);
        textViewNuevos = (TextView) findViewById(R.id.tv_new_devices);
        textViewEmparejados.setVisibility(View.INVISIBLE);
        textViewNuevos.setVisibility(View.INVISIBLE);

        progressBarBT = (ProgressBar) findViewById(R.id.progressBarBT);
        progressBarBT.setVisibility(View.INVISIBLE);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBTAdapter == null) BT_AVAILABLE = false;

        if (BT_AVAILABLE)
            if (mBTAdapter.isEnabled()) {
                sw_btOnOff.setChecked(true);
                listarEmparejados(null);
                //if (mBTAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
                //    sw_btVisibility.setChecked(true);
            }

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
                if (cd_visibility != null) cd_visibility.cancel();
                sw_btVisibility.setText(R.string.text_bt_visibility);
                sw_btVisibility.setChecked(false);

            }
        else {
            sw_btOnOff.setChecked(false);
            Toast.makeText(this.getBaseContext(), "Bluetooth not available", Toast.LENGTH_SHORT).show();
        }
    }

    public void enableVisibility(View v) {
        if (BT_AVAILABLE) {
            if (!sw_btVisibility.isChecked()) {
                if (cd_visibility != null) cd_visibility.cancel();
                sw_btVisibility.setText(R.string.text_bt_visibility);
            } else {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);
            }
        } else {
            sw_btVisibility.setChecked(false);
            Toast.makeText(this.getBaseContext(), "Bluetooth not available", Toast.LENGTH_SHORT).show();

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        Toast.makeText(getBaseContext(), "Request: " + requestCode + " -  Result: " + resultCode, Toast.LENGTH_SHORT).show();

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // Make sure the request was successful
                if (resultCode != RESULT_OK) {
                    sw_btOnOff.setChecked(false);
                }
                break;
            case REQUEST_DISCOVERABLE:
                // Aqui en resultCode tenemos los segundos que permanecerá visible
                if (resultCode != RESULT_CANCELED) {
                    //Marcamos el switch de encendido si es que no lo estaba
                    if (!sw_btOnOff.isChecked()) sw_btOnOff.setChecked(true);
                    //Creamos el contador
                    cd_visibility = new CountDownTimer(resultCode * 1000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            sw_btVisibility.setText(
                                    "Dispositivo visible (" +
                                            new SimpleDateFormat("mm:ss", Locale.ENGLISH).format(millisUntilFinished) +
                                            ")");
                        }

                        public void onFinish() {
                            sw_btVisibility.setText(R.string.text_bt_visibility);
                            sw_btVisibility.setChecked(false);
                        }
                    }.start();
                }
                break;
        }
    }

    public void listarEmparejados(View v) {

        listadoEmparejados.clear();
        adapterEmparejados.notifyDataSetChanged();
        textViewEmparejados.setVisibility(View.VISIBLE);

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
        if (c == 0) textViewEmparejados.setVisibility(View.INVISIBLE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

}
