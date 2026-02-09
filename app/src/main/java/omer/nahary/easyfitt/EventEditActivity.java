package omer.nahary.easyfitt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;

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

        // הצגת התאריך הנבחר
        selectedDateText.setText("Selected date: " + selectedDate.toString());

        saveEventButton.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        int selectedId = eventTypeGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "בחר סוג פעילות", Toast.LENGTH_SHORT).show();
            return;
        }

        // קבלת שעה ודקה מה-TimePicker
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        LocalDateTime dateTime = selectedDate.atTime(hour, minute);

        Event event;
        if (selectedId == runRadioButton.getId()) {
            event = new RunEvent(dateTime);
        } else {
            event = new WorkoutEvent(dateTime);
        }

        Toast.makeText(this, event.getEventType() + " saved!", Toast.LENGTH_SHORT).show();
        finish(); // סוגר וחוזר ללוח שנה
    }
}
