package com.moonturns.liverpg.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseAppLifecycleListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.moonturns.liverpg.R;
import com.moonturns.liverpg.UserLogin.LoginActivity;

import java.util.Map;

public class FirstActivity extends AppCompatActivity {

    private DatabaseReference mReference;

    private CountDownTimer timer;

    private ProgressBar progressBarFirst;

    private void crt() {
        progressBarFirst = this.findViewById(R.id.progressBarFirst);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        crt();
        checkInternetStatus();
    }

    //If user is avaible retur truen, not return false
    private boolean isUserAvaible() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null)
            return true;
        else
            return false;
    }

    //If user is avaible go to MainActivity, or not go to LoginActivity
    private void goToActivity() {
        if (isUserAvaible()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();

        }
    }

    //Check internet, if there is a connection goToActivity works or there is no a connection showAlert works
    private void checkInternetStatus() {
        mReference = FirebaseDatabase.getInstance().getReference(".info/connected");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if (connected) {
                    timer.cancel();
                    goToActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished == 3000)
                    progressBarFirst.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                showAlert();
            }
        }.start();
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
        builder.setMessage(getString(R.string.no_internet));
        builder.setPositiveButton(getString(R.string.connect), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkInternetStatus();
            }
        });
        builder.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }
}

