package com.teferi.abel.yeneshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.teferi.abel.yeneshop.Adapter.FragmentAdapter;
import com.teferi.abel.yeneshop.Fragments.AddFragment;
import com.teferi.abel.yeneshop.Fragments.ReportFragment;
import com.teferi.abel.yeneshop.Fragments.SaleFragment;
import com.teferi.abel.yeneshop.Fragments.StoreFragment;

public class HomeActivity extends AppCompatActivity {

    private static final String PREFERENCES_FILE = "user_data";
    private TextView ownername, shopname;
    private DrawerLayout drawerLayout;
    private ImageButton menu_btn;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        initializeViews();
        // Load user data
        loadUserData();
        // Setup tabs
        setupTabs();
        // Setup menu button
        setupMenuButton();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.main);
        menu_btn = findViewById(R.id.menu_button);
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        ownername = findViewById(R.id.textview_ownername);
        shopname = findViewById(R.id.textview_shopname);
    }

    private void loadUserData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
            String ownerName = sharedPreferences.getString("owner_name", "");
            String shopName = sharedPreferences.getString("shop_name", "");

            ownername.setText("Hello " + ownerName);
            shopname.setText(shopName + " store");
        } catch (Exception e) {
            e.printStackTrace();
            // Set default values if there's an error
            ownername.setText("Hello User");
            shopname.setText("My Store");
        }
    }

    private void setupTabs() {
        try {
            FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),
                    FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

            // Add fragments
            fragmentAdapter.addFragment(new AddFragment(), "Add", R.drawable.ic_add);
            fragmentAdapter.addFragment(new SaleFragment(), "Sales", R.drawable.ic_sale);
            fragmentAdapter.addFragment(new StoreFragment(), "Store", R.drawable.ic_store);
            fragmentAdapter.addFragment(new ReportFragment(), "Report", R.drawable.ic_report);

            // Set adapter
            viewPager.setAdapter(fragmentAdapter);
            tabLayout.setupWithViewPager(viewPager);

            // Set icons and customize tabs
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    try {
                        Drawable icon = ContextCompat.getDrawable(this, fragmentAdapter.getPageIcon(i));
                        if (icon != null) {
                            int size = (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    24,
                                    getResources().getDisplayMetrics()
                            );
                            icon.setBounds(0, 0, size, size);
                            tab.setIcon(icon);
                        }

                        // Add padding to tabs
                        View tabView = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                        params.setMargins(8, 0, 8, 0);
                        tabView.requestLayout();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting up tabs", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupMenuButton() {
        if (menu_btn != null) {
            menu_btn.setOnClickListener(view -> {
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save any necessary state here
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore any necessary state here
    }
}