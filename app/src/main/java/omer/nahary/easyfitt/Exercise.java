package omer.nahary.easyfitt;

import android.app.Activity;

public class Exercise {

    private String name;
    private int sets;
    private int minReps;
    private int maxReps;
    private int actualReps;
    private double weight;

    public Exercise(String name, int sets, int minReps, int maxReps, double weight) {
        this.name = name;
        this.sets = sets;
        this.minReps = minReps;
        this.maxReps = maxReps;
        this.weight = weight;
        this.actualReps = 0;
    }

    public void getAiRecommendation(Activity activity, Listener listener) {

        if (actualReps == 0) {
            listener.onSuccess("Perform your set to get AI advice");
            return;
        }

        String prompt =
                "You are a professional gym coach AI inside a fitness app called EasyFitt. " +
                        "Give SHORT and motivating workout advice.\n\n" +

                        "Exercise: " + name + "\n" +
                        "Sets: " + sets + "\n" +
                        "Target reps: " + minReps + "-" + maxReps + "\n" +
                        "Actual reps achieved: " + actualReps + "\n" +
                        "Current weight: " + weight + "kg\n\n" +

                        "Rules:\n" +
                        "- If user exceeded reps easily, suggest increasing weight.\n" +
                        "- If user failed badly, suggest lowering weight.\n" +
                        "- If performance was good, suggest staying.\n" +
                        "- Keep response under 20 words.\n" +
                        "- Return ONLY the recommendation.";

        AIHELPER.runAIModel(activity, prompt, listener);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getMinReps() {
        return minReps;
    }

    public void setMinReps(int minReps) {
        this.minReps = minReps;
    }

    public int getMaxReps() {
        return maxReps;
    }

    public void setMaxReps(int maxReps) {
        this.maxReps = maxReps;
    }

    public int getActualReps() {
        return actualReps;
    }

    public void setActualReps(int actualReps) {
        this.actualReps = actualReps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}