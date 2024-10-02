package com.example.abel.yeneshop;

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

    // Declare UI elements
    private EditText etUsername, etShop;
    private Button btnSignup;

    // SharedPreferences file name
    private static final String PREFERENCES_FILE = "user_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI elements
        etUsername = findViewById(R.id.shopname);
        etShop = findViewById(R.id.ownername);
        btnSignup = findViewById(R.id.button);

        // Set an OnClickListener for the signup button
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from input fields
                String shop_name = etUsername.getText().toString().trim();
                String owner_name = etShop.getText().toString().trim();

                // Validate the inputs
                if (TextUtils.isEmpty(shop_name)) {
                    etUsername.setError("Shop name is required");
                    return;
                }

                if (TextUtils.isEmpty(owner_name)) {
                    etShop.setError("Owner's name is required");
                    return;
                }

                // Save data in SharedPreferences
                saveUserData(shop_name, owner_name);

                // Show a success message
                Toast.makeText(Signup.this, "Signup successful!", Toast.LENGTH_SHORT).show();

                // Navigate to HomeAdd activity
                Intent intent = new Intent(Signup.this, HomeAdd.class);
                startActivity(intent);

                // Optionally, you can finish the current activity
                finish();
            }
        });
    }

    // Method to save user data in SharedPreferences
    private void saveUserData(String shop_name, String owner_name) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("shop", shop_name);
        editor.putString("owner", owner_name);
        // Ideally, you should hash the password before storing it
        editor.apply(); // Save the data
    }

    // Method to clear input fields after signup
    private void clearFields() {
        etUsername.setText("");
        etShop.setText("");
    }
}
