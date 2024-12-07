package com.teferi.abel.yeneshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
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
import com.teferi.abel.yeneshop.Menus.HelpActivity;

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
                    message = "All Items selected";
                } else if (itemId == R.id.navReport) {
                    message = "Report by category selected";
                } else if (itemId == R.id.navSetting) {
                    message = "Settings selected";
                } else if (itemId == R.id.navHelp) {
                    message = "Help selected";
                    Intent intent = new Intent(HomeActivity.this, HelpActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.navFeedback) {
                    message = "Feedback selected";
                } else if (itemId == R.id.navAbout) {
                    message = "About selected";
                }

                // Show toast message
                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();

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

            // Set tab text appearance
            tabLayout.setTabTextColors(Color.BLACK, Color.BLACK); // Set both normal and selected text color to black

            // Create rounded rectangle background drawable
            GradientDrawable selectedBackground = new GradientDrawable();
            selectedBackground.setShape(GradientDrawable.RECTANGLE);
            selectedBackground.setCornerRadius(getResources().getDimension(R.dimen.tab_corner_radius));
            selectedBackground.setColor(getResources().getColor(R.color.card_color));

            // Add tab selection listener
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    View tabView = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                    if (tabView != null) {
                        tabView.setBackground(selectedBackground);

                        // Make text bold when selected
                        TextView textView = findTextView(tabView);
                        if (textView != null) {
                            textView.setTypeface(Typeface.DEFAULT_BOLD);
                        }
                    }

                    Drawable icon = tab.getIcon();
                    if (icon != null) {
                        Drawable wrappedIcon = DrawableCompat.wrap(icon).mutate();
                        DrawableCompat.setTint(wrappedIcon, Color.WHITE);
                        tab.setIcon(wrappedIcon);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    View tabView = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition());
                    if (tabView != null) {
                        tabView.setBackgroundColor(Color.TRANSPARENT);

                        // Reset text to normal weight when unselected
                        TextView textView = findTextView(tabView);
                        if (textView != null) {
                            textView.setTypeface(Typeface.DEFAULT);
                        }
                    }

                    Drawable icon = tab.getIcon();
                    if (icon != null) {
                        icon.clearColorFilter();
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    // Do nothing
                }
            });

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

                        // Set initial background for each tab
                        GradientDrawable background = new GradientDrawable();
                        background.setShape(GradientDrawable.RECTANGLE);
                        background.setCornerRadius(getResources().getDimension(R.dimen.tab_corner_radius));
                        tabView.setBackground(background);

                        // Set initial text color and style
                        TextView textView = findTextView(tabView);
                        if (textView != null) {
                            textView.setTextColor(Color.BLACK);
                            textView.setTypeface(Typeface.DEFAULT);
                        }

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













