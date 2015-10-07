package es.dlacalle.finalpfg;

public interface Constants {

    //Intent request codes
    int BT_DEVICE_TO_CONNECT = 1;
    int REQUEST_ENABLE_BT = 3;
    int REQUEST_DISCOVERABLE = 4;

    // Message types sent from the Bluetooth Service Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;


    //Constantes para Maps
    String MAPS = "com.google.android.apps.maps";
    String EN = "en ";
    String HACIA = "hacia ";
    String INCORPORATE = "Incorpórate a";
    String CONTINUA = "Continúa por";

}
