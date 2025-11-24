package omer.nahary.easyfitt;

import java.time.LocalDate;
import java.util.ArrayList;

public class Event {
    private String name;
    private LocalDate date;

    // רשימה סטטית לשמירת כל האירועים
    public static ArrayList<Event> allEvents = new ArrayList<>();

    public Event(String name, LocalDate date) {
        this.name = name;
        this.date = date;
        allEvents.add(this); // מוסיף את האירוע לרשימה סטטית
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
