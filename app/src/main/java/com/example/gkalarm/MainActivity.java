package com.example.gkalarm;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.gkalarm.data.AlarmData;
import com.example.gkalarm.data.Persistence;
import static com.example.gkalarm.AlarmToneService.MSG_REPLY;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
        implements TimeSelectFragment.OnTimeSelectedListener,
        AlarmListFragment.OnListFragmentInteractionListener {

    public static final String EXTRA_ALARM_ON = "EXTRA_ALARM_ON";
    public static final String EXTRA_ALARM_TYPE = "EXTRA_ALARM_TYPE";
    public static final String EXTRA_ALARM_NAME = "EXTRA_ALARM_NAME";
    public static final String EXTRA_ALARM_ID = "EXTRA_ALARM_ID";

    AlarmManager alarmMgr;
    Intent alarmIntent;
    PendingIntent pendingAlarmIntent;
    static int listPosition;

    /** Messenger for communicating with the service. */
    Messenger mService = null;
    /** Target we publish for Service to send messages to MainActivity.*/
    Messenger mainActivityMessenger = null;
    /** Flag indicating whether we have called bind on the service. */
    boolean bound;

    /**
     * Handler of incoming messages from clients.
     */
    class ServiceMessageHandler extends Handler {
        Context context;

        ServiceMessageHandler (Context context) {
            context = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REPLY:
                    handleServiceReply(msg);

                default:
                    super.handleMessage(msg);
            }
        }

        private void handleServiceReply(Message msg) {
            Log.i("alarmApp", "Handling reply from service");

            Bundle msgData = msg.getData();
            if(!msgData.getBoolean("isPlaying")) {
                pendingAlarmIntent.cancel();
                alarmMgr.cancel(pendingAlarmIntent);

                for (Iterator<AlarmData.AlarmItem> iterator = AlarmData.ITEMS.iterator();
                     iterator.hasNext(); ) {
                    AlarmData.AlarmItem i = iterator.next();
                    if (listPosition == i.id) {
                        AlarmData.ITEMS.remove(i);
                        Log.i("alarmApp", "Removed : " + listPosition);
                        AlarmListFragment.alarmRecyclerViewAdapter.notifyDataSetChanged();
                        String result = Persistence.storeListInSharedPreferences(
                                getApplicationContext(), AlarmData.ITEMS);
                        Log.i("alarmApp", "Stored  : " + result);
                    }
                }

            } else {
                Toast.makeText(MainActivity.this, "Cannot delete, while alarm is playing, Please answer the question", Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            bound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainActivityMessenger = new Messenger(new ServiceMessageHandler (this));

        alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Create a pending intent for the BroadcastReceiver
        // TODO: replace request code with value from db (add db)
        alarmIntent = new Intent(MainActivity.this, AlarmBroadcastReceiver.class);
        pendingAlarmIntent = PendingIntent.getBroadcast(MainActivity.this,
                0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmData.addAll(Persistence.getListFromSharedPreferences(getApplicationContext()));
        AlarmListFragment.alarmRecyclerViewAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service
        bindService(new Intent(this, AlarmToneService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        if (item.getItemId() == R.id.add) {
            Log.i("alarmApp", "ADD CLICKED");
            showTimePickerDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback to run once user is done setting up the alarm
     *
     * @param hour   hour passed from TimeSelectFragment
     * @param minute minute passed from TimeSelectFragment
     */
    @Override
    public void onTimePicked(int hour, int minute, int alarmType, String alarmName) {

        Log.i("alarmApp", "ONTIMEPICKED() called");
        int alarmId = 0;
        if(!AlarmData.ITEMS.isEmpty()) {
            alarmId = AlarmData.ITEMS.get(AlarmData.ITEMS.size() -1).id + 1;
        }
        String timeString = hour + ":" + minute;


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        AlarmData.AlarmItem alarmItem = new AlarmData.AlarmItem(
                alarmId,
                timeString,
                alarmName,
                calendar.getTimeInMillis());

        AlarmData.addItem(alarmItem);
        AlarmListFragment.alarmRecyclerViewAdapter.notifyItemInserted(
                AlarmListFragment.alarmRecyclerViewAdapter.getItemCount() - 1);
        String result = Persistence.storeListInSharedPreferences(
                getApplicationContext(), AlarmData.ITEMS);
        Log.i("alarmApp", "Stored  : " + result);

        // Intent Extra which tells the Service which operation to carry out
        alarmIntent.putExtra(EXTRA_ALARM_ON, true);
        alarmIntent.putExtra(EXTRA_ALARM_TYPE, alarmType);
        alarmIntent.putExtra(EXTRA_ALARM_NAME, alarmName);
        alarmIntent.putExtra(EXTRA_ALARM_ID, alarmId);

        pendingAlarmIntent = PendingIntent.getBroadcast(MainActivity.this,
                    alarmItem.id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 30000, pendingAlarmIntent);
        } else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingAlarmIntent);
        }

    }

    /**
     * Show a custom DialogFragment with a TimePicker, EditText for alarm name and Tone selector
     *
     * See <a href=
     * "https://medium.com/@xabaras/creating-a-custom-dialog-with-dialogfragment-f0198dab656d"
     * >Creating A Custom Dialog With DialogFragment</a> for more information.
     */
    private void showTimePickerDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("timePickerDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new TimeSelectFragment();
        dialogFragment.show(ft, "timePickerDialog");
    }

    @Override
    public void onListFragmentInteraction(int position) {
        Log.i("alarmApp", "List item interaction");
        Log.i("alarmApp", String.valueOf(position));
    }

    public void serviceSayHello() {

        if (!bound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, AlarmToneService.MSG_SAY_HELLO, 0, 0);
        msg.replyTo = mainActivityMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void safeDeleteAlarm() {

        if (!bound) {
            return;
        }
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, AlarmToneService.MSG_REPLY, 0, 0);
        msg.replyTo = mainActivityMessenger;
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDeleteClicked(AlarmData.AlarmItem item, int position) {

        listPosition = position;
        alarmIntent = new Intent(this, AlarmBroadcastReceiver.class);
        pendingAlarmIntent = PendingIntent.getBroadcast(this,
                item.id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        safeDeleteAlarm();

    }

}
