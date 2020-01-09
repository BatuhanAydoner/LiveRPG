package com.moonturns.liverpg.UserLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.moonturns.liverpg.Main.MainActivity;
import com.moonturns.liverpg.R;
import com.moonturns.liverpg.UserRegister.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private String email = "", password = ""; //user enter informations
    private TextWatcher textWatcher; //for etUserLoginEmail and etUserLoginPassword

    private EditText etUserLoginEmail, etUserLoginPassword;
    private Button btnLogin, btnCreateANewAccount;
    private ProgressBar progressBarLogin;

    //init widgets
    private void crt() {
        etUserLoginEmail = this.findViewById(R.id.etUserLoginEmail);
        etUserLoginPassword = this.findViewById(R.id.etUserLoginPassword);
        btnLogin = this.findViewById(R.id.btnLogin);
        btnCreateANewAccount = this.findViewById(R.id.btnCreateANewAccount);
        progressBarLogin = this.findViewById(R.id.progressBarLogin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        crt();
        setTextWatcher();
        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();
        setmAuthStateListener();
        eventBtnLogin();
        eventBtnCreateANewAccount();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }

    //If Email and password are filled correct, make btnLogin background button_active and if Email and
    //password are not filled correct, make btnLogin background button_inactive
    private void setTextWatcher() {

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = etUserLoginEmail.getText().toString();
                String password = etUserLoginPassword.getText().toString();

                if (email.length() > 0 && password.length() >= 6) {
                    changeBtnLoginStyle(true);
                } else {
                    changeBtnLoginStyle(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        etUserLoginEmail.addTextChangedListener(textWatcher);
        etUserLoginPassword.addTextChangedListener(textWatcher);
    }

    //When this works, go to RegisterActivity
    private void eventBtnCreateANewAccount() {
        btnCreateANewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    //When login button is clicked, work loginToFirebase() method
    private void eventBtnLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBtnLoginStyle(false);
                getUserEnterInformations();
                loginToFirebase();
            }
        });
    }

    //User is signed in to Firebase
    private void loginToFirebase() {
        showPorgressBar();
        if (isEmailCorrect()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        dismissProgressBar();
                    } else {
                        changeBtnLoginStyle(true);
                        dismissProgressBar();
                        Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else {
            Toast.makeText(this, getResources().getString(R.string.enter_correct_email), Toast.LENGTH_LONG).show();
        }
    }

    //Get email and password informations from etUserLoginEmail and etUserLoginPassword
    private void getUserEnterInformations() {
        email = etUserLoginEmail.getText().toString();
        password = etUserLoginPassword.getText().toString();
    }

    //Set mAuthStateListener, if user is in system, go to MainActivity
    private void setmAuthStateListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);

                    finish();
                }
            }
        };
    }

    //Control email address type, if it is correct return true or not return false
    private boolean isEmailCorrect() {
       return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //Change btnLogin background
    private void changeBtnLoginStyle(boolean isCorrect) {
        //If user fills email and password correct it is true or nor false
        if (isCorrect) {
            btnLogin.setBackgroundResource(R.drawable.button_active);
            btnLogin.setTextColor(ContextCompat.getColor(LoginActivity.this, android.R.color.white));
            btnLogin.setEnabled(true);
        }else {
            btnLogin.setBackgroundResource(R.drawable.button_inactive);
            btnLogin.setTextColor(ContextCompat.getColor(LoginActivity.this, android.R.color.holo_blue_dark));
            btnLogin.setEnabled(false);
        }
    }

    private void showPorgressBar() {
        progressBarLogin.setVisibility(View.VISIBLE);
    }

    private void dismissProgressBar() {
        progressBarLogin.setVisibility(View.GONE);
    }

}
