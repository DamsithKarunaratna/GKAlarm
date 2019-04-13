package com.example.gkalarm;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.gkalarm.data.AlarmData;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements TimeSelectFragment.OnTimeSelectedListener,
        AlarmListFragment.OnListFragmentInteractionListener {

    public static final String EXTRA_ALARM_ON = "EXTRA_ALARM_ON";
    public static final String EXTRA_ALARM_TYPE = "EXTRA_ALARM_TYPE";

    AlarmManager alarmMgr;
    Intent alarmIntent;
    PendingIntent pendingAlarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Create a pending intent for the BroadcastReceiver
        // TODO: replace request code with value from db (add db)
        alarmIntent = new Intent(MainActivity.this, AlarmBroadcastReceiver.class);
        pendingAlarmIntent = PendingIntent.getBroadcast(MainActivity.this,
                0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
    public void onTimePicked(int hour, int minute, int alarmType) {

        Log.i("alarmApp", "ONTIMEPICKED() called");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        // Intent Extra which tells the Service which operation to carry out
        alarmIntent.putExtra(EXTRA_ALARM_ON, true);
        alarmIntent.putExtra(EXTRA_ALARM_TYPE, alarmType);

        pendingAlarmIntent = PendingIntent.getBroadcast(MainActivity.this,
                0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() - 30000, pendingAlarmIntent);
        } else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingAlarmIntent);
        }
    }

    /**
     * Show a custom DialogFragment with a Timepicker, EditText for alarm name and Tone selector
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
    public void onListFragmentInteraction(AlarmData.AlarmItem item) {
        Log.i("alarmApp", "List item interaction");
        Log.i("alarmApp", item.toString());
    }
}
