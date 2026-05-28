package omer.nahary.easyfitt;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Event.loadEvents(this);
        checkAndResetWeekly();
        checkNotificationPermission();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // טעינת הפרגמנט הראשון (לוח שנה) כברירת מחדל
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CalendarFragment())
                    .commit();
        }

        // הגדרת המעבר בין הפרגמנטים
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_calendar) {
                selectedFragment = new CalendarFragment();
            } else if (id == R.id.nav_nutrition) {
                // חיבור הפרגמנט החדש של התזונה שיצרנו!
                selectedFragment = new NutritionFragment();
            } else if (id == R.id.nav_ai_workout) {
                selectedFragment = new WorkoutAIFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // בדיקת אימונים שממתינים לדיווח
        new Handler().postDelayed(this::checkPendingEvents, 2000);
    }

    private void checkPendingEvents() {
        LocalDateTime now = LocalDateTime.now();
        if (Event.allEvents == null || Event.allEvents.isEmpty()) return;

        for (Event event : Event.allEvents) {
            if (event.getStatus() == Event.STATUS_PENDING &&
                    event.getDateTime().plusMinutes(30).isBefore(now)) {
                showMissedEventDialog(event);
                break;
            }
        }
    }

    private void showMissedEventDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Workout Report 🕒");
        builder.setMessage("Did you complete your " + event.getEventType() + "?");
        builder.setCancelable(false);

        builder.setPositiveButton("I did it! ✅", (dialog, which) -> {
            event.setStatus(Event.STATUS_COMPLETED);
            saveAndUpdate();
        });

        builder.setNegativeButton("I missed it ❌", (dialog, which) -> {
            event.setStatus(Event.STATUS_FAILED);
            saveAndUpdate();
        });
        builder.show();
    }

    private void saveAndUpdate() {
        Event.saveEvents(this);

        // רענון הפרגמנט הנוכחי אם הוא לוח שנה
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof CalendarFragment) {
            ((CalendarFragment) currentFragment).refreshCalendar();
        }

        Intent refreshIntent = new Intent("com.omer.easyfitt.REFRESH_EVENTS");
        refreshIntent.setPackage(getPackageName());
        sendBroadcast(refreshIntent);

        checkPendingEvents();
    }

    private void checkAndResetWeekly() {
        SharedPreferences timePrefs = getSharedPreferences("TimePrefs", Context.MODE_PRIVATE);
        String lastResetStr = timePrefs.getString("lastResetDate", null);
        LocalDate today = LocalDate.now();

        if (lastResetStr == null) {
            timePrefs.edit().putString("lastResetDate", today.toString()).apply();
            return;
        }

        LocalDate lastResetDate = LocalDate.parse(lastResetStr);
        if (ChronoUnit.DAYS.between(lastResetDate, today) >= 7) {
            showWeeklySummaryDialog(timePrefs, today);
        }
    }

    private void showWeeklySummaryDialog(SharedPreferences prefs, LocalDate today) {
        int runs = 0;
        int workouts = 0;
        for (Event e : Event.allEvents) {
            if (e.getStatus() == Event.STATUS_COMPLETED) {
                if (e.getEventType().toLowerCase().contains("run")) runs++;
                else workouts++;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Weekly Summary! 🏆")
                .setMessage("Completed " + runs + " runs and " + workouts + " workouts.")
                .setPositiveButton("Let's go! 🚀", (dialog, which) -> {
                    Event.allEvents.clear();
                    Event.saveEvents(this);
                    prefs.edit().putString("lastResetDate", today.toString()).apply();
                    // רענון לוח השנה אחרי האיפוס
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (currentFragment instanceof CalendarFragment) {
                        ((CalendarFragment) currentFragment).refreshCalendar();
                    }
                }).show();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    public void logoutAction() {
        FirebaseAuth.getInstance().signOut();
        if (Event.allEvents != null) {
            Event.allEvents.clear();
        }
        Intent intent = new Intent(this, Start.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}