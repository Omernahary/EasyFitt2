package omer.nahary.easyfitt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class CheckEventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String eventType = intent.getStringExtra("eventType");
        String eventTime = intent.getStringExtra("eventTime");
        String channelId = "easyfitt_alerts_v2";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Workouts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // כפתור 1: בוצע
        Intent doneIntent = new Intent(context, DoneReceiver.class);
        doneIntent.putExtra("eventTime", eventTime);
        PendingIntent donePending = PendingIntent.getBroadcast(context, eventTime.hashCode(), doneIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // כפתור 2: לא בוצע (מתוקן)
        Intent skipIntent = new Intent(context, SkipReceiver.class);
        skipIntent.putExtra("eventTime", eventTime); // הוספתי את הזמן כדי שנדע איזה אירוע לסמן ב-X
        PendingIntent skipPending = PendingIntent.getBroadcast(context, eventTime.hashCode() + 1, skipIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("סיכום אימון 🕒")
                .setContentText("סיימת את האימון המתוכנן שלך?")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(android.R.drawable.ic_input_add, "ביצעתי ✅", donePending)
                .addAction(android.R.drawable.ic_delete, "לא ביצעתי ❌", skipPending)
                .setAutoCancel(true);

        notificationManager.notify(eventTime.hashCode(), builder.build());
    }
}