package omer.nahary.easyfitt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class WorkoutPlanAdapter extends RecyclerView.Adapter<WorkoutPlanAdapter.WorkoutViewHolder> {

    private ArrayList<WorkoutPlan> workoutPlans;
    private OnWorkoutClickListener listener;
    private OnWorkoutLongClickListener longListener;

    public interface OnWorkoutClickListener {
        void onWorkoutClick(WorkoutPlan plan);
    }

    // הוספת ממשק ללחיצה ארוכה
    public interface OnWorkoutLongClickListener {
        void onWorkoutLongClick(WorkoutPlan plan);
    }

    public WorkoutPlanAdapter(ArrayList<WorkoutPlan> workoutPlans, OnWorkoutClickListener listener) {
        this.workoutPlans = workoutPlans;
        this.listener = listener;
    }

    public void setOnLongClickListener(OnWorkoutLongClickListener longListener) {
        this.longListener = longListener;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutPlan plan = workoutPlans.get(position);
        holder.textView.setText(plan.getWorkoutTitle());

        holder.itemView.setOnClickListener(v -> listener.onWorkoutClick(plan));

        // הגדרת לחיצה ארוכה
        holder.itemView.setOnLongClickListener(v -> {
            if (longListener != null) {
                longListener.onWorkoutLongClick(plan);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return workoutPlans.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
            textView.setPadding(20, 40, 20, 40);
            textView.setTextSize(18);
        }
    }
}