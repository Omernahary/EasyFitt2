package omer.nahary.easyfitt;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import omer.nahary.easyfitt.R;

public class WeeklyGoalsActivity extends AppCompatActivity {

    private EditText targetWorkoutsEditText, targetRunsEditText;
    private TextView workoutsProgressText, runsProgressText;
    private ImageView workoutIconFill, runIconFill;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_goals);

        targetWorkoutsEditText = findViewById(R.id.targetWorkoutsEditText);
        targetRunsEditText = findViewById(R.id.targetRunsEditText);
        workoutsProgressText = findViewById(R.id.workoutsProgressText);
        runsProgressText = findViewById(R.id.runsProgressText);
        workoutIconFill = findViewById(R.id.workoutIconFill);
        runIconFill = findViewById(R.id.runIconFill);

        String uid = FirebaseAuth.getInstance().getUid();
        prefs = getSharedPreferences("EasyFittGoals_" + uid, Context.MODE_PRIVATE);

        findViewById(R.id.saveGoalsButton).setOnClickListener(v -> saveGoals());
        findViewById(R.id.backButtonGoals).setOnClickListener(v -> finish());

        int targetW = prefs.getInt("targetWorkouts", 0);
        int targetR = prefs.getInt("targetRuns", 0);
        targetWorkoutsEditText.setText(String.valueOf(targetW));
        targetRunsEditText.setText(String.valueOf(targetR));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Event.loadEvents(this);
        refreshUI();
    }

    private void saveGoals() {
        String wStr = targetWorkoutsEditText.getText().toString();
        String rStr = targetRunsEditText.getText().toString();

        if (!wStr.isEmpty() && !rStr.isEmpty()) {
            prefs.edit()
                    .putInt("targetWorkouts", Integer.parseInt(wStr))
                    .putInt("targetRuns", Integer.parseInt(rStr))
                    .apply();

            refreshUI();
            Toast.makeText(this, "Goals updated!", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshUI() {
        int targetW = prefs.getInt("targetWorkouts", 0);
        int targetR = prefs.getInt("targetRuns", 0);
        int doneW = 0, doneR = 0;

        if (Event.allEvents != null) {
            for (Event e : Event.allEvents) {
                if (e != null && e.isCompleted()) {
                    if (e instanceof WorkoutEvent) doneW++;
                    else if (e instanceof RunEvent) doneR++;
                }
            }
        }

        workoutsProgressText.setText("Workouts completed: " + doneW + " / " + targetW);
        runsProgressText.setText("Runs completed: " + doneR + " / " + targetR);

        updateIconProgress(workoutIconFill, doneW, targetW);
        updateIconProgress(runIconFill, doneR, targetR);
    }

    private void updateIconProgress(ImageView icon, int done, int target) {
        if (icon != null && icon.getDrawable() != null && icon.getDrawable() instanceof ClipDrawable) {
            ClipDrawable clip = (ClipDrawable) icon.getDrawable();
            if (target > 0) {
                int level = (int) (((double) done / target) * 10000);
                clip.setLevel(Math.min(level, 10000));
            } else {
                clip.setLevel(0);
            }
        }
    }
}