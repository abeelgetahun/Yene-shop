package com.teferi.abel.yeneshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Signup extends AppCompatActivity {
    private EditText etOwnerName, etShopName;
    private Button btnSignup;
    private static final String PREFERENCES_FILE = "user_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Check if user is already signed up
        if (isUserSignedUp()) {
            navigateToHome();
            return;
        }

        // Initialize UI elements
        etOwnerName = findViewById(R.id.ownername);
        etShopName = findViewById(R.id.shopname);
        btnSignup = findViewById(R.id.button);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ownerName = etOwnerName.getText().toString().trim();
                String shopName = etShopName.getText().toString().trim();

                if (TextUtils.isEmpty(ownerName)) {
                    etOwnerName.setError("Owner's name is required");
                    return;
                }

                if (TextUtils.isEmpty(shopName)) {
                    etShopName.setError("Shop name is required");
                    return;
                }

                saveUserData(ownerName, shopName);
                Toast.makeText(Signup.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                navigateToHome();
            }
        });
    }

    private boolean isUserSignedUp() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        String ownerName = sharedPreferences.getString("owner_name", null);
        String shopName = sharedPreferences.getString("shop_name", null);
        return ownerName != null && shopName != null;
    }

    private void saveUserData(String ownerName, String shopName) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("owner_name", ownerName);
        editor.putString("shop_name", shopName);
        editor.apply();
    }

    private void navigateToHome() {
        Intent intent = new Intent(Signup.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to MainActivity
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
