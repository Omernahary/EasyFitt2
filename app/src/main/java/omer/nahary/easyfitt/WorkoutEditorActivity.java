package omer.nahary.easyfitt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class WorkoutEditorActivity extends AppCompatActivity {

    private EditText etWorkoutTitle;
    private RecyclerView rvExerciseTable;
    private ExerciseAdapter adapter;
    private ArrayList<Exercise> exercises;
    private ArrayList<WorkoutPlan> allPlans;
    private int planPosition;
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_editor);

        etWorkoutTitle = findViewById(R.id.etWorkoutTitle);
        rvExerciseTable = findViewById(R.id.rvExerciseTable);
        Button btnAdd = findViewById(R.id.btnAddExercise);
        Button btnSave = findViewById(R.id.btnSaveWorkout);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        planPosition = getIntent().getIntExtra("PLAN_POSITION", -1);

        loadAllPlans();

        if (planPosition != -1) {
            WorkoutPlan currentPlan = allPlans.get(planPosition);
            etWorkoutTitle.setText(currentPlan.getWorkoutTitle());
            exercises = currentPlan.getExercises();
            if (exercises == null) exercises = new ArrayList<>();
        } else {
            exercises = new ArrayList<>();
            etWorkoutTitle.setText("");
        }

        adapter = new ExerciseAdapter(exercises);
        rvExerciseTable.setLayoutManager(new LinearLayoutManager(this));
        rvExerciseTable.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            // Updated to match the new Exercise(Name, Sets, Min, Max, Weight)
            exercises.add(new Exercise("", 3, 8, 12, 0.0));
            adapter.notifyItemInserted(exercises.size() - 1);
        });

        btnSave.setOnClickListener(v -> {
            saveAndExit();
        });
    }

    private void loadAllPlans() {
        SharedPreferences prefs = getSharedPreferences("Workouts_" + userUid, Context.MODE_PRIVATE);
        String json = prefs.getString("workout_list", null);
        Type type = new TypeToken<ArrayList<WorkoutPlan>>() {}.getType();
        allPlans = new Gson().fromJson(json, type);
        if (allPlans == null) allPlans = new ArrayList<>();
    }

    private void saveAndExit() {
        String title = etWorkoutTitle.getText().toString().trim();
        if (title.isEmpty()) title = "Unnamed Workout";

        WorkoutPlan plan = new WorkoutPlan(title);
        plan.setExercises(exercises);

        if (planPosition != -1) {
            allPlans.set(planPosition, plan);
        } else {
            allPlans.add(plan);
        }

        SharedPreferences prefs = getSharedPreferences("Workouts_" + userUid, Context.MODE_PRIVATE);
        prefs.edit().putString("workout_list", new Gson().toJson(allPlans)).apply();

        Toast.makeText(this, "Workout Saved! 💪", Toast.LENGTH_SHORT).show();
        finish();
    }
}