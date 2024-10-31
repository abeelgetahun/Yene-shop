package com.teferi.abel.yeneshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Animation for app beginning
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Hooks
        image = findViewById(R.id.shoplogo);
        logo = findViewById(R.id.firstlogname);
        slogan = findViewById(R.id.firstmessage);

        // Set animations
        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserLoginStatus();
            }
        }, SPLASH_DURATION);
    }

    private void checkUserLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        String ownerName = sharedPreferences.getString("owner_name", null);
        String shopName = sharedPreferences.getString("shop_name", null);

        Intent intent;
        if (ownerName != null && shopName != null) {
            // User has already signed up, go to HomeAdd activity
            intent = new Intent(MainActivity.this, HomeActivity.class);
        } else {
            // User hasn't signed up, go to Signup activity
            intent = new Intent(MainActivity.this, Signup.class);
        }
        startActivity(intent);
        finish();
    }
}