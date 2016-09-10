package com.nvharikrishna.whoisthat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class PushNotificationListenerService extends NotificationListenerService {
    public PushNotificationListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        //Once a notification is posted start listening for our command "Who is that".

    }
}
