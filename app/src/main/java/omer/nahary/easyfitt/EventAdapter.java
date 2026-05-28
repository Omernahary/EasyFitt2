package omer.nahary.easyfitt;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_cell, parent, false);
        }

        TextView eventName = convertView.findViewById(R.id.eventNameText);

        if (event != null) {
            String statusIcon = "";
            int textColor = Color.BLACK;
            float alpha = 1.0f;

            // בדיקת הסטטוס לקביעת צבע ואייקון
            if (event.getStatus() == Event.STATUS_COMPLETED) {
                statusIcon = " ✅";
                textColor = Color.parseColor("#4CAF50"); // ירוק
                alpha = 0.6f;
            } else if (event.getStatus() == Event.STATUS_FAILED || event.getStatus() == Event.STATUS_MISSED) {
                statusIcon = " ❌";
                textColor = Color.RED; // אדום
                alpha = 0.6f;
            } else {
                statusIcon = "";
                textColor = Color.BLACK;
                alpha = 1.0f;
            }

            // עדכון הטקסט והצבע
            eventName.setText(event.getEventType() + " ב-" + event.getDateTime().toLocalTime() + statusIcon);
            eventName.setTextColor(textColor);
            convertView.setAlpha(alpha);

            // לחיצה רגילה לעדכון מהיר
            convertView.setOnClickListener(v -> {
                String[] options = {"ביצעתי ✅", "לא ביצעתי ❌", "ביטול"};
                new AlertDialog.Builder(getContext())
                        .setTitle("עדכון סטטוס")
                        .setItems(options, (dialog, which) -> {
                            if (which == 0) {
                                event.setStatus(Event.STATUS_COMPLETED);
                            } else if (which == 1) {
                                event.setStatus(Event.STATUS_FAILED);
                            }
                            if (which != 2) {
                                Event.saveEvents(getContext());
                                notifyDataSetChanged();
                            }
                        }).show();
            });

            // לחיצה ארוכה למחיקה
            convertView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("מחיקת אימון")
                        .setMessage("האם למחוק אימון זה?")
                        .setPositiveButton("מחק", (dialog, which) -> {
                            Event.allEvents.remove(event);
                            Event.saveEvents(getContext());
                            remove(event);
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("ביטול", null)
                        .show();
                return true;
            });
        }
        return convertView;
    }
}