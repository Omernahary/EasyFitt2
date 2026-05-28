package omer.nahary.easyfitt;

public class Exercise {
    private String name;
    private int sets;      // הוספנו שדה סטים
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

    public String getAiRecommendation() {
        if (actualReps == 0) return "Perform your set to get AI advice";

        int diff = actualReps - maxReps;
        double nextWeight = weight;

        if (diff > 0) {
            // לוגיקת הוספה אגרסיבית
            if (diff >= 10) nextWeight += 10;
            else if (diff >= 5) nextWeight += 5;
            else nextWeight += 2.5;

            return "AI Suggestion: Beast mode! Next time try " + nextWeight + "kg (+" + (nextWeight - weight) + "kg)";
        }

        if (actualReps < minReps) {
            nextWeight = Math.max(0, weight - 2.5);
            return "AI Suggestion: Too heavy. Next time try " + nextWeight + "kg";
        }

        return "AI Suggestion: Perfect! Stay at " + weight + "kg next time.";
    }

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }
    public int getMinReps() { return minReps; }
    public void setMinReps(int minReps) { this.minReps = minReps; }
    public int getMaxReps() { return maxReps; }
    public void setMaxReps(int maxReps) { this.maxReps = maxReps; }
    public int getActualReps() { return actualReps; }
    public void setActualReps(int actualReps) { this.actualReps = actualReps; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}