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

    private static final String PHONE_NUMBER = "+251 96 450 5926";
    private static final String EMAIL = "abelgetahun66@gmail.com";
    private static final String TELEGRAM_USERNAME = "https://t.me/yene_enat1";
    private static final String LINKEDIN_PROFILE =  "https://www.linkedin.com/in/abel-getahun-625060291?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindow();
        setContentView(R.layout.activity_feedback);

//        // Initialize buttons
//        FloatingActionButton fabBack = findViewById(R.id.fab_about_back);
//        Button btnLinkedIn = findViewById(R.id.btnLinkedIn_setting);
//        //Button btnPhone = findViewById(R.id.btnPhoneSetting);
//        Button btnTelegram = findViewById(R.id.btnTelegramSetting);
//        Button btnEmail = findViewById(R.id.btnEmailSetting);
//
//        // Set click listeners
//        fabBack.setOnClickListener(v -> finish());
//        btnLinkedIn.setOnClickListener(v -> openLinkedIn());
//       // btnPhone.setOnClickListener(v -> makePhoneCall());
//        btnTelegram.setOnClickListener(v -> openTelegram());
//        btnEmail.setOnClickListener(v -> sendEmail());
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

    private void openLinkedIn() {
        try {
            // Try to open LinkedIn app
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(LINKEDIN_PROFILE));
            intent.setPackage("com.linkedin.android");
            startActivity(intent);
        } catch (Exception e) {
            // If LinkedIn app is not installed, open in browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(LINKEDIN_PROFILE));
            startActivity(intent);
        }
    }

    private void makePhoneCall() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + PHONE_NUMBER));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to make phone call", Toast.LENGTH_SHORT).show();
        }
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