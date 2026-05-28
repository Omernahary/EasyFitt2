package omer.nahary.easyfitt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static omer.nahary.easyfitt.CalendarUtils.daysInWeekArray;
import static omer.nahary.easyfitt.CalendarUtils.monthYearFromDate;
import static omer.nahary.easyfitt.CalendarUtils.selectedDate;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventsForSelectedDate;

    // רסיבר להאזנה לעדכונים בזמן אמת מה-MainActivity
    private BroadcastReceiver refreshReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_week_view);

        monthYearText = findViewById(R.id.monthYearTV);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        eventListView = findViewById(R.id.listView);

        // טעינה ראשונית של האירועים מהזיכרון
        Event.loadEvents(this);

        eventsForSelectedDate = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventsForSelectedDate);
        eventListView.setAdapter(eventAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // הגדרת הרסיבר לעדכון מיידי של התצוגה
        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Event.loadEvents(WeekViewActivity.this);
                updateEventsForSelectedDate();
                setWeekView(); // מרענן גם את ה-V/X בלוח השנה למעלה
            }
        };
        // רישום הרסיבר למערכת
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(refreshReceiver, new IntentFilter("com.omer.easyfitt.REFRESH_EVENTS"), Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(refreshReceiver, new IntentFilter("com.omer.easyfitt.REFRESH_EVENTS"));
        }

        setWeekView();
        updateEventsForSelectedDate();
    }

    private void setWeekView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> days = daysInWeekArray(selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    public void previousweekAction(View view) {
        selectedDate = selectedDate.minusWeeks(1);
        setWeekView();
        updateEventsForSelectedDate();
    }

    public void nextweekAction(View view) {
        selectedDate = selectedDate.plusWeeks(1);
        setWeekView();
        updateEventsForSelectedDate();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
            try {
                int day = Integer.parseInt(dayText);
                selectedDate = selectedDate.withDayOfMonth(day);

                String message = "Selected date: " + selectedDate.toString();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                updateEventsForSelectedDate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void nextEventAction(View view) {
        startActivity(new Intent(this, EventEditActivity.class));
    }

    public void backToMainAction(View view) {
        finish();
    }

    private void updateEventsForSelectedDate() {
        if (eventsForSelectedDate == null) eventsForSelectedDate = new ArrayList<>();
        eventsForSelectedDate.clear();

        if (Event.allEvents != null) {
            for (Event event : Event.allEvents) {
                if (event == null || event.getDateTime() == null) continue;

                if (event.getDateTime().toLocalDate().isEqual(selectedDate)) {
                    eventsForSelectedDate.add(event);
                }
            }
        }

        if (eventAdapter != null) {
            eventAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // קריטי: טוען מחדש את האירועים מהדיסק למקרה שסומן "ביצעתי" בהתראה
        Event.loadEvents(this);
        // מעדכן את הרשימה שמוצגת על המסך
        updateEventsForSelectedDate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // הסרת רישום הרסיבר כדי למנוע דליפת זיכרון
        if (refreshReceiver != null) {
            unregisterReceiver(refreshReceiver);
        }
    }
}