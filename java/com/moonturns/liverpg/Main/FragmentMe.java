package com.moonturns.liverpg.Main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moonturns.liverpg.DatabaseModel.UserLevel;
import com.moonturns.liverpg.DatabaseModel.Users;
import com.moonturns.liverpg.R;
import com.moonturns.liverpg.UserLogin.LoginActivity;

public class FragmentMe extends Fragment {

    private String firebase_users; //database child
    private String firebase_all_plans; //database child
    private String firebase_completed_plans; //database child
    private String firebase_failed_plans; //database child

    private Users users;

    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private long countCompletedPlans = 0;
    private long countfailedPlans = 0;

    private TextView txtUsername, txtLevel, txtCompletedXp, txtFailedXp, txtCompletedPlan, txtFailedPlan, txtLevelPoint;
    private Button btnStartAgain;
    private ImageView imgLogOut;
    private ProgressBar progressBarMe;

    //init widgets
    private void crt(View view) {
        txtUsername = view.findViewById(R.id.txtUsername);
        txtLevel = view.findViewById(R.id.txtLevel);
        txtCompletedXp = view.findViewById(R.id.txtCompletedXp);
        txtFailedXp = view.findViewById(R.id.txtFailedXp);
        txtCompletedPlan = view.findViewById(R.id.txtCompletedPlan);
        txtFailedPlan = view.findViewById(R.id.txtFailedPlan);
        txtLevelPoint = view.findViewById(R.id.txtLevelPoint);
        btnStartAgain = view.findViewById(R.id.btnStartAgain);
        imgLogOut = view.findViewById(R.id.imgLogOut);
        progressBarMe = view.findViewById(R.id.progressBarMe);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_me, container, false);
        crt(view);
        showPorgressBar();
        firebaseUserLevelInformation();
        userLogOut();
        return view;
    }

    //Go to firebase database
    private void firebaseUserLevelInformation() {

        firebase_users = getString(R.string.users);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();

        mReference.child(firebase_users).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    users = dataSnapshot.getValue(Users.class);
                    firebasePlanInformations();
                } else {
                    dismissProgressBar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Go to firebase database
    private void firebasePlanInformations() {
        firebase_users = getString(R.string.users);
        firebase_all_plans = getString(R.string.all_plans);
        firebase_completed_plans = getString(R.string.completed_plans);
        firebase_failed_plans = getString(R.string.failed_plans);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();

        //completed plans
        mReference.child(firebase_users).child(mUser.getUid()).child(firebase_all_plans).child(firebase_completed_plans).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    countCompletedPlans = dataSnapshot.getChildrenCount();

                }
                firebaseUserFailedXp();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Go to firebase for user failed xp datas
    private void firebaseUserFailedXp() {
        //failed_plans
        mReference.child(firebase_users).child(mUser.getUid()).child(firebase_all_plans).child(firebase_failed_plans).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    countfailedPlans = dataSnapshot.getChildrenCount();
                }
                setUserInformations(users);
                setPlanInformations();
                dismissProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Set user level information to widgets
    private void setUserInformations(Users users) {
        UserLevel userLevel = users.getUserLevel();
        String username = users.getUsername();
        String level = userLevel.getUser_level();
        int completed_xp_point = Integer.valueOf(userLevel.getUser_completed_xp());
        int failed_xp_point = Integer.valueOf(userLevel.getUser_failed_xp());

        txtUsername.setText(username);
        txtLevel.setText("Level " + level);
        if (completed_xp_point == 0) {
            txtCompletedXp.setText("" + completed_xp_point);
        } else {
            txtCompletedXp.setText("+" + completed_xp_point);
        }

        if (failed_xp_point == 0) {
            txtFailedXp.setText("" + failed_xp_point);
        } else {
            txtFailedXp.setText("-" + failed_xp_point);
        }

        if (userLevel.getUser_level().equals("0")) {
            //Set user point and (user point * 1000)
            //this is next level point
            txtLevelPoint.setText(userLevel.getUser_point() + "/" + ((Integer.valueOf(userLevel.getUser_level()) + 1) * 1000));
        } else {
            //Set user point and (user point * 1000)
            //this is next level point
            txtLevelPoint.setText(userLevel.getUser_point() + "/" + (Integer.valueOf(userLevel.getUser_level()) * 1000));
        }
        eventBtnStartAgin();
    }

    //Set user level information to widgets
    private void setPlanInformations() {
        txtCompletedPlan.setText("" + countCompletedPlans);
        txtFailedPlan.setText("" + countfailedPlans);
    }

    //When user click imgLogOut, user log out and open Login Activity
    private void userLogOut() {
        imgLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutAlertDialog();
            }
        });
    }

    private void eventBtnStartAgin() {
        btnStartAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAlertDialog();
            }
        });
    }

    //When user click btnStartAgin, reset user level datas and all_plans
    private void setReset() {
        mReference.child(firebase_users).child(mUser.getUid()).child(firebase_all_plans).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    UserLevel userLevel = new UserLevel();
                    userLevel.setUser_completed_xp("0");
                    userLevel.setUser_failed_xp("0");
                    userLevel.setUser_level("0");
                    userLevel.setUser_point("0");
                    mReference.child(firebase_users).child(mUser.getUid()).child("userLevel").setValue(userLevel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            getActivity().onBackPressed();
                        }
                    });
                }
            }
        });
    }

    //Show an alert dialog for setReset()
    private void resetAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getString(R.string.all_data_delete));
        alert.setMessage(getString(R.string.sure));
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setReset();
            }
        });
        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    //Show an alert dialog for userLogOut()
    private void logOutAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getString(R.string.log_out_sign));
        alert.setMessage(getString(R.string.sure));
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);

                getActivity().finish();
            }
        });
        alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void showPorgressBar() {
        progressBarMe.setVisibility(View.VISIBLE);
    }

    private void dismissProgressBar() {
        progressBarMe.setVisibility(View.GONE);
    }
}
