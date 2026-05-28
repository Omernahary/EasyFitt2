package omer.nahary.easyfitt;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class UserSetupActivity extends AppCompatActivity {

    private EditText etWeight;
    private RadioGroup rgGoal;
    private Button btnSave, btnCancel; // נוסף כפתור ביטול
    private Map<String, CheckBox> checkBoxes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setup);

        etWeight = findViewById(R.id.et_setup_weight);
        rgGoal = findViewById(R.id.rg_setup_goal);
        btnSave = findViewById(R.id.btn_generate_plan);
        btnCancel = findViewById(R.id.btn_cancel); // אתחול כפתור ביטול

        int[] ids = {R.id.cb_chicken, R.id.cb_beef, R.id.cb_fish, R.id.cb_tuna, R.id.cb_eggs, R.id.cb_tofu,
                R.id.cb_rice, R.id.cb_potato, R.id.cb_pasta, R.id.cb_oatmeal, R.id.cb_quinoa, R.id.cb_bread,
                R.id.cb_avocado, R.id.cb_nuts, R.id.cb_salad, R.id.cb_veggies};

        String[] names = {"Chicken", "Beef", "Fish", "Tuna", "Eggs", "Tofu", "Rice", "Potato", "Pasta",
                "Oatmeal", "Quinoa", "Bread", "Avocado", "Nuts", "Salad", "Veggies"};

        for (int i = 0; i < ids.length; i++) {
            CheckBox cb = findViewById(ids[i]);
            if (cb != null) checkBoxes.put(names[i], cb);
        }

        // לחיצה על שמירה
        btnSave.setOnClickListener(v -> saveSettings());

        // לחיצה על ביטול - פשוט סוגר את המסך וחוזר אחורה
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveSettings() {
        String weightStr = etWeight.getText().toString();
        if (weightStr.isEmpty() || rgGoal.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double weight = Double.parseDouble(weightStr);
        String goal = (rgGoal.getCheckedRadioButtonId() == R.id.rb_setup_mass) ? "Bulk" : "Cut";

        Map<String, Boolean> prefs = new HashMap<>();
        for (Map.Entry<String, CheckBox> entry : checkBoxes.entrySet()) {
            prefs.put(entry.getKey(), entry.getValue().isChecked());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("weight", weight);
        data.put("goal", goal);
        data.put("foodPreferences", prefs);

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        // מראה למשתמש שזה בתהליך
        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        FirebaseFirestore.getInstance().collection("Users")
                .document(uid).set(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UserSetupActivity.this, "Settings Saved!", Toast.LENGTH_SHORT).show();
                    // פקודת הקסם: סוגר את המסך וחוזר לפרגמנט התזונה
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("SAVE");
                    Toast.makeText(UserSetupActivity.this, "Error saving settings", Toast.LENGTH_SHORT).show();
                });
    }
}