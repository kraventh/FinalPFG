package es.dlacalle.finalpfg;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import es.dlacalle.finalpfg.services.BTService;


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

    private NotificationReceiver notificationReceiver;

    private LocationManager locationManager;

    private LocationListener locationListener;

    private SharedPreferences pref;
    private SharedPreferences.OnSharedPreferenceChangeListener spfListener;

    private String velocidad;

    private String mensajePrevio="";

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
            Preference bt = findPreference("bluetooth_category_key");
            switch (msg.what) {
                case BTService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BTService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to) + " " + mConnectedDeviceName);
                            mListener.onMainFragmentInteraction(getString(R.string.title_connected_to) + " " + mConnectedDeviceName);
                            bt.setSummary("Conectado a " + mConnectedDeviceName);
                            break;
                        case BTService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            if (mListener != null)
                                mListener.onMainFragmentInteraction(getString(R.string.title_connecting));
                            break;
                        case BTService.STATE_LISTEN:
                        case BTService.STATE_NONE:
                            try {
                                setStatus(R.string.title_not_connected);
                                bt.setSummary("Disponible. Desconectado");
                                if (isAdded()) {
                                    mListener.onMainFragmentInteraction(getString(R.string.title_not_connected));
                                }
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
                    Intent iList = new Intent("es.dlacalle.finalpfg.NOTIFICATION_LISTENER_SERVICE_FINALPFG");
                    iList.putExtra("command", "list");
                    getActivity().sendBroadcast(iList);
                    return true;
                }
            });
        }

        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        notificationReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("es.dlacalle.finalpfg.NOTIFICATION_LISTENER_FINALPFG");
        getActivity().registerReceiver(notificationReceiver, filter);

        setStatus(R.string.title_not_connected);
        setMonApp();


        /********** get Gps location service LocationManager object ***********/
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                String str = "\nLatitud: " + location.getLatitude() + "\nLongitud: " + location.getLongitude();
                velocidad = String.valueOf(location.getSpeed());
                Log.d(TAG, "GPS" + str);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
                Log.d(TAG, "Gps activado ");
            }

            public void onProviderDisabled(String provider) {
                Log.d(TAG, "Gps desactivado ");
            }
        };

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        int frecuencia = Integer.parseInt(pref.getString("frecuencia_gps", "5000"));
        int distancia = Integer.parseInt(pref.getString("distancia_gps", "10"));
        if (pref.getBoolean("activar_gps", false)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, frecuencia, distancia, locationListener);
            Log.d(TAG, "LocationManager started with: " + frecuencia + "-" + distancia);
        }
        spfListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(
                    SharedPreferences prefs, String key) {
                Log.d(TAG, "SPFListener: " + key);
                int f = 1000;
                int d = 10;
                boolean activar_actualizar_location = true;
                switch (key) {
                    case "activar_gps":
                        activar_actualizar_location = prefs.getBoolean(key, false);

                        if (locationListener != null && locationManager != null)
                            locationManager.removeUpdates(locationListener);

                        break;
                    case "frecuencia_gps":
                        break;
                    case "distancia_gps":
                        break;
                }
                if (activar_actualizar_location) {
                    if (locationManager != null) {
                        try {
                            f = Integer.parseInt(prefs.getString("frecuencia_gps", "5000"));
                        } catch (NumberFormatException nfe) {
                            prefs.edit().putString(key, "1000").apply();
                        }
                        try {
                            d = Integer.parseInt(prefs.getString("distancia_gps", "10"));
                        } catch (NumberFormatException nfe) {
                            prefs.edit().putString(key, "10").apply();
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                f, d, locationListener);
                        Log.d(TAG, "LocationManager restarted: " + f + " - " + d);
                    }
                }
            }
        };
        pref.registerOnSharedPreferenceChangeListener(spfListener);
        if (!pref.getBoolean("enable_debug", false)) {
            PreferenceScreen prefScr = getPreferenceScreen();
            prefScr.removePreference(test);
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
                // Otherwise, setup
            } else if (mBTService == null) {
                setupApp();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
        getActivity().unregisterReceiver(notificationReceiver);
        pref.unregisterOnSharedPreferenceChangeListener(spfListener);
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

        // Initialize the BluetoothService to perform bluetooth connections
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

    /**
     * Updates the status on the action bar.
     */
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

    public void setMonApp() {

        Preference pref = findPreference("app_monitorizada");
        pref.setTitle(((EditTextPreference) findPreference("app_monitorizada_titulo")).getText());
        pref.setSummary(((EditTextPreference) findPreference("app_monitorizada_paquete")).getText());
        if (!pref.getTitle().equals("App no seleccionada")) {
            try {
                pref.setIcon(getActivity().getPackageManager().getApplicationIcon(pref.getSummary().toString()));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "setMonApp - Invalid Package Name");
            }
        }

    }

    //Función que prepara el texto para enviar al dispositivo externo
    public void parseNotifMaps(String notifText) {
        String mensaje[] = new String[7];
        for (int i = 0; i < mensaje.length; i++) mensaje[i] = "-;"; //Inicio los campos
        mensaje[0] = "maps;";
        boolean finmsg = false;

        Log.d(TAG, "Mensaje=> " + componerMensaje(mensaje));

        try {
            //Primero elimino el nombre del destino para facilitar las extracciones posteriores
            //notifText = notifText.substring(notifText.indexOf(": ") + 1);
            //
            // Empezamos con las situaciones
            //
            // el mensaje lo compondremos de la siguiente manera:
            // maps;distancia;direccion_a_pintar;via_destino;tiempo(distancia_restante);hora_llegada;velocidad
            // mensaje[0]: maps
            // mensaje[1]: distancia
            // mensaje[2]: direccion_a_pintar
            // mensaje[3]: via_destino
            // mensaje[4]: tiempo(distancia_restante)
            // mensaje[5]: hora_llegada
            // mensaje[6]: velocidad
            //


            if (notifText.contains("km - ")) {
                //Distancia restante por aquí
                mensaje[1] = notifText.substring(0, notifText.indexOf("km - ") - 1) + "k;";
            } else if (notifText.contains("m - ")) {
                mensaje[1] = notifText.substring(0, notifText.indexOf("m - ") - 1) + "m;";
            } // En otro caso, campo no disponible


            if (notifText.contains("Buscando GPS") || notifText.contains("dirigiendo") || notifText.contains("cambio de sentido")) {
                if (notifText.contains("cambio de sentido"))
                    mensaje[2] = "cambioSentido";
                else
                    mensaje[2] = "nogps";
                String enviar = componerMensaje(mensaje);
                mListener.onMainFragmentInteraction(enviar);
                sendMessage(enviar);
                return; // Y nos salimos
            }
            //Me da igual el punto cardinal, es siempre salir recto
            if (notifText.contains("norte") || notifText.contains("sur") ||
                    notifText.contains("oeste") || notifText.contains("este")) {
                mensaje[2] = "arriba;"; //Direccion a pintar

            }
            //Puede que tengamos que girar
            else if (notifText.contains("Gira")) {
                // a la izquierda
                if (notifText.contains("izquierda")) mensaje[2] = "izquierda;";
                    //o a la derecha
                else mensaje[2] = "derecha;";
            }
            //O puede ser una rotonda
            else if (notifText.contains("rotonda") || notifText.toLowerCase().contains("plaza")) {
                //Estoy considerando una rotonda como máximo de 5 salidas
                if (notifText.contains("primera")) mensaje[2] = "rotonda1;";
                else if (notifText.contains("segunda")) mensaje[2] = "rotonda2;";
                else if (notifText.contains("tercera")) mensaje[2] = "rotonda3;";
                else if (notifText.contains("cuarta")) mensaje[2] = "rotonda4;";
                else if (notifText.contains("quinta")) mensaje[2] = "rotonda5;";
                else if (notifText.contains("recto")) {
                    mensaje[2] = "rotondaRecto;";
                }
                //O puede ser que estés en una de esas rotondas con urbanización incluida y
                //tengas que salirte ya por la próxima salida
                else if (notifText.contains("Sal de la rotonda")) {
                    mensaje[2] = "rotondaSalida;";
                    if (notifText.contains(Constants.EN))
                        //si no sales en
                        mensaje[3] = notifText.substring(
                                notifText.indexOf(Constants.EN) + Constants.EN.length(), notifText.indexOf('\n'))
                                + ";";
                    else
                        //sales hacia
                        mensaje[3] = notifText.substring(
                                notifText.lastIndexOf(Constants.HACIA) + Constants.HACIA.length(), notifText.indexOf('\n'))
                                + ";";
                }
                finmsg = true;
            }
            // O puede que te tengas que incorporar a una autovía o autopista
            else if (notifText.contains(Constants.INCORPORATE)) {
                mensaje[2] = "Incorporate;";//notifText.substring(Constants.INCORPORATE.length()) + ";";
                mensaje[3] = notifText.substring(notifText.indexOf(Constants.INCORPORATE)+Constants.INCORPORATE.length(),
                        notifText.indexOf('\n')).toUpperCase()+";";
                finmsg = true;
            }

            // O puede que simplemente tengas que continuar por donde vas
            else if (notifText.contains(Constants.CONTINUA)) {
                mensaje[2] = "arriba;";
                finmsg = true;
            }
            Log.d(TAG, "Mensaje prerotonda => " + componerMensaje(mensaje));

            if (!finmsg) {
                //Añadimos el nombre de la vía por la que vamos
                if (notifText.contains(Constants.EN) || notifText.contains("por ")
                        || notifText.contains(Constants.HACIA) || notifText.contains("salida")) {
                    int inicio = notifText.indexOf(Constants.EN);
                    if (inicio < 0) inicio = notifText.indexOf("por ");
                    int fin = notifText.lastIndexOf(Constants.HACIA);

                    Log.d(TAG, "Posiciones vía - " + inicio + ":" + fin);

                    String tmp = "-;";
                    if ((fin < inicio) || inicio == -1) { //Varios motivos
                        //2 - Que sea salida
                        if (notifText.indexOf("salida") > 0 && notifText.indexOf(Constants.EN) > 0)
                            tmp = notifText.substring(notifText.indexOf("salida"), notifText.indexOf(Constants.EN) - 1) + ";";
                        else if (notifText.indexOf("salida") > 0 && notifText.indexOf(Constants.HACIA) > 0)
                            tmp = notifText.substring(notifText.indexOf("salida"), notifText.indexOf(Constants.HACIA) - 1) + ";";
                            // 3 - Que sea "por "
                        else if (notifText.indexOf("por ") > 0)
                            tmp = notifText.substring(inicio + 4, notifText.indexOf('\n')) + ";";
                    } else {
                        tmp = notifText.substring(inicio + Constants.EN.length(), fin - 1);
                        //Añadimos el nombre de la vía a la que nos dirigimos
                        if (notifText.contains(Constants.HACIA)) {
                            tmp += notifText.substring(
                                    notifText.lastIndexOf(Constants.HACIA) + Constants.HACIA.length(),
                                    notifText.indexOf('\n')) + ";";
                        }
                    }
                    mensaje[3] = tmp.toUpperCase();
                }
            }

            Log.d(TAG, "Mensaje posrotonda => " + componerMensaje(mensaje));
            //Añadimos la distancia al destino estimada en tiempo y kilometros
            if (notifText.contains(" hasta el destino")) {
                String hora = notifText.substring(notifText.indexOf('\n') + 2, notifText.indexOf(" hasta el destino")) + ";";
                hora = hora.replace(" h y ", "h:");
                hora = hora.replace(" min (", "m ");
                if (hora.indexOf("km") > 0) {
                    hora = hora.substring(0, hora.indexOf("km") - 1) + "km;";
                } else hora = hora.substring(0, hora.length() - 2);
                mensaje[4] = hora;
                //Añadimos la hora aproximada de llegada
                mensaje[5] = notifText.substring(notifText.indexOf("aproximada a las ") + 17) + ";";

            }

            Log.d(TAG, "Mensaje=> " + componerMensaje(mensaje));
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            if (pref.getBoolean("activar_gps", false)) {
                mensaje[6] = velocidad + ";";
            }
            String actual = componerMensaje(mensaje);
            if (pref.getBoolean("enviar_notificaciones", false))
                if (!mensajePrevio.equals(actual)) {
                    mensajePrevio = actual;
                    sendMessage(actual);
                    mListener.onMainFragmentInteraction(componerMensaje(mensaje));
                }
        } catch (NullPointerException e) {
            mListener.onMainFragmentInteraction("Notificación errónea");
        }
    }

    public String componerMensaje(String mensaje[]) {
        String compuesto = "";
        for (String aMensaje : mensaje) compuesto += aMensaje;
        Log.d(TAG, "Mensaje a enviar: " + compuesto);
        return compuesto;
    }

    //Receptor de las notificaciones
    class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String pkgName = intent.getStringExtra("package");
            String title = intent.getStringExtra("title");
            //String text = intent.getStringExtra("text");

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            //Obtengo el nombre del paquete cuyas notificaciones quiero atender
            //"Ninguna" es el valor que devuelve por defecto si el campo estuviese vacío
            if (pref.getBoolean("habilitar_historico_notificaciones", false)) {
                String prefPackage = pref.getString("app_monitorizada_paquete", "Ninguna");
                try {

                    if (pkgName.equals(prefPackage)) {
                        Log.d(TAG, "Notificacion recibida");

                        if (pkgName.equals(Constants.MAPS)) {
                            Log.d(TAG, "Es de Maps=> " + title);
                            String bigText = intent.getStringExtra("bigText");
                            parseNotifMaps(bigText);
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "NotifReceiver - Exception");
                }
            }

        }
    }

    //Interfaz con el activity
    public interface OnFragmentInteractionListener {
        void onMainFragmentInteraction(String string);
    }

}
