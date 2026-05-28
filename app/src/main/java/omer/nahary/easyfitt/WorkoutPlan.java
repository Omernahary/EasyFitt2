package omer.nahary.easyfitt;

import java.util.ArrayList;

public class WorkoutPlan {
    private String workoutTitle; // כותרת האימון
    private ArrayList<Exercise> exercises; // רשימת התרגילים בתוך האימון

    public WorkoutPlan(String workoutTitle) {
        this.workoutTitle = workoutTitle;
        this.exercises = new ArrayList<>();
    }

    public String getWorkoutTitle() { return workoutTitle; }
    public void setWorkoutTitle(String workoutTitle) { this.workoutTitle = workoutTitle; }
    public ArrayList<Exercise> getExercises() { return exercises; }
    public void setExercises(ArrayList<Exercise> exercises) { this.exercises = exercises; }

    public void addExercise(Exercise exercise) {
        this.exercises.add(exercise);
    }
}