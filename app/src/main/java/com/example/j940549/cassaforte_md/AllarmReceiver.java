package com.example.j940549.cassaforte_md;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class AllarmReceiver extends BroadcastReceiver {

    private static final int NOTIF_ID_= 100;
    private static final String PRIMARY_CHANEL_ID="primary_notification_chanel";
    private Context context;
    private NotificationManager notificationManager;



    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("brodcast", "ricevuto");
        this.context=context;

        notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIF_ID_,
                notificationIntent, PendingIntent
                        .FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context,PRIMARY_CHANEL_ID)
                .setSmallIcon(R.drawable.ic_record_voice_over_black_24dp)
                .setContentTitle("Notifica da Cassaforte")
                .setContentText("che ne dici di fare un backup?")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(NOTIF_ID_, notification.build());



        createNotificationChannel();

   }


private void createNotificationChannel(){
        notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
    NotificationChannel notificationChannel= new NotificationChannel(PRIMARY_CHANEL_ID,"Notifiche settimanale Backup", NotificationManager.IMPORTANCE_HIGH);
    notificationChannel.enableLights(true);
    notificationChannel.setLightColor(Color.CYAN);
    notificationChannel.enableVibration(true);
    notificationChannel.setDescription("Notifica settimanale backup");
    notificationManager.createNotificationChannel(notificationChannel);
}
    }


    }

