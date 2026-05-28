package omer.nahary.easyfitt;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DoneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String eventTime = intent.getStringExtra("eventTime");
        if (eventTime == null) return;

        Event.loadEvents(context);

        boolean updated = false;
        if (Event.allEvents != null) {
            for (Event e : Event.allEvents) {
                if (e.getDateTime() != null && e.getDateTime().toString().equals(eventTime)) {
                    e.setCompleted(true);
                    updated = true;
                    break;
                }
            }
        }

        if (updated) {
            Event.saveEvents(context);
            Log.d("DoneReceiver", "Event marked as completed.");
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(eventTime.hashCode());
        }
    }
}