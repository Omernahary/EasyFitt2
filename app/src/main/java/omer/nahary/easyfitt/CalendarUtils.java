package omer.nahary.easyfitt;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarUtils {

    public static LocalDate selectedDate;

    public static String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    // ימים בחודש (ללוח חודשי)
    public static ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = date.withDayOfMonth(1);

        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // מתחיל ב-Sunday=0

        for (int i = 0; i < 42; i++) {
            if (i < dayOfWeek || i >= daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek + 1));
            }
        }
        return daysInMonthArray;
    }

    // ימים בשבוע (ללוח שבועי)
    public static ArrayList<String> daysInWeekArray(LocalDate date) {
        ArrayList<String> daysInWeek = new ArrayList<>();
        LocalDate startOfWeek = date;

        // תחילת השבוע (ראשון)
        while (startOfWeek.getDayOfWeek() != DayOfWeek.SUNDAY) {
            startOfWeek = startOfWeek.minusDays(1);
        }

        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            daysInWeek.add(String.valueOf(day.getDayOfMonth()));
        }

        return daysInWeek;
    }
}
