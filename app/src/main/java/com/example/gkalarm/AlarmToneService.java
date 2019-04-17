package com.example.gkalarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import static com.example.gkalarm.MainActivity.EXTRA_ALARM_ID;
import static com.example.gkalarm.MainActivity.EXTRA_ALARM_NAME;
import static com.example.gkalarm.MainActivity.EXTRA_ALARM_ON;
import static com.example.gkalarm.MainActivity.EXTRA_ALARM_TYPE;
import static com.example.gkalarm.TimeSelectFragment.ALARM_ALIEN;
import static com.example.gkalarm.TimeSelectFragment.ALARM_BOMB_SIREN;
import static com.example.gkalarm.TimeSelectFragment.ALARM_SCHOOL_BELL;
import static com.example.gkalarm.TimeSelectFragment.ALARM_WATCH;

/**
 * @author Damsith Karunaratna
 *
 * Since the alarm needs to run in the background even when the app is not running a service which
 * handles the audio playback of the alarm tone was created.
 *
 * The service is started in the foreground and a notification is displayed while the service is
 * running. Clicking on the notfication will run an Intent to open the QuestionActivity.
 * If the QuestionActivity is started while the GKAlarm app is closed, it will not return to the
 * MainActivity when the user presses the back button. Hence getPendingIntentWithBackStack() is
 * called to inflate the backstack and preserve the expected user navigation
 *
 * See <a href=
 * "https://developer.android.com/training/notify-user/navigation"
 * >Start an Activity from a Notification</a> for more information.
 */
public class AlarmToneService extends Service {

    /**
     * Commands to the service
     */
    static final int MSG_SAY_HELLO = 1;
    static final int MSG_REPLY = 2;
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    Messenger mMessenger;



    String CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";
    NotificationCompat.Builder notificationBuilder;
    MediaPlayer mediaPlayer;
    Intent questionActivityIntent;
    PendingIntent pendingNotificationIntent;
    boolean alarmOn;
    static boolean isPlaying = false;
    int alarmType;

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        mMessenger = new Messenger(new IncomingHandler(this));
        return mMessenger.getBinder();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // Check the type of message received from MainActivity and act accordingly
        alarmOn = intent.getExtras().getBoolean(EXTRA_ALARM_ON);
        alarmType = intent.getExtras().getInt(EXTRA_ALARM_TYPE);

        if (alarmOn) {

            String alarmName = intent.getExtras().getString(EXTRA_ALARM_NAME);
            int alarmId = intent.getExtras().getInt(EXTRA_ALARM_ID);

            // Tapping the notification will open the QuestionActivity.
            questionActivityIntent = new Intent(this, QuestionActivity.class);
            questionActivityIntent.putExtra(EXTRA_ALARM_NAME, alarmName);
            questionActivityIntent.putExtra(EXTRA_ALARM_ID, alarmId);
            pendingNotificationIntent = PendingIntent.getActivity(
                    this, 0, questionActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT
            );

            //Notification channel is required in versions after Android Oreo
            createNotificationChannel();

            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Alarm! - " + alarmName + " click to answer the question")
                    // Set the intent that will fire when the user taps the notification
                    .setContentInfo("Doing stuff in the background...")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingNotificationIntent);

            Notification not = notificationBuilder.build();
            startForeground(1, not);

            switch (alarmType) {
                case ALARM_ALIEN:
                    mediaPlayer = MediaPlayer.create(this, R.raw.alien_siren);
                    mediaPlayer.setLooping(true);
                    isPlaying = true;
                    break;
                case ALARM_BOMB_SIREN:
                    mediaPlayer = MediaPlayer.create(this, R.raw.bomb_siren);
                    mediaPlayer.setLooping(true);
                    isPlaying = true;
                    break;
                case ALARM_SCHOOL_BELL:
                    mediaPlayer = MediaPlayer.create(this, R.raw.old_fashioned_school_bell);
                    mediaPlayer.setLooping(true);
                    isPlaying = true;
                    break;
                case ALARM_WATCH:
                    mediaPlayer = MediaPlayer.create(this, R.raw.analog_watch_alarm);
                    mediaPlayer.setLooping(true);
                    isPlaying = true;
                    break;
            }
            mediaPlayer.start();
            Toast.makeText(this, "Check notification", Toast.LENGTH_SHORT).show();
            Log.i("alarmApp", "Received intent - Alarm On : Starting media playback");

        } else {
            isPlaying = false;
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            Log.i("alarmApp", "Received intent - Alarm Off : Stopping media playback");
            stopForeground(true);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * A Notification channel is required in versions after Android Oreo
     * See <a href=
     * "https://developer.android.com/training/notify-user/navigation"
     * >Start an Activity from a Notification</a> for more information.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Handler of incoming messages from clients.
     */
    static class IncomingHandler extends Handler {
        private Context applicationContext; // unused.. consider removing

        IncomingHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Log.i("alarmApp", "hello! from Service");
                    break;
                case MSG_REPLY:
                    sendResponseToClient(msg);
                default:
                    super.handleMessage(msg);
            }
        }

        private void sendResponseToClient(Message msg){

            Message msgResponse = Message.obtain(null, MSG_REPLY);
            Bundle bundle = new Bundle();
            bundle.putBoolean("isPlaying", isPlaying);
            msgResponse.setData(bundle);

            try {
                msg.replyTo.send(msgResponse);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

}
