package com.moonturns.liverpg.UserRegister;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moonturns.liverpg.DatabaseModel.UserLevel;
import com.moonturns.liverpg.DatabaseModel.Users;
import com.moonturns.liverpg.Main.MainActivity;
import com.moonturns.liverpg.R;
import com.moonturns.liverpg.UserLogin.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    private String firebase_users = ""; //child of firebase database

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;

    private String username, email, password, passwordAgain;

    private EditText etRegisterUsername, etRegisterEmail, etRegisterPassword, etRegisterPasswordAgain;
    private Button btnSignup;
    private TextView txtReturnLogin;
    private ProgressBar progressBarRegister;

    //init widgets
    private void crt() {
        etRegisterUsername = this.findViewById(R.id.etRegisterUsername);
        etRegisterEmail = this.findViewById(R.id.etRegisterEmail);
        etRegisterPassword = this.findViewById(R.id.etRegisterPassword);
        etRegisterPasswordAgain = this.findViewById(R.id.etRegisterPasswordAgain);
        txtReturnLogin = this.findViewById(R.id.txtReturnLogin);
        btnSignup = this.findViewById(R.id.btnSignup);
        progressBarRegister = this.findViewById(R.id.progressBarRegister);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        crt();
        mAuth = FirebaseAuth.getInstance();
        eventBtnSignup();
        eventTxtReturnLogin();
        etTextWatcher();
    }

    //assign textWatcher to edittexts
    private void etTextWatcher() {
        etRegisterUsername.addTextChangedListener(textWatcher);
        etRegisterEmail.addTextChangedListener(textWatcher);
        etRegisterPassword.addTextChangedListener(textWatcher);
        etRegisterPasswordAgain.addTextChangedListener(textWatcher);
    }

    //If Email and password are filled correct, make btnLogin background button_active and if Email and
    //password are not filled correct, make btnLogin background button_inactive
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int usernameCount = etRegisterUsername.getText().length();
            int emailCount = etRegisterUsername.getText().length();
            int passwordCount = etRegisterPassword.getText().length();
            int passwordAgainCount = etRegisterPasswordAgain.getText().length();

            if (usernameCount > 0 && emailCount > 0 && passwordCount >= 6 && passwordAgainCount >= 6) {
                btnSignup.setBackground(getDrawable(R.drawable.button_active));
                btnSignup.setTextColor(ContextCompat.getColor(RegisterActivity.this, android.R.color.white));
                btnSignup.setEnabled(true);
            } else {
                btnSignup.setBackground(getDrawable(R.drawable.button_inactive));
                btnSignup.setTextColor(ContextCompat.getColor(RegisterActivity.this, android.R.color.black));
                btnSignup.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //When this works, back to LoginActivity
    private void eventTxtReturnLogin() {
        txtReturnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //event btnSignup
    private void eventBtnSignup() {
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPorgressBar();
                changeBtnLoginStyle(false);
                getUserInformations();
            }
        });
    }

    //Check users sign information username, email, password and password again
    private void getUserInformations() {
        username = etRegisterUsername.getText().toString();
        email = etRegisterEmail.getText().toString();
        password = etRegisterPassword.getText().toString();
        passwordAgain = etRegisterPasswordAgain.getText().toString();

        if (password.equals(passwordAgain)) {
            if (isEmailCorrect(email)) {
                checkEmailFromFirebase(email);
            } else {
                dismissProgressBar();
                Toast.makeText(this, getResources().getString(R.string.enter_correct_email), Toast.LENGTH_LONG).show();
            }
        } else {
            dismissProgressBar();
            Toast.makeText(this, getResources().getString(R.string.incorrect_passwords), Toast.LENGTH_LONG).show();
        }

    }

    //Check email, if email is current show a toast
    private void checkEmailFromFirebase(String email) {
        mReference = FirebaseDatabase.getInstance().getReference();
        mReference.child("users").orderByChild("user_email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    checkUsernameFromFirebase();
                } else {
                    dismissProgressBar();
                    changeBtnLoginStyle(true);
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.current_email), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Check username, if username is current show a toast
    private void checkUsernameFromFirebase() {
        mReference = FirebaseDatabase.getInstance().getReference();
        mReference.child("users").orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    userSignupToFirebase();
                } else {
                    dismissProgressBar();
                    changeBtnLoginStyle(true);
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.current_username), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Sign up user to Firebasenad save
    private void userSignupToFirebase() {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String user_id = task.getResult().getUser().getUid();
                    saveUserInformationsToFirebaseDatabase(user_id);
                } else {
                    dismissProgressBar();
                    changeBtnLoginStyle(true);
                    Toast.makeText(RegisterActivity.this, "Kayıt başarısız", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //User informations are saved to Firebase database
    private void saveUserInformationsToFirebaseDatabase(String userID) {

        firebase_users = this.getResources().getString(R.string.users);

        mReference = FirebaseDatabase.getInstance().getReference();

        UserLevel userLevel = new UserLevel();
        userLevel.setUser_level("1");
        userLevel.setUser_point("0");
        userLevel.setUser_completed_xp("0");
        userLevel.setUser_failed_xp("0");

        Users newUser = new Users();
        newUser.setUsername(username);
        newUser.setUser_email(email);
        newUser.setUser_password(password);
        newUser.setUser_id(userID);
        newUser.setUserLevel(userLevel);

        mReference.child(firebase_users).child(userID).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dismissProgressBar();
                    onBackPressed();
                } else {
                    dismissProgressBar();
                    changeBtnLoginStyle(true);
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_failed), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Control email address type, if it is correct return true or not return false
    private boolean isEmailCorrect(String email_address) {
        return Patterns.EMAIL_ADDRESS.matcher(email_address).matches();
    }

    //Change btnLogin background
    private void changeBtnLoginStyle(boolean isCorrect) {
        //If user fills email and password correct it is true or nor false
        if (isCorrect) {
            btnSignup.setBackgroundResource(R.drawable.button_active);
            btnSignup.setTextColor(ContextCompat.getColor(RegisterActivity.this, android.R.color.white));
            btnSignup.setEnabled(true);
        }else {
            btnSignup.setBackgroundResource(R.drawable.button_inactive);
            btnSignup.setTextColor(ContextCompat.getColor(RegisterActivity.this, android.R.color.holo_blue_dark));
            btnSignup.setEnabled(false);
        }
    }

    private void showPorgressBar() {
        progressBarRegister.setVisibility(View.VISIBLE);
    }

    private void dismissProgressBar() {
        progressBarRegister.setVisibility(View.GONE);
    }
}
