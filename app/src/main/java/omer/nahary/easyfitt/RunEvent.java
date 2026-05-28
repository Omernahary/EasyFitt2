package omer.nahary.easyfitt;

import java.time.LocalDateTime;

public class RunEvent extends Event {

    // פרטים מתקדמים שלא נבחרים כרגע
    private double distance; // בק"מ
    private int durationMinutes; // משך זמן בריצה

    public RunEvent(LocalDateTime dateTime) {
        super(dateTime);
    }

    @Override
    public String getEventType() {
        return "Run";
    }

    // Getters ו-Setters לשימוש בסקשן אחר
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}