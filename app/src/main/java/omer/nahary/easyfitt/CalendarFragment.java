package omer.nahary.easyfitt;

import static omer.nahary.easyfitt.CalendarUtils.daysInMonthArray;
import static omer.nahary.easyfitt.CalendarUtils.monthYearFromDate;
import static omer.nahary.easyfitt.CalendarUtils.selectedDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        monthYearText = view.findViewById(R.id.monthYearTV);
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);

        // חיבור כפתורי החיצים (לוודא שיש להם ID ב-XML)
        // אם ב-XML הוספת ID לכפתורים, תחליף פה את השמות ב-R.id
        View btnPrev = view.findViewById(R.id.btnPrevMonth);
        View btnNext = view.findViewById(R.id.btnNextMonth);
        Button btnWeekly = view.findViewById(R.id.btnWeekly);
        Button btnGoals = view.findViewById(R.id.btnGoals);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        if(btnPrev != null) btnPrev.setOnClickListener(v -> previousMonthAction());
        if(btnNext != null) btnNext.setOnClickListener(v -> nextMonthAction());
        if(btnWeekly != null) btnWeekly.setOnClickListener(v -> startActivity(new Intent(getContext(), WeekViewActivity.class)));
        if(btnGoals != null) btnGoals.setOnClickListener(v -> startActivity(new Intent(getContext(), WeeklyGoalsActivity.class)));
        if(btnLogout != null) btnLogout.setOnClickListener(v -> ((MainActivity)getActivity()).logoutAction());

        selectedDate = LocalDate.now();
        setMonthView();

        return view;
    }

    public void refreshCalendar() {
        setMonthView();
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> days = daysInMonthArray(selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    public void previousMonthAction() {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        // לוגיקה בלחיצה על יום
    }
}