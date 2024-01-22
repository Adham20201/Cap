package com.leo.h19305;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button callSignUp;
    RelativeLayout login_btn;
    LottieAnimationView login_btn_animation;
    ImageView image;
    TextView logoText, sloganText, login_btn_txt;
    TextInputLayout email, password;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        //Hooks
        callSignUp = findViewById(R.id.signup_screen);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        login_btn = findViewById(R.id.login_btn);
        login_btn_txt = findViewById(R.id.login_btn_txt);
        login_btn_animation = findViewById(R.id.login_btn_animation);

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(logoText, "logo_text");
                pairs[2] = new Pair<View, String>(sloganText, "logo_desc");
                pairs[3] = new Pair<View, String>(email, "username_tran");
                pairs[4] = new Pair<View, String>(password, "password_tran");
                pairs[5] = new Pair<View, String>(login_btn, "button_tran");
                pairs[6] = new Pair<View, String>(callSignUp, "login_signup_tran");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (validateEmailO()){
                    email.setError(null);
                    email.setErrorEnabled(false);
                }
            }
        });
        password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (validatePasswordO()){
                    password.setError(null);
                    password.setErrorEnabled(false);
                }
            }
        });

    }


    private void userLogin() {

        String emailTxt = email.getEditText().getText().toString().trim();
        String passwordTxt = password.getEditText().getText().toString().trim();

        if (!validateEmail() | !validatePassword()){
            return;
        }

        login_btn_txt.setVisibility(View.GONE);
        login_btn_animation.setVisibility(View.VISIBLE);
        login_btn_animation.playAnimation();

        mAuth.signInWithEmailAndPassword(emailTxt,passwordTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                login_btn_txt.setVisibility(View.VISIBLE);
                login_btn_animation.setVisibility(View.GONE);
                login_btn_animation.pauseAnimation();

                if (task.isSuccessful()){
                    startActivity(new Intent(LoginActivity.this,BluetoothConnectionActivity.class));
                }
            }
        });

    }

    private Boolean validateEmail(){
        String val = email.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            email.setError("Email is required");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()){
            email.setError("Please provide valid email");
            return false;
        }
        else {
            email.setError(null);
            email.setErrorEnabled(false);

            return true;
        }
    }
    private Boolean validatePassword(){
        String val = password.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            password.setError("Password is required");
            return false;
        }
        else if (val.length()<6){
            password.setError("Min password length should be 6 character!");
            return false;
        }
        else {
            password.setError(null);
            password.setErrorEnabled(false);

            return true;
        }
    }

    private Boolean validateEmailO(){
        String val = email.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()){
            return false;
        }
        else {
            return true;
        }
    }
    private Boolean validatePasswordO(){
        String val = password.getEditText().getText().toString().trim();
        if (val.isEmpty()){
            return false;
        }
        else if (val.length()<6){
            return false;
        }
        else {
            return true;
        }
    }

}