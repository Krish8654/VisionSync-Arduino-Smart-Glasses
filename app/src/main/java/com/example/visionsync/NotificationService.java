package com.example.visionsync;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationService
        extends NotificationListenerService {

    @Override
    public void onNotificationPosted(
            StatusBarNotification sbn) {

        try {

            String packageName =
                    sbn.getPackageName();

            String title = "";

            String text = "";

            if(sbn.getNotification().extras != null) {

                title = String.valueOf(
                        sbn.getNotification()
                                .extras.get("android.title"));

                text = String.valueOf(
                        sbn.getNotification()
                                .extras.get("android.text"));
            }

            String finalMessage;

            // WHATSAPP
            if(packageName.contains("whatsapp")) {

                finalMessage =
                        "WA\n" +
                                title +
                                "\n" +
                                text;
            }

            // OTHER APPS
            else {

                finalMessage =
                        title +
                                "\n" +
                                text;
            }

            MainActivity.sendData(finalMessage);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}