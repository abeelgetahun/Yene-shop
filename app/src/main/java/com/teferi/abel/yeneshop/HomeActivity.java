package com.teferi.abel.yeneshop;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

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

        tabLayout.setupWithViewPager(viewPager);

        FragmentAdapter fragmentAdapter=new FragmentAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragmentAdapter.addFragment(new AddFragment(),"Add");
        fragmentAdapter.addFragment(new SaleFragment(),"Sale");
        fragmentAdapter.addFragment(new StoreFragment(),"Store");
        fragmentAdapter.addFragment(new ReportFragment(),"Report");

        viewPager.setAdapter(fragmentAdapter);


        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }
}