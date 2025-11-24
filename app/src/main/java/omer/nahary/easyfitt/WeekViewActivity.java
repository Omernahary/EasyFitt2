package omer.nahary.easyfitt;

import android.content.Intent;
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

import java.time.LocalDate;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_week_view);

        monthYearText = findViewById(R.id.monthYearTV);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        eventListView = findViewById(R.id.listView);

        eventsForSelectedDate = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventsForSelectedDate);
        eventListView.setAdapter(eventAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
            int day = Integer.parseInt(dayText);
            selectedDate = selectedDate.withDayOfMonth(day);

            String message = "Selected date: " + selectedDate.toString();
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            updateEventsForSelectedDate();
        }
    }

    public void nextEventAction(View view) {
        startActivity(new Intent(this, EventEditActivity.class));
    }

    private void updateEventsForSelectedDate() {
        eventsForSelectedDate.clear();
        for (Event event : Event.allEvents) {
            if (event.getDate().isEqual(selectedDate)) {
                eventsForSelectedDate.add(event);
            }
        }
        eventAdapter.notifyDataSetChanged();
    }
}
