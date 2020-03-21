package com.example.ultimetable;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import com.example.ultimetable.R;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import java.util.Calendar;

public class AddEvent extends AppCompatActivity {

    Calendar addEvent = Calendar.getInstance();
    int hour = addEvent.get(Calendar.HOUR_OF_DAY);
    int minute = addEvent.get(Calendar.MINUTE);
    int day = addEvent.get(Calendar.DAY_OF_MONTH);
    int month = addEvent.get(Calendar.MONTH);
    int year = addEvent.get(Calendar.YEAR);

    // ints used to store user inputted calender attributes
    private int mYear, mMonth, mDay;
    private int mHourStart, mMinuteStart;
    private int mHourEnd, mMinuteEnd;

    EditText title;
    Switch allDay;
    Button startDate;
    Button startTime;
    Button endTime;
    EditText location;
    EditText description;
    Button submit;

    Database eventDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_add);

        title = (EditText) findViewById(R.id.title);
        allDay = (Switch) findViewById(R.id.all_day_switch);
        startDate = (Button) findViewById(R.id.start_date);
        startTime = (Button) findViewById(R.id.start_time);
        endTime = (Button) findViewById(R.id.end_time);
        location = (EditText) findViewById(R.id.location);
        description = (EditText) findViewById(R.id.description);
        submit = (Button) findViewById(R.id.submit_new_event);



        startDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEvent.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int yearPicker, int monthPicker, int dayOfMonthPicker) {
                                mYear = yearPicker;
                                mMonth = monthPicker;
                                mDay = dayOfMonthPicker;

                                startDate.setText(getDateReadable(yearPicker, monthPicker, dayOfMonthPicker));
                            }

                        }, year, month, day);
                datePickerDialog.show();
            }
        });


        startTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEvent.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDayPicker, int minutePicker) {
                                mHourStart = hourOfDayPicker;
                                mMinuteStart = minutePicker;

                                startTime.setText(getTimeReadable(hourOfDayPicker, minutePicker));
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });


        endTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEvent.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDayPicker, int minutePicker) {
                                mHourEnd = hourOfDayPicker;
                                mMinuteEnd = minutePicker;

                                endTime.setText(getTimeReadable(hourOfDayPicker, minutePicker));
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                eventDB = new Database(getApplicationContext());

                Calendar userSpec = Calendar.getInstance();
                userSpec.set(mYear, mMonth, mDay, mHourStart, mMinuteStart);
                long startTimeStamp = userSpec.getTimeInMillis();

                userSpec.set(mYear, mMonth, mDay, mHourEnd, mMinuteEnd);
                long endTimeStamp = userSpec.getTimeInMillis();

                long rowID = eventDB.addNewEvent(
                        title.getText().toString(),
                        startTimeStamp,
                        endTimeStamp,
                        location.getText().toString(),
                        description.getText().toString()
                );

                if (rowID == -1) {
                    Log.w(MainActivity.TAG,"Database.addNewEvent() failed for event with title " + title.getText().toString());
                } else {
                    Log.i(MainActivity.TAG,"Database.addNewEvent() successful for event with title " + title.getText().toString() + ". Event ID: " + rowID);
                }
            }
        });
    }

    private String getDateReadable(int yearReadable, int monthReadable, int dayReadable) {
        String sYear = Integer.toString(yearReadable);
        String sMonth, sDay;

        if (monthReadable < 10) { sMonth = "0" + monthReadable; } else { sMonth = Integer.toString(monthReadable); }
        if (dayReadable < 10) { sDay = "0" + dayReadable; } else { sDay = Integer.toString(dayReadable); }

        return sDay + "/" + sMonth + "/" + sYear;
    }

    private String getTimeReadable(int hourReadable, int minuteReadable) {
        String sHour, sMinute;

        if (hourReadable < 10) { sHour = "0" + hourReadable; } else { sHour = Integer.toString(hourReadable); }
        if (minuteReadable < 10) { sMinute = "0" + minuteReadable; } else { sMinute = Integer.toString(minuteReadable); }

        return sHour + ":" + sMinute;
    }
}
