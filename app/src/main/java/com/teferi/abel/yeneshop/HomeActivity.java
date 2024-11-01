package com.teferi.abel.yeneshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
    TextView ownername, shopname;

    DrawerLayout drawerLayout;
    ImageButton menu_btn;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.main);
        menu_btn = findViewById(R.id.menu_button);

        tabLayout=findViewById(R.id.tablayout);
        viewPager=findViewById(R.id.viewpager);

        // Load user data
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        String ownerName = sharedPreferences.getString("owner_name", "");
        String shopName = sharedPreferences.getString("shop_name", "");

        ownername=findViewById(R.id.textview_ownername);
        shopname=findViewById(R.id.textview_shopname);
        ownername.setText("Hello "+ownerName);
        shopname.setText(shopName+" store");


        tabLayout.setupWithViewPager(viewPager);

        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        // Add fragments with their respective icons
        fragmentAdapter.addFragment(new AddFragment(), "Add", R.drawable.ic_add);
        fragmentAdapter.addFragment(new SaleFragment(), "Sales", R.drawable.ic_sale);
        fragmentAdapter.addFragment(new StoreFragment(), "Store", R.drawable.ic_store);
        fragmentAdapter.addFragment(new ReportFragment(), "Report", R.drawable.ic_report);

        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);

        // Set icons for tabs
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(fragmentAdapter.getPageIcon(i));
            }
        }

        // Optional: Customize tab appearance
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // You can add custom behavior when a tab is selected
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // You can add custom behavior when a tab is unselected
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // You can add custom behavior when a tab is reselected
            }
        });


        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


//        // Optional: Method to clear user data (for testing)
//        private void clearUserData() {
//            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.clear();
//            editor.apply();
//        }

    }
}