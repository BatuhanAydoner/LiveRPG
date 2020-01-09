package com.moonturns.liverpg.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.MicrophoneInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

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
import com.moonturns.liverpg.DatabaseModel.UserPlans;
import com.moonturns.liverpg.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Handler;

public class RVLinePlan extends RecyclerView.Adapter<RVLinePlan.MyViewHolder> {

    private String firebase_users;
    private String firebase_all_plans; //child of firebase database
    private String firebase_completed_plans; //child of firebase database

    private FirebaseUser mUser;
    private DatabaseReference mReference;

    private Context context;
    private ArrayList<UserPlans> userPlansArrayList = new ArrayList<>();

    private View view;

    public RVLinePlan(Context context, ArrayList<UserPlans> userPlansArrayList) {
        this.context = context;
        this.userPlansArrayList = userPlansArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.only_line_plan, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserPlans userPlans = userPlansArrayList.get(position);

        holder.setData(userPlans, position);
    }

    @Override
    public int getItemCount() {
        return userPlansArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView root = (CardView) itemView;
        private ImageView imgFailed, imgCompleted;
        private TextView txtCalendar, txtTime, txtPlanContent, txtPlanPoint;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFailed = root.findViewById(R.id.imgFailed);
            imgCompleted = root.findViewById(R.id.imgCompleted);
            txtCalendar = root.findViewById(R.id.txtCalendar);
            txtTime = root.findViewById(R.id.txtTime);
            txtPlanContent = root.findViewById(R.id.txtPlanContent);
            txtPlanPoint = root.findViewById(R.id.txtPlanPoint);
        }

        public void setData(final UserPlans userPlans, final int position) {
            String calendar = userPlans.getCalendar();
            String time = userPlans.getTime();
            String planContent = userPlans.getText_content();
            String xp = userPlans.getXp();

            txtCalendar.setText(calendar);
            txtTime.setText(time);
            txtPlanContent.setText(planContent);
            txtPlanPoint.setText(xp + "XP");

            Shader shader = new LinearGradient(20f, 0f, 0f, 20f, Color.RED, Color.WHITE, Shader.TileMode.CLAMP);
            txtPlanPoint.getPaint().setShader(shader);

            imgCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCompletedPlans(userPlans, position);
                    getUserLevelFirebase(userPlans, true);
                }
            });

            imgFailed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFailedPlans(userPlans, position);
                    getUserLevelFirebase(userPlans, false);
                }
            });
        }
    }

    //Go to Firebase and set completed plan
    private void addCompletedPlans(final UserPlans userPlans, final int position) {

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();

        String plan_id = mReference.push().getKey();

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        String calendar = day + "/" + (month + 1) + "/" + year;
        String finishTime = hour + ":" + minute + ":" + second;

        firebase_users = context.getString(R.string.users); //child of firebase database
        firebase_all_plans = context.getString(R.string.all_plans); //child of firebase database
        firebase_completed_plans = context.getString(R.string.completed_plans); //child of firebase database
        final String plan = userPlans.getKind_of_plan();
        final String id = userPlans.getPlan_id();

        userPlans.setCalendar(calendar);
        userPlans.setFinish_time(finishTime);
        userPlans.setPlan_id(plan_id);

        mReference.child(firebase_users).child(mUser.getUid()).child(firebase_all_plans).child(firebase_completed_plans).child(plan_id).setValue(userPlans).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mReference.child(firebase_users).child(mUser.getUid()).child(firebase_all_plans).child(plan).child(id).removeValue();
                    deleteItem(position);
                }
            }
        });
    }

    //Go to Firebase and set failed plan
    private void addFailedPlans(UserPlans userPlans, final int position) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        String calendar = day + "/" + (month + 1) + "/" + year;
        String finishTime = hour + ":" + minute + ":" + second;

        userPlans.setCalendar(calendar);
        userPlans.setFinish_time(finishTime);

        final String firebase_users = context.getString(R.string.users); //child of firebase database
        final String firebase_all_plans = context.getString(R.string.all_plans); //child of firebase database
        final String firebase_failed_plans = context.getString(R.string.failed_plans); //child of firebase database
        final String plan = userPlans.getKind_of_plan();
        final String id = userPlans.getPlan_id();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();

        String plan_id = mReference.push().getKey();

        mReference.child(firebase_users).child(mUser.getUid()).child(firebase_all_plans).child(firebase_failed_plans).child(plan_id).setValue(userPlans).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mReference.child(firebase_users).child(mUser.getUid()).child(firebase_all_plans).child(plan).child(id).removeValue();
                    deleteItem(position);
                }
            }
        });
    }

    //When user click failed or completed button, delete item with a animation
    private void deleteItem(int position) {
        userPlansArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, userPlansArrayList.size());
    }

    //Get user_level datas from Firebase database
    private void getUserLevelFirebase(final UserPlans userPlans, final boolean isCompleted) {
        firebase_users = context.getString(R.string.users); //child of firebase database

        mReference.child(firebase_users).child(mUser.getUid()).child("userLevel").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    UserLevel userLevel = dataSnapshot.getValue(UserLevel.class);
                    setUserLevelFirebase(userPlans, userLevel, isCompleted);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Set to Firebase database new datas
    private void setUserLevelFirebase(UserPlans userPlans, UserLevel userLevel, boolean isCompleted) {
        int user_completed_xp = Integer.valueOf(userLevel.getUser_completed_xp()); //user completed plan xp
        int user_failed_xp = Integer.valueOf(userLevel.getUser_failed_xp()); //user failed plan xp
        int user_level = Integer.valueOf(userLevel.getUser_level()); //user level
        int user_point = Integer.valueOf(userLevel.getUser_point()); //user current point

        int plan_xp = Integer.parseInt(userPlans.getXp());

        if (isCompleted) {
            user_point += plan_xp;
            user_completed_xp += plan_xp;
            if (user_point >= (user_level * 1000)) { //user level control
                user_level++;
            }
        } else {
            user_point -= 2 * plan_xp;
            user_failed_xp += plan_xp;
            if (user_point < (user_level * 1000)) { //user level control
                if (user_level > 0 ) {
                    user_level--;
                }
            }
        }

        UserLevel newUserLevel = new UserLevel();
        newUserLevel.setUser_completed_xp(String.valueOf(user_completed_xp));
        newUserLevel.setUser_failed_xp(String.valueOf(user_failed_xp));
        newUserLevel.setUser_level(String.valueOf(user_level));
        newUserLevel.setUser_point(String.valueOf(user_point));

        mReference.child(firebase_users).child(mUser.getUid()).child("userLevel").setValue(newUserLevel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        });
    }
}
