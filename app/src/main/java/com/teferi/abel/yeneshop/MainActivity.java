package com.teferi.abel.yeneshop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_DURATION = 2000;
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;
    private static final String PREFERENCES_FILE = "user_data";
    private static final String REMAINING_DAYS_KEY = "remaining_days";
    private static final String LAST_ACCESS_DATE_KEY = "last_access_date";
    private static final int INITIAL_TRIAL_DAYS = 35;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_main);

        try {
            topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
            bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

            image = findViewById(R.id.shoplogo);
            logo = findViewById(R.id.firstlogname);
            slogan = findViewById(R.id.firstmessage);

            if (image != null) image.setAnimation(topAnim);
            if (logo != null) logo.setAnimation(bottomAnim);
            if (slogan != null) slogan.setAnimation(bottomAnim);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (checkTrialPeriod()) {
                        checkUserLoginStatus();
                    } else {
                        showCustomTrialExpiredDialog();
                    }
                }
            }, SPLASH_DURATION);
        } catch (Exception e) {
            e.printStackTrace();
            if (checkTrialPeriod()) {
                checkUserLoginStatus();
            } else {
                showCustomTrialExpiredDialog();
            }
        }
    }

    private void hideSystemBars() {
        // Enable immersive sticky mode for full-screen experience
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        // Set system bar background colors to match the app's background
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.background, getTheme())); // Status bar
        window.setNavigationBarColor(getResources().getColor(R.color.background, getTheme())); // Navigation bar

        // Ensure system bar content (icons/text) is invisible
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // Adjust for light/dark backgrounds if necessary
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR // Requires API level 26 or higher
        );
    }



    private boolean checkTrialPeriod() {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Get current date as string (without time)
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // First time user
        if (!prefs.contains(REMAINING_DAYS_KEY)) {
            editor.putInt(REMAINING_DAYS_KEY, INITIAL_TRIAL_DAYS);
            editor.putString(LAST_ACCESS_DATE_KEY, currentDate);
            editor.apply();
            return true;
        }

        // Get stored values
        int remainingDays = prefs.getInt(REMAINING_DAYS_KEY, 0);
        String lastAccessDate = prefs.getString(LAST_ACCESS_DATE_KEY, currentDate);

        // If no remaining days, trial has expired
        if (remainingDays <= 0) {
            return false;
        }

        // Check if date has changed
        if (!currentDate.equals(lastAccessDate)) {
            // Decrement remaining days by 1 if date has changed
            remainingDays--;
            editor.putInt(REMAINING_DAYS_KEY, remainingDays);
            editor.putString(LAST_ACCESS_DATE_KEY, currentDate);
            editor.apply();

            // Check if trial has expired after update
            if (remainingDays <= 0) {
                return false;
            }
        }

        return true;
    }


    private void checkUserLoginStatus() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
            String ownerName = sharedPreferences.getString("owner_name", null);
            String shopName = sharedPreferences.getString("shop_name", null);

            Intent intent;
            if (ownerName != null && shopName != null) {
                intent = new Intent(MainActivity.this, HomeActivity.class);
            } else {
                intent = new Intent(MainActivity.this, Signup.class);
            }
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            showCustomTrialExpiredDialog();
        }
    }

    private void showCustomTrialExpiredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_trail_expired, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ImageButton btnTelegram = dialogView.findViewById(R.id.btnTelegram);
        ImageButton btnPhone = dialogView.findViewById(R.id.btnPhone);
        ImageButton btnEmail = dialogView.findViewById(R.id.btnEmail);

        btnTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/yene_enat1"));
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
            }
        });

        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+251 96 450 5926"));
                startActivity(intent);
                finish();
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:abelgetahun66@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "YeneShop App Purchase Request");
                startActivity(Intent.createChooser(intent, "Send Email"));
                finish();
            }
        });

        dialog.show();
    }


}