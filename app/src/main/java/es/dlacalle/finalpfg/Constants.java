package es.dlacalle.finalpfg;

/**
 * Created by Pedro on 01/10/2015.
 */

public interface Constants {

    //Intent request codes
    public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_ENABLE_BT = 3;
    public static final int REQUEST_APP_MONITORIZADA = 4;
    public static final int REQUEST_DISCOVERABLE = 4;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // AppListActivity
    public static final String APP_NOMBRE = "app_nombre";
    public static final String APP_PAQUETE = "app_paquete";
    public static final String APP_ICONO = "app_icono";

    //Constantes para Maps
    public static final String MAPS = "com.google.android.apps.maps";
    public static final String EN = "en ";
    public static final String HACIA = "hacia ";
    public static final String INCORPORATE = "Incorpórate a";
    public static final String CONTINUA = "Continúa por";

}
