package omer.nahary.easyfitt;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SkipReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String eventTime = intent.getStringExtra("eventTime");
        if (eventTime == null) return;

        Event.loadEvents(context);

        if (Event.allEvents != null) {
            for (Event e : Event.allEvents) {
                if (e.getDateTime() != null && e.getDateTime().toString().equals(eventTime)) {
                    // כאן הקסם: מסמנים כנכשל (איקס)
                    e.setStatus(Event.STATUS_FAILED);
                    break;
                }
            }
            Event.saveEvents(context);
        }

        // סגירת ההתראה
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(eventTime.hashCode());
        }
    }
}