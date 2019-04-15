package com.example.gkalarm;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;


/**
 * @author Damsith Karunaratna
 * This is a custom DialogFragment which is shown when the user clicks on the "+" button in the
 * toolbar. It contains a TimePicker, a selector for the Alarm tone and an EditText for the alarm
 * name.
 * <p>
 * See <a href=
 * "https://medium.com/@xabaras/creating-a-custom-dialog-with-dialogfragment-f0198dab656d">
 * Creating A Custom Dialog With DialogFragment
 * </a> for more information.
 * <p>
 * Passing data to MainActivity.class is done using the Observer Pattern where MainActivity is
 * registered as a listener for the TimeSelectFragment
 * <p>
 * See <a href="https://developer.android.com/training/basics/fragments/communicating">
 * Communicating with fragments
 * </a> for more information.
 */
public class TimeSelectFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    public static final int ALARM_ALIEN = 0;
    public static final int ALARM_WATCH = 1;
    public static final int ALARM_BOMB_SIREN = 2;
    public static final int ALARM_SCHOOL_BELL = 3;

    EditText etAlarmName;
    Button doneButton, cancelButton;
    TimePicker timePicker;
    OnTimeSelectedListener listener;
    Spinner spinner;
    int alarmType;

    public TimeSelectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_select, container, false);

        etAlarmName = view.findViewById(R.id.et_alarm_name);
        doneButton = view.findViewById(R.id.set_alarm_button);
        cancelButton = view.findViewById(R.id.cancel_set_alarm_button);
        timePicker = view.findViewById(R.id.time_picker);
        spinner = view.findViewById(R.id.tone_spinner);
        alarmType = ALARM_ALIEN;

        // Create an ArrayAdapter using the tones array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.tones_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour, minute;

                // Check for  build version before running deprecated methods
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }

                // Notify the listener
                listener.onTimePicked(hour, minute, alarmType, etAlarmName.getText().toString());
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        timePicker.setIs24HourView(true);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Check if attaching Activity implements OnTimeSelectedListener and set it as the listener
        if (context instanceof OnTimeSelectedListener) {
            listener = (OnTimeSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimeSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case ALARM_ALIEN:
                alarmType = ALARM_ALIEN;
                break;
            case ALARM_BOMB_SIREN:
                alarmType = ALARM_BOMB_SIREN;
                break;
            case ALARM_SCHOOL_BELL:
                alarmType = ALARM_SCHOOL_BELL;
                break;
            case ALARM_WATCH:
                alarmType = ALARM_WATCH;
                break;
        }
        Log.i("alarmApp", "Tone selected - position : " + alarmType);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTimeSelectedListener {
        void onTimePicked(int hour, int minute, int alarmType, String alarmName);
    }
}
