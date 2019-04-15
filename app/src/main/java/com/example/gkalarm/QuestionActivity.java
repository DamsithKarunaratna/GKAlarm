package com.example.gkalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gkalarm.data.AlarmData;
import com.example.gkalarm.data.Persistence;
import com.example.gkalarm.data.QuestionData;

import java.util.Iterator;

public class QuestionActivity extends AppCompatActivity {

    Intent alarmIntent;
    PendingIntent pendingAlarmIntent;
    Button alarmOffButton;
    TextView tvQuestion;
    AlarmManager alarmMgr;
    QuestionData.Question questionModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        questionModel = QuestionData.getRandomQuestion();

        alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        String alarmName = getIntent().getExtras().getString(MainActivity.EXTRA_ALARM_NAME);
        final int alarmId = getIntent().getExtras().getInt(MainActivity.EXTRA_ALARM_ID);

        tvQuestion = findViewById(R.id.tv_question);
        tvQuestion.setText(questionModel.getQuestion());

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

                for (Iterator<AlarmData.AlarmItem> iterator =
                     AlarmData.ITEMS.iterator(); iterator.hasNext(); ) {
                    AlarmData.AlarmItem i = iterator.next();
                    if (alarmId == i.id) {
                        AlarmData.ITEMS.remove(i);
                        Log.i("alarmApp", "Removed : " + alarmId);
                        AlarmListFragment.alarmRecyclerViewAdapter.notifyDataSetChanged();
                        String result = Persistence.storeListInSharedPreferences(
                                getApplicationContext(), AlarmData.ITEMS);
                        Log.i("alarmApp", "Stored  : " + result);
                    }
                }

                finish();
            }
        });
    }
}
