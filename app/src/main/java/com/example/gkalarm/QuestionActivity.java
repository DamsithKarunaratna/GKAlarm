package com.example.gkalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gkalarm.data.AlarmData;
import com.example.gkalarm.data.Persistence;
import com.example.gkalarm.data.QuestionData;

import java.util.Iterator;

/**
 * @author Damsith Karunaratna
 *
 * Activity for answering general knowledge question and stopping the alarm
 *
 *
 */
public class QuestionActivity extends AppCompatActivity {

    Intent alarmIntent;
    PendingIntent pendingAlarmIntent;
    Button alarmOffButton;
    RadioGroup rbGroup;
    RadioButton rb1;
    RadioButton rb2;
    RadioButton rb3;
    RadioButton rb4;
    TextView tvQuestion;
    AlarmManager alarmMgr;
    QuestionData.Question questionModel;
    String alarmName;
    int alarmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        questionModel = QuestionData.getRandomQuestion();

        alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmName = getIntent().getExtras().getString(MainActivity.EXTRA_ALARM_NAME);
        alarmId = getIntent().getExtras().getInt(MainActivity.EXTRA_ALARM_ID);

        tvQuestion = findViewById(R.id.tv_question);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button_1);
        rb2 = findViewById(R.id.radio_button_2);
        rb3 = findViewById(R.id.radio_button_3);
        rb4 = findViewById(R.id.radio_button_4);

        tvQuestion.setText(questionModel.getQuestion());

        rb1.setText(questionModel.getOption1());
        rb2.setText(questionModel.getOption2());
        rb3.setText(questionModel.getOption3());
        rb4.setText(questionModel.getOption4());

        alarmIntent = new Intent(this, AlarmBroadcastReceiver.class);
        pendingAlarmIntent = PendingIntent.getBroadcast(this,
                alarmId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmOffButton = findViewById(R.id.question_off_button);
        alarmOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                    checkAnswer();
                } else {
                    Toast.makeText(QuestionActivity.this, "Please select an answer!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void checkAnswer() {
        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int selectedAnswer = rbGroup.indexOfChild(rbSelected) + 1;

        if (selectedAnswer == questionModel.getAnswer()) {
            Toast.makeText(this, "Correct answer", Toast.LENGTH_SHORT).show();
            stopAlarm();
        } else {
            Toast.makeText(this, "Wrong answer! try again", Toast.LENGTH_SHORT).show();
        }
    }

    void stopAlarm() {
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
}
