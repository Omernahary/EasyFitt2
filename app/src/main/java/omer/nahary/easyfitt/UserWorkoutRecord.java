package omer.nahary.easyfitt;

import java.time.LocalDate;
import java.util.HashMap;

public class UserWorkoutRecord {

    private static HashMap<LocalDate, Boolean> workoutMap = new HashMap<>();

    public static void markWorkoutDone(LocalDate date) {
        workoutMap.put(date, true);
    }

    public static void markWorkoutMissed(LocalDate date) {
        workoutMap.put(date, false);
    }

    public static boolean isWorkoutDone(LocalDate date) {
        return workoutMap.getOrDefault(date, false);
    }

    public static HashMap<LocalDate, Boolean> getWorkoutMap() {
        return workoutMap;
    }
}