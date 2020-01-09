package com.moonturns.liverpg.Utils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragmentList;
    private ArrayList<String> tabTitles;

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentList = new ArrayList<>();
        tabTitles = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }

    public void addTitles(ArrayList<String> tabTitles) {
        this.tabTitles = tabTitles;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
       return tabTitles.get(position);
    }

}
