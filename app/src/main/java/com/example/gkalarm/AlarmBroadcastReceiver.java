package com.example.gkalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        // Once the broadcast is received, start the AlarmToneService for tone playback
        Intent serviceIntent = new Intent(context, AlarmToneService.class);
        serviceIntent.putExtra(MainActivity.EXTRA_ALARM_ON,
                intent.getExtras().getBoolean(MainActivity.EXTRA_ALARM_ON));
        serviceIntent.putExtra(MainActivity.EXTRA_ALARM_TYPE,
                intent.getExtras().getInt(MainActivity.EXTRA_ALARM_TYPE));
        serviceIntent.putExtra(MainActivity.EXTRA_ALARM_NAME,
                intent.getExtras().getString(MainActivity.EXTRA_ALARM_NAME));
        serviceIntent.putExtra(MainActivity.EXTRA_ALARM_ID,
                intent.getExtras().getInt(MainActivity.EXTRA_ALARM_ID));

        // Start AlarmToneService
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
