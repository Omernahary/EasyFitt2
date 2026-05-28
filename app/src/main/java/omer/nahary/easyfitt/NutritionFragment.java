package omer.nahary.easyfitt;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NutritionFragment extends Fragment {

    private TextView tvDayTitle, tvTotalCal, tvTotalPro, tvTotalCarb;
    private View viewBreakfast, viewLunch, viewDinner;
    private Button btnSwitchGoal;
    private ImageButton btnEditFoods; // הורדנו את btnEditWeight
    private FirebaseFirestore db;

    private double currentWeight = 70.0;
    private String currentGoal = "Bulk";
    private Map<String, Boolean> userPrefs;
    private Button[] dayButtonsArray = new Button[7];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nutrition, container, false);
        db = FirebaseFirestore.getInstance();

        initViews(view);
        setupDayButtons(view);
        loadData();

        // כפתור החלפת מטרה - עכשיו מעדכן במיידי!
        btnSwitchGoal.setOnClickListener(v -> toggleGoal());

        // כפתור הפלוס - פותח את מסך בחירת המאכלים והמשקל
        btnEditFoods.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), UserSetupActivity.class));
        });

        return view;
    }

    private void initViews(View view) {
        tvDayTitle = view.findViewById(R.id.tv_current_day_title);
        tvTotalCal = view.findViewById(R.id.tv_total_cal);
        tvTotalPro = view.findViewById(R.id.tv_total_pro);
        tvTotalCarb = view.findViewById(R.id.tv_total_carb);
        btnSwitchGoal = view.findViewById(R.id.btn_switch_goal);
        btnEditFoods = view.findViewById(R.id.btn_edit_foods);

        // כאן אם ה-ID של המפתח השוודי עדיין ב-XML, אנחנו פשוט לא משתמשים בו ב-Java

        viewBreakfast = view.findViewById(R.id.meal_breakfast);
        viewLunch = view.findViewById(R.id.meal_lunch);
        viewDinner = view.findViewById(R.id.meal_dinner);
    }

    private void toggleGoal() {
        // החלפה לוגית מקומית כדי שהמשתמש יראה שינוי מיד
        currentGoal = currentGoal.equals("Bulk") ? "Cut" : "Bulk";

        // קריאה לפונקציית העדכון שתשנה את המספרים במסך ברגע הלחיצה!
        updateMenu();

        // עדכון ב-Firebase ברקע
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            db.collection("Users").document(uid).update("goal", currentGoal)
                    .addOnSuccessListener(aVoid -> {
                        // כאן אפשר להוסיף Toast קטן לביטחון
                        Toast.makeText(getActivity(), "Goal updated to " + currentGoal, Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        db.collection("Users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists() && isAdded()) {
                if (doc.contains("weight")) currentWeight = doc.getDouble("weight");
                String goal = doc.getString("goal");
                currentGoal = ("Mass".equals(goal) || "Bulk".equals(goal)) ? "Bulk" : "Cut";
                userPrefs = (Map<String, Boolean>) doc.get("foodPreferences");
                updateMenu();
            }
        });
    }

    private void updateMenu() {
        if (userPrefs == null || !isAdded()) return;

        btnSwitchGoal.setText("Goal: " + currentGoal + " (Switch)");

        int dailyCals = currentGoal.equals("Bulk") ? (int)(currentWeight * 35) : (int)(currentWeight * 25);
        int dailyProt = (int)(currentWeight * 2.2);
        int dailyCarbs = (int)((dailyCals - (dailyProt * 4) - (currentWeight * 0.8 * 9)) / 4);

        tvTotalCal.setText(String.valueOf(dailyCals));
        tvTotalPro.setText(dailyProt + "g");
        tvTotalCarb.setText(dailyCarbs + "g");

        List<String> pro = new ArrayList<>(), carbs = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : userPrefs.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue())) {
                String f = entry.getKey();
                if ("Chicken Beef Fish Tuna Eggs Tofu".contains(f)) pro.add(f);
                else if ("Rice Potato Pasta Oatmeal Quinoa Bread".contains(f)) carbs.add(f);
            }
        }
        Collections.shuffle(pro); Collections.shuffle(carbs);

        setupMeal(viewBreakfast, "Breakfast 🍳", "Eggs", "Oatmeal", (int)(dailyProt*0.25), (int)(dailyCarbs*0.2));
        setupMeal(viewLunch, "Lunch 🍗", pro.isEmpty() ? "Chicken" : pro.get(0), carbs.isEmpty() ? "Rice" : carbs.get(0), (int)(dailyProt*0.4), (int)(dailyCarbs*0.4));
        setupMeal(viewDinner, "Dinner 🥗", pro.size() > 1 ? pro.get(1) : "Beef", carbs.size() > 1 ? carbs.get(1) : "Potato", (int)(dailyProt*0.35), (int)(dailyCarbs*0.4));
    }

    private void setupMeal(View mealView, String nameStr, String pSrc, String cSrc, int p, int c) {
        if (mealView == null) return;
        TextView tvName = mealView.findViewById(R.id.tv_meal_name);
        TextView tvDesc = mealView.findViewById(R.id.tv_meal_description);
        TextView tvMac = mealView.findViewById(R.id.tv_meal_macros);

        if (tvName != null) tvName.setText(nameStr);
        int pG = pSrc.equals("Eggs") ? (p / 7) : (int)(p / 0.25);
        int cG = cSrc.equals("Bread") ? (c / 15) : (int)(c / 0.28);
        if (tvDesc != null) tvDesc.setText(pG + (pSrc.equals("Eggs") ? " Units " : "g ") + pSrc + " + " + cG + "g " + cSrc);
        if (tvMac != null) tvMac.setText("P: " + p + "g | C: " + c + "g");
    }

    private void setupDayButtons(View view) {
        int[] resIds = {R.id.btn_sun, R.id.btn_mon, R.id.btn_tue, R.id.btn_wed, R.id.btn_thu, R.id.btn_fri, R.id.btn_sat};
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        int todayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

        for (int i = 0; i < resIds.length; i++) {
            final int index = i;
            dayButtonsArray[i] = view.findViewById(resIds[i]);
            if (dayButtonsArray[i] == null) continue;

            if (i == todayIndex) {
                updateDayUI(i, days[i] + " (Today)");
            } else {
                dayButtonsArray[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
                dayButtonsArray[i].setTextColor(Color.BLACK);
            }

            dayButtonsArray[i].setOnClickListener(v -> {
                updateDayUI(index, days[index] + (index == todayIndex ? " (Today)" : ""));
                updateMenu();
            });
        }
    }

    private void updateDayUI(int index, String title) {
        tvDayTitle.setText(title);
        for (int i = 0; i < dayButtonsArray.length; i++) {
            if (dayButtonsArray[i] == null) continue;
            if (i == index) {
                dayButtonsArray[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1B5E20")));
                dayButtonsArray[i].setTextColor(Color.WHITE);
            } else {
                dayButtonsArray[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CCCCCC")));
                dayButtonsArray[i].setTextColor(Color.BLACK);
            }
        }
    }
}