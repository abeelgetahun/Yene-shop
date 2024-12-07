package com.teferi.abel.yeneshop.Menus;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teferi.abel.yeneshop.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
        setContentView(R.layout.activity_help);


        // Find the FloatingActionButton by its ID
        FloatingActionButton fabBack = findViewById(R.id.fab_help_back);
        // Set an OnClickListener to handle the back action
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and go back to the previous one
                finish();
            }
        });


        //NestedScrollView nestedScrollView = findViewById(R.id.nested_scroll_view);
        TextView textView = findViewById(R.id.tv_story);

        // Calculate the system bar height
        int systemBarHeight = getSystemBarHeight();

        // Apply padding to the top of the TextView (or the container)
        textView.setPadding(
                textView.getPaddingLeft(),
                textView.getPaddingTop() + systemBarHeight,
                textView.getPaddingRight(),
                textView.getPaddingBottom()
        );
    }

    // Method to calculate system bar height
    private int getSystemBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0; // Fallback to 0 if not found
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
        window.setStatusBarColor(getResources().getColor(R.color.white, getTheme())); // Status bar
        window.setNavigationBarColor(getResources().getColor(R.color.white, getTheme())); // Navigation bar

        // Ensure system bar content (icons/text) is invisible
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // Adjust for light/dark backgrounds if necessary
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR // Requires API level 26 or higher
        );
    }

}
