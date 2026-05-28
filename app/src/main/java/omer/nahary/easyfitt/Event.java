package omer.nahary.easyfitt;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class Event implements Serializable {
    public static ArrayList<Event> allEvents = new ArrayList<>();

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_COMPLETED = 1;
    public static final int STATUS_FAILED = 2;
    public static final int STATUS_MISSED = 3;

    private LocalDateTime dateTime;
    private int status = STATUS_PENDING;
    protected String typeString;

    public Event(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        this.typeString = getEventType();
    }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public boolean isCompleted() {
        return status == STATUS_COMPLETED;
    }

    public void setCompleted(boolean completed) {
        this.status = completed ? STATUS_COMPLETED : STATUS_PENDING;
    }

    public abstract String getEventType();

    private static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public static void saveEvents(Context context) {
        if (context == null) return;

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        try {
            SharedPreferences prefs = context.getSharedPreferences("EasyFitPrefs_" + uid, Context.MODE_PRIVATE);
            String json = getGson().toJson(allEvents);
            prefs.edit().putString("events", json).apply();
            Log.d("EventSystem", "Saved Successfully for user " + uid + ": " + allEvents.size() + " events");
        } catch (Exception e) {
            Log.e("EventSystem", "Save error: " + e.getMessage());
        }
    }

    public static void loadEvents(Context context) {
        if (context == null) return;

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            allEvents = new ArrayList<>();
            return;
        }

        try {
            SharedPreferences prefs = context.getSharedPreferences("EasyFitPrefs_" + uid, Context.MODE_PRIVATE);
            String json = prefs.getString("events", null);

            if (json == null || json.isEmpty() || json.equals("[]")) {
                allEvents = new ArrayList<>();
                return;
            }

            ArrayList<Event> tempEvents = new ArrayList<>();
            Gson gson = getGson();
            JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();

            for (JsonElement element : jsonArray) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    if (obj.has("typeString")) {
                        String type = obj.get("typeString").getAsString();
                        Event event = null;
                        if ("Run".equals(type)) {
                            event = gson.fromJson(obj, RunEvent.class);
                        } else if ("Workout".equals(type)) {
                            event = gson.fromJson(obj, WorkoutEvent.class);
                        }
                        if (event != null) tempEvents.add(event);
                    }
                }
            }
            allEvents = tempEvents;
            Log.d("EventSystem", "Loaded count for user " + uid + ": " + allEvents.size());
        } catch (Exception e) {
            Log.e("EventSystem", "Load error, resetting list", e);
            allEvents = new ArrayList<>();
        }
    }
}