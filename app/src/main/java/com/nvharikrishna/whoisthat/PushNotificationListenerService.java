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
//        registerReceiver(smsReceiver, intentFilter);
        Log.e(TAG, "Error********************************" );
        Log.d(TAG, "Debug********************************");
//        initializeNotificationReceiver();
//        registerNotificationReceiver();
    }


    private void initializeNotificationReceiver(){
        notificationListener = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Debug********************************");

//                Bundle bundle = intent.getExtras();
//                if(bundle!=null){
//                    Object[] pdus = (Object[])bundle.get("pdus");
//                    for(int i=0;i<pdus.length;i++){
//                        byte[] pdu = (byte[])pdus[i];
//                        SmsMessage message = SmsMessage.createFromPdu(pdu);
//                        String text = message.getDisplayMessageBody();
//                        String sender = getContactName(message.getOriginatingAddress());
//                        speaker.pause(LONG_DURATION);
//                        speaker.speak("You have a new message from" + sender + "!");
//                        speaker.pause(SHORT_DURATION);
//                        speaker.speak(text);
//                        smsSender.setText("Message from " + sender);
//                        smsText.setText(text);
//                    }
//                }

            }
        };
    }

    private void registerNotificationReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(notificationListener, intentFilter);
    }

//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        //Once a notification is posted start listening for our command "Who is that".

        Log.d(TAG, sbn.toString());
        Log.d(TAG, "SBN Package Name" + sbn.getPackageName());
        Log.d(TAG, "Notification Posted");

        Intent pushNotifIntent = new Intent("whoisthat.Recognize");
        pushNotifIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, sbn.getPackageName());
        pushNotifIntent.putExtra("message_to_speak", "You have received message from " + sbn.getNotification().extras.get("android.title"));
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