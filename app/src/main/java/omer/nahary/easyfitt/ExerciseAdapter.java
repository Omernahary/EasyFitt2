package omer.nahary.easyfitt;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private ArrayList<Exercise> exercises;

    public ExerciseAdapter(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_row, parent, false);

        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {

        Exercise exercise = exercises.get(position);

        holder.etName.setText(exercise.getName());
        holder.etSets.setText(String.valueOf(exercise.getSets()));
        holder.etMin.setText(String.valueOf(exercise.getMinReps()));
        holder.etMax.setText(String.valueOf(exercise.getMaxReps()));
        holder.etWeight.setText(String.valueOf(exercise.getWeight()));

        if (exercise.getActualReps() == 0) {
            holder.etActual.setText("");
        } else {
            holder.etActual.setText(String.valueOf(exercise.getActualReps()));
        }

        holder.tvAi.setText("Waiting for set data...");

        holder.btnDelete.setOnClickListener(v -> {

            int currentPos = holder.getAdapterPosition();

            if (currentPos != RecyclerView.NO_POSITION) {

                exercises.remove(currentPos);

                notifyItemRemoved(currentPos);
                notifyItemRangeChanged(currentPos, exercises.size());
            }
        });

        holder.etName.addTextChangedListener(new SimpleTextWatcher(s ->
                exercise.setName(s)));

        holder.etSets.addTextChangedListener(new SimpleTextWatcher(s ->
                exercise.setSets(parseSafe(s))));

        holder.etMin.addTextChangedListener(new SimpleTextWatcher(s ->
                exercise.setMinReps(parseSafe(s))));

        holder.etMax.addTextChangedListener(new SimpleTextWatcher(s ->
                exercise.setMaxReps(parseSafe(s))));

        holder.etWeight.addTextChangedListener(new SimpleTextWatcher(s ->
                exercise.setWeight(parseSafeDouble(s))));

        holder.etActual.addTextChangedListener(new SimpleTextWatcher(s -> {

            exercise.setActualReps(parseSafe(s));

            updateAiView(holder, exercise);
        }));
    }

    private void updateAiView(ExerciseViewHolder holder, Exercise exercise) {

        holder.tvAi.setText("Generating AI advice...");

        exercise.getAiRecommendation(
                (Activity) holder.itemView.getContext(),

                new Listener() {

                    @Override
                    public void onSuccess(String result) {

                        holder.tvAi.setText(result);
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                        holder.tvAi.setText("AI Error: " + errorMessage);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {

        return exercises.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        EditText etName;
        EditText etSets;
        EditText etMin;
        EditText etMax;
        EditText etActual;
        EditText etWeight;

        TextView tvAi;

        ImageButton btnDelete;

        public ExerciseViewHolder(@NonNull View itemView) {

            super(itemView);

            etName = itemView.findViewById(R.id.etExerciseName);
            etSets = itemView.findViewById(R.id.etSets);
            etMin = itemView.findViewById(R.id.etMinReps);
            etMax = itemView.findViewById(R.id.etMaxReps);
            etActual = itemView.findViewById(R.id.etActualReps);
            etWeight = itemView.findViewById(R.id.etWeight);

            tvAi = itemView.findViewById(R.id.tvAiRecommendation);

            btnDelete = itemView.findViewById(R.id.btnDeleteExercise);
        }
    }

    private int parseSafe(String s) {

        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseSafeDouble(String s) {

        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }

    interface SimpleWatcher {

        void onUpdate(String s);
    }

    class SimpleTextWatcher implements TextWatcher {

        SimpleWatcher sw;

        SimpleTextWatcher(SimpleWatcher sw) {

            this.sw = sw;
        }

        @Override
        public void afterTextChanged(Editable s) {

            sw.onUpdate(s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    }
}