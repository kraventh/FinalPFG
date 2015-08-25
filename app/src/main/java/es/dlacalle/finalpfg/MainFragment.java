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
import android.widget.Toast;

import es.dlacalle.finalpfg.connectivity.BTService;


/**
 * Fragment para preferencias y configuración
 */
public class MainFragment extends PreferenceFragment {

    /***************************************
     * Variables
     ***************************************/
    private static final String TAG = "MainFragment";
    // Intent request codes
    //private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    //private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private OnFragmentInteractionListener mListener;
    /* Name of the connected device */
    private String mConnectedDeviceName = null;

    /* String buffer for outgoing messages */
    private StringBuffer mOutStringBuffer;

    /* Local Bluetooth adapter */
    private BluetoothAdapter mBluetoothAdapter = null;

    /* Member object for the bt services */
    private BTService mBTService = null;

    private ActionBar actionBar = null;
    /**
     * The Handler that gets information back from the BluetoothChatService
     * Solución al problema memory leak del handler, tan sencillo como cambiar
     * "new Handler(){...}" por "new Handler(new Handler.Callback() {...});" y cambiar la firma de
     * "handleMessage" por "boolean"
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Activity activity = getActivity();
            switch (msg.what) {
                case BTService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BTService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to) + " " + mConnectedDeviceName);
                            mListener.onMainFragmentInteraction(getString(R.string.title_connected_to) + " " + mConnectedDeviceName);

                            break;
                        case BTService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            mListener.onMainFragmentInteraction(getString(R.string.title_connecting));
                            break;
                        case BTService.STATE_LISTEN:
                        case BTService.STATE_NONE:
                            try {
                                setStatus(R.string.title_not_connected);
                                mListener.onMainFragmentInteraction(getString(R.string.title_not_connected));
                            } catch (NullPointerException ne) {
                                Log.e(TAG, "NullPointerException setStatus-Handler");
                            }
                            break;
                    }
                    break;
                case BTService.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mListener.onMainFragmentInteraction("MESSAGE_WRITE - " + writeMessage);
                    //Toast.makeText(getActivity(), "MESSAGE_WRITE", Toast.LENGTH_SHORT).show();
                    break;
                case BTService.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mListener.onMainFragmentInteraction("MESSAGE_READ - " + readMessage);
                    //Toast.makeText(getActivity(), "MESSAGE_READ\n"+readMessage, Toast.LENGTH_SHORT).show();
                    break;
                case BTService.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(BTService.DEVICE_NAME);
                    if (null != activity) {
                        mListener.onMainFragmentInteraction(getString(R.string.title_connected_to) + " " + mConnectedDeviceName);
                        Toast.makeText(activity,
                                getString(R.string.title_connected_to) + " " + mConnectedDeviceName,
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case BTService.MESSAGE_TOAST:
                    if (null != activity) {
                        mListener.onMainFragmentInteraction(msg.getData().getString(BTService.TOAST));
                        Toast.makeText(activity, msg.getData().getString(BTService.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            return true;
        }

    });

    /****************************
     * Funciones del fragmento
     ****************************/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Cargar las preferencias desde el XML
        addPreferencesFromResource(R.xml.preferences);

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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBTService != null) {
            mBTService.stop();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        /* Preparar los campos */
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (data != null) {
            if (reqCode == MainActivity.BT_DEVICE_TO_CONNECT) {
            /* Conexion Segura */
                connectDevice(data, true);
            }
        }
    }

    /**
     * Set up the UI and background operations for bt.
     */
    private void setupApp() {
        Log.d(TAG, "setupApp()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mBTService = new BTService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Sends a message.
     */
    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mBTService != null) {
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
        } else Toast.makeText(getActivity(), "No conectado", Toast.LENGTH_SHORT).show();
    }

    /** Updates the status on the action bar. */
    private void setStatus(int resId) {
        actionBar.setSubtitle(resId);
    }

    private void setStatus(CharSequence subTitle) {
        actionBar.setSubtitle(subTitle);
    }

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

    public interface OnFragmentInteractionListener {
        void onMainFragmentInteraction(String string);
    }

}
