package es.dlacalle.finalpfg.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class NotificationService extends NotificationListenerService {

    Context context;
    private final static String TAG = "FinalPFG-NtfService";
    private NSReceiver nsReceiver;

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
        nsReceiver = new NSReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("es.dlacalle.finalpfg.NOTIFICATION_LISTENER_SERVICE_FINALPFG");
        registerReceiver(nsReceiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nsReceiver);
    }
    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {


        String pack = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = "";
        String bigText = "";
        try {
            text = extras.getCharSequence("android.text").toString();
            bigText = extras.getCharSequence("android.bigText").toString();
        } catch (NullPointerException ne) {
            Log.e(TAG, "NullPointerException: Notification Service");
        }

        Intent msgrcv = new Intent("es.dlacalle.finalpfg.NOTIFICATION_LISTENER_FINALPFG");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);
        msgrcv.putExtra("bigText", bigText);


        //LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
        sendBroadcast(msgrcv);

    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");

    }

    class NSReceiver extends BroadcastReceiver{
        @Override
        public void onReceive (Context context, Intent intent){
            if(intent.getStringExtra("command").equals("list")){
                for (StatusBarNotification sbn : NotificationService.this.getActiveNotifications()){
                    Intent notifList = new Intent("es.dlacalle.finalpfg.NOTIFICATION_LISTENER_FINALPFG");
                    String pack = sbn.getPackageName();
                    Bundle extras = sbn.getNotification().extras;
                    String title = extras.getString("android.title");
                    String text = "";
                    try {
                        text = extras.getCharSequence("android.text").toString();
                    } catch (NullPointerException ne) {
                        Log.e(TAG, "NullPointerException: Notification Service");
                    }
                    notifList.putExtra("package", pack);
                    notifList.putExtra("title", title);
                    notifList.putExtra("text", text);
                    sendBroadcast(notifList);
                }
            }
        }
    }
}
