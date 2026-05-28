package omer.nahary.easyfitt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Start extends AppCompatActivity {

    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(Start.this, Login.class));
        });

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(Start.this, Registerr.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // בדיקה: אם המשתמש כבר מחובר, שלח אותו ישר ל-MainActivity
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(Start.this, MainActivity.class));
            finish();
        }
    }
}