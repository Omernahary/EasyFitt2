package omer.nahary.easyfitt;

import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class Event {

    protected LocalDateTime dateTime;

    public static ArrayList<Event> allEvents = new ArrayList<>();

    public Event(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        allEvents.add(this);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public abstract String getEventType();
}
