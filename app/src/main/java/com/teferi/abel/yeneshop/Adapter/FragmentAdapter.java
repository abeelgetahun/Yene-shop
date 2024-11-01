package com.teferi.abel.yeneshop.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitle = new ArrayList<>();
    private final List<Integer> fragmentIcons = new ArrayList<>();

    public FragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String title, int iconResId) {
        if (fragment != null && title != null) {
            fragmentList.add(fragment);
            fragmentTitle.add(title);
            fragmentIcons.add(iconResId);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }

    public int getPageIcon(int position) {
        return fragmentIcons.get(position);
    }
}