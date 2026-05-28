package omer.nahary.easyfitt;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

import static omer.nahary.easyfitt.CalendarUtils.selectedDate;

public class EventEditActivity extends AppCompatActivity {

    private TextView selectedDateText;
    private RadioGroup eventTypeGroup;
    private RadioButton runRadioButton, workoutRadioButton;
    private TimePicker timePicker;
    private Button saveEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        selectedDateText = findViewById(R.id.selectedDateText);
        eventTypeGroup = findViewById(R.id.eventTypeGroup);
        runRadioButton = findViewById(R.id.runRadioButton);
        workoutRadioButton = findViewById(R.id.workoutRadioButton);
        timePicker = findViewById(R.id.timePicker);
        saveEventButton = findViewById(R.id.saveEventButton);

        // הכרחת ה-TimePicker להראות את זמן ישראל (Asia/Jerusalem)
        // זה יפתור את הפער של השעתיים אם המכשיר מוגדר על UTC
        TimeZone israelTime = TimeZone.getTimeZone("Asia/Jerusalem");
        Calendar now = Calendar.getInstance(israelTime);

        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        timePicker.setIs24HourView(true);
        timePicker.setHour(currentHour);
        timePicker.setMinute(currentMinute);

        selectedDateText.setText("Date: " + selectedDate.toString());
        saveEventButton.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        int selectedId = eventTypeGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please choose activity", Toast.LENGTH_SHORT).show();
            return;
        }

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // יצירת אובייקט זמן לפי מה שהמשתמש בחר ב-UI
        LocalDateTime dateTime = selectedDate.atTime(hour, minute);

        Event newEvent;
        if (selectedId == runRadioButton.getId()) {
            newEvent = new RunEvent(dateTime);
        } else {
            newEvent = new WorkoutEvent(dateTime);
        }

        Event.allEvents.add(newEvent);
        Event.saveEvents(this);

        // שליחה להלפר
        EventAlarmHelper.setEventAlarm(this, newEvent);

        Toast.makeText(this, "Event Saved!", Toast.LENGTH_SHORT).show();
        finish();
    }
}