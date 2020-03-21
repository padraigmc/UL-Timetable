package com.example.ultimetable;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public TimePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //if (MainActivity.DEBUG) Log.i(MainActivity.TAG, "onCreateDialog()");

        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //if (MainActivity.DEBUG) Log.i(MainActivity.TAG, "onTimeSet(): " + hourOfDay + ":" + minute);

        String setTime = hourOfDay + ":" + minute;

        // get ID of button used to show picker
        String buttonID = getArguments().getString("start_time");
        int resID = getResources().getIdentifier(buttonID, "id", "com.example.ultimetable");

        //if (MainActivity.DEBUG) Log.i(MainActivity.TAG, "onTimeSet(): " + buttonID + ":" + resID);


        // get button view and set text
        Button pickerButton = getActivity().findViewById(resID);
        pickerButton.setText(setTime);
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        if (MainActivity.DEBUG) Log.i(MainActivity.TAG, "This is from onCreateView()");
        return inflater.inflate(R.layout.fragment_time_picker, container, false);
    }
     */

}
