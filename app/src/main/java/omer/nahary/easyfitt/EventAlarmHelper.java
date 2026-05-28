package omer.nahary.easyfitt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class EventAlarmHelper {

    public static void setEventAlarm(Context context, Event event) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        // 1. קבלת הזמן מהאירוע (למשל 16:30) והוספת שעה אחת (17:30)
        LocalDateTime alarmTime = event.getDateTime().plusHours(1);

        // 2. התיקון הקריטי: המרה למילי-שניות תוך שימוש ב-Offset של המכשיר
        // אנחנו לוקחים את האופסט הנוכחי (למשל +2) ומחשבים ידנית
        long triggerAtMillis = alarmTime.toInstant(ZoneOffset.systemDefault().getRules().getOffset(alarmTime)).toEpochMilli();

        // 3. בדיקה מול הזמן הנוכחי האמיתי של המערכת
        long currentTime = System.currentTimeMillis();

        // אם החישוב יוצא רחוק מדי (בגלל הבאג של ה-7200 שניות), אנחנו "מכריחים" אותו להתיישר
        if (triggerAtMillis - currentTime > 4000000) { // מעל שעה וקצת, סימן שיש סטייה של אזור זמן
            triggerAtMillis -= (2 * 3600 * 1000); // מורידים שעתיים מהחישוב
            Log.d("EventAlarm", "detected timezone shift, adjusting by 2 hours");
        }

        // הגנה: אם אחרי הכל הזמן בעבר, נקפיץ תוך 5 שניות
        if (triggerAtMillis <= currentTime) {
            triggerAtMillis = currentTime + 5000;
        }

        Intent intent = new Intent(context, CheckEventReceiver.class);
        intent.putExtra("eventType", event.getEventType());
        intent.putExtra("eventTime", event.getDateTime().toString());

        int requestCode = event.getDateTime().toString().hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }

            long diff = (triggerAtMillis - System.currentTimeMillis()) / 1000;
            Log.d("EventAlarm", "Success! Seconds until alarm: " + diff);
        } catch (SecurityException e) {
            Log.e("EventAlarm", "Error", e);
        }
    }

    public static void cancelEventAlarm(Context context, Event event) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;
        Intent intent = new Intent(context, CheckEventReceiver.class);
        int requestCode = event.getDateTime().toString().hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}