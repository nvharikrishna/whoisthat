package com.nvharikrishna.whoisthat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.RecognizerIntent;
import android.util.Log;

public class PushNotificationListenerService extends NotificationListenerService {

    private static final String TAG = "PushNotifListener";
    private BroadcastReceiver notificationListener;

    public PushNotificationListenerService() {
        Log.d(TAG, "Debug********************************");
    }


    private void initializeNotificationReceiver(){
        notificationListener = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Debug********************************");
            }
        };
    }

    private void registerNotificationReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(notificationListener, intentFilter);
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        //Once a notification is posted start listening for our command "Who is that".

        Log.d(TAG, sbn.toString());
        Log.d(TAG, "SBN Package Name" + sbn.getPackageName());
        Log.d(TAG, "Notification Posted");

        Intent pushNotifIntent = new Intent("whoisthat.Recognize");
        pushNotifIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, sbn.getPackageName());
        Log.d(TAG, "packageName : " +sbn.getPackageName());

        String title;
        String message = "";
        final String PACKAGE_WHATSAPP = "com.whatsapp";
        final String PACKAGE_MESSAGE = "com.google.android.apps.messaging";
        final String PACKAGE_MISSED_CALL = "com.android.server.telecom";
        final String PACKAGE_CALL = "com.android.dialer";

        String packageName = sbn.getPackageName();
        switch(packageName){
            case PACKAGE_WHATSAPP:
                title = sbn.getNotification().extras.get("android.title") + " whatsapped you";
                message = sbn.getNotification().extras.get("android.text").toString();
                break;
            case PACKAGE_MESSAGE :
                title = sbn.getNotification().extras.get("android.title") + " messaged you";
                message = sbn.getNotification().extras.get("android.text").toString();
                break;
            case PACKAGE_MISSED_CALL :
                title = "Missed a call from " + sbn.getNotification().extras.get("android.text");
                break;
            default:
                title = "Received Notification from " + sbn.getNotification().extras.get("android.title");
        }

        Log.d(TAG, "title : " +title);
        Log.d(TAG, "message : " +message);

        pushNotifIntent.putExtra("title_to_speak", title);
        pushNotifIntent.putExtra("message_to_speak", message);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!PACKAGE_CALL.equals(packageName))
            sendBroadcast(pushNotifIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Got onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onListenerConnected(){
        Log.d(TAG, "Got Listener Conencted");
    }
}