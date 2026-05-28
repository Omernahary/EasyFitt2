package omer.nahary.easyfitt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String eventId = intent.getStringExtra("eventId");

        if ("ACTION_MARK_DONE".equals(action) && eventId != null) {
            // עדכון בפיירבייס שהאימון בוצע!
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference eventRef = FirebaseDatabase.getInstance()
                    .getReference("Users").child(userId).child("Events").child(eventId);

            eventRef.child("completed").setValue(true);

            Toast.makeText(context, "כל הכבוד! האימון סומן כבוצע ✅", Toast.LENGTH_LONG).show();

            // כאן נבטל את ההתראה מהמסך
            android.app.NotificationManager notificationManager =
                    (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(eventId.hashCode());
        }
    }
}