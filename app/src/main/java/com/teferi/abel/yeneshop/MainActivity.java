package com.teferi.abel.yeneshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_DURATION = 2000;
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;
    private static final String PREFERENCES_FILE = "user_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Animation for app beginning
            topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
            bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

            // Hooks
            image = findViewById(R.id.shoplogo);
            logo = findViewById(R.id.firstlogname);
            slogan = findViewById(R.id.firstmessage);

            // Set animations
            if (image != null) image.setAnimation(topAnim);
            if (logo != null) logo.setAnimation(bottomAnim);
            if (slogan != null) slogan.setAnimation(bottomAnim);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkUserLoginStatus();
                }
            }, SPLASH_DURATION);
        } catch (Exception e) {
            e.printStackTrace();
            // If there's an error, proceed directly to checking login status
            checkUserLoginStatus();
        }
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
            Toast.makeText(this, "Please contact the developer, ", Toast.LENGTH_SHORT).show();
            // Fallback to Signup activity if there's an error
        }
    }
}