package com.moonturns.liverpg.Main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moonturns.liverpg.DatabaseModel.UserPlans;
import com.moonturns.liverpg.R;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimerTask;
import java.util.logging.SimpleFormatter;

import javax.security.auth.login.LoginException;

public class NewPlanFragment extends DialogFragment {

    private String firebase_users = ""; //child of firebase database
    private String firebase_all_plans = ""; //child of firebase database

    private String firebase_daily_plans = ""; //If first item is selected this is daily_plans at radio group
    private String firebase_future_plans = ""; //If first item is selected this is future_plans at radio group

    private int day; //System day
    private int month; //System month
    private int year; //System year

    private FirebaseUser mUser;
    private DatabaseReference mReference;

    private String planChild; ///If first item is selected planChild is daily_plan, or second planChild is future_plan from radio group and first value is daily_plans from resources

    private int xpPoint = 1; //Point that user choiced
    private String calendar = ""; //System calendar day/month+1/year
    private String time = ""; //System clock
    private String textContent = ""; //Content that user wrote
    private String xp = "";

    private String plan_id = "";
    private UserPlans userPlans;

    private TextView txtNewPlanCalendar, txtNewPlanTime, txtPlanPoint;
    private EditText etNewPlanContent;
    private SeekBar seekBarPlanPoint;
    private ImageView imgCancel, imgOkay;
    private RadioGroup rgPlan;

    private void crt(View view) {
        txtNewPlanCalendar = view.findViewById(R.id.txtNewPlanCalendar);
        txtNewPlanTime = view.findViewById(R.id.txtNewPlanTime);
        txtPlanPoint = view.findViewById(R.id.txtPlanPoint);
        etNewPlanContent = view.findViewById(R.id.etNewPlanContent);
        seekBarPlanPoint = view.findViewById(R.id.seekBarPlanPoint);
        imgCancel = view.findViewById(R.id.imgCancel);
        imgOkay = view.findViewById(R.id.imgOkay);
        rgPlan = view.findViewById(R.id.rgPlan);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_plan, container, false);
        planChild = getString(R.string.daily_plans);
        crt(view);
        setSystemDatas();
        seekBarChangeXP();
        eventimgCancel();
        eventImgOkay();
        setRgPlan();
        return view;
    }

    //Seekbar is changed and txtPlanPoint is changed
    private void seekBarChangeXP() {
        seekBarPlanPoint.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                xpPoint = progress;
                String xp = String.valueOf(progress);
                if (xpPoint == 0) {
                    txtPlanPoint.setText("1" + " XP");
                }else {
                    txtPlanPoint.setText(xp + " XP");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //If imgCancel is clicked, dismiss dialog
    private void eventimgCancel() {
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    //If imgOkay is clicked, save to user database to Firebase
    private void eventImgOkay() {
        imgOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etNewPlanContent.getText().toString().isEmpty()) {
                    savePlanToFirebase();
                }else {
                    Toast.makeText(getContext(), getString(R.string.type_plan), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Save plan to Firebase
    private void savePlanToFirebase() {

        firebase_users = getString(R.string.users);
        firebase_all_plans = getString(R.string.all_plans);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();

        plan_id = mReference.push().getKey();
        getInformations();

        mReference.child(firebase_users).child(mUser.getUid()).child(firebase_all_plans).child(planChild).child(plan_id).setValue(userPlans).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dismiss();
                } else {
                    Toast.makeText(getContext(), getString(R.string.plan_failed), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //System calendar and time for widgets
    private void setSystemDatas() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        calendar = day + "/" + (month + 1) + "/" + year;
        time = simpleDateFormat.format(c.getTime());

        txtNewPlanCalendar.setText(calendar);
        txtNewPlanTime.setText(time);
    }

    //Get some system and plan informations and write userPlans
    private void getInformations() {

        Calendar c = Calendar.getInstance();

        textContent = etNewPlanContent.getText().toString();
        xp = String.valueOf(xpPoint);

        userPlans = new UserPlans();
        userPlans.setCalendar(calendar);
        userPlans.setTime(time);
        userPlans.setFinish_time("");
        userPlans.setText_content(textContent);
        userPlans.setXp(xp);
        userPlans.setPlan_id(plan_id);
        userPlans.setKind_of_plan(planChild);

    }

    //Set radio button, if R.id.rbToday is clicked planChild is daiy_plan, if R.id.rbFuture is clicked planChild is future_plan
    private void setRgPlan() {
        rgPlan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbToday:
                        firebase_daily_plans = getString(R.string.daily_plans);
                        planChild = firebase_daily_plans;
                        break;
                    case R.id.rbFuture:
                        firebase_future_plans = getString(R.string.future_plans);
                        planChild = firebase_future_plans;
                        break;
                }
            }
        });
    }

}
