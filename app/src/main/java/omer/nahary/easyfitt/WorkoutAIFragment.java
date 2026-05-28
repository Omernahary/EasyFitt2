package omer.nahary.easyfitt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class WorkoutAIFragment extends Fragment {

    private RecyclerView rvWorkoutsList;
    private WorkoutPlanAdapter adapter;
    private ArrayList<WorkoutPlan> workoutPlans;
    private String userUid;

    public WorkoutAIFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_ai, container, false);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        rvWorkoutsList = view.findViewById(R.id.rvWorkoutsList);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddWorkoutPlan);

        loadWorkoutsFromPrefs();
        setupAdapter();

        rvWorkoutsList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvWorkoutsList.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), WorkoutEditorActivity.class);
            intent.putExtra("PLAN_POSITION", -1);
            startActivity(intent);
        });

        return view;
    }

    private void setupAdapter() {
        adapter = new WorkoutPlanAdapter(workoutPlans, plan -> {
            // Click to Edit
            Intent intent = new Intent(getContext(), WorkoutEditorActivity.class);
            intent.putExtra("PLAN_POSITION", workoutPlans.indexOf(plan));
            startActivity(intent);
        });

        // Long Click to Delete
        adapter.setOnLongClickListener(plan -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Workout")
                    .setMessage("Are you sure you want to delete \"" + plan.getWorkoutTitle() + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        workoutPlans.remove(plan);
                        saveWorkoutsToPrefs();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Workout deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        rvWorkoutsList.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorkoutsFromPrefs();
        setupAdapter();
    }

    private void loadWorkoutsFromPrefs() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("Workouts_" + userUid, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("workout_list", null);
        Type type = new TypeToken<ArrayList<WorkoutPlan>>() {}.getType();
        workoutPlans = gson.fromJson(json, type);
        if (workoutPlans == null) workoutPlans = new ArrayList<>();
    }

    private void saveWorkoutsToPrefs() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences("Workouts_" + userUid, Context.MODE_PRIVATE);
        prefs.edit().putString("workout_list", new Gson().toJson(workoutPlans)).apply();
    }
}