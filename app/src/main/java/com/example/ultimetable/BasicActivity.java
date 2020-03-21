package com.example.ultimetable;

import android.database.Cursor;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class BasicActivity extends BaseActivity  {

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // List holding events to be added when month changes
        List<WeekViewEvent> eventList = new ArrayList<WeekViewEvent>();

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = (Calendar) startTime.clone();

        // get cursor of Event table query
        Cursor eventCursor = Database.getAllEventsCursor(newYear, newMonth);
        boolean result = eventCursor.moveToFirst();
        while (result) {
            // extract all row data from the cursor
            Long eventID = eventCursor.getLong(eventCursor.getColumnIndex(Database.KEY_ID));
            String title = eventCursor.getString(eventCursor.getColumnIndex(Database.KEY_TITLE_COLUMN));
            Long startTimeStamp = eventCursor.getLong(eventCursor.getColumnIndex(Database.KEY_START_TIME_COLUMN));
            Long endTimeStamp = eventCursor.getLong(eventCursor.getColumnIndex(Database.KEY_END_TIME_COLUMN));
            String location = eventCursor.getString(eventCursor.getColumnIndex(Database.KEY_LOCATION_COLUMN));
            String description = eventCursor.getString(eventCursor.getColumnIndex(Database.KEY_DESCRIPTION_COLUMN));

            // set startTime and endTime calenders to timestamps retrieved from db
            startTime.setTimeInMillis(startTimeStamp);
            endTime.setTimeInMillis(endTimeStamp);
            
            // get events object and add it to 
            WeekViewEvent event = new WeekViewEvent(eventID, title, location, startTime, endTime);
            event.setColor(getResources().getColor(R.color.event_color_04));
            eventList.add(event);

            // try to move cursor to next row
            result = eventCursor.moveToNext();
        }

        return eventList;
    }

}
