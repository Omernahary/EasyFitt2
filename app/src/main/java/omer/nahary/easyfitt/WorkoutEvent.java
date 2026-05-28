package omer.nahary.easyfitt;

import java.time.LocalDateTime;

public class WorkoutEvent extends Event {

    // פרטים מתקדמים שלא נבחרים כרגע
    private int sets;
    private int reps;
    private double weight; // במשקל ק"ג

    public WorkoutEvent(LocalDateTime dateTime) {
        super(dateTime);
    }

    @Override
    public String getEventType() {
        return "Workout";
    }

    // Getters ו-Setters לשימוש בסקשן אחר
    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}