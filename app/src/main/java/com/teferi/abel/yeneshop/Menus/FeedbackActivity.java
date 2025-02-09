package com.teferi.abel.yeneshop.Menus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teferi.abel.yeneshop.R;

public class FeedbackActivity extends AppCompatActivity {

   private static final String EMAIL = "abelgetahun66@gmail.com";
    private static final String TELEGRAM_USERNAME = "te_feri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindow();
        setContentView(R.layout.activity_feedback);

        // Initialize buttons
        FloatingActionButton fabBack = findViewById(R.id.fab_about_back);

        Button btnTelegram = findViewById(R.id.btnTelegramSetting);
        Button btnEmail = findViewById(R.id.btnEmailSetting);

        // Set click listeners
        fabBack.setOnClickListener(v -> finish());
        btnTelegram.setOnClickListener(v -> openTelegram());
        btnEmail.setOnClickListener(v -> sendEmail());
    }

    private void setupWindow() {
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.white));
        window.setNavigationBarColor(getColor(R.color.white));

        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(window, window.getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(true);
        windowInsetsController.setAppearanceLightNavigationBars(true);
    }



    private void openTelegram() {
        try {
            // Try to open Telegram app
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("tg://resolve?domain=" + TELEGRAM_USERNAME));
            startActivity(intent);
        } catch (Exception e) {
            // If Telegram app is not installed, open in browser
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://t.me/" + TELEGRAM_USERNAME));
            startActivity(intent);
        }
    }

    private void sendEmail() {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + EMAIL));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for YeneShop");
            startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open email client", Toast.LENGTH_SHORT).show();
        }
    }
}