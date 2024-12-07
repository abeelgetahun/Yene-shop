package com.teferi.abel.yeneshop.Menus;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teferi.abel.yeneshop.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply window configurations before setContentView
        setupWindow();
        setContentView(R.layout.activity_about);

        initializeViews();
    }

    private void setupWindow() {
        Window window = getWindow();
        if (window != null) {
            // Make system bars background white
            window.setStatusBarColor(getColor(R.color.white));
            window.setNavigationBarColor(getColor(R.color.white));

            // Make system bars text/icons black
            WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView());
            windowInsetsController.setAppearanceLightStatusBars(true);
            windowInsetsController.setAppearanceLightNavigationBars(true);
        }
    }

    private void initializeViews() {
        // Initialize views using lazy loading
        FloatingActionButton fabBack = findViewById(R.id.fab_about_back);
        if (fabBack != null) {
            fabBack.setOnClickListener(v -> finish());
        }

        setAppVersion();
        setupSocialMediaButtons();
    }

    private void setAppVersion() {
        TextView appVersion = findViewById(R.id.app_version);
        if (appVersion != null) {
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                appVersion.setText(getString(R.string.version_format, pInfo.versionName));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                appVersion.setVisibility(View.GONE);
            }
        }
    }

    private void setupSocialMediaButtons() {
        setupSocialButton(R.id.btnlinkedin, "YOUR_LINKEDIN_URL");
        setupSocialButton(R.id.btnintagram, "YOUR_INSTAGRAM_URL");
        setupSocialButton(R.id.btnfacebook, "YOUR_FACEBOOK_URL");
        setupSocialButton(R.id.btnx, "YOUR_X_URL");
    }

    private void setupSocialButton(int buttonId, String url) {
        ImageButton button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> openUrl(url));
        }
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}