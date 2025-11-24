package omer.nahary.easyfitt;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static omer.nahary.easyfitt.CalendarUtils.selectedDate;

public class EventEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        TextView selectedDateText = findViewById(R.id.selectedDateText);
        EditText eventNameInput = findViewById(R.id.eventNameInput);
        Button saveEventButton = findViewById(R.id.saveEventButton);

        // הצגת התאריך הנבחר
        selectedDateText.setText("Selected date: " + selectedDate.toString());

        saveEventButton.setOnClickListener(v -> {
            String eventName = eventNameInput.getText().toString().trim();
            if (!eventName.isEmpty()) {
                // יצירת Event חדש – נוסף אוטומטית ל-ArrayList סטטי
                new Event(eventName, selectedDate);

                Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show();
                finish(); // סוגר את ה-Activity וחוזר ל-WeekViewActivity
            } else {
                Toast.makeText(this, "Please enter an event name", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
