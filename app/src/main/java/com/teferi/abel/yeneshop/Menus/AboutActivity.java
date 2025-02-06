package com.teferi.abel.yeneshop.Menus;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teferi.abel.yeneshop.R;


public class AboutActivity extends AppCompatActivity {
    // Social media URLs
    private static final String FACEBOOK_URL = "https://www.facebook.com/abel.getahun.370";
    private static final String INSTAGRAM_URL = "https://www.instagram.com/abelu_23";
    private static final String LINKEDIN_URL = "https://www.linkedin.com/in/abel-getahun-625060291?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app";
    private static final String TWITTER_URL = "https://x.com/Ab_el__?t=Yt3BX66RsxietbVii9-eVA&s=35";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindow();
        setContentView(R.layout.activity_about);
        initializeViews();


        ImageView imageView = findViewById(R.id.gifImageView_about);

        // Load GIF from drawable folder
        Glide.with(this)
                .asGif()
                .load(R.drawable.gif_about)
                .into(imageView);
    }

    private void setupWindow() {
        Window window = getWindow();
        if (window != null) {
            window.setStatusBarColor(getColor(R.color.white));
            window.setNavigationBarColor(getColor(R.color.white));

            WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView());
            windowInsetsController.setAppearanceLightStatusBars(true);
            windowInsetsController.setAppearanceLightNavigationBars(true);
        }
    }

    private void initializeViews() {
        FloatingActionButton fabBack = findViewById(R.id.fab_about_back);
        if (fabBack != null) {
            fabBack.setOnClickListener(v -> finish());
        }

        setAppVersion();
//        setupSocialMediaButtons();
        //check this
    }

    private void setAppVersion() {
        TextView appVersion = findViewById(R.id.app_version);
        if (appVersion != null) {
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String versionName = pInfo.versionName;
                int versionCode = pInfo.versionCode;
                appVersion.setText(getString(R.string.version_format, versionName + " (" + versionCode + ")"));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                appVersion.setVisibility(View.GONE);
            }
        }
    }

    private void setupSocialMediaButtons() {
        Button btnFacebook = findViewById(R.id.btnfacebook);
        Button btnInstagram = findViewById(R.id.btnInstagram);
        Button btnLinkedIn = findViewById(R.id.btnLinkedIn);
        Button btnTwitter = findViewById(R.id.btnx);

        if (btnFacebook != null) {
            btnFacebook.setOnClickListener(v -> openUrl(FACEBOOK_URL));
        }
        if (btnInstagram != null) {
            btnInstagram.setOnClickListener(v -> openUrl(INSTAGRAM_URL));
        }
        if (btnLinkedIn != null) {
            btnLinkedIn.setOnClickListener(v -> openUrl(LINKEDIN_URL));
        }
        if (btnTwitter != null) {
            btnTwitter.setOnClickListener(v -> openUrl(TWITTER_URL));
        }
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Error opening link.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}