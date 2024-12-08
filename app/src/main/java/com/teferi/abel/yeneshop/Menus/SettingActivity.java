package com.teferi.abel.yeneshop.Menus;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teferi.abel.yeneshop.HomeActivity;
import com.teferi.abel.yeneshop.R;

public class SettingActivity extends AppCompatActivity {

    private EditText etShopName, etOwnerName;
    private MaterialButton btnUpdate;
    private FloatingActionButton fabBack;
    private static final String PREFERENCES_FILE = "user_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_setting);

        // Initialize views
        etShopName = findViewById(R.id.update_shop_name);
        etOwnerName = findViewById(R.id.update_owner_name);
        btnUpdate = findViewById(R.id.settingbutton);
        fabBack = findViewById(R.id.fab_setting_back);

        // Load and display saved data
        loadUserData();

        // Set up click listeners
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            }
        });

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity and go back
            }
        });
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        String shopName = sharedPreferences.getString("shop_name", "");
        String ownerName = sharedPreferences.getString("owner_name", "");

        etShopName.setText(shopName);
        etOwnerName.setText(ownerName);
    }

    private void updateUserData() {
        String shopName = etShopName.getText().toString().trim();
        String ownerName = etOwnerName.getText().toString().trim();

        if (shopName.isEmpty() || ownerName.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        saveUserData(ownerName, shopName);
        showSuccessDialog();
    }

    private void saveUserData(String ownerName, String shopName) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("owner_name", ownerName);
        editor.putString("shop_name", shopName);
        editor.apply();
    }

    private void showSuccessDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_success);
        dialog.setCancelable(false);

        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        dialog.findViewById(android.R.id.content).startAnimation(slideUp);

        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                navigateToHome();
            }
        }, 2000); // Show dialog for 2 seconds
    }

    private void navigateToHome() {
        Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}


