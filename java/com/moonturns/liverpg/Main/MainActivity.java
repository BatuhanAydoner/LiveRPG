package com.moonturns.liverpg.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moonturns.liverpg.R;
import com.moonturns.liverpg.Utils.MainViewPagerAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FragmentTransaction transaction;
    private FragmentMe fragmentMe;
    private ArrayList<String> tabTitles;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fabNewPlan;
    private ImageView imgMe;
    private ConstraintLayout root;
    private FrameLayout frameMe;

    //init widgets
    private void crt() {
        tabLayout = this.findViewById(R.id.tabLayout);
        viewPager = this.findViewById(R.id.viewPager);
        fabNewPlan = this.findViewById(R.id.fabNewPlan);
        imgMe = this.findViewById(R.id.imgMe);
        root = this.findViewById(R.id.root);
        frameMe = this.findViewById(R.id.frameMe);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        crt();
        setFragmentsToViewPager();
        eventFabNewPlan();
        eventImgMe();
    }

    private void setFragmentsToViewPager() {

        tabTitles = new ArrayList<>();
        tabTitles.add(getResources().getString(R.string.today));
        tabTitles.add(getResources().getString(R.string.future));

        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mainViewPagerAdapter.addFragment(new FragmentToday());
        mainViewPagerAdapter.addFragment(new FragmentCurrent());
        mainViewPagerAdapter.addTitles(tabTitles);

        viewPager.setAdapter(mainViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    //When floating action button is clicked, open new plan fragment
    private void eventFabNewPlan() {
        fabNewPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPlanFragment newPlanFragment = new NewPlanFragment();
                newPlanFragment.show(MainActivity.this.getSupportFragmentManager(), "newPlanFragment");
            }
        });
    }

    //Show FragmentToday
    private void eventImgMe() {
        imgMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentMe = new FragmentMe();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(frameMe.getId(), fragmentMe, "fragmentMe");
                transaction.commit();
                frameMe.setVisibility(View.VISIBLE);
                root.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (frameMe.getVisibility() == View.VISIBLE) {
            frameMe.setVisibility(View.GONE);
            root.setVisibility(View.VISIBLE);
            transaction.remove(fragmentMe);
        } else {
            super.onBackPressed();
        }
    }
}
