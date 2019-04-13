package com.example.gkalarm;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;


/**
 * @author Damsith Karunaratna
 * This is a custom DialogFragment which is shown when the user clicks on the "+" button in the
 * toolbar. It contains a TimePicker, a selector for the Alarm tone and an EditText for the alarm
 * name.
 *
 * See <a href=
 * "https://medium.com/@xabaras/creating-a-custom-dialog-with-dialogfragment-f0198dab656d">
 *          Creating A Custom Dialog With DialogFragment
 *  </a> for more information.
 *
 * Passing data to MainActivity.class is done using the Observer Pattern where MainActivity is
 * registered as a listener for the TimeSelectFragment
 *
 * See <a href="https://developer.android.com/training/basics/fragments/communicating">
 *          Communicating with fragments
 *      </a> for more information.
 */
public class TimeSelectFragment extends DialogFragment {

    Button doneButton;
    TimePicker timePicker;
    OnTimeSelectedListener listener;

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

        doneButton = view.findViewById(R.id.set_alarm_button);
        timePicker = view.findViewById(R.id.time_picker);

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
                listener.onTimePicked(hour  , minute);
                dismiss();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity
     *
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTimeSelectedListener {
        void onTimePicked(int hour, int minute);
    }
}
