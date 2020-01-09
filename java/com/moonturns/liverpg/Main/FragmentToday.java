package com.moonturns.liverpg.Main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moonturns.liverpg.DatabaseModel.UserPlans;
import com.moonturns.liverpg.R;
import com.moonturns.liverpg.Utils.RVLinePlan;

import java.util.ArrayList;

public class FragmentToday extends Fragment {

    private String firebase_users = ""; //child of firebase database
    private String firebase_all_plans = ""; //child of firebase database
    private String firebase_daily_plans = ""; //child of firebase database

    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private ArrayList<UserPlans> userPlansArrayList;
    private long childrenCount = 0; //daily_plans children count at firebase
    private boolean loaded = false;

    private RecyclerView rvTodayPlans;
    private FrameLayout frameNoPlanToday;

    //init widgets
    private void crt(View view) {
        rvTodayPlans = view.findViewById(R.id.rvTodayPlans);
        frameNoPlanToday = view.findViewById(R.id.frameNoPlanToday);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_today, container, false);
        crt(view);
        getCurrentPlansFromFirebase();
        return view;
    }

    //Get plan data from Firebase
    private void getCurrentPlansFromFirebase() {
        userPlansArrayList = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();

        firebase_users = getString(R.string.users);
        firebase_all_plans = getString(R.string.all_plans);
        firebase_daily_plans = getString(R.string.daily_plans);

        /*mReference.child(firebase_users).child(mUser.getUid()).child(firebase_all_plans).child(firebase_daily_plans).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot plans : dataSnapshot.getChildren()) {
                        UserPlans userPlans = plans.getValue(UserPlans.class);
                        userPlansArrayList.add(userPlans);
                    }
                    childrenCount = dataSnapshot.getChildrenCount();
                    frameNoPlanToday.setVisibility(View.GONE);
                    rvTodayPlans.setVisibility(View.VISIBLE);
                }
                setRvCurrentPlans();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        firebaseAddDeleteListener();


    }

    //Firebase child event listener, add or delete a plan
    private void firebaseAddDeleteListener() {
        mReference.child(firebase_users).child(mUser.getUid()).child(firebase_all_plans).child(firebase_daily_plans).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserPlans userPlans = dataSnapshot.getValue(UserPlans.class);
                userPlansArrayList.add(userPlans);
                loaded = true;
                if (childrenCount == 0) {
                    frameNoPlanToday.setVisibility(View.GONE);
                    rvTodayPlans.setVisibility(View.VISIBLE);
                    setRvCurrentPlans();
                }
                childrenCount++;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                childrenCount--;
                if (childrenCount == 0) {
                    frameNoPlanToday.setVisibility(View.VISIBLE);
                    rvTodayPlans.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Set recyclerview rvCurrentPlans
    private void setRvCurrentPlans() {
        RVLinePlan rvLinePlan = new RVLinePlan(getContext(), userPlansArrayList);
        LinearLayoutManager llm = new LinearLayoutManager(getContext()) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        };
        rvTodayPlans.setLayoutManager(llm);
        rvTodayPlans.setAdapter(rvLinePlan);
    }
}
