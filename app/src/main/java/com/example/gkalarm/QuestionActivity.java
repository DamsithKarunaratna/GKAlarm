package com.example.gkalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gkalarm.data.AlarmData;

public class QuestionActivity extends AppCompatActivity {

    Intent alarmIntent;
    PendingIntent pendingAlarmIntent;
    Button alarmOffButton;
    TextView tvQuestion;
    AlarmManager alarmMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        String alarmName = getIntent().getExtras().getString(MainActivity.EXTRA_ALARM_NAME);
        final int alarmId = getIntent().getExtras().getInt(MainActivity.EXTRA_ALARM_ID);

        tvQuestion = findViewById(R.id.tv_question);
        tvQuestion.setText(alarmName + " : " + alarmId);

        alarmIntent = new Intent(this, AlarmBroadcastReceiver.class);
        pendingAlarmIntent = PendingIntent.getBroadcast(this,
                alarmId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmOffButton = findViewById(R.id.question_off_button);

        alarmOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pendingAlarmIntent.cancel();
                alarmMgr.cancel(pendingAlarmIntent);
                alarmIntent.putExtra(MainActivity.EXTRA_ALARM_ON, false);
                sendBroadcast(alarmIntent);
                AlarmData.ITEMS.remove(alarmId - 1);
                AlarmListFragment.alarmRecyclerViewAdapter.notifyItemRemoved(alarmId - 1);
                finish();
            }
        });
    }
}
