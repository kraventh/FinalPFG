package es.dlacalle.finalpfg;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import es.dlacalle.finalpfg.connectivity.BTService;


/**
 * Fragment para preferencias y configuración
 */
public class MainActivityFragment extends PreferenceFragment {

    private static final String TAG = "MainActivityFragment";

    // Intent request codes
    //private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    //private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    /* Name of the connected device */
    private String mConnectedDeviceName = null;

    /* String buffer for outgoing messages */
    private StringBuffer mOutStringBuffer;

    /* Local Bluetooth adapter */
    private BluetoothAdapter mBluetoothAdapter = null;

    /* Member object for the chat services */
    private BTService mBTService = null;

    private ActionBar actionBar = null;

    private TextView textViewLog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Cargar las preferencias desde el XML
        addPreferencesFromResource(R.xml.preferences);

        // Monitoriza cambios en EditTextPreference
        // para guardar los cambios en un fichero.log si se pulsa Guardar
        Preference pref = findPreference("save_log");
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Toast.makeText(getActivity().getBaseContext(), "Guardado '" + newValue + "'",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Lanza un actitivity for result desde un preference
        Preference bt = findPreference("bluetooth_category_key");
        bt.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivityForResult(preference.getIntent(), MainActivity.BT_DEVICE_TO_CONNECT);

                return true;
            }
        });

        Preference test = findPreference("test_comunicaciones");
        if (test != null) {
            test.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    sendMessage("Hello World!");
                    return false;
                }
            });
        }


        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        setStatus(R.string.title_not_connected);

    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (reqCode == MainActivity.BT_DEVICE_TO_CONNECT) {
            /* Conexion Segura */
            connectDevice(data, true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupApp() will then be called during onActivityResult
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                // Otherwise, setup the chat session
            } else if (mBTService == null) {
                setupApp();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBTService != null) {
            mBTService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
//        if (mBTService != null) {
//            // Only if the state is STATE_NONE, do we know that we haven't started already
//            if (mBTService.getState() == BTService.STATE_NONE) {
//                // Start the Bluetooth chat services
//                mBTService.start();
//            }
//        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        /* Preparar los campos */
        textViewLog = (TextView)view.findViewById(R.id.tv_log);
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupApp() {
        Log.d(TAG, "setupApp()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mBTService = new BTService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mBTService.getState() != BTService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), "No conectado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mBTService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = getActivity();
            switch (msg.what) {
                case BTService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BTService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to) + " " + mConnectedDeviceName);
                            //Toast.makeText(getActivity(), getString(R.string.title_connected_to)+" "+
                            // mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                            break;
                        case BTService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BTService.STATE_LISTEN:
                        case BTService.STATE_NONE:
                            try {
                                setStatus(R.string.title_not_connected);
                            } catch (NullPointerException ne) {

                            }
                            break;
                    }
                    break;
                case BTService.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //Toast.makeText(getActivity(), "MESSAGE_WRITE", Toast.LENGTH_SHORT).show();
                    break;
                case BTService.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    textViewLog.append(readMessage);
                    //Toast.makeText(getActivity(), "MESSAGE_READ", Toast.LENGTH_SHORT).show();
                    break;
                case BTService.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(BTService.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, getString(R.string.title_connected_to) + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case BTService.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(BTService.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString("result");
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBTService.connect(device, secure);
    }

}
