package com.teferi.abel.yeneshop.Menus;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teferi.abel.yeneshop.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindow();
        setContentView(R.layout.activity_about);
        initializeViews();

        ImageView imageView = findViewById(R.id.gifImageView_about);
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        // Ensure the progress bar is visible until the GIF is loaded.
        progressBar.setVisibility(View.VISIBLE);

        // Load the GIF using Glide.
        Glide.with(this)
                .asGif()
                .load(R.drawable.gif_about)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<GifDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model,
                                                   Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
    }

    private void setupWindow() {
        Window window = getWindow();
        if (window != null) {
            window.setStatusBarColor(getColor(R.color.white));
            window.setNavigationBarColor(getColor(R.color.white));

            WindowInsetsControllerCompat windowInsetsController =
                    WindowCompat.getInsetsController(window, window.getDecorView());
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
}
