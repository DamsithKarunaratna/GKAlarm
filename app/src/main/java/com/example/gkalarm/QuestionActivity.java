package com.example.gkalarm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class QuestionActivity extends AppCompatActivity {

    Intent alarmIntent;
    Button alarmOffButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        alarmIntent = new Intent(this, AlarmBroadcastReceiver.class);

        alarmOffButton = findViewById(R.id.question_off_button);
        alarmOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmIntent.putExtra(MainActivity.EXTRA_ALARM_ON, false);
                sendBroadcast(alarmIntent);
            }
        });
    }
}
