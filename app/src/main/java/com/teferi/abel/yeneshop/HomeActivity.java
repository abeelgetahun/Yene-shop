package com.teferi.abel.yeneshop;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
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
import com.teferi.abel.yeneshop.Menus.AllItemsActivity;
import com.teferi.abel.yeneshop.Menus.FeedbackActivity;
import com.teferi.abel.yeneshop.Menus.HelpActivity;
import com.teferi.abel.yeneshop.Menus.ReportByCategory;
import com.teferi.abel.yeneshop.Menus.SettingActivity;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton menuButton;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView ownername, shopname;
    private static final String PREFERENCES_FILE = "user_data";
    private Fragment currentFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_content);

        initializeViews();
        loadUserData();
        setupTabs();
        setupNameScroll();


        // Set up navigation drawer header
        navigationView.inflateHeaderView(R.layout.drawer_header);
        // Set up navigation drawer menu
        navigationView.inflateMenu(R.menu.drawer_items);

        // Set initial fragment
        if (savedInstanceState == null) {
            viewPager.setCurrentItem(1); // Set SALES as default tab
        }

        /////////////




        // Handle menu button click
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Handle navigation item selection
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                String message = "";

                if (itemId == R.id.navAllItems) {
                    Intent intent = new Intent(HomeActivity.this, AllItemsActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.navReport) {
                    Intent intent = new Intent(HomeActivity.this, ReportByCategory.class);
                    startActivity(intent);
                } else if (itemId == R.id.navSetting) {
                    Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.navHelp) {
                    Intent intent = new Intent(HomeActivity.this, HelpActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.navFeedback) {
                    Intent intent = new Intent(HomeActivity.this, FeedbackActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.navAbout) {
                    Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
                    // Enable activity transitions
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this);
                    startActivity(intent, options.toBundle());
                }
                // Close the drawer
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setupTabs() {
        try {
            FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),
                    FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

            fragmentAdapter.addFragment(new AddFragment(), "ADD", R.drawable.ic_add);
            fragmentAdapter.addFragment(new SaleFragment(), "SALES", R.drawable.ic_sale);
            fragmentAdapter.addFragment(new StoreFragment(), "STORE", R.drawable.ic_store);
            fragmentAdapter.addFragment(new ReportFragment(), "REPORT", R.drawable.ic_report);

            viewPager.setAdapter(fragmentAdapter);
            tabLayout.setupWithViewPager(viewPager);

            // Set initial tab colors and indicator
            tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
            tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
            tabLayout.setSelectedTabIndicatorHeight((int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics()));

            // Set up tabs with custom layouts
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    View customView = createCustomTabView(fragmentAdapter.getPageTitle(i).toString(),
                            fragmentAdapter.getPageIcon(i));
                    tab.setCustomView(customView);

                    // Set initial state for unselected tabs
                    TextView textView = customView.findViewById(R.id.tab_text);
                    ImageView iconView = customView.findViewById(R.id.tab_icon);
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    textView.setTypeface(Typeface.DEFAULT);
                    iconView.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                }
            }

            // Add tab selection listener
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    View customView = tab.getCustomView();
                    if (customView != null) {
                        TextView textView = customView.findViewById(R.id.tab_text);
                        ImageView iconView = customView.findViewById(R.id.tab_icon);

                        textView.setTypeface(Typeface.DEFAULT_BOLD);
                        textView.setTextColor(Color.WHITE);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        iconView.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    View customView = tab.getCustomView();
                    if (customView != null) {
                        TextView textView = customView.findViewById(R.id.tab_text);
                        ImageView iconView = customView.findViewById(R.id.tab_icon);

                        textView.setTypeface(Typeface.DEFAULT);
                        textView.setTextColor(Color.BLACK);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        iconView.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    // Do nothing
                }
            });

            // Set initial selected tab and trigger selection
            viewPager.setCurrentItem(1);
            TabLayout.Tab initialTab = tabLayout.getTabAt(1);
            if (initialTab != null) {
                initialTab.select();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting up tabs", Toast.LENGTH_SHORT).show();
        }
    }

    private View createCustomTabView(String title, int iconResId) {
        View view = getLayoutInflater().inflate(R.layout.custom_tab_layout, null);
        TextView textView = view.findViewById(R.id.tab_text);
        ImageView iconView = view.findViewById(R.id.tab_icon);

        textView.setText(title);
        iconView.setImageResource(iconResId);

        return view;
    }


    // Helper method to find TextView in tab view
    private TextView findTextView(View view) {
        if (view instanceof TextView) {
            return (TextView) view;
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                TextView textView = findTextView(viewGroup.getChildAt(i));
                if (textView != null) {
                    return textView;
                }
            }
        }
        return null;
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
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        ownername = findViewById(R.id.textview_ownername);
        shopname = findViewById(R.id.textview_shopname);
        navigationView = findViewById(R.id.nav_view);
        menuButton = findViewById(R.id.menu_button);
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




    private void loadUserData() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
            String ownerName = sharedPreferences.getString("owner_name", "");
            String shopName = sharedPreferences.getString("shop_name", "");

            ownername.setText("Welcome back, " + ownerName + "!             " + "Welcome back, " + ownerName + "!   ");

            shopname.setText(shopName + " store");
        } catch (Exception e) {
            e.printStackTrace();
            ownername.setText("Hello User");
            shopname.setText("My Store");
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













