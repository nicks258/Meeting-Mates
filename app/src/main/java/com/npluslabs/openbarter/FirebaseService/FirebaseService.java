package com.npluslabs.openbarter.FirebaseService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.npluslabs.openbarter.R;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.Random;

public class FirebaseService extends FirebaseMessagingService {
    NotificationManager notificationManager;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("Firebase", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("Firebase", "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                Log.i("scheduleJob","Runing");
            } else {
                // Handle message within 10 seconds
                Log.i("Wowo","Else");
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("Body Firebase", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        //Setting up Notification channels for android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }
        int notificationId = new Random().nextInt(60000);
        Logger.addLogAdapter(new AndroidLogAdapter());
        Logger.i("Notification Data-> " + remoteMessage.getData());
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "77")
                .setSmallIcon(R.drawable.ic_baseline_shopping_basket_24px)  //a resource for your custom small icon
//                .setContentTitle(remoteMessage.getData().get("title")) //the "title" value you sent in your notification
                .setContentTitle(remoteMessage.getNotification().getBody()) //the "title" value you sent in your notification
                .setContentText("Message from FCM") //ditto
                .setAutoCancel(true)  //dismisses the notification on click
                .setSound(defaultSoundUri);
        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
//        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
//        String adminChannelDescription = getString(R.string.notifications_admin_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel("900", "Admin Channel", NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription("Description");
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_baseline_shopping_basket_24px : R.drawable.ic_launcher_background;
    }

}