package com.teferi.abel.yeneshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.teferi.abel.yeneshop.Adapter.FragmentAdapter;
import com.teferi.abel.yeneshop.Fragments.AddFragment;
import com.teferi.abel.yeneshop.Fragments.ReportFragment;
import com.teferi.abel.yeneshop.Fragments.SaleFragment;
import com.teferi.abel.yeneshop.Fragments.StoreFragment;
import com.teferi.abel.yeneshop.Menus.AboutActivity;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String PREFERENCES_FILE = "user_data";
    private TextView ownername, shopname;
    private DrawerLayout drawerLayout;
    private ImageButton menu_btn;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        loadUserData();
        setupTabs();
        setupMenuButton();

        setupNameScroll();

    }

    private void setupNameScroll() {
        ownername.setSelected(true);
        ownername.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        ownername.setSingleLine(true);
        ownername.setMarqueeRepeatLimit(-1); // -1 means forever
        ownername.setFocusable(true);
        ownername.setFocusableInTouchMode(true);
        ownername.requestFocus();
    }


    private void initializeViews() {
        drawerLayout = findViewById(R.id.main);
        menu_btn = findViewById(R.id.menu_button);
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        ownername = findViewById(R.id.textview_ownername);
        shopname = findViewById(R.id.textview_shopname);
        navigationView = findViewById(R.id.nav_view);
    }

    // Create a custom TextView class for automatic scrolling
    public static class MarqueeTextView extends androidx.appcompat.widget.AppCompatTextView {
        public MarqueeTextView(Context context) {
            super(context);
        }

        public MarqueeTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        public boolean isFocused() {
            return true;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (ownername != null) {
            ownername.setSelected(true);
            ownername.requestFocus();
        }
    }


    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navAbout) {
            try {
                Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(HomeActivity.this,
                        "Error opening About page: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else if (id == R.id.navSetting) {
            // Handle settings
        } else if (id == R.id.navHelp) {
            // Handle help
        } else if (id == R.id.navFeedback) {
            // Handle feedback
        } else if (id == R.id.navAllItems) {
            // Handle all items
        } else if (id == R.id.navReport) {
            // Handle report
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadUserData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
            String ownerName = sharedPreferences.getString("owner_name", "");
            String shopName = sharedPreferences.getString("shop_name", "");

            ownername.setText("Welcome back, " + ownerName+"!             "+"Welcome back, " + ownerName+"!   ");

            shopname.setText(shopName + " store");
        } catch (Exception e) {
            e.printStackTrace();
            ownername.setText("Hello User");
            shopname.setText("My Store");
        }
    }

    private void setupTabs() {
        try {
            FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),
                    FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

            fragmentAdapter.addFragment(new AddFragment(), "Add", R.drawable.ic_add);
            fragmentAdapter.addFragment(new SaleFragment(), "Sales", R.drawable.ic_sale);
            fragmentAdapter.addFragment(new StoreFragment(), "Store", R.drawable.ic_store);
            fragmentAdapter.addFragment(new ReportFragment(), "Report", R.drawable.ic_report);

            viewPager.setAdapter(fragmentAdapter);
            tabLayout.setupWithViewPager(viewPager);

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

                        View tabView = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                        int marginDp = 8;
                        int marginPx = (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                marginDp,
                                getResources().getDisplayMetrics()
                        );
                        params.setMargins(marginPx, 0, marginPx, 0);
                        tabView.requestLayout();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            tabLayout.post(() -> {
                ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);
                for (int i = 0; i < slidingTabStrip.getChildCount() - 1; i++) {
                    View tab = slidingTabStrip.getChildAt(i);
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                    params.rightMargin = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            16,
                            getResources().getDisplayMetrics()
                    );
                }
            });

            viewPager.setCurrentItem(1);

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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}